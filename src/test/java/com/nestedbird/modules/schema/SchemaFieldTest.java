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

import com.nestedbird.modules.schema.annotations.SchemaView;
import com.nestedbird.testcategory.Fast;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class SchemaFieldTest {

    @Category(Fast.class)
    public static class isArray {
        private SchemaField testClass_testSet;

        @Before
        public void setUp() throws Exception {
            testClass_testSet = SchemaField.builder().field(testClass.class.getField("testSet")).build();
        }

        @Test
        public void Should_Detect_Field_AsArray() {
            Boolean expected = true;
            Boolean compared = testClass_testSet.isArray();
            assertEquals("field must be detected as an array", expected, compared);
        }
    }

    @Category(Fast.class)
    public static class getFieldName {
        private SchemaField testClass_testSet;
        private SchemaField testClass_testOverridenName;
        private SchemaField testOverridenNameByEmptyString;

        @Before
        public void setUp() throws Exception {
            testClass_testSet = SchemaField.builder().field(testClass.class.getField("testSet")).build();
            testClass_testOverridenName = SchemaField.builder().field(testClass.class.getField("testOverridenName")).build();
            testOverridenNameByEmptyString = SchemaField.builder().field(testClass.class.getField("testOverridenNameByEmptyString")).build();
        }

        @Test
        public void Should_Match_Field_Name() {
            String expected = "testSet";
            String compared = testClass_testSet.getName();
            assertEquals("field names must match", expected, compared);
        }

        @Test
        public void Should_Match_Field_Name_By_Annotation() {
            String expected = "overridden";
            String compared = testClass_testOverridenName.getName();
            assertEquals("field names must match", expected, compared);
        }

        @Test
        public void Should_Match_Field_Name_Not_Empty_String() {
            String expected = "testOverridenNameByEmptyString";
            String compared = testOverridenNameByEmptyString.getName();
            assertEquals("field names must match", expected, compared);
        }
    }

    @Category(Fast.class)
    public static class getTypeOverride {
        private SchemaField testClass_testSet;
        private SchemaField testClass_testOverridenName;
        private SchemaField testOverridenNameByEmptyString;
        private SchemaField testOverriddenType;

        @Before
        public void setUp() throws Exception {
            testClass_testSet = SchemaField.builder().field(testClass.class.getField("testSet")).build();
            testClass_testOverridenName = SchemaField.builder().field(testClass.class.getField("testOverridenName")).build();
            testOverridenNameByEmptyString = SchemaField.builder().field(testClass.class.getField("testOverridenNameByEmptyString")).build();
            testOverriddenType = SchemaField.builder().field(testClass.class.getField("testOverriddenType")).build();
        }

        @Test
        public void Should_Override_Type() {
            Class<?> expected = String.class;
            Class<?> compared = testOverriddenType.getTypeOverride();
            assertEquals("field names must match", expected, compared);
        }

        @Test
        public void Should_Not_Override_Type() {
            Class<?> expected = String.class;
            Class<?> compared = testClass_testOverridenName.getTypeOverride();
            assertEquals("field names must match", expected, compared);
        }
    }

    @Category(Fast.class)
    public class testClass {
        @SchemaView(value = "Array", type = String.class)
        public Set<String> testSet = new HashSet<>(0);

        @SchemaView(name = "overridden")
        public String testOverridenName;

        @SchemaView(name = "")
        public String testOverridenNameByEmptyString;

        @SchemaView(type = String.class)
        public Integer testOverriddenType;
    }
}