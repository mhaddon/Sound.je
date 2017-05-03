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

package com.nestedbird.models.privilege;

import com.nestedbird.models.core.Audited.AuditedRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Privilege repository.
 */
@Repository
public interface PrivilegeRepository extends AuditedRepository<Privilege> {
    /**
     * Find by name privilege.
     *
     * @param name the name
     * @return the privilege
     */
    Privilege findFirstByName(final String name);
}