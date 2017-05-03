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

import com.google.common.base.Strings;
import com.google.common.util.concurrent.RateLimiter;
import com.nestedbird.util.JoinPointToStringHelper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiter Aspect that limits the requests of rest endpoints.
 * Annotate an endpoint with @RateLimit in order to activate this aspect
 * <p>
 * This rate limiter has hard and soft limits. Soft delays the server response but it will always respond.
 * Hard immediately closes the connection with an error.
 * <p>
 * Based on: https://www.javacodegeeks.com/2015/07/throttle-methods-with-spring-aop-and-guava-rate-limiter.html
 * and Baeldung
 */
@Aspect
@Component
public class RateLimiterAspect {
    /**
     * List of current active RateLimiters for soft Rate Limiting
     */
    private final Map<String, RateLimiter> limiterMap = new ConcurrentHashMap<>();

    /**
     * List all the occurrences each method has had
     */
    private final Map<String, RateLimiterCallOccurrences> callOccurrences = new HashMap<>();


    /**
     * Binds to RateLimit annotation
     *
     * @param joinPoint the join point
     * @param limit     the limit annotation data
     * @throws RequestLimitExceeded hard error if limit is exceeded
     */
    @Before("@annotation(limit)")
    public void rateLimt(final JoinPoint joinPoint, final RateLimit limit) throws RequestLimitExceeded {
        final String key = getOrCreate(joinPoint, limit);

        handleHardRateLimiting(key, limit);
        handleSoftRateLimiting(key, joinPoint, limit);
    }

    /**
     * retrieves the key from the limit annotation data, or uses the name of the method
     *
     * @param joinPoint the join point
     * @param limit     the limit annotation data
     * @return the methods identifier
     */
    private String getOrCreate(final JoinPoint joinPoint, final RateLimit limit) {
        return Optional.ofNullable(Strings.emptyToNull(limit.key()))
                .orElseGet(() -> JoinPointToStringHelper.toString(joinPoint));
    }

    /**
     * Handles hard rate limiting, this closes the connection with an error
     *
     * @param key   the methods key
     * @param limit the limit annotation data
     * @throws RequestLimitExceeded request limit exceeded
     * @todo the error is not caught nicely and shown to the user correctly
     */
    private void handleHardRateLimiting(final String key, final RateLimit limit) throws RequestLimitExceeded {
        if (!callOccurrences.containsKey(key)) {
            callOccurrences.put(key, new RateLimiterCallOccurrences());
        }

        // record occurrence
        final RateLimiterCallOccurrences rateLimiterCallOccurrences = callOccurrences.get(key);
        rateLimiterCallOccurrences.addOccurrence();
        callOccurrences.put(key, rateLimiterCallOccurrences.filterOld());

        // check occurrences in past hour
        if (limit.limitPerHour() > 0 && rateLimiterCallOccurrences.getOccurrencesInPastHour() > limit.limitPerHour()) {
            throw new RequestLimitExceeded("You have exceeded the request limit on this endpoint");
        }

        // check occurrences in past minute
        if (limit.limitPerMinute() > 0 && rateLimiterCallOccurrences.getOccurrencesInPastMinute() > limit.limitPerMinute()) {
            throw new RequestLimitExceeded("You have exceeded the request limit on this endpoint");
        }
    }

    /**
     * Handle soft rate limiting, this pauses the current thread
     *
     * @param key       the methods key
     * @param joinPoint the join point
     * @param limit     the limit annotation data
     */
    private void handleSoftRateLimiting(final String key, final JoinPoint joinPoint, final RateLimit limit) {
        if (limit.value() > 0) {
            final RateLimiter limiter = limiterMap.computeIfAbsent(key, name -> RateLimiter.create(limit.value()));
            limiter.acquire();
        }
    }
}
