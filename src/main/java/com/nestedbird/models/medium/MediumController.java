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

package com.nestedbird.models.medium;

import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.core.Base.BaseController;
import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseService;
import com.nestedbird.modules.formparser.ParameterMapParser;
import com.nestedbird.modules.resourceparser.MediumParser;
import com.nestedbird.util.Mutable;
import com.nestedbird.util.QueryBlock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * The type Medium controller.
 */
@RestController
@EnableScheduling
@RequestMapping("api/v1/Media/")
public class MediumController extends BaseController<Medium> {

    private final RedissonClient redissonClient;
    private final MediumRepository mediumRepository;
    private final MediumService mediumService;
    private final MediumParser mediumParser;
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Instantiates a new Medium controller.
     *
     * @param redissonClient   the redisson client
     * @param mediumRepository the medium repository
     * @param mediumService    the medium service
     * @param mediumParser     the medium parser
     */
    @Autowired
    MediumController(final RedissonClient redissonClient,
                     final MediumRepository mediumRepository,
                     final MediumService mediumService,
                     final MediumParser mediumParser) {
        this.redissonClient = redissonClient;
        this.mediumRepository = mediumRepository;
        this.mediumService = mediumService;
        this.mediumParser = mediumParser;
    }

    @Override
    public BaseRepository<Medium> getRepository() {
        return mediumRepository;
    }

    @Override
    public Class<Medium> getEntityClass() {
        return Medium.class;
    }

    @Override
    public BaseService<Medium> getService() {
        return this.mediumService;
    }

    /**
     * Retrieves hot medium elements from the cache and retrieves the object information from the database
     *
     * @param pageable pagination settings
     * @return list of medium elements
     */
    @RequestMapping(value = "Hot", method = RequestMethod.GET)
    Page<Medium> listHot(final Pageable pageable) {
        final int page = pageable.getPageNumber();
        final int count = pageable.getPageSize();

        final RScoredSortedSet<String> set = redissonClient.getScoredSortedSet("HotMediaByScore");
        if (set.size() == 0) {
            retrieveHotToCache();
        }

        final Collection<ScoredEntry<String>> mediaCollection = set.entryRange(page * count, ((page + 1) * count) - 1);

        final List<Medium> mediaList = mediaCollection.stream()
                .map(entry -> mediumService.findOne(entry.getValue()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(AuditedEntity::getActive)
                .collect(Collectors.toList());

        return new PageImpl<>(mediaList, pageable, set.size());
    }

    /**
     * Orders the media by "hotness" and saves the ordered result to the redis cache
     */
    @Scheduled(cron = "0 0 */2 * * *")
    @Transactional
    public void retrieveHotToCache() {
        final RScoredSortedSet<String> set = redissonClient.getScoredSortedSet("HotMediaByScore");

        final Query query = entityManager.createNativeQuery(
                "CALL getMediaByHot()",
                Medium.class
        );

        @SuppressWarnings("unchecked") final List<Medium> results = (List<Medium>) query.getResultList();

        for (final Medium medium : results) {
            mediumRepository.setMediumScoreById(medium.getScore(), medium.getScoreFinal(), medium.getId());
        }

        set.clear();
        for (final Medium medium : results) {
            set.add(medium.getScoreFinal(), medium.getId());
        }
    }

    /**
     * Parse url medium.
     *
     * @param request the request
     * @return the medium
     */
    @RequestMapping(value = "parseurl", method = RequestMethod.POST,
            headers = "content-type=application/x-www-form-urlencoded",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Medium parseUrl(final HttpServletRequest request) {
        final ParameterMapParser parser = ParameterMapParser.parse(request.getParameterMap());
        final Mutable<Medium> medium = Mutable.of(null);

        QueryBlock.create()

                // Ensure the request has sent the required data
                .require(parser.has("url"), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Request does not have required data")
                        .done(data -> data.put("url", parser.get("url").toString())))

                // Return the processes medium
                .done(data -> medium.mutate(mediumParser.parseUrl(data.get("url").toString())));

        medium.ofNullable().ifPresent(newMedium ->
                medium.mutate(mediumService
                        .findFirstBySourceIdAndType(newMedium.getSourceId(), newMedium.getType())
                        .orElseGet(() -> mediumService.saveAndFlush(newMedium).orElse(newMedium)))
        );

        return medium.get();
    }

}