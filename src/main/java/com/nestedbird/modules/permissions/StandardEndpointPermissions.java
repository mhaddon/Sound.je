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

import org.springframework.context.annotation.Configuration;

@Configuration
public class StandardEndpointPermissions implements EndpointPermissions {
    @Override
    public String[] ignore() {
        return new String[]{
                "/**/*.css",
                "/**/*.js",
                "/**/*.png",
                "/**/*.jpg",
                "/**/*.jpeg",
                "/**/*.svg",
                "/**/*.gif",
                "/**/*.ico",
                "/**/*.json",
                "/**/*.xml",
                "/**/*.txt",
                "/**/*.ttf",
                "/swagger-ui.html",
                "/swagger-resources/**"
        };
    }

    @Override
    public String[] publicGET() {
        return new String[]{
                "/session",
                "/login/register",
                "/login/reset",
                "/login/check",
                "/login/reset/request",
                "/api",
                "/api/*/search/",
                "/api/*/search/*/",
                "/api/documentation",


                "/",
                "/News",
                "/About",
                "/Events",
                "/Events/*",
                "/Events/*/*",
                "/Locations",
                "/Locations/*",
                "/Locations/*/*",
                "/Media",
                "/Medium/*",
                "/Medium/*/*",
                "/Artists",
                "/Artists/*",
                "/Artists/*/*",
                "/Songs",
                "/Songs/*",
                "/Songs/*/*",
                "/Admin",
                "/login",
                "/logout",
                "/search",
                "/search/*",
                "/api"
                //                "/api/*/Events/updateFB",
                //                "/api/*/ScannedPages/manualrequest"
        };
    }

    @Override
    public String[] adminGET() {
        return new String[0];
    }

    @Override
    public String[] moderatorGET() {
        return new String[]{
                "/Records",
                "/Records/*",
                "/records/*/*"
        };
    }

    @Override
    public String[] publicPOST() {
        return new String[]{
                "/login/check",
                "/login/register/create",
                "/login/register/confirm",
                "/login/reset/confirm"
        };
    }

    @Override
    public String[] schemaGET() {
        return new String[0];
    }

    @Override
    public String[] entityPOST() {
        return new String[0];
    }

    @Override
    public String[] entityPUT() {
        return new String[0];
    }

    @Override
    public String[] entityDELETE() {
        return new String[0];
    }
}
