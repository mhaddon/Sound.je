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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class MutableTest {

    public static class mutate {
        @Before
        public void setUp() throws Exception {}

        @Test
        public void Can_Mutate_String_From_Null() {
            String expected = "a";

            Mutable<String> mutable = Mutable.of(null);
            mutable.mutate("a");
            String compared = mutable.get();

            assertEquals("string must match", expected, compared);
        }

        @Test
        public void Can_Mutate_String() {
            String expected = "a";

            Mutable<String> mutable = Mutable.of("b");
            mutable.mutate("a");
            String compared = mutable.get();

            assertEquals("string must match", expected, compared);
        }

        @Test
        public void Can_Mutate_Numbers() {
            Integer expected = 1;

            Mutable<Integer> mutable = Mutable.of(2);
            mutable.mutate(1);
            Integer compared = mutable.get();

            assertEquals("string must match", expected, compared);
        }

        @Test
        public void Can_Mutate_Array() {
            String[] expected = new String[]{"test"};

            Mutable<String[]> mutable = Mutable.of(new String[]{});
            mutable.mutate(new String[]{"test"});
            String[] compared = mutable.get();

            assertArrayEquals("string must match", expected, compared);
        }
    }
}