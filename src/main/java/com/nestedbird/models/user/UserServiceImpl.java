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

package com.nestedbird.models.user;

import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.core.Audited.AuditedRepository;
import com.nestedbird.models.core.Audited.AuditedServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * The type User service.
 */
@Service
@Transactional
class UserServiceImpl extends AuditedServiceImpl<User> implements UserService {

    private final UserRepository userRepository;

    /**
     * Instantiates a new User service.
     *
     * @param userRepository the user repository
     */
    @Autowired
    UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected AuditedRepository<User> getRepository() {
        return userRepository;
    }

    @Override
    public Optional<User> findFirstByEmail(final String email) {
        return Optional.ofNullable(userRepository.findFirstByEmail(email))
                .filter(AuditedEntity::getActive);
    }
}