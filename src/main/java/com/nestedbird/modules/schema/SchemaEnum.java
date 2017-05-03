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

package com.nestedbird.modules.schema;


import com.nestedbird.models.core.DataObject;
import lombok.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is a wrapper for enumerations so we can list all of the possible options in a dropdown
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SchemaEnum extends DataObject implements Serializable {
    /**
     * Of schema enum.
     *
     * @param clazz the clazz
     * @return the schema enum
     */
    public static SchemaEnum of(Class<? extends Enum> clazz) {
        final List<String> options = Arrays.stream(clazz.getEnumConstants())
                .map(Enum::toString)
                .collect(Collectors.toList());

        return SchemaEnum.builder().options(options).build();
    }

    /**
     * All possible options
     */
    private final List<String> options;
}
