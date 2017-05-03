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

package com.nestedbird.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;

@RunWith(Enclosed.class)
public class LoginTest {
    public static class check {
        @Before
        public void setUp() throws Exception {}

        @Test
        public void Request_Fails_With_No_Data() {
            final String url = "http://localhost:8081/login/check";
            final Response response = RestAssured.given().accept(ContentType.JSON).get(url);

            assertEquals(400, response.statusCode());
        }

        @Test
        public void Request_Succeeds_With_Data() {
            final String url = "http://localhost:8081/login/check";
            final Response response = RestAssured.given()
                    .formParam("email", "test@test.com")
                    .accept(ContentType.JSON).get(url);

            assertEquals(200, response.statusCode());
        }
    }
}
