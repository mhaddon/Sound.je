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
import com.nestedbird.models.core.Audited.AuditedServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The type Event time service.
 */
@Service
@Transactional
class EventTimeServiceImpl extends AuditedServiceImpl<EventTime> implements EventTimeService {

    private final EventTimeRepository eventTimeRepository;

    /**
     * Instantiates a new Event time service.
     *
     * @param eventTimeRepository the event time repository
     */
    @Autowired
    EventTimeServiceImpl(final EventTimeRepository eventTimeRepository) {
        this.eventTimeRepository = eventTimeRepository;
    }

    @Override
    protected AuditedRepository<EventTime> getRepository() {
        return eventTimeRepository;
    }

    @Override
    public Page<EventTime> listAllByPageUpcoming(final Pageable pageable) {
        return eventTimeRepository.findAllByActiveAndStartTimeAfter(
                true,
                new DateTime().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0),
                pageable
        );
    }
}