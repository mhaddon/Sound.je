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

import com.nestedbird.models.core.Base.BaseController;
import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseService;
import com.nestedbird.models.medium.Medium;
import com.nestedbird.models.song.Song;
import com.nestedbird.modules.formparser.ParameterMapParser;
import com.nestedbird.modules.resourceparser.PageParser;
import com.nestedbird.util.Mutable;
import com.nestedbird.util.QueryBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * The type Artist controller.
 */
@RestController
@RequestMapping("api/v1/Artists/")
public class ArtistController extends BaseController<Artist> {
    private final ArtistRepository artistRepository;
    private final ArtistService artistService;
    private final PageParser pageParser;

    /**
     * Instantiates a new Artist controller.
     *
     * @param artistService    the artist service
     * @param artistRepository the artist repository
     */
    @Autowired
    ArtistController(final ArtistService artistService,
                     final ArtistRepository artistRepository,
                     final PageParser pageParser) {
        this.artistService = artistService;
        this.artistRepository = artistRepository;
        this.pageParser = pageParser;
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
     * List media page.
     *
     * @param pageable the pageable
     * @param id       the id
     * @return the page
     */
    @RequestMapping(value = "{id}/Media", method = RequestMethod.GET)
    public Page<Medium> listMedia(final Pageable pageable, @PathVariable final String id) {
        return artistService.listAllMediumByPage(pageable, id);
    }

    /**
     * List songs page.
     *
     * @param pageable the pageable
     * @param id       the id
     * @return the page
     */
    @RequestMapping(value = "{id}/Songs", method = RequestMethod.GET)
    public Page<Song> listSongs(final Pageable pageable, @PathVariable final String id) {
        return artistService.listAllSongsByPage(pageable, id);
    }

    @RequestMapping(value = "parseurl", method = RequestMethod.POST,
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