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

package com.nestedbird.jackson;

import com.nestedbird.models.core.Base.BaseEntity;
import com.nestedbird.models.core.DataObject;
import lombok.*;

import java.io.Serializable;

/**
 * The type Search result.
 *
 * @param <T> the type parameter
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchResult<T extends BaseEntity> extends DataObject implements Serializable {
    private final String name;
    private final String url;
    private final Float score;
    private final String category;
    private final String id;
    private final T entity;
}
