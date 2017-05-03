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

package com.nestedbird.models.occurrence;

import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.event.Event;
import org.joda.time.DateTime;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * The interface Occurrence repository.
 */
@RepositoryRestResource(exported = false)
public interface OccurrenceRepository extends BaseRepository<Occurrence> {
    /**
     * Find one by event and start time occurrence.
     *
     * @param event     the event
     * @param startTime the start time
     * @return the occurrence
     */
    Occurrence findFirstByEventAndStartTime(final Event event, final DateTime startTime);
}
