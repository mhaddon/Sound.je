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

import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.core.Base.BaseController;
import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseService;
import com.nestedbird.models.event.EventService;
import com.nestedbird.models.occurrence.Occurrence;
import com.nestedbird.modules.formparser.ParameterMapParser;
import com.nestedbird.modules.paginator.Paginator;
import com.nestedbird.modules.resourceparser.LocationParser;
import com.nestedbird.util.Mutable;
import com.nestedbird.util.QueryBlock;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Location controller.
 */
@RestController
@RequestMapping("/api/v1/Locations")
@Api(tags = "Locations")
public class LocationController extends BaseController<Location> {

    private final LocationRepository locationRepository;
    private final LocationService locationService;
    private final LocationParser locationParser;
    private final EventService eventService;

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
                       final LocationParser locationParser,
                       final EventService eventService) {
        this.locationService = locationService;
        this.locationRepository = locationRepository;
        this.locationParser = locationParser;
        this.eventService = eventService;
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

    @ApiOperation("Lists all upcoming events at a location")
    @RequestMapping(value = "/{id}/Events/Upcoming", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public Page<Occurrence> listUpcomingEvents(final Pageable pageable,
                                       @ApiParam("UUID Id of Location") @PathVariable final String id) {
        final List<Occurrence> occurrences = locationService.findOne(id)
                .filter(AuditedEntity::getActive)
                .map(eventService::retrieveUpcomingByLocation)
                .map(ArrayList::new)
                .orElse(new ArrayList());

        return Paginator.<Occurrence>of(pageable)
                .paginate(occurrences);
    }

    @ApiOperation("Lists all events at a location")
    @RequestMapping(value = "/{id}/Events", method = RequestMethod.GET)
    @SuppressWarnings("unchecked")
    public Page<Occurrence> listEvents(final Pageable pageable,
                                       @ApiParam("UUID Id of Location") @PathVariable final String id) {
        final List<Occurrence> occurrences = locationService.findOne(id)
                .filter(AuditedEntity::getActive)
                .map(eventService::retrieveByLocation)
                .map(ArrayList::new)
                .orElse(new ArrayList());

        return Paginator.<Occurrence>of(pageable)
                .paginate(occurrences);
    }

    /**
     * Parse url location.
     *
     * @param request the request
     * @return the location
     */
    @RequestMapping(value = "/parseurl", method = RequestMethod.POST,
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