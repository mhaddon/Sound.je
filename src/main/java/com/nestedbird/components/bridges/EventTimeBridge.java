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

package com.nestedbird.components.bridges;

import com.nestedbird.models.event.ParsedEventData;
import com.nestedbird.models.eventtime.EventTime;
import com.nestedbird.modules.entitysearch.SearchAnalysers;

import java.util.*;

/**
 * EventTime bridge for lucene.
 * This tells lucene how to read Set<EventTime> types.
 * EventTime fields want to record all of their possible occurrences into lucene so future occurrences can be searched
 */
public class EventTimeBridge extends CoreBridgeDiscriminator {

    @Override
    @SuppressWarnings("unchecked")
    public void save(final String name,
                     final Object object,
                     final BridgeController.Presets presets) {
        Optional.ofNullable(object).ifPresent(value -> {
            if (Collection.class.isAssignableFrom(value.getClass())) {
                final List<EventTime> times = new ArrayList<>((Collection) value);
                times.stream()
                        .map(EventTime::getOccurrences)
                        .flatMap(List::stream)
                        .map(ParsedEventData::getStartTime)
                        .forEach(presets::recordDateTime);
            } else {
                final EventTime time = (EventTime) value;
                time.getOccurrences().stream()
                        .map(ParsedEventData::getStartTime)
                        .forEach(presets::recordDateTime);
            }
        });
    }

    @Override
    Map<String, String> getFieldAnalysers() {
        final Map<String, String> map = new HashMap<>();

        map.put("ms", null);
        map.put("default", SearchAnalysers.ENGLISH_WORD_ANALYSER);

        return map;
    }
}