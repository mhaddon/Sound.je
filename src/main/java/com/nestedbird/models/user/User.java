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

package com.nestedbird.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.roles.Role;
import com.nestedbird.modules.entitysearch.SearchAnalysers;
import com.nestedbird.modules.schema.Schema;
import com.nestedbird.modules.schema.annotations.SchemaRepository;
import com.nestedbird.modules.schema.annotations.SchemaView;
import lombok.*;
import org.hibernate.search.annotations.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The type User.
 */
@Entity
@Indexed
@Table(name = "users")
@Cacheable
@SchemaRepository(UserRepository.class)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"roles"})
@NoArgsConstructor(force = true)
public class User extends AuditedEntity implements Serializable {
    /**
     * The constant PASSWORD_ENCODER.
     */
    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder(12);

    /**
     * The users first name
     */
    @Column(name = "first_name", length = 15)
    @Field(boost = @Boost(2.0f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(min = 2, max = 15)
    @SchemaView
    private String firstName;

    /**
     * The users last name
     */
    @Column(name = "last_name", length = 15)
    @Field(boost = @Boost(2.0f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 15)
    @SchemaView
    private String lastName;

    /**
     * the users email/username
     */
    @Column(name = "email", nullable = false, length = 30)
    @Field(analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 30)
    @SchemaView
    private String email;

    /**
     * a hashed version of the password
     */
    @JsonIgnore
    @Column(name = "password")
    private String password;

    /**
     * is this account enabled
     */
    @Column(name = "enabled")
    @SchemaView(Schema.BOOLEAN)
    private Boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @SchemaView(value = Schema.BaseEntityArray, type = Role.class)
    private Set<Role> roles = new HashSet<>(0);

    @Builder
    private User(final String id,
                 final String firstName,
                 final String lastName,
                 final String email,
                 final String password,
                 final Boolean enabled,
                 final Set<Role> roles,
                 final Boolean active) {
        super(id);

        // Fatal if null
        if (password == null) throw new NullPointerException("A user cannot have a null password");
        setPassword(password);

        if (email == null) throw new NullPointerException("A user cannot have a null email");
        this.email = email;

        // Null Safe
        this.firstName = Optional.ofNullable(firstName).orElse("");
        this.lastName = Optional.ofNullable(lastName).orElse("");
        this.enabled = Optional.ofNullable(enabled).orElse(false);
        this.roles = Optional.ofNullable(roles).orElse(new HashSet<>());
        setActive(Optional.ofNullable(active).orElse(false));
    }

    /**
     * encodes the password then sets it
     *
     * @param password new password
     * @return the password
     */
    public User setPassword(final String password) {
        this.password = PASSWORD_ENCODER.encode(password);
        return this;
    }

    /**
     * Add role.
     *
     * @param role the role
     */
    public void addRole(final Role role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    @Override
    public String getUrl() {
        return "#";
    }

    @Override
    public String getDefiningName() {
        return firstName;
    }
}
