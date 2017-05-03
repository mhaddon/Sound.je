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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * The SchemaReader class parses a class file and turns it into a List of SchemaElements.
 */
public class SchemaReader {

    /**
     * Converts fields to SchemaElements
     *
     * @param clazz Class we want to scan
     * @return List of SchemaElements
     */
    public static List<SchemaElement> read(final Class clazz) {
        final List<SchemaElement> schemaElementList = new ArrayList<>();

        /*
         * Loop over all fields, and if it is a valid item,
         * then add its information to the stored list of fields.
         */
        Class currentClass = clazz;
        do {
            final List<SchemaElement> classFields = new ArrayList<>();
            for (final Field field : currentClass.getDeclaredFields()) {
                final SchemaField fieldInfo = SchemaField.builder().field(field).build();

                if (fieldInfo.isVisible()) {
                    classFields.add(createSchemaElement(fieldInfo));
                }
            }

            schemaElementList.addAll(0, classFields);
            currentClass = currentClass.getSuperclass();
        } while (currentClass.getSuperclass() != null);


        return schemaElementList;
    }

    /**
     * Creates a new SchemaElement from the information in a SchemaField
     *
     * @param fieldInfo Information of the SchemaField
     * @return new SchemaElement
     */
    private static SchemaElement createSchemaElement(final SchemaField fieldInfo) {
        final SchemaElement schemaElement = new SchemaElement(
                fieldInfo.getTypeOverride().getSimpleName(),
                fieldInfo.getName(),
                fieldInfo.getView(),
                fieldInfo.getValidationAnnotations(),
                fieldInfo.isEnabled(),
                fieldInfo.getMappings());

        if (fieldInfo.isArray()) {
            schemaElement.setAdditionalSchemaData(SchemaReader.read(fieldInfo.getTypeOverride()));
        } else if (fieldInfo.isEnum()) {
            @SuppressWarnings("unchecked")
            SchemaEnum enumeration = SchemaEnum.of((Class<? extends Enum>) fieldInfo.getTypeOverride());
            schemaElement.setEnumDetails(enumeration.getOptions());
        }

        return schemaElement;
    }

    private SchemaReader() {
        throw new IllegalAccessError("Utility Class");
    }
}
