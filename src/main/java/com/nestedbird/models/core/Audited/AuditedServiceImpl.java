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

package com.nestedbird.models.core.Audited;

import com.nestedbird.models.core.Base.BaseServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The type Audited service.
 *
 * @param <T> the type parameter
 */
public abstract class AuditedServiceImpl<T extends AuditedEntity> extends BaseServiceImpl<T> implements AuditedService<T> {
    @Override
    public Page<T> findByActive(final Pageable pageable, final Boolean active) {
        return getRepository().findByActive(pageable, active);
    }

    protected abstract AuditedRepository<T> getRepository();
}
