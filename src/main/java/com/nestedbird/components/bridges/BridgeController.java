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

import lombok.Builder;
import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.LuceneOptions;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;

import java.util.Locale;

/**
 * The type Bridge controller.
 */
@Builder
public class BridgeController {
    private final LuceneOptions luceneOptions;
    private final Document document;
    private final String baseName;

    /**
     * The type Presets.
     */
    public class Presets {
        private static final String DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

        /**
         * Record date time.
         *
         * @param value the value
         */
        public void recordDateTime(final DateTime value) {
            addNumberToDocument("ms", value.getMillis());
            addStringToDocument(value.monthOfYear().getAsText(Locale.UK));
            addStringToDocument(appendSuffix(value.dayOfMonth().get()));
            addStringToDocument(value.dayOfWeek().getAsText(Locale.UK));
            addStringToDocument(value.toString(DateTimeFormat.forPattern(DATETIME_FORMAT)));
        }

        /**
         * Add number to document.
         *
         * @param subName the sub name
         * @param value   the value
         */
        public void addNumberToDocument(final String subName, final Number value) {
            luceneOptions.addNumericFieldToDocument(getName(subName), value, document);
        }

        /**
         * Add string to document.
         *
         * @param value the value
         */
        public void addStringToDocument(final String value) {
            luceneOptions.addFieldToDocument(getName(), value, document);
        }

        private String appendSuffix(int number) {
            final int radix = number % 10;
            String suffix = "th";
            if (radix == 1) suffix = "st";
            else if (radix == 2) suffix = "nd";
            else if (radix == 3) suffix = "rd";

            return String.valueOf(number) + suffix;
        }

        private String getName(final String subName) {
            return (baseName + "." + subName).replaceAll("^\\.+", "");
        }

        private String getName() {
            return baseName.replaceAll("^\\.+", "");
        }

        /**
         * Record period.
         *
         * @param value the value
         */
        public void recordPeriod(final Period value) {
            addNumberToDocument("ms", value.getMillis());
            addNumberToDocument(value.getSeconds());
        }

        /**
         * Add number to document.
         *
         * @param value the value
         */
        public void addNumberToDocument(final Number value) {
            luceneOptions.addNumericFieldToDocument(getName(), value, document);
        }

        /**
         * Add string to document.
         *
         * @param subName the sub name
         * @param value   the value
         */
        public void addStringToDocument(final String subName, final String value) {
            luceneOptions.addFieldToDocument(getName(subName), value, document);
        }
    }
}
