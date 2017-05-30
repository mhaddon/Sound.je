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

package com.nestedbird.models.scannedpage;

import com.nestedbird.models.core.Base.BaseEndpointPermissions;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ScannedPageEndpointPermissions extends BaseEndpointPermissions {
    @Override
    protected String apiName() {
        return "ScannedPages";
    }

    @Override
    public String[] publicGET() {
        return new String[]{};
    }

    @Override
    public String[] moderatorGET() {
        return new String[]{
                APIUrl,
                APIUrl + "/schema",
                APIItemUrl
        };
    }

    @Override
    public String[] adminGET() {
        return new String[] {
                APIUrl + "/manualrequest",
                APIUrl + "/testrequest"
        };
    }

    @Override
    public String[] entityPOST() {
        return new String[]{
                APIUrl,
                APIUrl + "/parseurl"
        };
    }
}