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

package com.nestedbird.modules.facebookreader;

import com.nestedbird.jackson.facebook.FacebookEvent;
import com.nestedbird.jackson.facebook.FacebookPage;
import com.nestedbird.jackson.facebook.FacebookPost;
import com.nestedbird.models.core.DataObject;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * The type Facebook scan collection.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FacebookScanCollection extends DataObject implements Serializable {
    private final List<FacebookEvent> events;
    private final List<FacebookPost> posts;
    private final List<FacebookPage> pages;
}
