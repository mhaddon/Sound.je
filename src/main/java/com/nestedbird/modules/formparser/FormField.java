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

package com.nestedbird.modules.formparser;


import com.nestedbird.models.core.DataObject;
import com.nestedbird.util.Mutable;
import com.nestedbird.util.PatternMatcher;
import lombok.*;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * This class processes the name of a Form Field
 * For example, if a form field has the name of "sausage", then it is referencing the field sausage.
 * <p>
 * If a form field has the name "shop.sausage",
 * then its referencing the sausage field inside the shop class
 * <p>
 * If a form field has the name "shop[1].sausage",
 * then its referencing sausage which is a field in an object in the shop array
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FormField extends DataObject implements Serializable {
    /**
     * This Pattern matches an array
     */
    private static final Pattern arrayPattern = Pattern.compile("^([\\w]+)\\[([0-9]+)\\]$");
    /**
     * This Pattern matches an array of objects
     */
    private static final Pattern arrayOfObjectsPattern = Pattern.compile("^([\\w]+)\\[([0-9]+)\\]\\.([\\w]+)$");
    /**
     * This Pattern matches an object
     */
    private static final Pattern objectPattern = Pattern.compile("^([\\w]+)\\.([\\w]+)$");

    /**
     * Parse a form fields name into a FormField
     *
     * @param fieldName the entity name
     * @return the form field
     */
    static FormField parse(final String fieldName) {
        final Mutable<FormField> formField = Mutable.of(FormField.builder().name(fieldName).type(FormFieldType.STRING).build());

        PatternMatcher
                .of(arrayPattern, fieldName)
                .then(formField.mutate(e ->
                        FormField.builder()
                                .name(e.group(1))
                                .index(Integer.parseInt(e.group(2)))
                                .childName(null)
                                .type(FormFieldType.ARRAY)
                                .build()));

        PatternMatcher
                .of(arrayOfObjectsPattern, fieldName)
                .then(formField.mutate(e ->
                        FormField.builder()
                                .name(e.group(1))
                                .index(Integer.parseInt(e.group(2)))
                                .childName(e.group(3))
                                .type(FormFieldType.ARRAYOFOBJECT)
                                .build()));

        PatternMatcher
                .of(objectPattern, fieldName)
                .then(formField.mutate(e ->
                        FormField.builder()
                                .name(e.group(1))
                                .index(null)
                                .childName(e.group(3))
                                .type(FormFieldType.OBJECT)
                                .build()));

        return formField.get();
    }

    /**
     * the name of this form field element
     */
    private final String name;

    /**
     * the array index (if it has one)
     */
    private final Integer index;

    /**
     * The child object name (if it has one)
     */
    private final String childName;

    /**
     * The type of element it is
     */
    private final FormFieldType type;
}