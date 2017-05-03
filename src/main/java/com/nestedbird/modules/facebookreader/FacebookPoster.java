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

import com.google.common.base.Strings;
import com.nestedbird.config.SocialConfigSettings;
import com.nestedbird.jackson.facebook.FacebookPost;
import com.nestedbird.models.event.Event;
import com.nestedbird.models.location.Location;
import com.nestedbird.models.occurrence.Occurrence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * This is mostly a proof of concept class that... while works.. should be rewritten and improved
 * One problem with it is it seems impossible to tag people/things when sending data through the API
 * even when you follow the rules on how to do it.
 * This seems to be to stop spam or something else, but facebook isnt very transparent about it.
 */
@Configuration
public class FacebookPoster {

    private final SocialConfigSettings socialConfigSettings;

    /**
     * Instantiates a new Facebook poster.
     *
     * @param socialConfigSettings the social config settings
     */
    @Autowired
    public FacebookPoster(final SocialConfigSettings socialConfigSettings) {
        this.socialConfigSettings = socialConfigSettings;
    }

    /**
     * Create occurrence.
     *
     * @param occurrence the occurrence
     * @return the occurrence
     */
    public Occurrence create(Occurrence occurrence) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("message", createMessage(occurrence));
        map.add("link", occurrence.getUrl());
        map.add("place", occurrence.getEvent().flatMap(Event::getLocation).map(Location::getFacebookId).map(String::valueOf).orElse(""));
        map.add("published", "false");
        map.add("scheduled_publish_time", String.valueOf(occurrence.getStartTime().minusHours((int) (Math.random() * 6 + 1)).getMillis() / 1000));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<FacebookPost> response = restTemplate.postForEntity(generateRequestUrl(), request, FacebookPost.class);

        occurrence.setFacebookPostId(response.getBody().getId());
        return occurrence;
    }

    private String createMessage(final Occurrence occurrence) {
        String eventArtists = occurrence.getEvent()
                .map(Event::getAllArtistNames)
                .orElse(new ArrayList<>()).stream()
                .collect(Collectors.joining(", "));

        if (eventArtists.lastIndexOf(',') > 0) {
            int start = eventArtists.lastIndexOf(',');
            eventArtists = eventArtists.substring(0, start) +
                    " and" +
                    eventArtists.substring(start + 1);
        }

        final String venueName = occurrence.getEvent().flatMap(Event::getLocation).map(Location::getName).orElse("");
        final String facebookId = occurrence.getEvent().flatMap(Event::getLocation).map(Location::getFacebookId).map(String::valueOf).orElse("");
        String venue = venueName;

        if (facebookId.length() > 0) {
            venue = "@[" + facebookId + ":1:" + venueName + "]";
        }

        return generateMessage(
                eventArtists,
                venue,
                Strings.padStart(String.valueOf(occurrence.getStartTime().getHourOfDay()), 2, '0') +
                        ":" +
                        Strings.padStart(String.valueOf(occurrence.getStartTime().getMinuteOfHour()), 2, '0')
        );
    }

    private String generateRequestUrl() {
        return String.format(
                "https://graph.facebook.com/NestedBird/feed/?access_token=%s",
                socialConfigSettings.getFbAccessToken()
        );
    }

    private String generateMessage(String artists, String venue, String time) {
        return String.format(
                "Later we have %s live at %s, at: %s.",
                artists,
                venue,
                time
        );
    }
}
