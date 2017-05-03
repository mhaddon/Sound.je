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

import com.nestedbird.models.core.Audited.AuditedRepository;
import com.nestedbird.models.medium.Medium;
import com.nestedbird.models.song.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * The interface Artist repository.
 */
@Repository
public interface ArtistRepository extends AuditedRepository<Artist> {
    /**
     * Find all media page.
     *
     * @param pageable the pageable
     * @param artistId the artist id
     * @return the page
     */
    @Query("SELECT m FROM Medium m WHERE m.song.artist.id=:artistId AND m.active=true")
    Page<Medium> findAllMedia(final Pageable pageable, @Param("artistId") final String artistId);

    /**
     * Find all songs page.
     *
     * @param pageable the pageable
     * @param artistId the artist id
     * @return the page
     */
    @Query("SELECT s FROM Song s WHERE s.artist.id=:artistId AND s.active=true")
    Page<Song> findAllSongs(final Pageable pageable, @Param("artistId") final String artistId);


    Artist findFirstByFacebookId(final String facebookId);
}
