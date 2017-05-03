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

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import javax.validation.constraints.Null;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class UUIDConverterTest {

    public static class toBase64 {
        @Before
        public void setUp() throws Exception {}

        @Test
        public void Should_Match_When_Encoded() {
            String expected = "WqX1NdooQr-NhaxY2BsaHg";
            String compared = UUIDConverter.toBase64("5aa5f535-da28-42bf-8d85-ac58d81b1a1e");
            assertEquals("string must match", expected, compared);
        }

        @Test(expected=IllegalArgumentException.class)
        public void Should_ThrowExeption_When_UUIDIsInvalid() {
            UUIDConverter.toBase64("sausages");
        }

        @Test(expected=NullPointerException.class)
        public void Should_ThrowExeption_When_UUIDIsNull() {
            UUIDConverter.toBase64(null);
        }
    }


    public static class fromBase64 {
        @Before
        public void setUp() throws Exception {}

        @Test
        public void Should_Match_When_Decoded() {
            String expected = "5aa5f535-da28-42bf-8d85-ac58d81b1a1e";
            String compared = UUIDConverter.fromBase64("WqX1NdooQr-NhaxY2BsaHg");
            assertEquals("string must match", expected, compared);
        }

        @Test(expected=NullPointerException.class)
        public void Should_ThrowExeption_When_StringIsNull() {
            UUIDConverter.fromBase64(null);
        }

        @Test(expected=IllegalArgumentException.class)
        public void Should_ThrowExeption_When_StringIsWrongLength() {
            UUIDConverter.fromBase64("sausages");
        }
    }

    public static class isUUID {
        @Before
        public void setUp() throws Exception {}

        @Test
        public void Should_Detect_Valid_UUID() {
            Boolean expected = true;
            Boolean compared = UUIDConverter.isUUID("5aa5f535-da28-42bf-8d85-ac58d81b1a1e");
            assertEquals("uuid must be valid", expected, compared);
        }

        @Test
        public void Should_Detect_Invalid_UUID() {
            Boolean expected = false;
            Boolean compared = UUIDConverter.isUUID("5aa5f535-da28-42bf-8d85-ac58d81b1a1");
            assertEquals("uuid must be invalid", expected, compared);
        }

        @Test
        public void Should_Return_False_On_Null() {
            Boolean expected = false;
            Boolean compared = UUIDConverter.isUUID(null);
            assertEquals("uuid must be invalid", expected, compared);
        }
    }
}