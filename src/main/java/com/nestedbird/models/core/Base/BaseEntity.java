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

package com.nestedbird.models.core.Base;

import com.nestedbird.models.core.DataObject;
import com.nestedbird.modules.entitysearch.SearchAnalysers;
import com.nestedbird.modules.schema.annotations.SchemaView;
import com.nestedbird.util.UUIDConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilterFactory;
import org.apache.lucene.analysis.charfilter.MappingCharFilterFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.phonetic.PhoneticFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;
import org.hibernate.search.annotations.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

/**
 * This is the Base Entity that every database object extends
 */
@MappedSuperclass
@AnalyzerDef(name = SearchAnalysers.ENGLISH_WORD_ANALYSER,
        charFilters = {
                @CharFilterDef(factory = HTMLStripCharFilterFactory.class),
                @CharFilterDef(factory = MappingCharFilterFactory.class, params = {
                        @Parameter(name = "mapping", value = "properties/analyser/mapping-chars.properties")
                })
        },
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = StandardFilterFactory.class),
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
                        @Parameter(name = "language", value = "English")
                }),
                @TokenFilterDef(factory = SynonymFilterFactory.class, params = {
                        @Parameter(name = "ignoreCase", value = "true"),
                        @Parameter(name = "synonyms", value = "properties/analyser/synonyms.properties")
                }),
                @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class),
                @TokenFilterDef(factory = PhoneticFilterFactory.class, params = {
                        @Parameter(name = "encoder", value = "DoubleMetaphone")
                }),
                @TokenFilterDef(factory = StopFilterFactory.class, params = {
                        @Parameter(name = "words", value = "properties/analyser/stoplist.properties"),
                        @Parameter(name = "ignoreCase", value = "true")
                })
        })
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class BaseEntity extends DataObject implements Serializable {
    /**
     * The ID of this entity
     */
    @Id
    @Column(name = "id", unique = true, nullable = false, columnDefinition = "CHAR(36)")
    @SchemaView(locked = true)
    @Getter
    private final String id;

    /**
     * Instantiates a new Base entity with a random UUID
     */
    protected BaseEntity() {
        this(null);
    }

    /**
     * Instantiates a new Base entity.
     *
     * @param id the id
     */
    protected BaseEntity(final String id) {
        this.id = Optional.ofNullable(id).orElseGet(() -> UUID.randomUUID().toString());
    }

    /**
     * Gets id encoded in base 64.
     *
     * @return the uuid id encoded in base 64
     */
    public String getIdBase64() {
        return UUIDConverter.toBase64(id);
    }

    /**
     * Overridden method that gets the elements url
     * Return # if no url
     *
     * @return url of the entity
     */
    public abstract String getUrl();

    /**
     * Retrieves a defining name that names this object
     *
     * @return name of object
     */
    public abstract String getDefiningName();
}
