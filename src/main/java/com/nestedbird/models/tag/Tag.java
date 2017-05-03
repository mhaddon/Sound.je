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

package com.nestedbird.models.tag;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nestedbird.components.bridges.TagBridge;
import com.nestedbird.models.core.Base.BaseEntity;
import com.nestedbird.models.core.Tagged.TaggedEntity;
import com.nestedbird.modules.entitysearch.SearchAnalysers;
import com.nestedbird.modules.schema.annotations.SchemaRepository;
import com.nestedbird.modules.schema.annotations.SchemaView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Cacheable
@Indexed
@SchemaRepository(TagRepository.class)
@Boost(2.0f)
@AnalyzerDiscriminator(impl = TagBridge.class)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class Tag extends BaseEntity implements Serializable {
    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private Set<TaggedEntity> entities = new HashSet<>();

    @Column(name = "name", nullable = false, length = 50)
    @Field(boost = @Boost(2f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @NotNull
    @Size(min = 2, max = 50)
    @SchemaView
    private String name;

    @Override
    public String getUrl() {
        return "#";
    }

    @Override
    public String getDefiningName() {
        return name;
    }
}
