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

package com.nestedbird.models.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * Provides functionality for every object without tying a specific purpose to the object.
 */
@Slf4j
public abstract class DataObject implements Serializable {
    @Override
    public String toString() {
        return toJSON();
    }

    /**
     * Convert this object to json
     *
     * @return the json
     */
    public String toJSON() {
        String returnVar = "";
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Hibernate5Module());

        try {
            returnVar = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            logger.info("[DataObject] [toJson] Convert Object To JSON Failure", ex);
        }

        return returnVar;
    }
}
