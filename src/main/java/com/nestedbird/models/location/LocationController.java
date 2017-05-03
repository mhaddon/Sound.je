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

package com.nestedbird.models.location;

import com.nestedbird.models.core.Base.BaseController;
import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseService;
import com.nestedbird.modules.formparser.ParameterMapParser;
import com.nestedbird.modules.resourceparser.LocationParser;
import com.nestedbird.util.Mutable;
import com.nestedbird.util.QueryBlock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * The type Location controller.
 */
@RestController
@RequestMapping("api/v1/Locations/")
public class LocationController extends BaseController<Location> {

    private final LocationRepository locationRepository;
    private final LocationService locationService;
    private final LocationParser locationParser;

    /**
     * Instantiates a new Location controller.
     *
     * @param locationRepository the location repository
     * @param locationService    the location service
     * @param locationParser     the location parser
     */
    @Autowired
    LocationController(final LocationRepository locationRepository,
                       final LocationService locationService,
                       final LocationParser locationParser) {
        this.locationService = locationService;
        this.locationRepository = locationRepository;
        this.locationParser = locationParser;
    }

    @Override
    public BaseRepository<Location> getRepository() {
        return locationRepository;
    }

    @Override
    public Class<Location> getEntityClass() {
        return Location.class;
    }

    @Override
    public BaseService<Location> getService() {
        return this.locationService;
    }

    /**
     * Parse url location.
     *
     * @param request the request
     * @return the location
     */
    @RequestMapping(value = "parseurl", method = RequestMethod.POST,
            headers = "content-type=application/x-www-form-urlencoded",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Location parseUrl(final HttpServletRequest request) {
        final ParameterMapParser parser = ParameterMapParser.parse(request.getParameterMap());
        final Mutable<Location> location = Mutable.of(null);

        QueryBlock.create()

                // Ensure the request has sent the required data
                .require(parser.has("url"), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Request does not have required data")
                        .done(data -> data.put("url", parser.get("url").toString())))

                // Return the processes medium
                .done(data -> location.mutate(locationParser.parseUrl(data.get("url").toString())));

        location.ofNullable().ifPresent(newLocation ->
                location.mutate(locationService
                        .findFirstByFacebookIdOrName(newLocation.getFacebookId(), newLocation.getName())
                        .orElseGet(() -> locationService.saveAndFlush(newLocation).orElse(newLocation)))
        );

        return location.get();
    }

}