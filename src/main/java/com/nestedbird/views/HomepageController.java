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

package com.nestedbird.views;

import com.nestedbird.modules.ratelimiter.RateLimit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.management.ManagementFactory;
import java.util.Optional;

/**
 * This controller is responsible for serving the homepage and error endpoints
 */
@Controller
public class HomepageController {

    private final Environment env;

    /**
     * Instantiates a new Homepage controller.
     *
     * @param env the env
     */
    @Autowired
    public HomepageController(final Environment env) {
        this.env = env;
    }

    /**
     * On 404 we redirect to the 404 page
     *
     * @param model the page Model information
     * @return template path
     */
    @RequestMapping("/404")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String notFound(final Model model) {
        return home(model);
    }

    /**
     * Serves the homepage for a list of whitelisted urls
     *
     * @param model The page model information
     * @return template path
     */
    @RateLimit(limitPerMinute = 250)
    @RequestMapping(value = {
            "/",
            "/News",
            "/About",
            "/Events",
            "/Events/{id}",
            "/Events/{id}/{name}",
            "/Locations",
            "/Locations/{id}",
            "/Locations/{id}/{name}",
            "/Media",
            "/Medium/{id}",
            "/Medium/{id}/{name}",
            "/Artists",
            "/Artists/{id}",
            "/Artists/{id}/{name}",
            "/Songs",
            "/Songs/{id}",
            "/Songs/{id}/{name}",
            "/Admin",
            "/login",
            "/logout",
            "/Records",
            "/Records/{name}",
            "/search",
            "/search/{name}"
    })
    public String home(final Model model) {
        model.addAttribute("profile", Optional.ofNullable(env.getProperty("envTarget")).orElse("dev"));
        model.addAttribute("version", ManagementFactory.getRuntimeMXBean().getStartTime());

        return "homepage/index";
    }
}