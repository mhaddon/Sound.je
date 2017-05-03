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

package com.nestedbird.jackson.youtube;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nestedbird.models.core.DataObject;
import lombok.*;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Optional;

/**
 * The type Youtube snippet.
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"thumbnails"})
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutubeSnippet extends DataObject implements Serializable {
    private final String publishedAt;
    private final String channelId;
    private final String title;
    private final String description;
    private final String channelTitle;

    @JsonProperty("thumbnails")
    private final YoutubeThumbnails thumbnails;

    /**
     * Gets published at parsed.
     *
     * @return the published at parsed
     */
    public DateTime getPublishedAtParsed() {
        return Optional.ofNullable(publishedAt)
                .map(dateTime -> org.joda.time.format.DateTimeFormat
                        .forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                        .parseDateTime(dateTime))
                .orElse(new DateTime());
    }

    /**
     * Gets thumbnails.
     *
     * @return the thumbnails
     */
    @JsonIgnore
    public Optional<YoutubeThumbnails> getThumbnails() {
        return Optional.ofNullable(thumbnails);
    }
}
