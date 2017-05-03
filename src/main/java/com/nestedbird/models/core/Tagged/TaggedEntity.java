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

package com.nestedbird.models.core.Tagged;

import com.nestedbird.components.bridges.TagBridge;
import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.tag.Tag;
import com.nestedbird.modules.entitysearch.SearchAnalysers;
import com.nestedbird.modules.schema.Schema;
import com.nestedbird.modules.schema.annotations.SchemaView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"tags"})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class TaggedEntity extends AuditedEntity implements Serializable {
    @ManyToMany(fetch = FetchType.EAGER)
    @Field(bridge = @FieldBridge(impl = TagBridge.class),
            analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @JoinTable(name = "tags_entities",
            joinColumns = @JoinColumn(name = "entity_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "id"))
    @SchemaView(value = Schema.BaseEntityArray, type = Tag.class)
    private Set<Tag> tags = new HashSet<>(0);

    protected TaggedEntity() {
        super();
    }

    protected TaggedEntity(final String id) {
        super(id);
    }
}
