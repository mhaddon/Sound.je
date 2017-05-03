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

package com.nestedbird.models.core;

import com.nestedbird.models.persistentlogins.PersistentLoginsRepository;
import com.nestedbird.models.privilege.Privilege;
import com.nestedbird.models.privilege.PrivilegeRepository;
import com.nestedbird.models.roles.Role;
import com.nestedbird.models.roles.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Ensure certain fields are already saved to the database
 */
@Component
public class DatabaseLoader implements ApplicationRunner {

    private final RoleRepository roles;

    private final PrivilegeRepository privileges;

    private final PersistentLoginsRepository persistentLogins;

    /**
     * Instantiates a new Database loader.
     *
     * @param roles      the roles
     * @param privileges the privileges
     */
    @Autowired
    public DatabaseLoader(final RoleRepository roles,
                          final PrivilegeRepository privileges,
                          final PersistentLoginsRepository persistentLogins) {
        this.roles = roles;
        this.privileges = privileges;
        this.persistentLogins = persistentLogins;
    }

    /**
     * Ensure certain fields are already saved to the database
     *
     * @param applicationArguments
     */
    @Override
    public void run(final ApplicationArguments applicationArguments) {
        savePrivileges();
        saveRoles();
        deletePersistentLogins();
    }

    private void savePrivileges() {
        final List<Privilege> privilegesList = new ArrayList<>();

        privilegesList.add(Privilege.builder().name("PRIV_GET_ENTITY_SCHEMA").active(true).build());
        privilegesList.add(Privilege.builder().name("PRIV_CREATE_ENTITY").active(true).build());
        privilegesList.add(Privilege.builder().name("PRIV_UPDATE_ENTITY").active(true).build());
        privilegesList.add(Privilege.builder().name("PRIV_DELETE_ENTITY").active(true).build());
        privilegesList.add(Privilege.builder().name("PRIV_ADMIN").active(true).build());
        privilegesList.add(Privilege.builder().name("PRIV_MODERATOR").active(true).build());
        privilegesList.add(Privilege.builder().name("PRIV_USER").active(true).build());

        privilegesList.stream()
                .filter(privilege -> privileges.findFirstByName(privilege.getName()) == null)
                .forEach(privileges::saveAndFlush);
    }

    private void saveRoles() {
        final List<Role> roleList = new ArrayList<>();
        roleList.add(createOrGetRole("Admin", getAdminPrivileges()));
        roleList.add(createOrGetRole("MODERATOR", getModeratorPrivileges()));
        roleList.add(createOrGetRole("User", getUserPrivileges()));

        roleList.forEach(roles::saveAndFlush);
    }

    private void deletePersistentLogins() {
        persistentLogins.delete(persistentLogins.findAll());
    }

    private Role createOrGetRole(final String name, final Set<Privilege> privileges) {
        Role role = roles.findFirstByName(name);
        if (roles.findFirstByName(name) == null) {
            role = Role.builder().name(name).active(true).build();
        }
        role.setPrivileges(privileges);
        return role;
    }

    private Set<Privilege> getAdminPrivileges() {
        return new HashSet<>(privileges.findAll());
    }

    private Set<Privilege> getModeratorPrivileges() {
        return privileges.findAll().stream()
                .filter(e -> !e.getName().equals("PRIV_ADMIN"))
                .filter(e -> !e.getName().equals("PRIV_DELETE_ENTITY"))
                .collect(Collectors.toSet());
    }

    private Set<Privilege> getUserPrivileges() {
        return privileges.findAll().stream()
                .filter(e -> e.getName().equals("PRIV_USER"))
                .collect(Collectors.toSet());
    }
}
