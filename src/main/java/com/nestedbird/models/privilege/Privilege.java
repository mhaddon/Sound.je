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

package com.nestedbird.models.privilege;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nestedbird.components.bridges.RoleBridge;
import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.roles.Role;
import com.nestedbird.modules.entitysearch.SearchAnalysers;
import com.nestedbird.modules.schema.annotations.SchemaRepository;
import com.nestedbird.modules.schema.annotations.SchemaView;
import lombok.*;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The type Privilege.
 */
@Entity
@Table(name = "privileges")
@Cacheable
@Indexed
@SchemaRepository(PrivilegeRepository.class)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"roles"})
@NoArgsConstructor(force = true)
public class Privilege extends AuditedEntity implements Serializable {
    /**
     * Name of this privilege
     */
    @Column(name = "name", nullable = false, length = 100)
    @Field(boost = @Boost(2.0f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @NotNull
    @Size(max = 100)
    @SchemaView
    private String name;

    /**
     * The roles that are associated to this privilege belongs to
     */
    @ManyToMany(mappedBy = "privileges")
    @JsonBackReference(value = "privToRole")
    @Field(bridge = @FieldBridge(impl = RoleBridge.class),
            analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    private Set<Role> roles = new HashSet<>();

    /**
     * Instantiates a new Privilege.
     *
     * @param id     the id
     * @param name   the name
     * @param roles  the roles
     * @param active the active
     */
    @Builder
    public Privilege(final String id,
                     final String name,
                     final Set<Role> roles,
                     final Boolean active) {
        super(id);

        // Null safe
        this.name = Optional.ofNullable(name).orElse("");
        this.roles = Optional.ofNullable(roles).orElse(new HashSet<>());
        setActive(Optional.ofNullable(active).orElse(false));
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
