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

import com.nestedbird.jackson.facebook.FacebookCover;
import com.nestedbird.models.artist.Artist;
import com.nestedbird.models.scannedpage.ScannedPage;
import com.nestedbird.modules.facebookreader.FacebookReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * The type Page parser.
 */
@Component
public class PageParser {
    private final FacebookReader facebookReader;

    /**
     * Instantiates a new Page parser.
     *
     * @param facebookReader the facebook reader
     */
    @Autowired
    public PageParser(final FacebookReader facebookReader) {
        this.facebookReader = facebookReader;
    }

    /**
     * Parse scanned page from url scanned page.
     *
     * @param url the url
     * @return the scanned page
     */
    public ScannedPage parseScannedPageFromUrl(final String url) {
        final ScannedPage scannedPage = new ScannedPage();
        final String id = facebookReader.getIdFromUrl(url);

        Optional.ofNullable(facebookReader.requestPage(id)).ifPresent(fbPage ->
                scannedPage.setName(fbPage.getName())
                        .setFacebookId(fbPage.getId())
                        .setSourceUrl(fbPage.getLink())
                        .setImageUrl(fbPage.getCover().map(FacebookCover::getSource).orElse(""))
        );

        return scannedPage;
    }

    public Artist parseArtistFromUrl(final String url) {
        final Artist artist = new Artist();
        final String id = facebookReader.getIdFromUrl(url);

        Optional.ofNullable(facebookReader.requestPage(id)).ifPresent(fbPage ->
                artist.setName(fbPage.getName())
                        .setFacebookId(fbPage.getId())
                        .setDescription(Optional.ofNullable(fbPage.getBio()).orElse(fbPage.getAbout()))
                        .setImageUrl(fbPage.getCover().map(FacebookCover::getSource).orElse(""))
                        .setWebsiteUrl(fbPage.getWebsite())
        );

        return artist;
    }
}
