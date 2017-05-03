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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nestedbird.models.core.DataObject;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * The type Youtube list response.
 */
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"items"})
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class YoutubeListResponse extends DataObject implements Serializable {

    private final String kind;
    private final String etag;
    private final List<YoutubeVideo> items;

}
