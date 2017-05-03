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

import com.nestedbird.jackson.facebook.*;
import com.nestedbird.jackson.youtube.*;
import com.nestedbird.models.medium.Medium;
import com.nestedbird.models.medium.MediumType;
import com.nestedbird.modules.facebookreader.FacebookReader;
import com.nestedbird.modules.soundcloudreader.SoundcloudReader;
import com.nestedbird.modules.youtubereader.YoutubeReader;
import com.nestedbird.util.Mutable;
import com.nestedbird.util.PatternMatcher;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * The type Medium parser.
 */
@Component
public class MediumParser {
    private static final Pattern youtubePattern = Pattern.compile("(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*");
    private static final Pattern facebookPattern = Pattern.compile("\\/videos\\/(\\d+)");

    private final YoutubeReader youtubeReader;
    private final FacebookReader facebookReader;
    private final SoundcloudReader soundcloudReader;

    /**
     * Instantiates a new Medium parser.
     *
     * @param youtubeReader    the youtube reader
     * @param facebookReader   the facebook reader
     * @param soundcloudReader the soundcloud reader
     */
    @Autowired
    public MediumParser(final YoutubeReader youtubeReader,
                        final FacebookReader facebookReader,
                        final SoundcloudReader soundcloudReader) {
        this.youtubeReader = youtubeReader;
        this.facebookReader = facebookReader;
        this.soundcloudReader = soundcloudReader;
    }

    /**
     * Parse url medium.
     *
     * @param url the url
     * @return the medium
     */
    public Medium parseUrl(final String url) {
        final Mutable<Medium> parsedMedium = Mutable.of(null);

        if (url.toLowerCase().contains("soundcloud.com/")) {
            parsedMedium.mutate(parseSoundcloudUrl(url));
        } else if (url.toLowerCase().contains("youtube.com/") || url.toLowerCase().contains("youtu.be/")) {
            parsedMedium.mutate(parseYoutubeUrl(url));
        } else if (url.toLowerCase().contains("facebook.com/")) {
            parsedMedium.mutate(parseFacebookUrl(url));
        }

        return parsedMedium.get();
    }

    /**
     * Parse soundcloud url medium.
     *
     * @param url the url
     * @return the medium
     */
    public Medium parseSoundcloudUrl(final String url) {
        final Medium medium = new Medium();

        Optional.ofNullable(soundcloudReader.requestSongDataFromUrl(url)).ifPresent(song ->
                medium.setSourceUrl(song.getUri())
                        .setArtUrl(song.getArtworkUrl())
                        .setCreationDateTime(song.getCreatedAtParsed())
                        .setData(song.toJSON())
                        .setType(MediumType.SOUNDCLOUD)
                        .setSubmissionDateTime(DateTime.now())
                        .setSourceCommentCount(Math.toIntExact(song.getCommentCount()))
                        .setSourceFavouriteCount(Math.toIntExact(song.getFavouriteCount()))
                        .setSourcePlaybackCount(Math.toIntExact(song.getPlaybackCount()))
                        .setSourceId(String.valueOf(song.getId()))
        );

        return medium;
    }

    /**
     * Parse youtube url medium.
     *
     * @param url the url
     * @return the medium
     */
    public Medium parseYoutubeUrl(final String url) {
        final Mutable<String> id = Mutable.of("");

        PatternMatcher.of(youtubePattern, url)
                .then(matcher -> id.mutate(matcher.group(0)));

        return parseYoutubeId(id.get());
    }

    /**
     * Parse facebook url medium.
     *
     * @param url the url
     * @return the medium
     */
    public Medium parseFacebookUrl(final String url) {
        final Mutable<String> id = Mutable.of("");

        PatternMatcher.of(facebookPattern, url)
                .then(matcher -> id.mutate(matcher.group(0)));

        return parseYoutubeId(id.get());
    }

    /**
     * Parse youtube id medium.
     *
     * @param id the id
     * @return the medium
     */
    public Medium parseYoutubeId(final String id) {
        final YoutubeListResponse listResponse = youtubeReader.requestVideoData(id);
        final Medium medium = new Medium();

        Optional.ofNullable(listResponse.getItems().get(0)).ifPresent(video ->
                medium.setSourceUrl("https://www.youtube.com/watch?v=" + video.getId())
                        .setArtUrl(video.getSnippet().flatMap(YoutubeSnippet::getThumbnails).flatMap(YoutubeThumbnails::getHighestRes).map(YoutubeThumbnail::getUrl).orElse("/images/blackfat.jpg"))
                        .setCreationDateTime(video.getSnippet().map(YoutubeSnippet::getPublishedAtParsed).orElse(new DateTime(0)))
                        .setData(video.toJSON())
                        .setType(MediumType.YOUTUBE)
                        .setSubmissionDateTime(DateTime.now())
                        .setSourcePlaybackCount(video.getStatistics().map(YoutubeStatistics::getViewCount).map(Integer::parseInt).orElse(0))
                        .setSourceCommentCount(video.getStatistics().map(YoutubeStatistics::getCommentCount).map(Integer::parseInt).orElse(0))
                        .setSourceFavouriteCount(video.getStatistics().map(YoutubeStatistics::getLikeCount).map(Integer::parseInt).orElse(0))
                        .setSourceId(video.getId())
        );

        return medium;
    }

    /**
     * Parse facebook id medium.
     *
     * @param id the id
     * @return the medium
     */
    public Medium parseFacebookId(final String id) {
        final Medium medium = new Medium();

        Optional.ofNullable(facebookReader.requestVideo(id)).ifPresent(video ->
                medium.setSourceUrl("https://www.facebook.com" + video.getPermalinkUrl())
                        .setArtUrl(video.getPicture())
                        .setCreationDateTime(video.getCreatedTimeParsed())
                        .setData(video.toJSON())
                        .setType(MediumType.FACEBOOK)
                        .setSubmissionDateTime(DateTime.now())
                        .setSourceCommentCount(video.getComments().flatMap(FacebookComments::getSummary).map(FacebookCommentsSummary::getTotalCount).orElse(0))
                        .setSourceFavouriteCount(video.getLikes().flatMap(FacebookLikes::getSummary).map(FacebookLikesSummary::getTotalCount).orElse(0))
                        .setSourceId(video.getId())
        );

        return medium;
    }

}
