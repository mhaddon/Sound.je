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

package com.nestedbird.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.function.BiConsumer;

/**
 * This utility class provides additional JSONObject/JSONArray functionality
 */
@UtilityClass
@Slf4j
public class JSONUtil {
    /**
     * Merge "source" into "target". If fields have equal name, merge them recursively.
     *
     * @param target On merge conflicts this object loses
     * @param source On merge conflicts this object wins
     * @return the merged object (target).
     * @throws JSONException the json exception
     * @source http ://stackoverflow.com/a/15070484/1507692
     */
    public static JSONObject deepMerge(final JSONObject target, final JSONObject source) throws JSONException {
        final JSONObject newObject = new JSONObject(target.toString());
        final Iterator keys = source.keys();
        while (keys.hasNext()) {
            final String key = keys.next().toString();
            final Object value = source.get(key);

            if (!newObject.has(key)) {
                newObject.put(key, value);
            } else {
                newObject.put(key, merge(newObject.get(key), value));
            }
        }
        return newObject;
    }


    /**
     * merges two JSON values recursively
     *
     * @param target On merge conflicts this object loses
     * @param source On merge conflicts this object wins
     * @return the merged object
     * @throws JSONException the json exception
     */
    private static Object merge(final Object target, final Object source) throws JSONException {
        Object newObject = source;

        if ((target instanceof JSONObject) && (source instanceof JSONObject)) {
            newObject = deepMerge((JSONObject) target, (JSONObject) source);
        } else if ((target instanceof JSONArray) && (source instanceof JSONArray)) {
            newObject = mergeJSONArray((JSONArray) target, (JSONArray) source);
        }

        return newObject;
    }

    /**
     * Merge two JSON arrays
     *
     * @param target On merge conflicts this object loses
     * @param source On merge conflicts this object wins
     * @return the merged json array
     * @throws JSONException the json exception
     */
    private static JSONArray mergeJSONArray(final JSONArray target, final JSONArray source) throws JSONException {
        final JSONArray newArray = padJSONArray(target, source.length());

        for (Integer i = 0; i < source.length(); i++) {
            if (!source.isNull(i)) {
                Object newValue = source.get(i);

                if (!newArray.isNull(i)) {
                    newValue = merge(newArray.get(i), source.get(i));
                }

                newArray.put(i, newValue);
            }
        }

        return newArray;
    }

    /**
     * Pad a JSON array to ensure it has enough indexes to fill the padlength, this is to stop index out of bounds
     *
     * @param array     array to pad
     * @param padLength the pad length
     * @return the json array
     * @throws JSONException the json exception
     */
    private static JSONArray padJSONArray(final JSONArray array, final Integer padLength) throws JSONException {
        final JSONArray newArray = new JSONArray(array.toString());

        for (Integer i = newArray.length(); i < padLength; i++) {
            newArray.put(i, null);
        }

        return newArray;
    }

    /**
     * Loops over all JSONObject data
     *
     * @param data
     * @param consumer
     */
    public void loopObjectData(final JSONObject data, final BiConsumer<String, Object> consumer) {
        final Iterator keys = data.keys();
        while (keys.hasNext()) {
            final String key = keys.next().toString();
            try {
                consumer.accept(key, data.get(key));
            } catch (JSONException e) {
                logger.info("[JSONUtil] [loopData] Failure To Retrieve JSONObject Key", e);
            }
        }
    }

    /**
     * Loops over all JSONArray data
     *
     * @param data
     * @param consumer
     */
    public void loopArrayData(final JSONArray data, final BiConsumer<Integer, Object> consumer) {
        for (int i = 0; i < data.length(); i++) {
            try {
                consumer.accept(i, data.getJSONObject(i));
            } catch (JSONException e) {
                logger.info("[JSONUtil] [loopData] Failure To Retrieve JSONArray Key", e);
            }
        }
    }
}