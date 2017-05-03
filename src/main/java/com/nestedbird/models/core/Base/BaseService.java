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
 * Base Entity service
 *
 * @param <T> What type of base entity does this service support
 */
public interface BaseService<T extends BaseEntity> {
    /**
     * Retrieve paginated list of all base entities
     *
     * @param pageable the pagination settings
     * @return page of entities
     */
    Page<T> listAllByPage(final Pageable pageable);

    /**
     * Retrieves a single element by id
     *
     * @param id the element to retrieve
     * @return optional of element
     */
    Optional<T> findOne(final String id);

    /**
     * Save an entity
     *
     * @param entity entity to save
     * @return optional of saved element
     */
    Optional<T> saveAndFlush(final T entity);
}
