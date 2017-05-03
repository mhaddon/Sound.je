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

package com.nestedbird.models.core.Base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * The type Base service.
 *
 * @param <T> the type parameter
 */
public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {

    @Override
    public Page<T> listAllByPage(final Pageable pageable) {
        return getRepository().findAll(pageable);
    }

    /**
     * Gets repository.
     *
     * @return the repository
     */
    protected abstract BaseRepository<T> getRepository();

    @Override
    public Optional<T> findOne(final String id) {
        return Optional.ofNullable(getRepository().findOne(id));
    }

    @Override
    public Optional<T> saveAndFlush(final T entity) {
        return Optional.ofNullable(getRepository().saveAndFlush(entity));
    }
}
