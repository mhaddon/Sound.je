/*
 *  NestedBird  Copyright (C) 2016-2017  Michael Haddon
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License version 3
 *  as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nestedbird.models.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.nestedbird.config.SocialConfigSettings;
import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.core.Base.BaseController;
import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseService;
import com.nestedbird.models.eventtime.EventTime;
import com.nestedbird.models.occurrence.Occurrence;
import com.nestedbird.models.occurrence.OccurrenceRepository;
import com.nestedbird.modules.facebookreader.FacebookPoster;
import com.nestedbird.modules.formparser.ParameterMapParser;
import com.nestedbird.modules.resourceparser.EventParser;
import com.nestedbird.util.Mutable;
import com.nestedbird.util.QueryBlock;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Event controller.
 */
@RestController
@RequestMapping("api/v1/Events/")
@Slf4j
public class EventController extends BaseController<Event> {

    private final EventRepository eventRepository;
    private final OccurrenceRepository occurrenceRepository;
    private final EventService eventService;
    private final RedissonClient redissonClient;
    private final SocialConfigSettings socialConfigSettings;
    private final FacebookPoster facebookPoster;
    private final EventParser eventParser;
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Instantiates a new Event controller.
     *
     * @param eventService         the event service
     * @param eventRepository      the event repository
     * @param occurrenceRepository the occurrence repository
     * @param redissonClient       the redisson client
     * @param socialConfigSettings the social config settings
     * @param facebookPoster       the facebook poster
     * @param eventParser          the event parser
     */
    @Autowired
    EventController(final EventService eventService,
                    final EventRepository eventRepository,
                    final OccurrenceRepository occurrenceRepository,
                    final RedissonClient redissonClient,
                    final SocialConfigSettings socialConfigSettings,
                    final FacebookPoster facebookPoster,
                    final EventParser eventParser) {
        this.eventService = eventService;
        this.eventRepository = eventRepository;
        this.occurrenceRepository = occurrenceRepository;
        this.redissonClient = redissonClient;
        this.socialConfigSettings = socialConfigSettings;
        this.facebookPoster = facebookPoster;
        this.eventParser = eventParser;
    }

    @Override
    public BaseRepository<Event> getRepository() {
        return eventRepository;
    }

    @Override
    public Class<Event> getEntityClass() {
        return Event.class;
    }

    @Override
    public BaseService<Event> getService() {
        return this.eventService;
    }

    /**
     * Automatically post events to facebook...
     * This doesnt really work as expected and facebook seems to block it
     * It is mostly proof of concept
     */
    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    @RequestMapping(value = "/updateFB", method = RequestMethod.GET)
    public void postToFacebook() {
        if (!socialConfigSettings.getFbAutoPost())
            return;

        final Page<Occurrence> upcomingEvents = listUpcoming(new PageRequest(0, 100));

        upcomingEvents.getContent().stream()
                .filter(e -> e.getStartTime().isBefore(DateTime.now().plusWeeks(2)))
                .forEach(e -> {
                    e.getEvent().ifPresent(event -> {
                        if (occurrenceRepository.findFirstByEventAndStartTime(event, e.getStartTime()) == null) {
                            occurrenceRepository.saveAndFlush(facebookPoster.create(e));
                        }
                    });
                });
    }

    /**
     * Retrieve paginated list of upcoming events
     *
     * @param pageable pagination settings
     * @return page of events
     */
    @RequestMapping(value = "/Upcoming", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Occurrence> listUpcoming(final Pageable pageable) {
        final int page = pageable.getPageNumber();
        final int count = pageable.getPageSize();

        // if cache is not set, retrieve new set of data
        final RScoredSortedSet<String> set = redissonClient.getScoredSortedSet("UpcomingEvents");
        if (set.size() == 0) {
            retrieveUpcomingToCache();
        }

        final Collection<ScoredEntry<String>> eventCollection = set.entryRange(page * count, ((page + 1) * count) - 1);
        final List<Occurrence> eventList = convertEventCollectionToListOfOccurrences(eventCollection);
        return new PageImpl<>(eventList, pageable, set.size());
    }

    /**
     * Calculate upcoming events and save them to redis cache
     */
    @Scheduled(cron = "0 */10 * * * *")
    @Transactional
    public void retrieveUpcomingToCache() {
        final RScoredSortedSet<String> set = redissonClient.getScoredSortedSet("UpcomingEvents");

        final Query query = entityManager.createNativeQuery(
                "CALL getUpcomingEvents()",
                Event.class
        );

        @SuppressWarnings("unchecked") final List<Event> results = ((List<Event>) query.getResultList()).stream()
                .filter(AuditedEntity::getActive).collect(Collectors.toList());

        final List<ParsedEventData> parsedResults = new ArrayList<>();

        // calculate all future occurrences
        results.forEach((Event event) ->
                event.getTimes()
                        .forEach((EventTime eventTime) ->
                                parsedResults.addAll(eventTime.getFutureOccurrences())));

        // save to cache
        set.clear();
        for (ParsedEventData parsedEventData : parsedResults) {
            set.add(parsedEventData.getStartTime().getMillis() / 1000.0, parsedEventData.toJSON());
        }
    }

    /**
     * Processes the collection we store in the cache, retrieves information we need from the database, and mutates
     * some of the data, in preparation before sending it to the user.
     *
     * @param eventCollection collection from cache
     * @return list of occurrences
     */
    private List<Occurrence> convertEventCollectionToListOfOccurrences(final Collection<ScoredEntry<String>> eventCollection) {
        final List<Occurrence> eventList = new ArrayList<>();
        for (ScoredEntry<String> entry : eventCollection) {
            final ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new Hibernate5Module());

            try {
                final JsonNode parsedEventData = objectMapper.readTree(entry.getValue());

                eventService.findOne(parsedEventData.get("eventId").textValue())
                        .ifPresent(event -> {
                            final Occurrence parsedEvent = new Occurrence();
                            parsedEvent.setEvent(event);

                            if (parsedEventData.get("duration").textValue() != null) {
                                parsedEvent.setDuration(Period.parse(parsedEventData.get("duration").textValue()));
                            }

                            parsedEvent.setStartTime(DateTime.parse(parsedEventData.get("startTime").textValue()));
                            //                    parsedEvent.setId(event.getId() + "-" + parsedEvent.getStartTime().getMillis());
                            eventList.add(parsedEvent);
                        });
            } catch (IOException e) {
                logger.info("[EventController] [convertEventCollectionToListOfOccurrences] Failure To Read JSON From Cache", e);
            }
        }
        return eventList;
    }

    /**
     * Parse url event.
     *
     * @param request the request
     * @return the event
     */
    @RequestMapping(value = "parseurl", method = RequestMethod.POST,
            headers = "content-type=application/x-www-form-urlencoded",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Event parseUrl(final HttpServletRequest request) {
        final ParameterMapParser parser = ParameterMapParser.parse(request.getParameterMap());
        final Mutable<Event> event = Mutable.of(null);

        QueryBlock.create()

                // Ensure the request has sent the required data
                .require(parser.has("url"), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Request does not have required data")
                        .done(data -> data.put("url", parser.get("url").toString())))

                // Return the processes medium
                .done(data -> event.mutate(eventParser.parseUrl(data.get("url").toString())));

        event.ofNullable().ifPresent(newEvent ->
                event.mutate(eventService
                        .findFirstByFacebookId(newEvent.getFacebookId())
                        .orElseGet(() -> eventService.saveAndFlush(newEvent).orElse(newEvent)))
        );

        return event.get();
    }
}