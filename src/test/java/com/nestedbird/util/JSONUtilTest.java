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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class JSONUtilTest {
    public static class deepMerge {
        @Before
        public void setUp() throws Exception {}

        @Test
        public void Can_Concatenate_Fields() throws JSONException {
            JSONObject object = new JSONObject();
            object.put("a", "a");
            object.put("b", "b");

            JSONObject object2 = new JSONObject();
            object2.put("c", "c");
            object2.put("d", "d");

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("a", "a");
            expectedObject.put("b", "b");
            expectedObject.put("c", "c");
            expectedObject.put("d", "d");

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Can_Concatenate_Fields_With_Null() throws JSONException {
            JSONObject object = new JSONObject();
            object.put("a", "a");
            object.put("b", "b");

            JSONObject object2 = new JSONObject();
            object2.put("c", "c");
            object2.put("d", "d");
            object2.put("e", null);

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("a", "a");
            expectedObject.put("b", "b");
            expectedObject.put("c", "c");
            expectedObject.put("d", "d");
            expectedObject.put("e", null);

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Cannot_Override_Fields_With_Null() throws JSONException {
            JSONObject object = new JSONObject();
            object.put("a", "a");
            object.put("b", "b");
            object.put("e", "NOT NULL");

            JSONObject object2 = new JSONObject();
            object2.put("c", "c");
            object2.put("d", "d");
            object2.put("e", null);

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("a", "a");
            expectedObject.put("b", "b");
            expectedObject.put("c", "c");
            expectedObject.put("d", "d");
            expectedObject.put("e", "NOT NULL");

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Can_Concatenate_And_Overwrite_Fields() throws JSONException {
            JSONObject object = new JSONObject();
            object.put("a", "a");
            object.put("b", "b");

            JSONObject object2 = new JSONObject();
            object2.put("b", "sausage");
            object2.put("c", "c");
            object2.put("d", "d");

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("a", "a");
            expectedObject.put("b", "sausage");
            expectedObject.put("c", "c");
            expectedObject.put("d", "d");

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Can_Concatenate_Empty_Object() throws JSONException {
            JSONObject object = new JSONObject();

            JSONObject object2 = new JSONObject();
            object2.put("b", "sausage");
            object2.put("c", "c");
            object2.put("d", "d");

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("b", "sausage");
            expectedObject.put("c", "c");
            expectedObject.put("d", "d");

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Can_Concatenate_In_Empty_Object() throws JSONException {
            JSONObject object = new JSONObject();
            object.put("a", "a");
            object.put("b", "b");

            JSONObject object2 = new JSONObject();

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("a", "a");
            expectedObject.put("b", "b");

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Can_Concatenate_Object() throws JSONException {
            JSONObject object = new JSONObject();
            object.put("a", "a");
            object.put("b", "b");

            JSONObject nestedObject2 = new JSONObject();
            nestedObject2.put("test", "test");

            JSONObject object2 = new JSONObject();
            object2.put("object", nestedObject2);

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONObject expectedNestedObject = new JSONObject();
            expectedNestedObject.put("test", "test");


            JSONObject expectedObject = new JSONObject();
            expectedObject.put("a", "a");
            expectedObject.put("b", "b");
            expectedObject.put("object", expectedNestedObject);

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Can_Concatenate_Object_Into_Object() throws JSONException {
            JSONObject nestedObject = new JSONObject();
            nestedObject.put("sausage", "ham");

            JSONObject object = new JSONObject();
            object.put("a", "a");
            object.put("b", "b");
            object.put("object", nestedObject);

            JSONObject nestedObject2 = new JSONObject();
            nestedObject2.put("test", "test");

            JSONObject object2 = new JSONObject();
            object2.put("object", nestedObject2);

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONObject expectedNestedObject = new JSONObject();
            expectedNestedObject.put("sausage", "ham");
            expectedNestedObject.put("test", "test");

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("a", "a");
            expectedObject.put("b", "b");
            expectedObject.put("object", expectedNestedObject);

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Can_Override_Object_Value() throws JSONException {
            JSONObject nestedObject = new JSONObject();
            nestedObject.put("test", "ham");

            JSONObject object = new JSONObject();
            object.put("a", "a");
            object.put("b", "b");
            object.put("object", nestedObject);

            JSONObject nestedObject2 = new JSONObject();
            nestedObject2.put("test", "test");

            JSONObject object2 = new JSONObject();
            object2.put("object", nestedObject2);

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONObject expectedNestedObject = new JSONObject();
            expectedNestedObject.put("test", "test");

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("a", "a");
            expectedObject.put("b", "b");
            expectedObject.put("object", expectedNestedObject);

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Can_Override_Key_Type_Clash() throws JSONException {
            JSONObject object = new JSONObject();
            object.put("a", "a");
            object.put("b", "b");
            object.put("object", "c");

            JSONObject nestedObject2 = new JSONObject();
            nestedObject2.put("test", "test");

            JSONObject object2 = new JSONObject();
            object2.put("object", nestedObject2);

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONObject expectedNestedObject = new JSONObject();
            expectedNestedObject.put("test", "test");

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("a", "a");
            expectedObject.put("b", "b");
            expectedObject.put("object", expectedNestedObject);

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Can_Merge_Arrays() throws JSONException {
            JSONArray array = new JSONArray();
            array.put("a");
            array.put("b");

            JSONObject object = new JSONObject();
            object.put("array", array);

            JSONArray array2 = new JSONArray();
            array2.put(null);
            array2.put("c");

            JSONObject object2 = new JSONObject();
            object2.put("array", array2);

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONArray expectedArray = new JSONArray();
            expectedArray.put("a");
            expectedArray.put("c");

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("array", expectedArray);

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Can_Merge_Larger_Array_Into_Smaller() throws JSONException {
            JSONArray array = new JSONArray();
            array.put("a");
            array.put("b");

            JSONObject object = new JSONObject();
            object.put("array", array);

            JSONArray array2 = new JSONArray();
            array2.put(null);
            array2.put("c");
            array2.put("v");

            JSONObject object2 = new JSONObject();
            object2.put("array", array2);

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONArray expectedArray = new JSONArray();
            expectedArray.put("a");
            expectedArray.put("c");
            expectedArray.put("v");

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("array", expectedArray);

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Can_Merge_Array_Over_Null() throws JSONException {
            JSONArray array = new JSONArray();
            array.put("a");
            array.put(null);

            JSONObject object = new JSONObject();
            object.put("array", array);

            JSONArray array2 = new JSONArray();
            array2.put(null);
            array2.put("c");

            JSONObject object2 = new JSONObject();
            object2.put("array", array2);

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONArray expectedArray = new JSONArray();
            expectedArray.put("a");
            expectedArray.put("c");

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("array", expectedArray);

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Can_Merge_Array_With_Lingering_Null() throws JSONException {
            JSONArray array = new JSONArray();
            array.put("a");
            array.put("b");

            JSONObject object = new JSONObject();
            object.put("array", array);

            JSONArray array2 = new JSONArray();
            array2.put(null);
            array2.put("c");
            array2.put(null);

            JSONObject object2 = new JSONObject();
            object2.put("array", array2);

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONArray expectedArray = new JSONArray();
            expectedArray.put("a");
            expectedArray.put("c");
            expectedArray.put(null);

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("array", expectedArray);

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Can_Merge_Array_Of_Objects() throws JSONException {
            JSONObject nestedObject = new JSONObject();
            nestedObject.put("a", "a");

            JSONArray array = new JSONArray();
            array.put(nestedObject);

            JSONObject object = new JSONObject();
            object.put("array", array);

            JSONObject nestedObject2 = new JSONObject();
            nestedObject2.put("b", "b");

            JSONArray array2 = new JSONArray();
            array2.put(nestedObject2);

            JSONObject object2 = new JSONObject();
            object2.put("array", array2);

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONObject expectedNestedObject = new JSONObject();
            expectedNestedObject.put("a", "a");
            expectedNestedObject.put("b", "b");

            JSONArray expectedArray = new JSONArray();
            expectedArray.put(expectedNestedObject);

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("array", expectedArray);

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }

        @Test
        public void Can_Merge_Array_With_Type_Clash() throws JSONException {
            JSONArray array = new JSONArray();
            array.put("a");

            JSONObject object = new JSONObject();
            object.put("array", array);

            JSONObject nestedObject2 = new JSONObject();
            nestedObject2.put("b", "b");

            JSONArray array2 = new JSONArray();
            array2.put(nestedObject2);

            JSONObject object2 = new JSONObject();
            object2.put("array", array2);

            JSONObject newObject = JSONUtil.deepMerge(object, object2);

            JSONObject expectedNestedObject = new JSONObject();
            expectedNestedObject.put("b", "b");

            JSONArray expectedArray = new JSONArray();
            expectedArray.put(expectedNestedObject);

            JSONObject expectedObject = new JSONObject();
            expectedObject.put("array", expectedArray);

            assertEquals("string must match", expectedObject.toString(), newObject.toString());
        }
    }
}
