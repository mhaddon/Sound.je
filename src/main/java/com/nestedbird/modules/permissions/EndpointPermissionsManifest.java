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

package com.nestedbird.modules.permissions;

import com.nestedbird.models.artist.ArtistEndpointPermissions;
import com.nestedbird.models.event.EventEndpointPermissions;
import com.nestedbird.models.eventtime.EventTimeEndpointPermissions;
import com.nestedbird.models.location.LocationEndpointPermissions;
import com.nestedbird.models.medium.MediumEndpointPermissions;
import com.nestedbird.models.privilege.PrivilegeEndpointPermissions;
import com.nestedbird.models.roles.RoleEndpointPermissions;
import com.nestedbird.models.scannedpage.ScannedPageEndpointPermissions;
import com.nestedbird.models.song.SongEndpointPermissions;
import com.nestedbird.models.tag.TagEndpointPermissions;
import com.nestedbird.models.user.UserEndpointPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class EndpointPermissionsManifest implements EndpointPermissions {
    private final List<EndpointPermissions> permissionsList;

    @Autowired
    public EndpointPermissionsManifest(final ArtistEndpointPermissions artistEndpointPermissions,
                                       final StandardEndpointPermissions standardEndpointPermissions,
                                       final EventEndpointPermissions eventEndpointPermissions,
                                       final EventTimeEndpointPermissions eventTimeEndpointPermissions,
                                       final LocationEndpointPermissions locationEndpointPermissions,
                                       final MediumEndpointPermissions mediumEndpointPermissions,
                                       final TagEndpointPermissions tagEndpointPermissions,
                                       final PrivilegeEndpointPermissions privilegeEndpointPermissions,
                                       final RoleEndpointPermissions roleEndpointPermissions,
                                       final ScannedPageEndpointPermissions scannedPageEndpointPermissions,
                                       final SongEndpointPermissions songEndpointPermissions,
                                       final UserEndpointPermissions userEndpointPermissions) {
        permissionsList = Arrays.asList(
                artistEndpointPermissions,
                standardEndpointPermissions,
                eventEndpointPermissions,
                eventTimeEndpointPermissions,
                locationEndpointPermissions,
                mediumEndpointPermissions,
                tagEndpointPermissions,
                privilegeEndpointPermissions,
                roleEndpointPermissions,
                scannedPageEndpointPermissions,
                songEndpointPermissions,
                userEndpointPermissions
        );
    }

    @Override
    public String[] ignore() {
        return permissionsList.stream()
                .map(EndpointPermissions::ignore)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }

    @Override
    public String[] publicGET() {
        return permissionsList.stream()
                .map(EndpointPermissions::publicGET)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }

    @Override
    public String[] adminGET() {
        return permissionsList.stream()
                .map(EndpointPermissions::adminGET)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }

    @Override
    public String[] moderatorGET() {
        return permissionsList.stream()
                .map(EndpointPermissions::moderatorGET)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }

    @Override
    public String[] publicPOST() {
        return permissionsList.stream()
                .map(EndpointPermissions::publicPOST)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }

    @Override
    public String[] schemaGET() {
        return permissionsList.stream()
                .map(EndpointPermissions::schemaGET)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }

    @Override
    public String[] entityPOST() {
        return permissionsList.stream()
                .map(EndpointPermissions::entityPOST)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }

    @Override
    public String[] entityPUT() {
        return permissionsList.stream()
                .map(EndpointPermissions::entityPUT)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }

    @Override
    public String[] entityDELETE() {
        return permissionsList.stream()
                .map(EndpointPermissions::entityDELETE)
                .flatMap(Arrays::stream)
                .toArray(String[]::new);
    }
}
