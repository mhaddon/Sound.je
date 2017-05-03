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
import com.nestedbird.models.core.Audited.AuditedServiceImpl;
import com.nestedbird.models.medium.Medium;
import com.nestedbird.models.song.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * The type Artist service.
 */
@Service
@Transactional
public class ArtistServiceImpl extends AuditedServiceImpl<Artist> implements ArtistService {

    private final ArtistRepository artistRepository;

    /**
     * Instantiates a new Artist service.
     *
     * @param artistRepository the artist repository
     */
    @Autowired
    ArtistServiceImpl(final ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    protected AuditedRepository<Artist> getRepository() {
        return artistRepository;
    }

    @Override
    public Page<Medium> listAllMediumByPage(final Pageable pageable, final String id) {
        return artistRepository.findAllMedia(pageable, id);
    }

    @Override
    public Page<Song> listAllSongsByPage(final Pageable pageable, final String id) {
        return artistRepository.findAllSongs(pageable, id);
    }

    @Override
    public Optional<Artist> findFirstByFacebookId(final String facebookId) {
        return Optional.ofNullable(artistRepository.findFirstByFacebookId(facebookId));
    }
}