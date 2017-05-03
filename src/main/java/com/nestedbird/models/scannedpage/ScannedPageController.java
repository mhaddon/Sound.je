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

package com.nestedbird.models.scannedpage;

import com.nestedbird.config.SocialConfigSettings;
import com.nestedbird.jackson.facebook.FacebookEvent;
import com.nestedbird.jackson.facebook.FacebookPage;
import com.nestedbird.models.core.Base.BaseController;
import com.nestedbird.models.core.Base.BaseRepository;
import com.nestedbird.models.core.Base.BaseService;
import com.nestedbird.modules.facebookreader.FacebookReader;
import com.nestedbird.modules.facebookreader.FacebookScanCollection;
import com.nestedbird.modules.facebookreader.FacebookScanner;
import com.nestedbird.modules.formparser.ParameterMapParser;
import com.nestedbird.modules.resourceparser.EventParser;
import com.nestedbird.modules.resourceparser.PageParser;
import com.nestedbird.modules.resourceparser.PostParser;
import com.nestedbird.util.Mutable;
import com.nestedbird.util.QueryBlock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The type Scanned page controller.
 */
@RestController
@RequestMapping("api/v1/ScannedPages/")
@Slf4j
public class ScannedPageController extends BaseController<ScannedPage> {
    private final ScannedPageRepository scannedPageRepository;

    private final ScannedPageService scannedPageService;

    private final SocialConfigSettings socialConfigSettings;

    private final FacebookReader facebookReader;

    private final FacebookScanner facebookScanner;

    private final PageParser pageParser;

    private final EventParser eventParser;

    private final PostParser postParser;

    /**
     * Instantiates a new Scanned page controller.
     *
     * @param scannedPageRepository the scanned page repository
     * @param scannedPageService    the scanned page service
     * @param socialConfigSettings  the social config settings
     * @param facebookReader        the facebook reader
     * @param facebookScanner       the facebook scanner
     * @param pageParser            the page parser
     * @param eventParser           the event parser
     * @param postParser            the post parser
     */
    @Autowired
    ScannedPageController(final ScannedPageRepository scannedPageRepository,
                          final ScannedPageService scannedPageService,
                          final SocialConfigSettings socialConfigSettings,
                          final FacebookReader facebookReader,
                          final FacebookScanner facebookScanner,
                          final PageParser pageParser,
                          final EventParser eventParser,
                          final PostParser postParser) {
        this.scannedPageRepository = scannedPageRepository;
        this.scannedPageService = scannedPageService;
        this.socialConfigSettings = socialConfigSettings;
        this.facebookReader = facebookReader;
        this.facebookScanner = facebookScanner;
        this.pageParser = pageParser;
        this.eventParser = eventParser;
        this.postParser = postParser;
    }

    @Override
    public BaseRepository<ScannedPage> getRepository() {
        return scannedPageRepository;
    }

    @Override
    public Class<ScannedPage> getEntityClass() {
        return ScannedPage.class;
    }

    @Override
    public BaseService<ScannedPage> getService() {
        return this.scannedPageService;
    }

    /**
     * Parse url scanned page.
     *
     * @param request the request
     * @return the scanned page
     */
    @RequestMapping(value = "parseurl", method = RequestMethod.POST,
            headers = "content-type=application/x-www-form-urlencoded",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ScannedPage parseUrl(final HttpServletRequest request) {
        final ParameterMapParser parser = ParameterMapParser.parse(request.getParameterMap());
        final Mutable<ScannedPage> scannedPage = Mutable.of(null);

        QueryBlock.create()

                // Ensure the request has sent the required data
                .require(parser.has("url"), e -> e
                        .fail(HttpStatus.BAD_REQUEST, "Request does not have required data")
                        .done(data -> data.put("url", parser.get("url").toString())))

                // Return the processes medium
                .done(data -> scannedPage.mutate(pageParser.parseScannedPageFromUrl(data.get("url").toString())));

        scannedPage.ofNullable().ifPresent(newScannedPage ->
                scannedPage.mutate(scannedPageService
                        .findFirstByFacebookId(newScannedPage.getFacebookId())
                        .orElseGet(() -> scannedPageService.saveAndFlush(newScannedPage).orElse(newScannedPage)))
        );

        return scannedPage.get();
    }

    /**
     * Manual request.
     */
    @RequestMapping(value = "manualrequest", method = RequestMethod.GET)
    void manualRequest() {
        retrieveEventsToDatabase();
    }

    /**
     * Scan facebook for for all events and save them to the database
     */
    @Scheduled(cron = "0 0 0 * * *")
    //    @Transactional
    public void retrieveEventsToDatabase() {
        if (!socialConfigSettings.getFbScan())
            return;

        final FacebookScanCollection scanCollection = facebookScanner.scan(getFacebookPages());

        logger.info("[Scanner] [Result] Found " + scanCollection.getPosts().size() + " Posts");
        logger.info("[Scanner] [Result] Found " + scanCollection.getEvents().size() + " Events");
        logger.info("[Scanner] [Result] Found " + scanCollection.getPages().size() + " Pages");

        scanCollection.getEvents()
                .forEach(eventParser::parse);

        scanCollection.getPosts()
                .forEach(postParser::parse);
    }

    /**
     * retrieves all facebook pages from the database
     *
     * @return list of facebook pages
     */
    private List<FacebookPage> getFacebookPages() {
        return scannedPageRepository.findAll().stream()
                .filter(ScannedPage::getActive)
                .peek(e -> logger.info("[Scanner] Scanning: " + e.getName() + "[" + e.getId() + "]"))
                .map(e -> facebookReader.requestPage(e.getFacebookId()))
                .filter(Objects::nonNull)
                .peek(e -> logger.info("[Scanner] [Report] Found Page: " + e.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Test request list.
     *
     * @return the list
     */
    @RequestMapping(value = "testrequest", method = RequestMethod.GET)
    List<FacebookEvent> testRequest() {
        return facebookScanner.scan(getFacebookPages()).getEvents();
    }
}
