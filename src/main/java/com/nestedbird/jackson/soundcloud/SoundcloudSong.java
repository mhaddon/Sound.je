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

package com.nestedbird.jackson.soundcloud;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nestedbird.models.core.DataObject;
import lombok.*;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Optional;

/**
 * The type Soundcloud song.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SoundcloudSong extends DataObject implements Serializable {
    private final String kind;
    private final Long id;

    @JsonProperty("created_at")
    private final String createdAt;

    @JsonProperty("user_id")
    private final Long userId;
    private final Long duration;
    private final Boolean commentable;
    private final String state;

    @JsonProperty("original_content_size")
    private final Long originalContentSize;

    @JsonProperty("last_modified")
    private final String lastModified;
    private final String sharing;

    @JsonProperty("tag_list")
    private final String tagList;
    private final String permalink;
    private final Boolean streamable;

    @JsonProperty("embeddable_by")
    private final String embeddableBy;

    @JsonProperty("purchase_url")
    private final String purchaseUrl;

    @JsonProperty("purchase_title")
    private final String purchaseTitle;

    @JsonProperty("label_id")
    private final Long labelId;
    private final String genre;
    private final String title;
    private final String description;

    @JsonProperty("label_name")
    private final String labelName;
    private final String uri;

    @JsonProperty("permalink_url")
    private final String permalinkUrl;

    @JsonProperty("artwork_url")
    private final String artworkUrl;

    @JsonProperty("stream_url")
    private final String streamUrl;

    @JsonProperty("download_url")
    private final String downloadUrl;

    @JsonProperty("playback_count")
    private final Long playbackCount;

    @JsonProperty("download_count")
    private final Long downloadCount;

    @JsonProperty("favoritings_count")
    private final Long favouriteCount;

    @JsonProperty("reposts_count")
    private final Long repostCount;

    @JsonProperty("comment_count")
    private final Long commentCount;
    private final Boolean downloadable;

    /**
     * Gets start time parsed.
     *
     * @return the start time parsed
     */
    public DateTime getCreatedAtParsed() {
        return Optional.ofNullable(createdAt)
                .map(dateTime -> org.joda.time.format.DateTimeFormat
                        .forPattern("yyyy/MM/dd HH:mm:ss Z")
                        .parseDateTime(dateTime))
                .orElse(new DateTime());
    }
}
