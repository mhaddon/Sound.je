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
import java.util.Optional;

/**
 * This class is the standard error wrapper that is returned to the user when an error occurs
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class ApiError extends DataObject implements Serializable {
    /**
     * http error status
     */
    private final Integer status;

    /**
     * error title, or brief message
     */
    private final String error;

    /**
     * detailed error message
     */
    private final String message;

    /**
     * path to error
     */
    private final String path;

    /**
     * timestamp of error occurrence
     */
    private final Long timestamp;

    @Builder
    private ApiError(final Integer status,
                     final String error,
                     final String message,
                     final String path,
                     final Long timestamp) {
        this.status = Optional.ofNullable(status).orElse(400);
        this.error = Optional.ofNullable(error).orElse("Unknown Error");
        this.message = Optional.ofNullable(message).orElse("");
        this.path = path;
        this.timestamp = Optional.ofNullable(timestamp).orElse(System.currentTimeMillis() / 1000L);
    }
}
