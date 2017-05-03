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
 * The type Facebook video.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacebookVideo extends DataObject implements Serializable {
    private final String id;

    @JsonProperty("created_time")
    private final String createdTime;

    @JsonProperty("from")
    private final FacebookOwner from;
    private final String icon;
    private final String title;
    private final String source;

    @JsonProperty("permalink_url")
    private final String permalinkUrl;
    private final String picture;

    @JsonProperty("likes")
    private final FacebookLikes likes;

    @JsonProperty("comments")
    private final FacebookComments comments;

    /**
     * Gets start time parsed.
     *
     * @return the start time parsed
     */
    public DateTime getCreatedTimeParsed() {
        return Optional.ofNullable(createdTime)
                .map(dateTime -> org.joda.time.format.DateTimeFormat
                        .forPattern("yyyy-MM-dd'T'HH:mm:ssZ")
                        .parseDateTime(dateTime))
                .orElse(new DateTime());
    }

    /**
     * Gets from.
     *
     * @return the from
     */
    @JsonIgnore
    public Optional<FacebookOwner> getFrom() {
        return Optional.ofNullable(from);
    }

    /**
     * Gets likes.
     *
     * @return the likes
     */
    @JsonIgnore
    public Optional<FacebookLikes> getLikes() {
        return Optional.ofNullable(likes);
    }

    /**
     * Gets comments.
     *
     * @return the comments
     */
    @JsonIgnore
    public Optional<FacebookComments> getComments() {
        return Optional.ofNullable(comments);
    }
}
