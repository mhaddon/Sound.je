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
import org.springframework.stereotype.Component;

/**
 * This class is responsible for configuring the social media
 * This includes reading properties from the properties files and creating required beans
 */
@Component
public class SocialConfig {
    /**
     * Should this application support the ability to automatically post events to facebook
     */
    private final Boolean fbAutoPost;

    /**
     * Whether the application will automatically scan FB
     */
    private final Boolean fbScan;

    /**
     * Facebook API Key
     */
    private final String fbAccessToken;

    /**
     * Youtube API Key
     */
    private final String ytKey;

    /**
     * Soundcloud API Key
     */
    private final String scClientId;

    /**
     * Instantiates a new Social config.
     *
     * @param fbAutoPost    the fb auto post
     * @param fbScan        the fb scan
     * @param fbAccessToken the fb access token
     * @param ytKey         the yt key
     * @param scClientId    the sc client id
     */
    public SocialConfig(@Value("${facebook.auto_post}") final Boolean fbAutoPost,
                        @Value("${facebook.scan}") final Boolean fbScan,
                        @Value("${facebook.access_token}") final String fbAccessToken,
                        @Value("${youtube.key}") final String ytKey,
                        @Value("${soundcloud.client_id}") final String scClientId) {
        this.fbAutoPost = fbAutoPost;
        this.fbScan = fbScan;
        this.fbAccessToken = fbAccessToken;
        this.ytKey = ytKey;
        this.scClientId = scClientId;
    }


    /**
     * Creates new SocialConfig bean
     *
     * @return the social config settings
     */
    @Bean
    public SocialConfigSettings socialConfigSettings() {
        return SocialConfigSettings.builder()
                .fbAutoPost(fbAutoPost)
                .fbScan(fbScan)
                .fbAccessToken(fbAccessToken)
                .ytKey(ytKey)
                .scClientId(scClientId)
                .build();
    }
}
