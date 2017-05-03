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

package com.nestedbird.models.persistentlogins;

import com.nestedbird.models.core.DataObject;
import com.nestedbird.modules.schema.annotations.SchemaRepository;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * The type Persistent logins.
 */
@Entity
@Table(name = "persistent_logins")
@SchemaRepository(PersistentLoginsRepository.class)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class PersistentLogins extends DataObject implements Serializable {
    @Column(name = "username", length = 100, nullable = false)
    private String username;

    @Column(name = "series", length = 64)
    @Id
    private String series;

    @Column(name = "token", length = 64, nullable = false)
    private String token;

    @Column(name = "last_used", nullable = false)
    private Timestamp lastUsed;

    /**
     * Instantiates a new Persistent logins.
     *
     * @param username the username
     * @param series   the series
     * @param token    the token
     * @param lastUsed the last used
     */
    @Builder
    private PersistentLogins(final String username,
                             final String series,
                             final String token,
                             final Timestamp lastUsed) {
        // Null safe
        this.username = Optional.ofNullable(username).orElse("");
        this.series = Optional.ofNullable(series).orElse("");
        this.token = Optional.ofNullable(token).orElse("");
        this.lastUsed = Optional.ofNullable(lastUsed).orElse(new Timestamp(0));
    }
}
