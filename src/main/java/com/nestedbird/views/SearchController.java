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

package com.nestedbird.views;

import com.nestedbird.jackson.SearchResult;
import com.nestedbird.models.artist.Artist;
import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.core.Base.BaseEntity;
import com.nestedbird.models.event.Event;
import com.nestedbird.models.location.Location;
import com.nestedbird.models.medium.Medium;
import com.nestedbird.modules.entitysearch.EntitySearch;
import com.nestedbird.modules.paginator.Paginator;
import com.nestedbird.modules.ratelimiter.RateLimit;
import com.nestedbird.util.Mutable;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Search controller.
 */
@Controller
@RequestMapping("api/v1/search/")
@Slf4j
public class SearchController {
    /**
     * This searches the lucene storage
     */
    private final EntitySearch entitySearch;

    /**
     * Instantiates a new Search controller.
     *
     * @param entitySearch the entity search
     */
    @Autowired
    public SearchController(final EntitySearch entitySearch) {
        this.entitySearch = entitySearch;
    }

    /**
     * Search page.
     *
     * @param query    the query
     * @param pageable the pageable
     * @return the page
     * @throws ParseException the parse exception
     */
    @RequestMapping("/")
    @RateLimit(limitPerMinute = 100)
    @ResponseBody
    public Page<SearchResult> search(@RequestParam("query") final String query,
                                     final Pageable pageable) throws ParseException {
        final List<Class<? extends BaseEntity>> classes = new ArrayList<>();
        classes.add(Artist.class);
        //        classes.add(Song.class);
        classes.add(Medium.class);
        classes.add(Location.class);

        return searchResults(classes, query, pageable);
    }

    private Page<SearchResult> searchResults(final List<Class<? extends BaseEntity>> classes,
                                             final String query,
                                             final Pageable pageable) {
        final List<SearchResult> results = classes.stream()
                .map(e -> search(e, query))
                .flatMap(List::stream)
                .sorted(Comparator.comparing(SearchResult::getScore, Comparator.reverseOrder()))
                .collect(Collectors.toList());

        return paginate(results, pageable);
    }

    private <T extends BaseEntity> List<SearchResult<T>> search(final Class<T> clazz, final String query) {
        final List<SearchResult<T>> results = new ArrayList<>();
        try {
            results.addAll(entitySearch.search(clazz, query).stream()
                    .map(e -> processResult(clazz, e))
                    .filter(e -> e.getScore() > 0)
                    .collect(Collectors.toList()));
        } catch (ParseException e) {
            logger.info("[SearchController] [search] Failure To Parse Query", e);
        }
        return results;
    }

    private Page<SearchResult> paginate(final List<SearchResult> results, final Pageable pageable) {
        return Paginator.<SearchResult>of(pageable).paginate(results);
    }

    @SuppressWarnings("unchecked")
    private <T extends BaseEntity> SearchResult<T> processResult(final Class<T> clazz, final Object[] result) {
        final T entity = (T) result[2];

        return SearchResult.<T>builder()
                .url(entity.getUrl())
                .category(clazz.getSimpleName())
                .name(entity.getDefiningName())
                .score(calculateScore(entity, (Float) result[0]))
                .id(entity.getId())
                .entity(entity)
                .build();
    }

    private <T extends BaseEntity> Float calculateScore(final T entity, final Float score) {
        final Mutable<Float> newScore = Mutable.of(score);

        if (Event.class.isAssignableFrom(entity.getClass())) {
            newScore.mutateIf(newScore.get() / 2, !((Event) entity).isInFuture());
        }

        if (AuditedEntity.class.isAssignableFrom(entity.getClass())) {
            newScore.mutateIf(0F, !((AuditedEntity) entity).getActive());
        }

        return newScore.get();
    }

    /**
     * Search events page.
     *
     * @param query    the query
     * @param pageable the pageable
     * @return the page
     * @throws ParseException the parse exception
     */
    @RequestMapping("/events/")
    @RateLimit(limitPerMinute = 100)
    @ResponseBody
    public Page<SearchResult> searchEvents(@RequestParam("query") final String query,
                                           final Pageable pageable) throws ParseException {
        final List<Class<? extends BaseEntity>> classes = new ArrayList<>();
        classes.add(Event.class);

        return searchResults(classes, query, pageable);
    }
}
