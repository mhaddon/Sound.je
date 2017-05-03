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

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.hibernate.search.bridge.TwoWayFieldBridge;
import org.joda.time.Period;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * JodaPeriod bridge for lucene.
 * This tells lucene how to read JodaPeriod types.
 * JodaPeriod types want to have their information stored in ms, so there is a common unit of time for all possible
 * period fields.
 */
public class JodaPeriodSplitBridge extends CoreBridgeDiscriminator implements TwoWayFieldBridge {

    @Override
    public Object get(final String name, final Document document) {
        final IndexableField stringPeriod = document.getField(name);
        if (stringPeriod != null) {
            return Period.seconds((Integer) stringPeriod.numericValue());
        } else {
            return null;
        }
    }

    @Override
    public String objectToString(final Object value) {
        final Period period = (Period) Optional.ofNullable(value).orElse(new Period(0));

        return String.valueOf(period.getSeconds());
    }

    @Override
    public void save(final String name,
                     final Object value,
                     final BridgeController.Presets presets) {
        if (value != null) {
            final Period period = (Period) value;
            presets.recordPeriod(period);
        } else {
            presets.recordPeriod(new Period(0));
        }
    }

    @Override
    Map<String, String> getFieldAnalysers() {
        final Map<String, String> map = new HashMap<>();

        map.put("ms", null);
        map.put("default", null);

        return map;
    }
}