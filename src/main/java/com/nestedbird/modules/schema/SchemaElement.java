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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This POJO contains the data about a schema element
 */
public class SchemaElement implements Serializable {
    /**
     * The Schema Elements type
     */
    private final String type;

    /**
     * The Schema Elements name
     */
    private final String name;

    /**
     * How the Schema Element will be displayed in the gui?
     */
    private final String view;

    /**
     * What validations does the Schema Element have?
     */
    private final Map<String, Object> validations;

    /**
     * Is the Schema Element enabled
     */
    private final Boolean enabled;

    /**
     * Extra enum details
     */
    private final List<String> enumDetails;

    /**
     * Does this Schema Element have additional data associated with it
     * For example, if its an array it might have a schema for its array elements too
     */
    private final List<SchemaElement> additionalSchemaData;

    /**
     * What default variable mappings does it have
     * This lets you set child objects in arrays to equal a fields value by default
     */
    private final String[] mappings;

    /**
     * Instantiates a new Schema element.
     *
     * @param type        the type
     * @param name        the name
     * @param view        the view
     * @param validations the validations
     * @param enabled     the enabled
     * @param mappings    the mappings
     */
    public SchemaElement(String type, String name, String view, Map<String, Object> validations, Boolean enabled, String[] mappings) {
        this.type = type;
        this.name = name;
        this.view = view;
        this.validations = validations;
        this.enabled = enabled;
        this.mappings = mappings;
        this.additionalSchemaData = new ArrayList<>();
        this.enumDetails = new ArrayList<>();
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets validations.
     *
     * @return the validations
     */
    public Map<String, Object> getValidations() {
        return validations;
    }

    /**
     * Gets view.
     *
     * @return the view
     */
    public String getView() {
        return view;
    }

    /**
     * Gets enabled.
     *
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * Get mappings string [ ].
     *
     * @return the string [ ]
     */
    public String[] getMappings() {
        return mappings;
    }

    /**
     * Gets additional schema data.
     *
     * @return the additional schema data
     */
    public List<SchemaElement> getAdditionalSchemaData() {
        return additionalSchemaData;
    }

    /**
     * Sets additional schema data.
     *
     * @param additionalSchemaData the additional schema data
     */
    public void setAdditionalSchemaData(List<SchemaElement> additionalSchemaData) {
        this.additionalSchemaData.clear();
        this.additionalSchemaData.addAll(additionalSchemaData);
    }

    /**
     * Gets enum details.
     *
     * @return the enum details
     */
    public List<String> getEnumDetails() {
        return enumDetails;
    }

    /**
     * Sets enum details.
     *
     * @param enumDetails the enum details
     */
    public void setEnumDetails(List<String> enumDetails) {
        this.enumDetails.clear();
        this.enumDetails.addAll(enumDetails);
    }

    @Override
    public String toString() {
        return "SchemaElement{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", view='" + view + '\'' +
                ", validations=" + validations +
                ", enabled=" + enabled +
                '}';
    }
}
