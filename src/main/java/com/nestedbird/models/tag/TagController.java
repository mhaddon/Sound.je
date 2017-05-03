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

package com.nestedbird.models.tag;

import com.nestedbird.models.core.Base.BaseController;
import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/Tags/")
@Slf4j
public class TagController extends BaseController<Tag> {

    private final TagRepository tagRepository;
    private final TagService tagService;

    @Autowired
    public TagController(final TagRepository tagRepository,
                         final TagService tagService) {
        this.tagRepository = tagRepository;
        this.tagService = tagService;
    }

    @Override
    public BaseRepository<Tag> getRepository() {
        return tagRepository;
    }

    @Override
    public Class<Tag> getEntityClass() {
        return Tag.class;
    }

    @Override
    public BaseService<Tag> getService() {
        return this.tagService;
    }
}