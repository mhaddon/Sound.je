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

package com.nestedbird.modules.resourceparser;

import com.nestedbird.jackson.facebook.FacebookCover;
import com.nestedbird.jackson.facebook.FacebookLocation;
import com.nestedbird.models.location.Location;
import com.nestedbird.modules.facebookreader.FacebookReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * The type Location parser.
 */
@Component
public class LocationParser {
    private final FacebookReader facebookReader;

    /**
     * Instantiates a new Location parser.
     *
     * @param facebookReader the facebook reader
     */
    @Autowired
    public LocationParser(final FacebookReader facebookReader) {
        this.facebookReader = facebookReader;
    }

    /**
     * Parse url location.
     *
     * @param url the url
     * @return the location
     */
    public Location parseUrl(final String url) {
        final Location location = new Location();
        final String id = facebookReader.getIdFromUrl(url);

        Optional.ofNullable(facebookReader.requestPlace(id)).ifPresent(fbLocataion -> {
            location.setCity(fbLocataion.getLocation().map(FacebookLocation::getCity).orElse(""))
                    .setCountry(fbLocataion.getLocation().map(FacebookLocation::getCountry).orElse(""))
                    .setCoordinates(fbLocataion.getLocation().map(FacebookLocation::getCoordinates).orElse("0,0"))
                    .setStreet(fbLocataion.getLocation().map(FacebookLocation::getStreet).orElse(""))
                    .setFacebookId(Long.parseLong(fbLocataion.getId()))
                    .setDescription(fbLocataion.getAbout())
                    .setName(fbLocataion.getName())
                    .setImageUrl(fbLocataion.getCover().map(FacebookCover::getSource).orElse(""));
        });

        return location;
    }
}
