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

package com.nestedbird.modules.ratelimiter;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Records all the times a Rate Limiter has been called.
 */
public class RateLimiterCallOccurrences {
    private List<DateTime> callOccurrences = new ArrayList<>();

    /**
     * Add occurrence at the current time
     */
    public void addOccurrence() {
        callOccurrences.add(new DateTime());
    }

    /**
     * Gets call occurrences.
     *
     * @return the call occurrences
     */
    public List<DateTime> getCallOccurrences() {
        return callOccurrences;
    }

    /**
     * Gets occurrences in past hour.
     *
     * @return the occurrences in past hour
     */
    public long getOccurrencesInPastHour() {
        return callOccurrences.stream()
                .filter(e -> e.isAfter((new DateTime()).minusHours(1)))
                .count();
    }

    /**
     * Gets occurrences in past minute.
     *
     * @return the occurrences in past minute
     */
    public long getOccurrencesInPastMinute() {
        return callOccurrences.stream()
                .filter(e -> e.isAfter((new DateTime()).minusMinutes(1)))
                .count();
    }

    /**
     * Filter out old call occurrences.
     *
     * @return the call occurrences
     */
    public RateLimiterCallOccurrences filterOld() {
        callOccurrences = callOccurrences.stream()
                .filter(e -> e.isAfter((new DateTime()).minusHours(1)))
                .collect(Collectors.toList());

        return this;
    }
}
