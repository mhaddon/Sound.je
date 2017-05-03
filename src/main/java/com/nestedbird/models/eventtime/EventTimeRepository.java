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

package com.nestedbird.models.eventtime;

import com.nestedbird.models.core.Audited.AuditedRepository;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * The interface Event time repository.
 */
@RepositoryRestResource(exported = false)
public interface EventTimeRepository extends AuditedRepository<EventTime> {
    /**
     * Find all by start time after page.
     *
     * @param active   the active
     * @param dateTime the date time
     * @param pageable the pageable
     * @return the page
     */
    Page<EventTime> findAllByActiveAndStartTimeAfter(@Param("active") final Boolean active,
                                                     @Param("dateTime") final DateTime dateTime,
                                                     final Pageable pageable);
}
