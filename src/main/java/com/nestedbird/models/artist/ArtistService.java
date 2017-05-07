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

import com.nestedbird.models.core.Audited.AuditedService;
import com.nestedbird.models.medium.Medium;
import com.nestedbird.models.song.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * The interface Artist service.
 */
public interface ArtistService extends AuditedService<Artist> {
    /**
     * List all medium by page page.
     *
     * @param pageable the pageable
     * @param id       the id
     * @return the page
     */
    Page<Medium> listAllMediumByPage(final Pageable pageable, final String id);

    /**
     * List all songs by page page.
     *
     * @param pageable the pageable
     * @param id       the id
     * @return the page
     */
    Page<Song> listAllSongsByPage(final Pageable pageable, final String id);

    /**
     * Retrieves an artist by facebook id
     *
     * @param facebookId facebook page id
     * @return artist
     */
    Optional<Artist> findFirstByFacebookId(final String facebookId);
}
