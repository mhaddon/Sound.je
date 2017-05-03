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

package com.nestedbird;

import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * This is the entry point for the webserver
 *
 * @author Michael Haddon
 * @since 23 Feb 2017
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableEmailTools
@PropertySource(value = {
        "classpath:properties/application/application.properties",
        "classpath:properties/application/application.override.properties",
        "classpath:properties/application/application-${envTarget:dev}.properties",
        "classpath:properties/application/application-${envTarget:dev}.override.properties",

        "classpath:properties/database/database.properties",
        "classpath:properties/database/database.override.properties",
        "classpath:properties/database/database-${envTarget:dev}.properties",
        "classpath:properties/database/database-${envTarget:dev}.override.properties",

        "classpath:properties/logging/logging.properties",
        "classpath:properties/logging/logging.override.properties",
        "classpath:properties/logging/logging-${envTarget:dev}.properties",
        "classpath:properties/logging/logging-${envTarget:dev}.override.properties",

        "classpath:properties/redis/redis.properties",
        "classpath:properties/redis/redis.override.properties",
        "classpath:properties/redis/redis-${envTarget:dev}.properties",
        "classpath:properties/redis/redis-${envTarget:dev}.override.properties",

        "classpath:properties/social/social.properties",
        "classpath:properties/social/social.override.properties",
        "classpath:properties/social/social-${envTarget:dev}.properties",
        "classpath:properties/social/social-${envTarget:dev}.override.properties",

        "classpath:properties/email/email.properties",
        "classpath:properties/email/email.override.properties",
        "classpath:properties/email/email-${envTarget:dev}.properties",
        "classpath:properties/email/email-${envTarget:dev}.override.properties",

        "classpath:properties/login/login.properties",
        "classpath:properties/login/login.override.properties",
        "classpath:properties/login/login-${envTarget:dev}.properties",
        "classpath:properties/login/login-${envTarget:dev}.override.properties",

        "classpath:properties/security/security.properties",
        "classpath:properties/security/security.override.properties",
        "classpath:properties/security/security-${envTarget:dev}.properties",
        "classpath:properties/security/security-${envTarget:dev}.override.properties"
}, ignoreResourceNotFound = true)
@ComponentScan("com.nestedbird")
public class App {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(final String[] args) {
        System.out.println("\n" +
                "    NestedBird  Copyright (C) 2016-2017  Michael Haddon\n" +
                "    This program comes with ABSOLUTELY NO WARRANTY.\n" +
                "    This is free software, and you are welcome to redistribute it\n" +
                "    under certain conditions.\n" +
                "    View LICENSE for more information.\n");

        SpringApplication.run(App.class, args);
    }
}