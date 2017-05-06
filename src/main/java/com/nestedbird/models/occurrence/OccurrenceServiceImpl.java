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

package com.nestedbird.models.occurrence;


import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseServiceImpl;
import com.nestedbird.models.event.EventRepository;
import com.nestedbird.models.event.ParsedEventData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@Transactional
public class OccurrenceServiceImpl extends BaseServiceImpl<Occurrence> implements OccurrenceService {
    private final OccurrenceRepository occurrenceRepository;
    private final EventRepository eventRepository;

    @Autowired
    public OccurrenceServiceImpl(final OccurrenceRepository occurrenceRepository,
                                 final EventRepository eventRepository) {
        this.occurrenceRepository = occurrenceRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    protected BaseRepository<Occurrence> getRepository() {
        return this.occurrenceRepository;
    }

    @Override
    public Occurrence parseParsedEventData(final ParsedEventData parsedEventData) {
        return Optional.ofNullable(eventRepository.findOne(parsedEventData.getEventId()))
                .map(event -> {
                    final Occurrence occurrence = new Occurrence();
                    occurrence.setEvent(event);
                    occurrence.setDuration(parsedEventData.getDuration());
                    occurrence.setStartTime(parsedEventData.getStartTime());
                    return occurrence;
                })
                .orElse(null);
    }
}
