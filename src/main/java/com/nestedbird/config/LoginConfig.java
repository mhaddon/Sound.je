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

package com.nestedbird.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class is responsible for configuring the login
 * This includes reading properties from the properties files and creating required beans
 */
@Configuration
public class LoginConfig {
    /**
     * How many failed login attempts per email address are allowed
     */
    private final Integer maxEmailLoginAttempts;

    /**
     * What is the cooldown period that you cannot exceed failed login attempts in
     */
    private final Integer emailAttemptsCooldownInMinutes;

    /**
     * How many failed login attempts per ip address are allowed
     */
    private final Integer maxIPLoginAttempts;

    /**
     * What is the cooldown period that you cannot exceed failed login attempts in
     */
    private final Integer ipAttemptsCooldownInMinutes;

    /**
     * Instantiates a new Login config.
     *
     * @param maxEmailLoginAttempts          the max email login attempts
     * @param emailAttemptsCooldownInMinutes the email attempts cooldown in minutes
     * @param maxIPLoginAttempts             the max ip login attempts
     * @param ipAttemptsCooldownInMinutes    the ip attempts cooldown in minutes
     */
    public LoginConfig(@Value("${login.email.maxattempts}") final Integer maxEmailLoginAttempts,
                       @Value("${login.email.cooldown}") final Integer emailAttemptsCooldownInMinutes,
                       @Value("${login.ip.maxattempts}") final Integer maxIPLoginAttempts,
                       @Value("${login.ip.cooldown}") final Integer ipAttemptsCooldownInMinutes) {
        this.maxEmailLoginAttempts = maxEmailLoginAttempts;
        this.emailAttemptsCooldownInMinutes = emailAttemptsCooldownInMinutes;
        this.maxIPLoginAttempts = maxIPLoginAttempts;
        this.ipAttemptsCooldownInMinutes = ipAttemptsCooldownInMinutes;
    }

    /**
     * Bean containing the immutable login information.
     *
     * @return immutable login information
     */
    @Bean
    public LoginConfigSettings loginConfigSettings() {
        return LoginConfigSettings.builder()
                .maxEmailLoginAttempts(maxEmailLoginAttempts)
                .maxIPLoginAttempts(maxIPLoginAttempts)
                .emailAttemptsCooldownInMinutes(emailAttemptsCooldownInMinutes)
                .ipAttemptsCooldownInMinutes(ipAttemptsCooldownInMinutes)
                .build();
    }
}
