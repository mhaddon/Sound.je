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

package com.nestedbird.modules.sitemap;

import com.nestedbird.models.core.DataObject;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class SitemapEntity extends DataObject implements Serializable {
    private final String externalUrl;
    private final String relativeUrl;
    private final DateTime lastModified;
    private final String changeFrequency;
    private final Double priority;

    @Builder
    private SitemapEntity(final String externalUrl,
                          final String relativeUrl,
                          final DateTime lastModified,
                          final String changeFrequency,
                          final Double priority) {
        this.externalUrl = Optional.ofNullable(externalUrl).orElse("");
        this.relativeUrl = Optional.ofNullable(relativeUrl).orElse("");
        this.lastModified = Optional.ofNullable(lastModified).orElse(new DateTime());
        this.changeFrequency = Optional.ofNullable(changeFrequency).orElse("daily");
        this.priority = Optional.ofNullable(priority).orElse(0.5);
    }

    public String generate() {
        // @formatter:off
        return StringUtils.join(new String[]{
                "<url>",
                    "<loc>" + externalUrl + StringEscapeUtils.escapeXml11(relativeUrl) + "</loc>",
                    "<lastmod>" + StringEscapeUtils.escapeXml11(lastModified.toString()) + "</lastmod>",
                    "<changefreq>" + changeFrequency + "</changefreq>",
                    "<priority>" + priority + "</priority>",
                "</url>"
        }, "");
        // @formatter:on
    }
}
