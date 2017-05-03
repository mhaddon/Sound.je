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

package com.nestedbird.util;

import com.nestedbird.jackson.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The container for a specific query for QueryBlock.
 * The result of whether or not a QueryBlock succeeds is passed into QueryBlockQuery, which does the processing of
 * the result.
 * QueryBlockQuery has a fail and done event, for failures and successes respectively.
 */
public class QueryBlockQuery {
    /**
     * Create query block query.
     *
     * @return the query block query
     */
    public static QueryBlockQuery create() {
        return new QueryBlockQuery();
    }

    /**
     * Create query block query.
     *
     * @param data    the persistent data
     * @param enabled did we succeed
     * @return the query block query
     */
    public static QueryBlockQuery create(final Map<String, Object> data, final Boolean enabled) {
        return new QueryBlockQuery(data, enabled);
    }

    /**
     * Persistent data for this query object
     */
    private final Map<String, Object> data;
    /**
     * Whether or not this elements check failed
     */
    private Boolean enabled = true;
    /**
     * HTTP response from this query result
     */
    private ResponseEntity response;

    private QueryBlockQuery() {
        this.data = new HashMap<>();
        response = ResponseEntity.ok().body(null);
    }

    private QueryBlockQuery(final Map<String, Object> data, final Boolean enabled) {
        this.data = data;
        this.enabled = enabled;
        response = ResponseEntity.ok().body(null);
    }

    /**
     * Fail query block query.
     *
     * @param httpStatus the http status
     * @param error      the error
     * @return the query block query
     */
    public QueryBlockQuery fail(final HttpStatus httpStatus, final String error) {
        if (!enabled) {
            final ApiError apiError = ApiError.builder()
                    .status(httpStatus.value())
                    .error(error)
                    .message(error).build();

            response = ResponseEntity.status(httpStatus).body(apiError);
        }

        return this;
    }

    /**
     * On fail query block query.
     *
     * @param onFail the on fail
     * @return the query block query
     */
    public QueryBlockQuery onFail(final Consumer<Map<String, Object>> onFail) {
        if (!enabled) {
            onFail.accept(data);
        }

        return this;
    }

    /**
     * Done response entity.
     *
     * @param done the done
     * @return the response entity
     */
    public ResponseEntity done(final Consumer<Map<String, Object>> done) {
        if (enabled) {
            done.accept(data);
        }

        return response;
    }

    /**
     * Done response entity.
     *
     * @return the response entity
     */
    public ResponseEntity done() {
        return response;
    }

    /**
     * Did this check succeed
     *
     * @return the boolean
     */
    public Boolean isEnabled() {
        return enabled;
    }

    /**
     * Gets data.
     *
     * @return the data
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Gets response.
     *
     * @return the response
     */
    public ResponseEntity getResponse() {
        return response;
    }
}
