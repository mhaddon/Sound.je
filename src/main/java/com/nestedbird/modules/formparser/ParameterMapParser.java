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

import com.nestedbird.util.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The ParameterMapParser processes the payload from a form submission.
 * It converts the string array to a JSONObject with all children objects/arrays nested.
 * <p>
 * The instanced version of ParameterMapVersion serves as offering an abstraction over JSONObject
 */
@Slf4j
public class ParameterMapParser {

    /**
     * Converts a parameter map to a JSONObject
     * The return type is a ParameterMapParser which has an abstraction over the normal JSONObject
     *
     * @param parameterMap the parameter map
     * @return new parameter map parser
     */
    public static ParameterMapParser parse(final Map<String, String[]> parameterMap) {
        final JSONObject data = parameterMap.entrySet().stream()
                .parallel()
                .reduce(
                        new JSONObject(),
                        ParameterMapParser::parseAccumulator,
                        ParameterMapParser::parseCombiner
                );

        return new ParameterMapParser(data);
    }

    /**
     * Adds new parsed data to our existing collection of parsed data
     *
     * @param object existing data that will lose on conflicts
     * @param entry  new data from form that will be processed and will win on conflicts
     * @return new object
     */
    private static JSONObject parseAccumulator(final JSONObject object, final Entry<String, String[]> entry) {
        return parseCombiner(object, createObjectWithKey(entry));
    }

    /**
     * Combines two JSONObjects together
     *
     * @param object  object that will lose on conflicts
     * @param object2 object that will win on conflicts
     * @return new object
     */
    private static JSONObject parseCombiner(final JSONObject object, final JSONObject object2) {
        try {
            return JSONUtil.deepMerge(
                    object,
                    object2
            );
        } catch (JSONException e) {
            logger.info("[ParameterMapParser] [parseCombiner] Failure To Merge JSON", e);
        }
        return object;
    }

    /**
     * Converts the form submission result to a new JSONObject
     *
     * @param entry entry from the form submission
     * @return new json object
     */
    private static JSONObject createObjectWithKey(final Entry<String, String[]> entry) {
        final FormField formField = FormField.parse(entry.getKey());
        final String value = getEntryValue(entry);

        JSONObject newObject = new JSONObject();

        try {
            newObject = createNewObject(value, formField);
        } catch (JSONException e) {
            logger.info("[ParameterMapParser] [createObjectWithKey] Failure To Process JSON", e);
        }

        return newObject;
    }

    /**
     * Spring stores the entry values in a map, as each entry value can have several options, if the same html
     * name has several inputs.
     * We only want the last value we recieve, and we ignore the rest.
     *
     * @param entries - the map of entries
     * @return - The Single Remaining Entry
     */
    private static String getEntryValue(final Entry<String, String[]> entries) {
        return entries.getValue()[entries.getValue().length - 1];
    }

    /**
     * Create new JSONObject out of the result from a FormField
     *
     * @param value     The value of the form entry
     * @param formField The parsed schema of a form entry key
     * @return new JSONObject
     * @throws JSONException the json exception
     */
    private static JSONObject createNewObject(final String value, final FormField formField) throws JSONException {
        final JSONObject newObject = new JSONObject();

        if (formField.getType().equals(FormFieldType.ARRAY)) {
            newObject.put(formField.getName(), convertValueToArray(value));
        } else if (formField.getType().equals(FormFieldType.ARRAYOFOBJECT)) {
            newObject.put(formField.getName(), convertValueToArrayOfObject(value, formField));
        } else if (formField.getType().equals(FormFieldType.OBJECT)) {
            newObject.put(formField.getName(), convertValueToObject(value, formField));
        } else if (formField.getType().equals(FormFieldType.STRING)) {
            newObject.put(formField.getName(), value);
        }

        return newObject;
    }

    /**
     * Convert value to array
     *
     * @param value The value of the form entity
     * @return the json array
     */
    private static JSONArray convertValueToArray(final String value) {
        final JSONArray childArray = new JSONArray();
        childArray.put(value);
        return childArray;
    }

    /**
     * Convert value to array of objects.
     *
     * @param value     The value of the form entity
     * @param formField The parsed schema of a form entry key
     * @return the json array
     * @throws JSONException the json exception
     */
    private static JSONArray convertValueToArrayOfObject(final String value,
                                                         final FormField formField) throws JSONException {
        final JSONObject childObject = new JSONObject();
        childObject.put(formField.getChildName(), value);

        final JSONArray childArray = new JSONArray();
        childArray.put(childObject);

        return childArray;
    }

    /**
     * Convert value to json object.
     *
     * @param value     The value of the form entity
     * @param formField The parsed schema of a form entry key
     * @return the json object
     * @throws JSONException the json exception
     */
    private static JSONObject convertValueToObject(final String value, final FormField formField) throws JSONException {
        final JSONObject childObject = new JSONObject();
        childObject.put(formField.getChildName(), value);
        return childObject;
    }

    /**
     * Parsed form payload
     */
    private final JSONObject data;

    /**
     * Instantiates a new Parameter map parser.
     */
    public ParameterMapParser() {
        data = new JSONObject();
    }

    /**
     * Instantiates a new Parameter map parser.
     *
     * @param data the initial data
     */
    public ParameterMapParser(final JSONObject data) {
        this.data = data;
    }

    /**
     * Retrieve the value of a key from data
     *
     * @param key The key
     * @return the value
     */
    public Object get(final String key) {
        try {
            return data.get(key);
        } catch (JSONException e) {
            logger.info("[ParameterMapParser] [createObjectWithKey] Failure To Retrieve JSONObject Key", e);
        }
        return null;
    }

    /**
     * Checks if a key exists in the data
     *
     * @param key the key
     * @return if it exists
     */
    public Boolean has(final String key) {
        return data.has(key);
    }

    /**
     * Loops over shallowly over all the keys in the data
     *
     * @param consumer the consumer
     */
    public void loopKeys(final Consumer<String> consumer) {
        loopData((key, value) -> consumer.accept(key));
    }

    /**
     * Loops over shallowly over all the entries in the data
     *
     * @param consumer the consumer
     */
    public void loopData(final BiConsumer<String, Object> consumer) {
        final Iterator keys = data.keys();
        while (keys.hasNext()) {
            final String key = keys.next().toString();
            try {
                consumer.accept(key, data.get(key));
            } catch (JSONException e) {
                logger.info("[ParameterMapParser] [createObjectWithKey] Failure To Retrieve JSONObject Key", e);
            }
        }
    }

    /**
     * Loops over shallowly over all the entries in the data
     *
     * @param consumer the consumer
     */
    public void loopData(final Consumer<Object> consumer) {
        loopData((key, value) -> consumer.accept(value));
    }
}
