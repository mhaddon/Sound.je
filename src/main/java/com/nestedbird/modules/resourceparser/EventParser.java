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

package com.nestedbird.modules.resourceparser;

import com.nestedbird.jackson.facebook.*;
import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.event.Event;
import com.nestedbird.models.event.EventRepository;
import com.nestedbird.models.event.EventService;
import com.nestedbird.models.eventtime.EventTime;
import com.nestedbird.models.eventtime.EventTimeRepository;
import com.nestedbird.models.location.Location;
import com.nestedbird.models.location.LocationService;
import com.nestedbird.modules.facebookreader.FacebookReader;
import com.nestedbird.util.Mutable;
import com.nestedbird.util.PatternMatcher;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

/**
 * The type Event parser.
 */
@Component
public class EventParser {
    private static final Pattern facebookEventPattern = Pattern.compile(".*\\/events\\/([\\d]+).*");

    private final FacebookReader facebookReader;

    private final EventRepository eventRepository;

    private final EventTimeRepository eventTimeRepository;

    private final LocationService locationService;

    private final EventService eventService;

    /**
     * Instantiates a new Event parser.
     *
     * @param facebookReader      the facebook reader
     * @param eventRepository     the event repository
     * @param eventTimeRepository the event time repository
     * @param locationService     the location service
     * @param eventService        the event service
     */
    @Autowired
    public EventParser(final FacebookReader facebookReader,
                       final EventRepository eventRepository,
                       final EventTimeRepository eventTimeRepository,
                       final LocationService locationService,
                       final EventService eventService) {
        this.facebookReader = facebookReader;
        this.eventRepository = eventRepository;
        this.eventTimeRepository = eventTimeRepository;
        this.locationService = locationService;
        this.eventService = eventService;
    }

    /**
     * Parse event.
     *
     * @param facebookEvent the facebook event
     * @return the event
     */
    public Event parse(final FacebookEvent facebookEvent) {
        return Optional.ofNullable(facebookEvent)
                .filter(this::filterOutNonJerseyPlaces)
                .filter(Objects::nonNull)
                .map(fbEvent -> eventService.findFirstByFacebookId(Long.valueOf(fbEvent.getId()))
                        .map(event -> updateEvent(event, fbEvent))
                        .orElseGet(() -> processFbEvent(fbEvent)))
                .map(eventRepository::saveAndFlush)
                .orElse(null);
    }

    /**
     * Update event event.
     *
     * @param event   the event
     * @param fbEvent the fb event
     * @return the event
     */
    public Event updateEvent(final Event event, final FacebookEvent fbEvent) {
        event.setImageUrl(fbEvent.getCover().map(FacebookCover::getSource).orElse(""));
        event.setUpdatedTime(fbEvent.getUpdatedTime(), "yyyy-MM-dd'T'HH:mm:ss+SSSS");
        return event;
    }

    /**
     * Process fb event event.
     *
     * @param fbEvent the fb event
     * @return the event
     */
    public Event processFbEvent(final FacebookEvent fbEvent) {
        final Event event = Event.builder()
                .name(
                        Optional.ofNullable(fbEvent.getName())
                                .map(name -> name.replaceAll("([\\ud800-\\udbff\\udc00-\\udfff])", ""))
                                .orElse("unknown name")
                )
                .description(
                        Optional.ofNullable(fbEvent.getDescription())
                                .map(desc -> desc.replaceAll("([\\ud800-\\udbff\\udc00-\\udfff])", ""))
                                .orElse("")
                )
                .imageUrl(
                        fbEvent.getCover()
                                .map(FacebookCover::getSource)
                                .orElse("")
                )
                .location(
                        findLocationByFacebookPlace(fbEvent.getPlace().orElse(null))
                                .orElseGet(() -> locationService.findOne("7172dc45-d025-4f59-84f2-c2d8cd70ce3a").orElse(null))
                )
                .facebookId(
                        Long.valueOf(fbEvent.getId())
                )
                .processedDate(new DateTime())
                .build()
                .setUpdatedTime(fbEvent.getUpdatedTime(), "yyyy-MM-dd'T'HH:mm:ss+SSSS");

        eventRepository.saveAndFlush(event);

        final EventTime eventTime = EventTime.builder()
                .event(event)
                .active(true)
                .build()
                .setStartTime(fbEvent.getStartTime(), "yyyy-MM-dd'T'HH:mm:ss+SSSS");

        event.setTimes(new HashSet<>(Arrays.asList(new EventTime[]{eventTime})));

        eventTimeRepository.saveAndFlush(eventTime);

        return event;
    }

    /**
     * find a location that matches what we have stored in our database
     *
     * @param facebookPlace place from facebook
     * @return location, defaults to jersey
     */
    private Optional<Location> findLocationByFacebookPlace(final FacebookPlace facebookPlace) {
        final Mutable<Location> location = Mutable.of(null);

        Optional.ofNullable(facebookPlace)
                .filter(e -> facebookPlace.getId() != null)
                .ifPresent(place -> location.mutate(
                        locationService.findFirstByFacebookIdOrName(
                                Long.parseLong(facebookPlace.getId()),
                                facebookPlace.getName())
                                .filter(AuditedEntity::getActive)
                                .orElse(null)
                ));

        return location.ofNullable();
    }

    /**
     * Filter out non jersey places boolean.
     *
     * @param facebookEvent the facebook event
     * @return the boolean
     */
    public boolean filterOutNonJerseyPlaces(final FacebookEvent facebookEvent) {
        final String countryCouldBe = Optional.ofNullable(facebookEvent)
                .flatMap(FacebookEvent::getPlace)
                .flatMap(FacebookPlace::getLocation)
                .map(FacebookLocation::getCountry)
                .orElse("jersey");

        return countryCouldBe.toLowerCase().contains("jersey");
    }

    /**
     * Parse url event.
     *
     * @param url the url
     * @return the event
     */
    public Event parseUrl(final String url) {
        final Mutable<String> id = Mutable.of(null);

        PatternMatcher.of(facebookEventPattern, url)
                .then(matches -> id.mutate(matches.group(1)));

        return parseId(id.get());
    }

    /**
     * Parse id event.
     *
     * @param id the id
     * @return the event
     */
    public Event parseId(final String id) {
        final Mutable<Event> event = Mutable.of(new Event());

        Optional.ofNullable(facebookReader.requestEvent(id))
                .map(this::parse)
                .ifPresent(event::mutate);

        return event.get();
    }
}
