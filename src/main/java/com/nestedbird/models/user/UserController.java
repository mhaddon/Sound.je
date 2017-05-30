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

import com.nestedbird.models.core.Base.BaseController;
import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * The type User controller.
 */
@RestController
@RequestMapping("/api/v1/Users")
@ApiIgnore
public class UserController extends BaseController<User> {

    private final UserRepository userRepository;

    private final UserService userService;

    /**
     * Instantiates a new User controller.
     *
     * @param userRepository the user repository
     * @param userService    the user service
     */
    @Autowired
    UserController(final UserRepository userRepository, final UserService userService) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public BaseRepository<User> getRepository() {
        return userRepository;
    }

    @Override
    public Class<User> getEntityClass() {
        return User.class;
    }

    @Override
    public BaseService<User> getService() {
        return this.userService;
    }
}