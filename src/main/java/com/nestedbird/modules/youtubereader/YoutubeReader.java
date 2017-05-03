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

package com.nestedbird.modules.youtubereader;

import com.nestedbird.config.SocialConfigSettings;
import com.nestedbird.jackson.youtube.YoutubeListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * The type Youtube reader.
 */
@Component
@Slf4j
public class YoutubeReader {
    /**
     * Social media settings information
     */
    private final SocialConfigSettings socialConfigSettings;

    /**
     * Instantiates a new Youtube reader.
     *
     * @param socialConfigSettings the social config settings
     */
    @Autowired
    public YoutubeReader(final SocialConfigSettings socialConfigSettings) {
        this.socialConfigSettings = socialConfigSettings;
    }

    /**
     * Request a video information
     *
     * @param id ID of Entity
     * @return the youtube list song
     */
    public YoutubeListResponse requestVideoData(final String id) {
        final String requestUrl = generateRequestUrl(id);
        return request(requestUrl, YoutubeListResponse.class);
    }

    private String generateRequestUrl(final String id) {
        return String.format(
                "https://www.googleapis.com/youtube/v3/videos?id=%s&key=%s&part=snippet,contentDetails,statistics,status",
                id,
                socialConfigSettings.getYtKey()
        );
    }

    /**
     * This is the method that actually makes the http request
     *
     * @param url              request url
     * @param deconstructClass class of request object
     * @param <T>              type of request object this is
     * @return request object
     */
    private <T> T request(final String url, final Class<T> deconstructClass) {
        final RestTemplate restTemplate = new RestTemplate();
        T deconstructedResponse = null;

        try {
            deconstructedResponse = restTemplate.getForObject(url, deconstructClass);
        } catch (HttpClientErrorException err) {
            logger.info("[YoutubeReader] [request] Failure To Retrieve YouTube Resource (" + url + ")", err);
        }

        return deconstructedResponse;
    }
}
