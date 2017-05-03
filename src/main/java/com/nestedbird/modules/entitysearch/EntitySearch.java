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

package com.nestedbird.modules.entitysearch;

import com.nestedbird.models.core.Base.BaseEntity;
import com.nestedbird.modules.paginator.Paginator;
import com.nestedbird.util.Mutable;
import com.nestedbird.util.PatternMatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.engine.ProjectionConstants;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This autowirable class is responsible for searching the lucene store
 */
@Configuration
@Slf4j
public class EntitySearch {
    private static final Pattern startsWithNotPattern = Pattern.compile("^NOT");
    private static final Pattern queryNumericPattern = Pattern.compile("^\\[([\\w\\.]+)\\:(.*)\\]$");
    private static final Pattern queryRangePattern = Pattern.compile("^\\[([\\d]+|\\*) ?TO ?([\\d]+|\\*)\\]$");
    private static final Pattern lessThanPattern = Pattern.compile("^\\<([\\d]+)$");
    private static final Pattern greaterThanPattern = Pattern.compile("^\\>([\\d]+)$");
    private static final Pattern numericEqualsPattern = Pattern.compile("^([\\d]+)|\\*$");

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Search only return data list.
     *
     * @param <T>       the type parameter
     * @param clazz     the clazz
     * @param queryText the query text
     * @return the list
     * @throws ParseException the parse exception
     */
    @SuppressWarnings("unchecked")
    public final <T extends BaseEntity> List<T> searchOnlyReturnData(final Class<T> clazz, final String queryText) throws ParseException {
        return search(clazz, queryText).stream()
                .map(e -> (T) e[2])
                .collect(Collectors.toList());
    }

    /**
     * Searches the lucene store for a specific query
     *
     * @param <T>       What type of information are we searching
     * @param clazz     The class of the information we are searching
     * @param queryText The query text
     * @return list of entities
     * @throws ParseException the parse exception
     */
    public final <T extends BaseEntity> List<Object[]> search(final Class<T> clazz, final String queryText) throws ParseException {
        final FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        final SearchFactory searchFactory = fullTextEntityManager.getSearchFactory();

        final QueryParser parser = new MultiFieldQueryParser(getClassLuceneFields(clazz), searchFactory.getAnalyzer(clazz));

        final List<Query> parsedQueries = Arrays.stream(queryText.split("AND"))
                .map(e -> parseQuery(e, parser))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        final BooleanQuery.Builder bq = new BooleanQuery.Builder();
        parsedQueries.forEach(e -> bq.add(e, BooleanClause.Occur.MUST));

        final FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(bq.build(), clazz);
        jpaQuery.setProjection(ProjectionConstants.SCORE, ProjectionConstants.EXPLANATION, ProjectionConstants.THIS);

        return (List<Object[]>) jpaQuery.getResultList();
    }

    /**
     * The standard query parser does not seem to parse queries how i expect.
     * The problem occurs with integers and ranges,
     * It also doesnt allow the user to normally say NOT ... at the start of the search.
     * This method provides a simple abstraction over the normal query language and adds a tiny bit better support
     * <p>
     * Integer queries are wrapped in []
     * <p>
     * It allows a few things:
     * 1. Prefixing queries with NOT, is treated like *:* AND NOT
     * 2. Support for integers greater than [name:>integer] ie: [times:>100]
     * 3. Support for integers lesser than [name:<integer] ie: [times:<100]
     * 4. Support for integer ranges [name:[small_integer TO large_integer]] ie: [times:[1 TO 10]]
     * 5. Support for integer range queries and normal queries in same query. ie: "a:1 AND [times:>1]"
     *
     * @param queryText our query string
     * @param parser    query parser
     * @return query element
     */
    private Query parseQuery(final String queryText, final QueryParser parser) {
        final Mutable<Query> query = Mutable.of(null);
        final Mutable<String> queryMessage = Mutable.of(queryText.trim());

        // Ensure that NOT prefixes are instead *:* AND NOT
        PatternMatcher.of(startsWithNotPattern, queryMessage.get())
                .then(startsWithNotMatcher ->
                        queryMessage.mutate(queryMessage.get().replaceFirst("NOT", "*:* AND NOT")));

        PatternMatcher.of(queryNumericPattern, queryMessage.get())
                // Process integer range
                .then(queryNumericMatcher -> {
                    final String fieldName = queryNumericMatcher.group(1).trim();
                    final String fieldQuery = queryNumericMatcher.group(2).trim();
                    final Long[] ranges = getRangeValues(fieldQuery);

                    // query.mutate(LongPoint.newRangeQuery(fieldName, ranges[0], ranges[1]));
                    query.mutate(NumericRangeQuery.newLongRange(fieldName, ranges[0], ranges[1], true, true));
                })
                // This is a normal string query
                .otherwise(queryNumericMatcher -> query.mutate(wrappedQueryParserParse(queryMessage.get(), parser)));

        return query.get();
    }

    /**
     * Wraps the query parser in a try-catch
     *
     * @param queryText the query text
     * @param parser    the query parser
     * @return new query
     */
    private Query wrappedQueryParserParse(final String queryText, final QueryParser parser) {
        if (queryText.length() > 0) {
            try {
                return parser.parse(queryText);
            } catch (ParseException e) {
                logger.info("[EntitySearch] [wrappedQueryParserParse] Failure To Parse Query", e);
            }
        }
        return null;
    }

    /**
     * Retrieves range start and end values from the query
     *
     * @param fieldQuery query string
     * @return array of range values
     */
    private Long[] getRangeValues(final String fieldQuery) {
        final Matcher queryRangeMatcher = queryRangePattern.matcher(fieldQuery);
        final Matcher lessThanMatcher = lessThanPattern.matcher(fieldQuery);
        final Matcher greaterThanMatcher = greaterThanPattern.matcher(fieldQuery);
        final Matcher numericEqualsMatcher = numericEqualsPattern.matcher(fieldQuery);

        Long startRange = 0L;
        Long endRange = Long.MAX_VALUE;

        if (queryRangeMatcher.find()) {
            if (!queryRangeMatcher.group(1).equals("*")) {
                startRange = Long.parseLong(queryRangeMatcher.group(1));
            }
            if (!queryRangeMatcher.group(2).equals("*")) {
                endRange = Long.parseLong(queryRangeMatcher.group(2));
            }
        } else if (lessThanMatcher.find()) {
            if (!lessThanMatcher.group(1).equals("*")) {
                endRange = Long.parseLong(lessThanMatcher.group(1));
            }
        } else if (greaterThanMatcher.find()) {
            if (!greaterThanMatcher.group(1).equals("*")) {
                startRange = Long.parseLong(greaterThanMatcher.group(1));
            }
        } else if (numericEqualsMatcher.find()) {
            if (!numericEqualsMatcher.group(1).equals("*")) {
                endRange = Long.parseLong(numericEqualsMatcher.group(1));
                startRange = Long.parseLong(numericEqualsMatcher.group(1));
            }
        }

        return new Long[]{startRange, endRange};
    }

    /**
     * Get all lucene fields in a class, this searches up the tree for parent nodes that this class extends
     *
     * @param clazz the class we are searching
     * @return string array of field names
     */
    private String[] getClassLuceneFields(final Class clazz) {
        final List<String> fields = new ArrayList<>();

        Class currentClass = clazz;
        do {
            for (final Field field : currentClass.getDeclaredFields()) {
                if (field.getAnnotation(org.hibernate.search.annotations.Field.class) != null) {
                    fields.add(field.getName());
                }
                if (field.getAnnotation(org.hibernate.search.annotations.IndexedEmbedded.class) != null) {
                    final List<String> luceneFields = Arrays.stream(getClassLuceneFields(field.getType()))
                            .map(e -> field.getName() + "." + e)
                            .collect(Collectors.toList());
                    fields.addAll(luceneFields);
                }
            }
            currentClass = currentClass.getSuperclass();
        } while (currentClass.getSuperclass() != null);

        return fields.toArray(new String[fields.size()]);
    }

    /**
     * This sorts a list, and it tries to match the JPA Sort query syntax
     *
     * @param <T>  the type of data we are sorting
     * @param list the list of data we are sorting
     * @param sort the sort settings
     * @return the sorted list
     */
    public final <T extends BaseEntity> List<T> sort(final List<T> list, final Sort sort) {
        final List<T> sortedList = cloneList(list);

        if (sort != null) {
            sort.forEach(order -> {
                sortedList.sort((Object a, Object b) -> {
                    final Object fieldA = deepValue(a, order.getProperty());
                    final Object fieldB = deepValue(b, order.getProperty());

                    if (order.getDirection().equals(Sort.Direction.ASC)) {
                        return fieldA.toString().compareTo(fieldB.toString());
                    } else {
                        return fieldB.toString().compareTo(fieldA.toString());
                    }
                });
            });
        }

        return sortedList;
    }

    /**
     * Clone a list
     *
     * @param list list to clone
     * @return new list
     */
    private <T extends BaseEntity> List<T> cloneList(final List<T> list) {
        return !list.isEmpty() ? ((List) ((ArrayList) list).clone()) : new ArrayList<>();
    }

    /**
     * This follows a path (like object.childobject.value), to its end value, so it can compare the values of two
     * paths.
     * This method follows the path recursively until it reaches the end value.
     *
     * @param object object to search
     * @param path   path of value
     * @return result
     */
    private Object deepValue(final Object object, final String path) {
        final List<String> paths = new LinkedList<>(Arrays.asList(path.split("\\.")));
        final String currentPath = paths.get(0);

        final PropertyAccessor accessor = PropertyAccessorFactory.forDirectFieldAccess(object);
        Object field = accessor.getPropertyValue(currentPath);

        paths.remove(0);

        if ((field != null) && (!paths.isEmpty())) {
            field = deepValue(field, String.join(".", paths));
        }

        return field;
    }


    /**
     * This paginates the contents in the list
     *
     * @param <T>      the type parameter
     * @param list     the list of data we are paginating
     * @param pageable the pagination settings
     * @return the page
     */
    public final <T extends BaseEntity> Page<T> paginate(final List<T> list, final Pageable pageable) {
        return Paginator.<T>of(pageable).paginate(list);
    }

    /**
     * This paginates the list and it sorts it
     *
     * @param <T>      the type parameter
     * @param list     the list of data we are paginating
     * @param pageable the pagination settings
     * @param sort     the sorting settings
     * @return the page
     */
    public final <T extends BaseEntity> Page<T> paginate(final List<T> list,
                                                         final Pageable pageable,
                                                         final Sort sort) {
        final List<T> sortedList = sort(cloneList(list), sort);
        return paginate(sortedList, pageable);
    }
}
