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

package com.nestedbird.models.core;

import com.nestedbird.models.artist.Artist;
import com.nestedbird.models.artist.ArtistRepository;
import com.nestedbird.models.event.Event;
import com.nestedbird.models.event.EventRepository;
import com.nestedbird.models.eventtime.EventTime;
import com.nestedbird.models.location.Location;
import com.nestedbird.models.location.LocationRepository;
import lombok.experimental.UtilityClass;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DatabaseJunkLoader implements ApplicationRunner {
    @UtilityClass
    static final private class LocationNameGenerator {
        static final String[] firstWords = new String[]{
                "Humble", "Ye", "Ye Olde", "Warm", "Raving", "Troubled", "The", "Old", "Worn", "Rusty", "Ancient"
        };
        static final String[] secondWords = new String[]{
                "Abode", "Hearth", "Door", "Gate", "Park", "House", "Place", "Hinge", "Pub", "Inn", "Cave", "Home"
        };

        static private String generate() {
            return firstWord() + " " + secondWord();
        }

        static private String firstWord() {
            return firstWords[new Random().nextInt(firstWords.length)];
        }

        static private String secondWord() {
            return secondWords[new Random().nextInt(secondWords.length)];
        }
    }

    @UtilityClass
    static final private class ArtistNameGenerator {
        static final String[] firstWords = new String[]{
                "Rancid", "Insane", "Black", "Iron", "Holy", "Rabid", "Bloody", "Satan's", "Bastard", "Forsaken",
                "Hell's", "Forbidden", "Dark", "Frantic", "Devil's", "Evil", "Inner", "Bleeding", "Guilty", "Witch's",
                "Heavy", "Illegal", "Fallen", "Sinister", "Crazy", "Troubled"
        };
        static final String[] secondWords = new String[]{
                "Empire", "Fury", "Rage", "Zombies", "Sin", "Warriors", "Angels", "Death", "Anarchy", "Henchmen",
                "Kill", "Vengeance", "Tendencies", "Magic", "Soldier", "Gods", "Goblin", "Spawn", "Temple", "Realm",
                "Hate", "Slaves", "Thorn", "Abyss", "Fire", "Secrets"
        };

        static private String generate() {
            return firstWord() + " " + secondWord();
        }

        static private String firstWord() {
            return firstWords[new Random().nextInt(firstWords.length)];
        }

        static private String secondWord() {
            return secondWords[new Random().nextInt(secondWords.length)];
        }
    }
    private final ArtistRepository artistRepository;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;


    @Autowired
    public DatabaseJunkLoader(final ArtistRepository artistRepository,
                              final EventRepository eventRepository,
                              final LocationRepository locationRepository) {
        this.artistRepository = artistRepository;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public void run(final ApplicationArguments applicationArguments) throws Exception {
        generateArtists();
        generateLocations();
        generateEvents();
    }

    private void generateArtists() {
        if (artistRepository.findAll().isEmpty()) {
            for (Integer i = 0; i < 50; i++) {
                artistRepository.saveAndFlush(
                        Artist.builder()
                                .active(true)
                                .description("test artist")
                                .name(ArtistNameGenerator.generate())
                                .build()
                );
            }
        }
    }

    private void generateLocations() {
        if (locationRepository.findAll().isEmpty()) {
            for (Integer i = 0; i < 10; i++) {
                locationRepository.saveAndFlush(
                        Location.builder()
                                .active(true)
                                .description("test location")
                                .name(LocationNameGenerator.generate())
                                .coordinates("49.2144,2.1312")
                                .build()
                );
            }
        }
    }

    private void generateEvents() {
        if (eventRepository.findAll().isEmpty()) {
            for (Integer i = 0; i < 350; i++) {
                eventRepository.saveAndFlush(
                        Event.builder()
                                .active(true)
                                .artists(getRandomArtistSet())
                                .location(getRandomLocation())
                                .description("test event")
                                .times(getRandomEventTimeSet())
                                .build()
                );
            }
        }
    }

    private Set<Artist> getRandomArtistSet() {
        final HashSet<Artist> set = new HashSet<>();

        for (Integer i = 0; i < Math.random() * 4; i++) {
            set.add(getRandomArtist());
        }

        return set;
    }

    private Location getRandomLocation() {
        final Random random = new Random();
        final List<Location> locations = locationRepository.findAll();
        return locations.get(random.nextInt(locations.size()));
    }

    private Set<EventTime> getRandomEventTimeSet() {
        final HashSet<EventTime> set = new HashSet<>();

        set.add(
                EventTime.builder()
                        .active(true)
                        .startTime(DateTime.now().plusDays((int) (100 * Math.random())))
                        .build()
        );

        return set;
    }

    private Artist getRandomArtist() {
        final Random random = new Random();
        final List<Artist> artists = artistRepository.findAll();
        return artists.get(random.nextInt(artists.size()));
    }
}
