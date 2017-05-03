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

package com.nestedbird.models.roles;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nestedbird.components.bridges.PrivilegeBridge;
import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.privilege.Privilege;
import com.nestedbird.models.user.User;
import com.nestedbird.modules.entitysearch.SearchAnalysers;
import com.nestedbird.modules.schema.Schema;
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
 * The type Role.
 */
@Entity
@Table(name = "roles")
@Cacheable
@Indexed
@SchemaRepository(RoleRepository.class)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"privileges", "users"})
@NoArgsConstructor(force = true)
public class Role extends AuditedEntity implements Serializable {
    /**
     * The name of this role
     */
    @Column(name = "name", nullable = false, length = 100)
    @Field(boost = @Boost(2.0f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @NotNull
    @Size(max = 100)
    @SchemaView
    private String name;

    /**
     * The list of privileges that this role contains
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "roles_privileges",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    @JsonManagedReference(value = "privToRole")
    @SchemaView(value = Schema.BaseEntityArray, type = Privilege.class)
    @Field(bridge = @FieldBridge(impl = PrivilegeBridge.class),
            analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    private Set<Privilege> privileges = new HashSet<>();

    /**
     * All events the artist is participating in
     */
    @ManyToMany(mappedBy = "roles")
    @ContainedIn
    @JsonBackReference(value = "eventArtistParent")
    private Set<User> users = new HashSet<>();

    /**
     * Instantiates a new Role.
     *
     * @param id         the id
     * @param name       the name
     * @param privileges the privileges
     * @param users      the users
     * @param active     the active
     */
    @Builder
    public Role(final String id,
                final String name,
                final Set<Privilege> privileges,
                final Set<User> users,
                final Boolean active) {
        super(id);

        // Null safe
        this.name = Optional.ofNullable(name).orElse("");
        this.privileges = Optional.ofNullable(privileges).orElse(new HashSet<>());
        this.users = Optional.ofNullable(users).orElse(new HashSet<>());
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
