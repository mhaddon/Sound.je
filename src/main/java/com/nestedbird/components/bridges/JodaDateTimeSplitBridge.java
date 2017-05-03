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

import com.nestedbird.modules.entitysearch.SearchAnalysers;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.hibernate.search.bridge.TwoWayFieldBridge;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JodaDateTime bridge for lucene.
 * This tells lucene how to read JodaDateTime types.
 * JodaDateTime types want to have their information stored in ms, so there is a common unit of time for all possible
 * period fields.
 */
public class JodaDateTimeSplitBridge extends CoreBridgeDiscriminator implements TwoWayFieldBridge {
    private static final String DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    /**
     * Set year, month, day and ms in separate fields
     */
    @Override
    public void save(final String name,
                     final Object value,
                     final BridgeController.Presets presets) {
        if (value != null) {
            final DateTime dateTime = (DateTime) value;
            presets.recordDateTime(dateTime);
        } else {
            presets.recordDateTime(new DateTime(0));
        }
    }

    @Override
    Map<String, String> getFieldAnalysers() {
        final Map<String, String> map = new HashMap<>();

        map.put("ms", null);
        map.put("default", SearchAnalysers.ENGLISH_WORD_ANALYSER);

        return map;
    }

    @Override
    public Object get(final String name,
                      final Document document) {
        final IndexableField stringDateTime = document.getField(name);

        if (stringDateTime != null) {
            return DateTime.parse(stringDateTime.stringValue(), DateTimeFormat.forPattern(DATETIME_FORMAT));
        } else {
            return null;
        }
    }

    @Override
    public String objectToString(final Object value) {
        final DateTime dateTime = (DateTime) Optional.ofNullable(value).orElse(new DateTime(0));

        return dateTime.toString(DateTimeFormat.forPattern(DATETIME_FORMAT));
    }
}