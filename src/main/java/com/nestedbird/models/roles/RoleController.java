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

package com.nestedbird.models.roles;

import com.nestedbird.models.core.Base.BaseController;
import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Role controller.
 */
@RestController
@RequestMapping("api/v1/Roles/")
public class RoleController extends BaseController<Role> {
    private final RoleRepository roleRepository;

    private final RoleService roleService;

    /**
     * Instantiates a new Role controller.
     *
     * @param roleRepository the role repository
     * @param roleService    the role service
     */
    @Autowired
    RoleController(final RoleRepository roleRepository,
                   final RoleService roleService) {
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }

    @Override
    public BaseRepository<Role> getRepository() {
        return roleRepository;
    }

    @Override
    public Class<Role> getEntityClass() {
        return Role.class;
    }

    @Override
    public BaseService<Role> getService() {
        return this.roleService;
    }
}