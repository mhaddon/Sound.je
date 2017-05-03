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

package com.nestedbird.modules.facebookreader;

import com.nestedbird.config.SocialConfigSettings;
import com.nestedbird.jackson.facebook.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * This class is responsible for requesting and reading data from facebooks graph API
 */
@Component
@Slf4j
public class FacebookReader {
    /**
     * Social media settings information
     */
    private final SocialConfigSettings socialConfigSettings;

    /**
     * Instantiates a new Facebook reader.
     *
     * @param socialConfigSettings the social config settings
     */
    @Autowired
    public FacebookReader(final SocialConfigSettings socialConfigSettings) {
        this.socialConfigSettings = socialConfigSettings;
    }

    /**
     * Gets id from url.
     *
     * @param url the url
     * @return the id from url
     */
    public String getIdFromUrl(final String url) {
        final FacebookId facebookId = request(encodeUrl(url), FacebookId.class);


        return Optional.ofNullable(facebookId)
                .map(FacebookId::getId)
                .orElse(null);
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
            logger.info("[FacebookReader] [request] Failure To Retrieve Facebook Resource (" + url + ")", err);
        }

        return deconstructedResponse;
    }

    private String encodeUrl(final String url) {
        return generateRequestUrl(StringEscapeUtils.escapeHtml4(url));
    }

    /**
     * Generates the URL to request the data from facebook with
     *
     * @param id the identifier of the resource
     * @return the url
     */
    private String generateRequestUrl(final String id) {
        return String.format(
                "https://graph.facebook.com/?id=%s&access_token=%s",
                id,
                socialConfigSettings.getFbAccessToken()
        );
    }

    /**
     * Request video facebook video.
     *
     * @param id the id
     * @return the facebook video
     */
    public FacebookVideo requestVideo(final String id) {
        final String url = generateRequestUrl(
                id,
                new String[]{
                        "id",
                        "created_time",
                        "from",
                        "icon",
                        "title",
                        "source",
                        "permalink_url",
                        "picture",
                        "likes.summary(true)",
                        "comments.summary(true)"
                }
        );
        return request(url, FacebookVideo.class);
    }

    /**
     * Generates the URL to request the data from facebook with
     *
     * @param id     the identifier of the resource
     * @param fields what info are we requesting
     * @return the url
     */
    private String generateRequestUrl(final String id, final String[] fields) {
        return String.format(
                "https://graph.facebook.com/%s/?fields=%s&access_token=%s&limit=60",
                id,
                String.join(",", fields),
                socialConfigSettings.getFbAccessToken()
        );
    }

    /**
     * Request a pages posts
     *
     * @param id Facebook Page ID
     * @return the facebook posts
     */
    public FacebookPosts requestPagePosts(final String id) {
        final String url = generateRequestUrl(
                id,
                "Posts",
                new String[]{
                        "story",
                        "message",
                        "link",
                        "place",
                        "created_time"
                }
        );
        return request(url, FacebookPosts.class);
    }

    /**
     * Generates the URL to request the data from facebook with
     *
     * @param id         the identifier of the resource
     * @param nestedItem a child identifier
     * @param fields     what info are we requesing
     * @return the url
     */
    private String generateRequestUrl(final String id, final String nestedItem, final String[] fields) {
        return String.format(
                "https://graph.facebook.com/%s/%s/?fields=%s&access_token=%s&limit=60",
                id,
                nestedItem,
                String.join(",", fields),
                socialConfigSettings.getFbAccessToken()
        );
    }

    /**
     * Request place facebook place.
     *
     * @param id the id
     * @return the facebook place
     */
    public FacebookPlace requestPlace(final String id) {
        final String url = generateRequestUrl(
                id,
                new String[]{
                        "id",
                        "name",
                        "cover",
                        "picture.type(large)",
                        "location",
                        "about"
                }
        );
        return request(url, FacebookPlace.class);
    }

    /**
     * Request a facebook event
     *
     * @param id the Event id
     * @return the facebook event
     */
    public FacebookEvent requestEvent(final String id) {
        final String url = generateRequestUrl(
                id,
                new String[]{
                        "id",
                        "name",
                        "description",
                        "place",
                        "timezone",
                        "start_time",
                        "end_time",
                        "cover",
                        "picture",
                        "updated_time",
                        "owner",
                        "is_page_owned"
                }
        );
        return request(url, FacebookEvent.class);
    }

    /**
     * Request a pages events
     *
     * @param id page id
     * @return the facebook events
     */
    public FacebookEvents requestPageEvents(final String id) {
        final String url = generateRequestUrl(
                id,
                "Events",
                new String[]{
                        "id",
                        "name",
                        "description",
                        "place",
                        "timezone",
                        "start_time",
                        "end_time",
                        "cover",
                        "picture",
                        "updated_time",
                        "owner",
                        "is_page_owned"
                }
        );
        return request(url, FacebookEvents.class);
    }

    /**
     * Request facebook page
     *
     * @param id page id
     * @return the facebook page
     */
    public FacebookPage requestPage(final String id) {
        final String url = generateRequestUrl(
                id,
                new String[]{
                        "cover",
                        "name",
                        "website",
                        "link",
                        "id",
                        "about",
                        "description",
                        "bio"
                }
        );
        return request(url, FacebookPage.class);
    }
}
