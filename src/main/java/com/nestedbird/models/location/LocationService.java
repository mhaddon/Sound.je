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

package com.nestedbird.models.location;

import com.nestedbird.models.core.Audited.AuditedService;

import java.util.Optional;

/**
 * The interface Location service.
 */
public interface LocationService extends AuditedService<Location> {
    /**
     * Find first by facebook id optional.
     *
     * @param facebookId the facebook id
     * @return the optional
     */
    Optional<Location> findFirstByFacebookId(final Long facebookId);

    /**
     * Find first by facebook id or name optional.
     *
     * @param facebookId the facebook id
     * @param name       the name
     * @return the optional
     */
    Optional<Location> findFirstByFacebookIdOrName(final Long facebookId, final String name);
}
