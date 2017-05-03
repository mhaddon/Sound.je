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

import java.lang.annotation.*;

/**
 * The interface Rate limit.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * Value int.
     *
     * @return soft rate limit in queries per second
     */
    int value() default 0;


    /**
     * Key string.
     *
     * @return rate limit identifier (optional)
     */
    String key() default "";

    /**
     * Limit per hour int.
     *
     * @return hard rate limit in queries per hour
     */
    int limitPerHour() default 0;

    /**
     * Limit per minute int.
     *
     * @return hard rate limit in queries per minute
     */
    int limitPerMinute() default 0;
}
