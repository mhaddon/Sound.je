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

package com.nestedbird.jackson;

import com.nestedbird.models.core.DataObject;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * This class is a wrapper for handling session information
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SessionData extends DataObject implements Serializable {
    /**
     * CSRF token value
     */
    private final String tokenValue;

    /**
     * CSRF parameter name
     */
    private final String tokenParameterName;

    /**
     * CSRF header name
     */
    private final String tokenHeaderName;

    /**
     * Currently logged into email
     */
    private final String email;

    /**
     * Currently logged into userid
     */
    private final String userId;

    /**
     * The authorities of the user
     */
    private final Object[] authorities;

    @Builder
    private SessionData(final String tokenValue,
                        final String tokenParameterName,
                        final String tokenHeaderName,
                        final String email,
                        final String userId,
                        final Object[] authorities) {
        this.tokenValue = tokenValue;
        this.tokenParameterName = tokenParameterName;
        this.tokenHeaderName = tokenHeaderName;
        this.email = email;
        this.userId = userId;
        this.authorities = authorities;
    }
}
