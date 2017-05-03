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
 * This class is responsible for configuring the server
 * This includes reading properties from the properties files and creating required beans
 */
@Configuration
public class ServerConfig {
    /**
     * The servers external url
     */
    private final String externalUrl;

    /**
     * Instantiates a new Server config.
     *
     * @param externalUrl the external url
     */
    public ServerConfig(@Value("${server.external.url}") final String externalUrl) {
        this.externalUrl = externalUrl;
    }

    /**
     * Bean that contains the server configuration settings
     *
     * @return the server config settings
     */
    @Bean
    public ServerConfigSettings serverConfigSettings() {
        return ServerConfigSettings.builder()
                .externalUrl(externalUrl)
                .build();
    }
}
