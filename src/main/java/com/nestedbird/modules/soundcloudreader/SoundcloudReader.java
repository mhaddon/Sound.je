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

package com.nestedbird.modules.soundcloudreader;

import com.nestedbird.config.SocialConfigSettings;
import com.nestedbird.jackson.soundcloud.SoundcloudSong;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * The type Soundcloud reader.
 */
@Component
@Slf4j
public class SoundcloudReader {
    /**
     * Social media settings information
     */
    private final SocialConfigSettings socialConfigSettings;

    /**
     * Instantiates a new Soundcloud reader.
     *
     * @param socialConfigSettings the social config settings
     */
    @Autowired
    public SoundcloudReader(final SocialConfigSettings socialConfigSettings) {
        this.socialConfigSettings = socialConfigSettings;
    }

    /**
     * Request a songs information
     *
     * @param url URL of Entity
     * @return the soundcloud song
     */
    public SoundcloudSong requestSongDataFromUrl(final String url) {
        final String requestUrl = generateRequestUrl(url);
        return request(requestUrl, SoundcloudSong.class);
    }

    private String generateRequestUrl(final String url) {
        return String.format(
                "https://api.soundcloud.com/resolve.json?url=%s&client_id=%s",
                StringEscapeUtils.escapeHtml4(url),
                socialConfigSettings.getScClientId()
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
            logger.info("[SoundcloudReader] [request] Failure To Retrieve SoundCloud Resource (" + url + ")", err);
        }

        return deconstructedResponse;
    }

    /**
     * Request a songs information
     *
     * @param id Id of Entity
     * @return the soundcloud song
     */
    public SoundcloudSong requestSongData(final String id) {
        final String requestUrl = generateRequestUrlForSongId(id);
        return request(requestUrl, SoundcloudSong.class);
    }

    private String generateRequestUrlForSongId(final String id) {
        return String.format(
                "https://api.soundcloud.com/tracks/%s.json?client_id=%s",
                id,
                socialConfigSettings.getScClientId()
        );
    }
}
