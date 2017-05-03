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

import com.nestedbird.jackson.facebook.*;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Facebook scanner.
 */
@Component
@Slf4j
public class FacebookScanner implements Serializable {
    private static final Pattern eventUrlPattern = Pattern.compile("facebook.com\\/events\\/([0-9]+)");

    private final transient FacebookReader facebookReader;

    /**
     * Instantiates a new Facebook scanner.
     *
     * @param facebookReader the facebook reader
     */
    @Autowired
    public FacebookScanner(final FacebookReader facebookReader) {
        this.facebookReader = facebookReader;
    }

    /**
     * Scan facebook scan collection.
     *
     * @param pages the pages
     * @return the facebook scan collection
     */
    public FacebookScanCollection scan(final List<FacebookPage> pages) {
        final List<FacebookPost> posts = retrievePosts(pages);
        final List<FacebookEvent> events = retrieveEvents(pages, posts);

        return FacebookScanCollection.builder()
                .events(events)
                .posts(posts)
                .pages(pages)
                .build();
    }

    private List<FacebookPost> retrievePosts(final List<FacebookPage> pages) {
        return pages.stream()
                .map(FacebookPage::getId)
                .map(facebookReader::requestPagePosts)
                .map(FacebookPosts::getData)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .filter(e -> e.getCreatedTimeParsed().isAfter(DateTime.now().minusDays(2)))
                .sorted(Comparator.comparing(FacebookPost::getCreatedTimeParsed, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    private List<FacebookEvent> retrieveEvents(final List<FacebookPage> pages, final List<FacebookPost> posts) {
        return Stream.concat(retrieveEventsFromPosts(posts).stream(), retrieveEventsFromWall(pages).stream())
                .filter(event -> event.getStartTimeParsed().isAfterNow())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<FacebookEvent> retrieveEventsFromPosts(final List<FacebookPost> posts) {
        return posts.stream()
                .filter(this::postContainsEvent)
                .map(post -> facebookReader.requestEvent(getFacebookEventIdFromPost(post)))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<FacebookEvent> retrieveEventsFromWall(final List<FacebookPage> pages) {
        return pages.stream()
                .map(e -> facebookReader.requestPageEvents(e.getId()).getData())
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private String getFacebookEventIdFromPost(final FacebookPost post) {
        Matcher m = eventUrlPattern.matcher(post.getLink());
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private Boolean postContainsEvent(final FacebookPost post) {
        return post.getLink() != null && eventUrlPattern.matcher(post.getLink()).find();
    }
}
