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

package com.nestedbird.models.song;

import com.nestedbird.models.core.Audited.AuditedRepository;
import com.nestedbird.models.core.Audited.AuditedServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The type Song service.
 */
@Service
@Transactional
class SongServiceImpl extends AuditedServiceImpl<Song> implements SongService {
    private final SongRepository songRepository;

    /**
     * Instantiates a new Song service.
     *
     * @param songRepository the song repository
     */
    @Autowired
    SongServiceImpl(final SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    protected AuditedRepository<Song> getRepository() {
        return songRepository;
    }
}