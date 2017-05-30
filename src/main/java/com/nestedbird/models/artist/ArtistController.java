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

package com.nestedbird.models.artist;

import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.core.Base.BaseController;
import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseService;
import com.nestedbird.models.event.EventService;
import com.nestedbird.models.medium.Medium;
import com.nestedbird.models.occurrence.Occurrence;
import com.nestedbird.models.song.Song;
import com.nestedbird.modules.formparser.ParameterMapParser;
import com.nestedbird.modules.paginator.Paginator;
import com.nestedbird.modules.resourceparser.PageParser;
import com.nestedbird.util.Mutable;
import com.nestedbird.util.QueryBlock;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Artist controller.
 */
@RestController
@RequestMapping("/api/v1/Artists")
@Api(tags = "Artists")
public class ArtistController extends BaseController<Artist> {


    private final ArtistRepository artistRepository;
    private final ArtistService artistService;
    private final PageParser pageParser;
    private final EventService eventService;


    /**
     * Instantiates a new Artist controller.
     *
     * @param artistService    the artist service
     * @param artistRepository the artist repository
     * @param eventRepository
     * @param redissonClient
     */
    @Autowired
    ArtistController(final ArtistService artistService,
                     final ArtistRepository artistRepository,
                     final PageParser pageParser,
                     final EventService eventService) {
        this.artistService = artistService;
        this.artistRepository = artistRepository;
        this.pageParser = pageParser;
        this.eventService = eventService;
    }

    @Override
    public BaseRepository<Artist> getRepository() {
        return artistRepository;
    }

    @Override
    public Class<Artist> getEntityClass() {
        return Artist.class;
    }

    @Override
    public BaseService<Artist> getService() {
        return this.artistService;
    }

    /**
     * Lists all of an artists upcoming events
     *
     * @param pageable request pagination settings
     * @param id       artist Id
     * @return Page of Occurrences
     */
    @ApiOperation("Lists all of an artists upcoming events")
    @RequestMapping(value = "/{id}/Events/Upcoming", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public Page<Occurrence> listUpcomingEvents(final Pageable pageable,
                                               @ApiParam("UUID Id of Artist") @PathVariable final String id) {
        final List<Occurrence> occurrences = artistService.findOne(id)
                .filter(AuditedEntity::getActive)
                .map(eventService::retrieveUpcomingByArtist)
                .map(ArrayList::new)
                .orElse(new ArrayList());

        return Paginator.<Occurrence>of(pageable)
                .paginate(occurrences);
    }

    @ApiOperation(
            value = "Lists all of an artists events"
    )
    @RequestMapping(value = "/{id}/Events", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public Page<Occurrence> listEvents(final Pageable pageable,
                                       @ApiParam(value = "UUID Id of Artist", required = true) @PathVariable final String id) {
        final List<Occurrence> occurrences = artistService.findOne(id)
                .filter(AuditedEntity::getActive)
                .map(eventService::retrieveByArtist)
                .map(ArrayList::new)
                .orElse(new ArrayList());

        return Paginator.<Occurrence>of(pageable)
                .paginate(occurrences);
    }

    /**
     * Lists all of an artists medium elements
     *
     * @param pageable the pageable
     * @param id       the id
     * @return the page
     */
    @ApiOperation(
            value = "Lists all of an artists medium elements"
    )
    @RequestMapping(value = "/{id}/Media", method = RequestMethod.GET)
    public Page<Medium> listMedia(final Pageable pageable,
                                  @ApiParam(value = "UUID Id of Artist", required = true) @PathVariable final String id) {
        return artistService.listAllMediumByPage(pageable, id);
    }

    /**
     * Lists all of an artists songs elements
     *
     * @param pageable the pageable
     * @param id       the id
     * @return the page
     */
    @ApiOperation(
            value = "Lists all of an artists songs elements"
    )
    @RequestMapping(value = "/{id}/Songs", method = RequestMethod.GET)
    public Page<Song> listSongs(final Pageable pageable,
                                @ApiParam(value = "UUID Id of Artist", required = true) @PathVariable final String id) {
        return artistService.listAllSongsByPage(pageable, id);
    }

    @RequestMapping(value = "/parseurl", method = RequestMethod.POST,
            headers = "content-type=application/x-www-form-urlencoded",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Artist parseUrl(final HttpServletRequest request) {
        final ParameterMapParser parser = ParameterMapParser.parse(request.getParameterMap());
        final Mutable<Artist> artist = Mutable.of(null);

        QueryBlock.create()

                // Ensure the request has sent the required data
                .require(parser.has("url"), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Request does not have required data")
                        .done(data -> data.put("url", parser.get("url").toString())))

                // Return the processes medium
                .done(data -> artist.mutate(pageParser.parseArtistFromUrl(data.get("url").toString())));

        artist.ofNullable().ifPresent(newArtist ->
                artist.mutate(artistService
                        .findFirstByFacebookId(newArtist.getFacebookId())
                        .orElseGet(() -> artistService.saveAndFlush(newArtist).orElse(newArtist)))
        );

        return artist.get();
    }
}