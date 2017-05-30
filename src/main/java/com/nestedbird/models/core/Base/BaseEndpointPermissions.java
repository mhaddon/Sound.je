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

package com.nestedbird.models.core.Base;

import com.nestedbird.modules.permissions.EndpointPermissions;

abstract public class BaseEndpointPermissions implements EndpointPermissions {
    protected final String APIUrl = "/api/*/" + apiName();
    protected final String APIItemUrl = APIUrl + "/*-*-*-*-*";

    abstract protected String apiName();

    public String[] ignore() {
        return new String[]{};
    }

    public String[] publicGET() {
        return new String[]{
                APIUrl,
                APIItemUrl
        };
    }

    @Override
    public String[] adminGET() {
        return new String[]{};
    }

    public String[] moderatorGET() {
        return new String[]{
                APIUrl + "/schema"
        };
    }

    @Override
    public String[] publicPOST() {
        return new String[]{};
    }

    @Override
    public String[] schemaGET() {
        return new String[]{};
    }

    public String[] entityPOST() {
        return new String[]{
                APIUrl
        };
    }

    public String[] entityPUT() {
        return new String[]{
                APIItemUrl
        };
    }

    public String[] entityDELETE() {
        return new String[]{
                APIItemUrl
        };
    }
}