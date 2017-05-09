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

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Enclosed.class)
public class PatternMatcherTest {
    private static Pattern uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f‌​]{4}-[0-9a-f]{12}$");
    private static String validPattern = "5aa5f535-da28-42bf-8d85-ac58d81b1a1e";

    public static class matches {
        @Before
        public void setUp() throws Exception {
        }

        @Test
        public void Null_Query_Equals_False() {
            Boolean expected = false;
            Boolean compared = PatternMatcher.of(uuidPattern, null).doesMatch();

            assertEquals("values must match", expected, compared);
        }

        @Test
        public void Invalid_Match_Equals_False() {
            Boolean expected = false;
            Boolean compared = PatternMatcher.of(uuidPattern, "asd").doesMatch();

            assertEquals("values must match", expected, compared);
        }

        @Test
        public void Valid_Match_Equals_True() {
            Boolean expected = true;
            Boolean compared = PatternMatcher.of(uuidPattern, validPattern).doesMatch();

            assertEquals("values must match", expected, compared);
        }

        @Test(expected = NullPointerException.class)
        public void Null_Pattern_Throws_Exception() {
            PatternMatcher.of(null, "test");
        }
    }

    public static class then {
        @Before
        public void setUp() throws Exception {
        }

        @Test
        public void Properly_Runs_Then_Callback() {
            Boolean expected = true;
            Mutable<Boolean> compared = Mutable.of(false);

            PatternMatcher.of(uuidPattern, validPattern)
                    .then(matches -> compared.mutate(true));

            assertEquals("values must match", expected, compared.get());
        }

        @Test
        public void Properly_Runs_Then_Callback_Without_Running_Otherwise() {
            Boolean expected = true;
            Mutable<Boolean> compared = Mutable.of(false);

            PatternMatcher.of(uuidPattern, validPattern)
                    .then(matches -> compared.mutate(true))
                    .otherwise(matches -> fail());

            assertEquals("values must match", expected, compared.get());
        }
    }

    public static class otherwise {
        @Before
        public void setUp() throws Exception {
        }

        @Test
        public void Properly_Runs_Then_Callback() {
            Boolean expected = true;
            Mutable<Boolean> compared = Mutable.of(false);

            PatternMatcher.of(uuidPattern, "sausage")
                    .otherwise(matches -> compared.mutate(true));

            assertEquals("values must match", expected, compared.get());
        }

        @Test
        public void Properly_Runs_Then_Callback_Without_Running_Then() {
            Boolean expected = true;
            Mutable<Boolean> compared = Mutable.of(false);

            PatternMatcher.of(uuidPattern, "sausage")
                    .then(matches -> fail())
                    .otherwise(matches -> compared.mutate(true));

            assertEquals("values must match", expected, compared.get());
        }
    }

}
