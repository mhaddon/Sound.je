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

import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The Query Block is a class that basically replaces the need or desire of having nested if statements when
 * checking authentication/authorisation and other things that may have a different return response depending
 * on which part fails.
 */
public class QueryBlock {
    /**
     * Create query block.
     *
     * @return the query block
     */
    public static QueryBlock create() {
        return new QueryBlock();
    }

    /**
     * Create query block.
     *
     * @param data the data
     * @return the query block
     */
    public static QueryBlock create(final Map<String, Object> data) {
        return new QueryBlock(data);
    }

    /**
     * The persistent data between each queryblock query
     */
    private final Map<String, Object> data;
    /**
     * If any queries fail then enabled will be set to false, this stops additional queries from being checked
     */
    private Boolean enabled = true;
    /**
     * The ultimate response object that is sent to the client after we end
     */
    private ResponseEntity responseEntity;

    private QueryBlock() {
        this.data = new HashMap<>();
        this.responseEntity = ResponseEntity.ok().body(null);
    }

    private QueryBlock(final Map<String, Object> data) {
        this.data = data;
        this.responseEntity = ResponseEntity.ok().body(null);
    }

    /**
     * Require query block.
     *
     * @param condition the condition
     * @param done      the done
     * @return the query block
     */
    public QueryBlock require(final boolean condition, final Function<QueryBlockQuery, ResponseEntity> done) {
        return query(condition, done);
    }

    /**
     * Query query block.
     *
     * @param condition the condition
     * @param done      the done
     * @return the query block
     */
    public QueryBlock query(final boolean condition, final Function<QueryBlockQuery, ResponseEntity> done) {
        if (enabled) {
            responseEntity = done.apply(QueryBlockQuery.create(data, condition));

            if (!condition) {
                enabled = false;
            }
        }

        return this;
    }

    /**
     * Require query block.
     *
     * @param conditional the conditional
     * @param done        the done
     * @return the query block
     */
    public QueryBlock require(final Function<Map<String, Object>, Boolean> conditional,
                              final Function<QueryBlockQuery, ResponseEntity> done) {
        return query(conditional, done);
    }

    /**
     * Query query block.
     *
     * @param conditional the conditional
     * @param done        the done
     * @return the query block
     */
    public QueryBlock query(final Function<Map<String, Object>, Boolean> conditional,
                            final Function<QueryBlockQuery, ResponseEntity> done) {
        if (enabled) {
            Boolean condition = conditional.apply(data);

            responseEntity = done.apply(QueryBlockQuery.create(data, condition));

            if (!condition) {
                enabled = false;
            }
        }

        return this;
    }

    /**
     * Done response entity.
     *
     * @param done        the done
     * @param successBody the success body
     * @return the response entity
     */
    public ResponseEntity done(final Consumer<Map<String, Object>> done, final String successBody) {
        if (enabled) {
            this.done(done);
            responseEntity = ResponseEntity.ok().body(successBody);
        }

        return responseEntity;
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

        return responseEntity;
    }

    /**
     * Done response entity.
     *
     * @param done the done
     * @return the response entity
     */
    public ResponseEntity doneAndOut(final Function<Map<String, Object>, String> done) {
        if (enabled) {
            responseEntity = ResponseEntity.ok().body(done.apply(data));
        }

        return responseEntity;
    }

    /**
     * Done response entity.
     *
     * @param done        the done
     * @param successBody the success body
     * @return the response entity
     */
    public ResponseEntity done(final Consumer<Map<String, Object>> done,
                               final Function<Map<String, Object>, String> successBody) {
        if (enabled) {
            this.done(done);
            responseEntity = ResponseEntity.ok().body(successBody.apply(data));
        }

        return responseEntity;
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
     * Gets response entity.
     *
     * @return the response entity
     */
    public ResponseEntity getResponseEntity() {
        return responseEntity;
    }
}
