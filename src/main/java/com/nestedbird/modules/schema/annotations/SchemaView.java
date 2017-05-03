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

package com.nestedbird.modules.schema.annotations;

import com.nestedbird.modules.schema.Schema;

import java.lang.annotation.*;

/**
 * The SchemaView is responsible for specifying how a specific Field should be read by the SchemaReader
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SchemaView {
    /**
     * How should the data be parsed, view Schema to see possible options
     *
     * @return the string
     */
    String value() default Schema.TEXT;

    /**
     * Name string.
     *
     * @return the string
     */
    String name() default "";

    /**
     * Type class.
     *
     * @return the class
     */
    Class<?> type() default Object.class;

    /**
     * Visible boolean.
     *
     * @return the boolean
     */
    boolean visible() default true;

    /**
     * Locked boolean.
     *
     * @return is this variable locked, meaning it cannot be changed, null results can still be changed
     */
    boolean locked() default false;

    /**
     * Mappings string [ ].
     *
     * @return the string [ ]
     */
    String[] mappings() default {};
}
