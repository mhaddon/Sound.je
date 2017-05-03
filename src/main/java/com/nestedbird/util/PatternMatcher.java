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

import lombok.Data;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a wrapper around the Pattern & Matcher Regex functionality that java provides
 */
@Data
public class PatternMatcher {
    /**
     * Create new PatternMatcher out of a Pattern and Query
     *
     * @param pattern The pattern to search with
     * @param query   The query to search
     * @return a new PatternMatcher
     */
    public static PatternMatcher of(final Pattern pattern, final String query) {
        return new PatternMatcher(pattern, query);
    }

    /**
     * The Pattern that we are going to search with
     */
    private final Pattern pattern;
    /**
     * The Matcher which is the processed pattern and query
     */
    private final Matcher matcher;
    /**
     * The string query is used with the pattern to create the matcher
     */
    private final String query;
    /**
     * Whether or not we should parse the results
     */
    private final Boolean shouldParse;

    private PatternMatcher(final Pattern pattern, final String query) {
        this(pattern, query, true);
    }

    private PatternMatcher(final Pattern pattern, final String query, final Boolean shouldParse) {
        if (pattern == null) throw new NullPointerException("Pattern cannot be null");

        this.pattern = pattern;
        this.query = query;
        this.shouldParse = shouldParse;
        this.matcher = query != null ? pattern.matcher(query) : null;
    }

    /**
     * If the pattern matches then we run the passed consumer
     *
     * @param consumer consumer to run if pattern matches
     * @return this pattern matcher
     */
    public PatternMatcher then(final Consumer<Matcher> consumer) {
        if (doesMatch() && shouldParse)
            consumer.accept(matcher);

        return this;
    }

    /**
     * Does the matcher match
     *
     * @return whether it matches
     */
    public Boolean doesMatch() {
        return matcher != null && matcher.reset().find();
    }

    /**
     * If the pattern does not match then we run this passed consumer
     *
     * @param consumer consumer to run if pattern does not match
     * @return this pattern matcher
     */
    public PatternMatcher otherwise(final Consumer<Pattern> consumer) {
        if (!doesMatch() && shouldParse)
            consumer.accept(pattern);

        return this;
    }

    /**
     * Create new PatternMatcher out of a Pattern and Query
     *
     * @param pattern The pattern to search with
     * @param query   The query to search
     * @return a new PatternMatcher
     */
    public PatternMatcher otherwiseOf(final Pattern pattern, final String query) {
        if (!doesMatch())
            return new PatternMatcher(pattern, query);
        return new PatternMatcher(pattern, query, false);
    }
}
