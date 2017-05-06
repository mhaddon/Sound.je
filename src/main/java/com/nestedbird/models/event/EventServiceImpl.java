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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.nestedbird.models.artist.Artist;
import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.core.Audited.AuditedRepository;
import com.nestedbird.models.core.Audited.AuditedServiceImpl;
import com.nestedbird.models.eventtime.EventTime;
import com.nestedbird.models.location.Location;
import com.nestedbird.models.occurrence.Occurrence;
import com.nestedbird.models.occurrence.OccurrenceService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Event service.
 */
@Service
@Transactional
@Slf4j
class EventServiceImpl extends AuditedServiceImpl<Event> implements EventService {
    private static final String REDIS_KEY_UPCOMING_EVENTS = "UpcomingEvents";

    private final OccurrenceService occurrenceService;
    private final EventRepository eventRepository;
    private final RedissonClient redissonClient;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Instantiates a new Event service.
     *
     * @param eventRepository the event repository
     * @param redissonClient
     */
    @Autowired
    EventServiceImpl(final EventRepository eventRepository,
                     final RedissonClient redissonClient,
                     final OccurrenceService occurrenceService) {
        this.eventRepository = eventRepository;
        this.redissonClient = redissonClient;
        this.occurrenceService = occurrenceService;
    }

    @Override
    protected AuditedRepository<Event> getRepository() {
        return eventRepository;
    }

    @Override
    public Optional<Event> findFirstByFacebookId(final Long facebookId) {
        return Optional.ofNullable(eventRepository.findFirstByFacebookId(facebookId));
    }

    @Override
    public RScoredSortedSet<String> getUpcomingEventsFromStore() {
        final RScoredSortedSet<String> set = redissonClient.getScoredSortedSet(REDIS_KEY_UPCOMING_EVENTS);
        if (set.size() == 0) {
            updateUpcomingStore();
        }
        return set;
    }

    @Override
    public Set<ParsedEventData> retrieveUpcoming() {
        return getAllPossibleUpcoming().stream()
                .filter(AuditedEntity::getActive)
                .map(Event::getTimes)
                .flatMap(Set::stream)
                .map(EventTime::getFutureOccurrences)
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }

    public void updateUpcomingStore() {
        final RScoredSortedSet<String> set = redissonClient.getScoredSortedSet(REDIS_KEY_UPCOMING_EVENTS);

        set.clear();
        retrieveUpcoming()
                .forEach(parsedEventData ->
                        set.add(parsedEventData.getStartTime().getMillis() / 1000.0, parsedEventData.toJSON()));
    }

    @Override
    public Page<Occurrence> getUpcomingOccurrences(final Pageable pageable) {
        final RScoredSortedSet<String> set = getUpcomingEventsFromStore();

        final int page = pageable.getPageNumber();
        final int count = pageable.getPageSize();

        final List<ScoredEntry<String>> occurrenceCollection = new ArrayList<>(set.entryRange(page * count, ((page + 1) * count) - 1));
        final Page<ScoredEntry<String>> occurrences = new PageImpl<>(occurrenceCollection, pageable, set.size());

        return occurrences
                .map(this::convertParsedEventToOccurrence);
    }

    @Override
    public Set<Occurrence> retrieveUpcomingByArtist(final Artist artist) {
        return getAllPossibleUpcoming().stream()
                .filter(AuditedEntity::getActive)
                .filter(event -> event.getArtists().contains(artist))
                .map(Event::getTimes)
                .flatMap(Set::stream)
                .map(EventTime::getFutureOccurrences)
                .flatMap(List::stream)
                .map(occurrenceService::parseParsedEventData)
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    private List<Event> getAllPossibleUpcoming() {
        return entityManager.createNativeQuery(
                "CALL getUpcomingEvents()",
                Event.class
        ).getResultList();
    }

    @Override
    public Set<Occurrence> retrieveUpcomingByLocation(final Location location) {
        return getAllPossibleUpcoming().stream()
                .filter(AuditedEntity::getActive)
                .filter(event -> event.getLocation().orElse(null).equals(location))
                .map(Event::getTimes)
                .flatMap(Set::stream)
                .map(EventTime::getFutureOccurrences)
                .flatMap(List::stream)
                .map(occurrenceService::parseParsedEventData)
                .collect(Collectors.toSet());
    }

    private Occurrence convertParsedEventToOccurrence(final ScoredEntry<String> parsedEvent) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Hibernate5Module());

        try {
            final ParsedEventData parsedEventData = objectMapper.readValue(parsedEvent.getValue(), ParsedEventData.class);
            return occurrenceService.parseParsedEventData(parsedEventData);
        } catch (IOException e) {
            logger.info("[EventController] [convertEventCollectionToListOfOccurrences] Failure To Read JSON From Cache", e);
        }
        return null;
    }
}