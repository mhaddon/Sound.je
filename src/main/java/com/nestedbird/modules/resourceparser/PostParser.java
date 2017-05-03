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

package com.nestedbird.modules.resourceparser;

import com.nestedbird.jackson.facebook.FacebookPost;
import com.nestedbird.models.event.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The type Post parser.
 */
@Component
public class PostParser {
    private final EventParser eventParser;

    private final MediumParser mediumParser;

    private final EventService eventService;

    /**
     * Instantiates a new Post parser.
     *
     * @param eventParser  the event parser
     * @param mediumParser the medium parser
     */
    @Autowired
    public PostParser(final EventParser eventParser,
                      final MediumParser mediumParser,
                      final EventService eventService) {
        this.eventParser = eventParser;
        this.mediumParser = mediumParser;
        this.eventService = eventService;
    }

    /**
     * Parse.
     *
     * @param facebookPost the facebook post
     */
    public void parse(final FacebookPost facebookPost) {
        // todo remember which events we have already scanned
        if (facebookPost.getLink().contains("facebook.com/events/")) {
            eventParser.parseUrl(facebookPost.getLink());
        }

        // todo add authorising system first
        //        } else if (facebookPost.getLink().contains("soundcloud.com/")) {
        //            mediumParser.parseSoundcloudUrl(facebookPost.getLink());
        //        } else if (facebookPost.getLink().contains("youtube.com/")) {
        //            mediumParser.parseYoutubeUrl(facebookPost.getLink());
        //        }
        // todo add checking to ignore facebook pages we do not want
        //        else if (facebookPost.getLink().contains("facebook.com/")) {
        //            mediumParser.parseFacebookUrl(facebookPost.getLink());
        //        }
    }


}
