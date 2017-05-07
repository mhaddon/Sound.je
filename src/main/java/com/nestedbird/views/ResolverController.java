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

import com.nestedbird.models.artist.ArtistService;
import com.nestedbird.models.core.Base.BaseEntity;
import com.nestedbird.models.event.EventService;
import com.nestedbird.models.location.LocationService;
import com.nestedbird.models.medium.MediumService;
import com.nestedbird.util.Mutable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class ResolverController {
    private final EventService eventService;

    private final ArtistService artistService;

    private final LocationService locationService;

    private final MediumService mediumService;

    @Autowired
    public ResolverController(final EventService eventService,
                              final ArtistService artistService,
                              final LocationService locationService,
                              final MediumService mediumService) {
        this.eventService = eventService;
        this.artistService = artistService;
        this.locationService = locationService;
        this.mediumService = mediumService;
    }


    @RequestMapping(value = "/resolve/{id}")
    public void resolveEntityId(@PathVariable("id") final String id,
                                final HttpServletResponse response) {
        final Mutable<BaseEntity> entity = Mutable.of(null);
        eventService.findOne(id).map(BaseEntity.class::cast).ifPresent(entity::mutate);
        artistService.findOne(id).map(BaseEntity.class::cast).ifPresent(entity::mutate);
        locationService.findOne(id).map(BaseEntity.class::cast).ifPresent(entity::mutate);
        mediumService.findOne(id).map(BaseEntity.class::cast).ifPresent(entity::mutate);

        if (entity.isPresent()) {
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", entity.get().getUrl());
        } else {
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", "/404");
        }
    }
}
