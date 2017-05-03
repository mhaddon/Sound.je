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
 * The type Youtube thumbnails.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutubeThumbnails extends DataObject implements Serializable {
    @JsonProperty("default")
    private final YoutubeThumbnail normal;

    @JsonProperty("medium")
    private final YoutubeThumbnail medium;

    @JsonProperty("high")
    private final YoutubeThumbnail high;

    @JsonProperty("standard")
    private final YoutubeThumbnail standard;

    @JsonProperty("maxres")
    private final YoutubeThumbnail maxres;

    /**
     * Gets normal.
     *
     * @return the normal
     */
    @JsonIgnore
    public Optional<YoutubeThumbnail> getNormal() {
        return Optional.ofNullable(normal);
    }

    /**
     * Gets medium.
     *
     * @return the medium
     */
    @JsonIgnore
    public Optional<YoutubeThumbnail> getMedium() {
        return Optional.ofNullable(medium);
    }

    /**
     * Gets high.
     *
     * @return the high
     */
    @JsonIgnore
    public Optional<YoutubeThumbnail> getHigh() {
        return Optional.ofNullable(high);
    }

    /**
     * Gets standard.
     *
     * @return the standard
     */
    @JsonIgnore
    public Optional<YoutubeThumbnail> getStandard() {
        return Optional.ofNullable(standard);
    }

    /**
     * Gets maxres.
     *
     * @return the maxres
     */
    @JsonIgnore
    public Optional<YoutubeThumbnail> getMaxres() {
        return Optional.ofNullable(maxres);
    }

    /**
     * Gets highest res.
     *
     * @return the highest res
     */
    @JsonIgnore
    public Optional<YoutubeThumbnail> getHighestRes() {
        YoutubeThumbnail highestRes = null;
        if (normal != null) highestRes = normal;
        if (medium != null) highestRes = medium;
        if (high != null) highestRes = high;
        if (standard != null) highestRes = standard;
        if (maxres != null) highestRes = maxres;

        return Optional.ofNullable(highestRes);
    }
}
