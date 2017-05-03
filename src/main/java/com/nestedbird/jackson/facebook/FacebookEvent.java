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

package com.nestedbird.jackson.facebook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nestedbird.models.core.DataObject;
import lombok.*;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Optional;

/**
 * The type Facebook event.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookEvent extends DataObject implements Serializable {
    private final String id;
    private final String name;
    private final String description;

    @JsonProperty("place")
    private final FacebookPlace place;
    private final String timezone;

    @JsonProperty("start_time")
    private final String startTime;

    @JsonProperty("cover")
    private final FacebookCover cover;
    //    private final FacebookPicture picture;

    @JsonProperty("updated_time")
    private final String updatedTime;

    @JsonProperty("owner")
    private final FacebookOwner owner;

    @JsonProperty("is_page_owned")
    private final Boolean isPageOwned;

    /**
     * Gets place.
     *
     * @return the place
     */
    @JsonIgnore
    public Optional<FacebookPlace> getPlace() {
        return Optional.ofNullable(place);
    }

    /**
     * Gets start time parsed.
     *
     * @return the start time parsed
     */
    public DateTime getStartTimeParsed() {
        return Optional.ofNullable(startTime)
                .map(dateTime -> org.joda.time.format.DateTimeFormat
                        .forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
                        .parseDateTime(dateTime))
                .orElse(new DateTime());
    }

    /**
     * Gets cover.
     *
     * @return the cover
     */
    @JsonIgnore
    public Optional<FacebookCover> getCover() {
        return Optional.ofNullable(cover);
    }

    /**
     * Gets owner.
     *
     * @return the owner
     */
    @JsonIgnore
    public Optional<FacebookOwner> getOwner() {
        return Optional.ofNullable(owner);
    }
}