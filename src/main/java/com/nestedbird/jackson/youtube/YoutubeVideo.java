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

import java.io.Serializable;
import java.util.Optional;

/**
 * The type Youtube video.
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"snippet", "contentDetails", "status", "statistics"})
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutubeVideo extends DataObject implements Serializable {
    private final String kind;
    private final String etag;
    private final String id;

    @JsonProperty("snippet")
    private final YoutubeSnippet snippet;

    @JsonProperty("contentDetails")
    private final YoutubeContentDetails contentDetails;

    @JsonProperty("status")
    private final YoutubeStatus status;

    @JsonProperty("statistics")
    private final YoutubeStatistics statistics;

    /**
     * Gets snippet.
     *
     * @return the snippet
     */
    @JsonIgnore
    public Optional<YoutubeSnippet> getSnippet() {
        return Optional.ofNullable(snippet);
    }

    /**
     * Gets content details.
     *
     * @return the content details
     */
    @JsonIgnore
    public Optional<YoutubeContentDetails> getContentDetails() {
        return Optional.ofNullable(contentDetails);
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    @JsonIgnore
    public Optional<YoutubeStatus> getStatus() {
        return Optional.ofNullable(status);
    }

    /**
     * Gets statistics.
     *
     * @return the statistics
     */
    @JsonIgnore
    public Optional<YoutubeStatistics> getStatistics() {
        return Optional.ofNullable(statistics);
    }
}
