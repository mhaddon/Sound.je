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

import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
class TagServiceImpl extends BaseServiceImpl<Tag> implements TagService {

    private final TagRepository tagRepository;

    @Autowired
    TagServiceImpl(final TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    protected BaseRepository<Tag> getRepository() {
        return tagRepository;
    }

    @Override
    public Optional<Tag> findFirstByName(final String name) {
        return Optional.ofNullable(tagRepository.findFirstByName(name));
    }
}