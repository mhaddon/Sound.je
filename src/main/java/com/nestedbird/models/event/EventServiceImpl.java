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

package com.nestedbird.models.event;

import com.nestedbird.models.core.Audited.AuditedRepository;
import com.nestedbird.models.core.Audited.AuditedServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * The type Event service.
 */
@Service
@Transactional
class EventServiceImpl extends AuditedServiceImpl<Event> implements EventService {

    private final EventRepository eventRepository;

    /**
     * Instantiates a new Event service.
     *
     * @param eventRepository the event repository
     */
    @Autowired
    EventServiceImpl(final EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    protected AuditedRepository<Event> getRepository() {
        return eventRepository;
    }

    @Override
    public Optional<Event> findFirstByFacebookId(final Long facebookId) {
        return Optional.ofNullable(eventRepository.findFirstByFacebookId(facebookId));
    }
}