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

import com.nestedbird.models.core.Base.BaseController;
import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The type Event time controller.
 */
@RestController
@RequestMapping("/api/v1/EventTimes")
@ApiIgnore
public class EventTimeController extends BaseController<EventTime> {

    private final EventTimeRepository eventTimeRepository;
    private final EventTimeService eventTimeService;

    /**
     * Instantiates a new Event time controller.
     *
     * @param eventTimeRepository the event time repository
     * @param eventTimeService    the event time service
     */
    @Autowired
    EventTimeController(final EventTimeRepository eventTimeRepository,
                        final EventTimeService eventTimeService) {
        this.eventTimeService = eventTimeService;
        this.eventTimeRepository = eventTimeRepository;
    }

    @Override
    @SuppressWarnings("unchecked")
    public BaseRepository<EventTime> getRepository() {
        return eventTimeRepository;
    }

    @Override
    public Class<EventTime> getEntityClass() {
        return EventTime.class;
    }

    @Override
    public BaseService<EventTime> getService() {
        return this.eventTimeService;
    }

    /**
     * List all upcomming event times
     *
     * @param pageable pagination settings
     * @return list of upcomming event times
     */
    @RequestMapping(value = "/Upcoming", method = RequestMethod.GET)
    Page<EventTime> listUpcoming(final Pageable pageable) {
        return eventTimeService.listAllByPageUpcoming(pageable);
    }
}