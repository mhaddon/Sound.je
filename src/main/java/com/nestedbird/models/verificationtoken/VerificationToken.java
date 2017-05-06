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

package com.nestedbird.models.verificationtoken;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nestedbird.models.core.Base.BaseEntity;
import com.nestedbird.models.user.User;
import lombok.*;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Optional;

/**
 * The type Verification token.
 */
@Entity
@Table(name = "verification_token")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"user"})
@NoArgsConstructor(force = true)
public class VerificationToken extends BaseEntity {
    @OneToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(name = "expiry_date")
    private DateTime expiryDate;

    @Column(name = "type", nullable = false)
    private String type = VerificationTokenType.NONE;

    @Builder
    private VerificationToken(final String id,
                              final User user,
                              final DateTime expiryDate,
                              final String type) {
        super(id);

        // Null safe
        this.expiryDate = Optional.ofNullable(expiryDate).orElse(new DateTime());
        this.type = Optional.ofNullable(type).orElse(VerificationTokenType.NONE);

        // Not null safe
        this.user = user;
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    @JsonIgnore
    public Optional<User> getUser() {
        return Optional.ofNullable(user);
    }

    @Override
    public String getUrl() {
        return "#";
    }

    @Override
    public String getDefiningName() {
        return getIdBase64();
    }
}
