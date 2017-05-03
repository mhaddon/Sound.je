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

import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.modules.entitysearch.SearchAnalysers;
import com.nestedbird.modules.schema.annotations.SchemaRepository;
import com.nestedbird.modules.schema.annotations.SchemaView;
import lombok.*;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Optional;

/**
 * The type Scanned page.
 */
@Entity
@Table(name = "scannedpages")
@Cacheable
@Indexed
@SchemaRepository(ScannedPageRepository.class)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class ScannedPage extends AuditedEntity implements Serializable {
    /**
     * name of the scanned page
     */
    @Column(name = "name", length = 100)
    @Field(boost = @Boost(2.0f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 100)
    @SchemaView
    private String name;

    /**
     * the pages source url
     */
    @Column(name = "url", length = 100)
    @Field(boost = @Boost(2.0f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 100)
    @SchemaView
    private String sourceUrl;

    /**
     * the associated images url
     */
    @Column(name = "image_url", length = 250)
    @Field
    @Size(max = 250)
    @SchemaView
    private String imageUrl;

    /**
     * what activity does this page have
     */
    @Column(name = "activity", length = 100)
    @Field
    @Size(max = 100)
    @SchemaView
    private String activity;

    /**
     * the associated facebook id for the page
     */
    @Column(name = "facebook_id", length = 20)
    @Field
    @SchemaView
    private String facebookId;

    /**
     * Instantiates a new Scanned page.
     *
     * @param name       the name
     * @param sourceUrl  the source url
     * @param imageUrl   the image url
     * @param activity   the activity
     * @param facebookId the facebook id
     */
    @Builder
    private ScannedPage(final String id,
                        final String name,
                        final String sourceUrl,
                        final String imageUrl,
                        final String activity,
                        final String facebookId,
                        final Boolean active) {
        super(id);

        // Null safe
        this.name = Optional.ofNullable(name).orElse("");
        this.sourceUrl = Optional.ofNullable(sourceUrl).orElse("");
        this.imageUrl = Optional.ofNullable(imageUrl).orElse("");
        this.activity = Optional.ofNullable(activity).orElse("");
        setActive(Optional.ofNullable(active).orElse(false));

        // Not null safe
        this.facebookId = facebookId;
    }

    @Override
    public String getUrl() {
        return "#";
    }

    @Override
    public String getDefiningName() {
        return name;
    }
}
