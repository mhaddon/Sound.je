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

package com.nestedbird.views;

import com.nestedbird.modules.sitemap.SitemapGenerator;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The type Sitemap controller.
 */
@Controller
@EnableScheduling
public class SitemapController {
    private final SitemapGenerator sitemapGenerator;

    private final RedissonClient redissonClient;

    /**
     * Instantiates a new Sitemap controller.
     *
     * @param sitemapGenerator the sitemap generator
     * @param redissonClient   the redisson client
     */
    @Autowired
    public SitemapController(final SitemapGenerator sitemapGenerator,
                             final RedissonClient redissonClient) {
        this.sitemapGenerator = sitemapGenerator;
        this.redissonClient = redissonClient;
    }

    /**
     * Update redisson cache.
     */
    @Scheduled(cron = "0 0 */2 * * *")
    public void updateRedissonCache() {
        final RBucket<String> bucket = redissonClient.getBucket("SiteMapCache");
        bucket.set(sitemapGenerator.generate());
    }

    /**
     * Home string.
     *
     * @param model the model
     * @return the string
     */
    @RequestMapping("/sitemap.xml")
    @ResponseBody
    public String home(final Model model) {
        final RBucket<String> bucket = redissonClient.getBucket("SiteMapCache");

        if (bucket.get() == null || bucket.get().isEmpty()) {
            bucket.set(sitemapGenerator.generate());
        }

        return bucket.get();
    }
}
