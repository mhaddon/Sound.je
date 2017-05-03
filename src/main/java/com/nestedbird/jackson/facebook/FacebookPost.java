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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nestedbird.models.core.DataObject;
import lombok.*;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Optional;

/**
 * The type Facebook post.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookPost extends DataObject implements Serializable {
    private final String message;
    private final String link;
    @JsonProperty("created_time")
    private final String createdTime;
    private final String id;

    /**
     * Gets created time parsed.
     *
     * @return the created time parsed
     */
    public DateTime getCreatedTimeParsed() {
        return Optional.ofNullable(createdTime)
                .map(dateTime -> org.joda.time.format.DateTimeFormat
                        .forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
                        .parseDateTime(dateTime))
                .orElse(new DateTime());
    }
}
