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

import com.nestedbird.models.core.Base.BaseController;
import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Privilege controller.
 */
@RestController
@RequestMapping("api/v1/Privileges/")
public class PrivilegeController extends BaseController<Privilege> {
    private final PrivilegeRepository privilegeRepository;

    private final PrivilegeService privilegeService;

    /**
     * Instantiates a new Privilege controller.
     *
     * @param privilegeRepository the privilege repository
     * @param privilegeService    the privilege service
     */
    @Autowired
    PrivilegeController(final PrivilegeRepository privilegeRepository,
                        final PrivilegeService privilegeService) {
        this.privilegeRepository = privilegeRepository;
        this.privilegeService = privilegeService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public BaseRepository<Privilege> getRepository() {
        return privilegeRepository;
    }

    @Override
    public Class<Privilege> getEntityClass() {
        return Privilege.class;
    }

    @Override
    public BaseService<Privilege> getService() {
        return this.privilegeService;
    }
}