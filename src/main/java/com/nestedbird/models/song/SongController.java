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

import com.nestedbird.models.core.Base.BaseController;
import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Song controller.
 */
@RestController
@RequestMapping("api/v1/Songs/")
public class SongController extends BaseController<Song> {

    private final SongRepository songRepository;

    private final SongService songService;

    /**
     * Instantiates a new Song controller.
     *
     * @param songRepository the song repository
     * @param songService    the song service
     */
    @Autowired
    SongController(final SongRepository songRepository, final SongService songService) {
        this.songService = songService;
        this.songRepository = songRepository;
    }

    @Override
    public BaseRepository<Song> getRepository() {
        return songRepository;
    }

    @Override
    public Class<Song> getEntityClass() {
        return Song.class;
    }

    @Override
    public BaseService<Song> getService() {
        return this.songService;
    }
}