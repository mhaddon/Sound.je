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

package com.nestedbird.components.authentication;

/**
 * This exception is ran when an account has too many failed logins
 */
public class LoginAttemptExceededException extends Exception {

    /**
     * Instantiates a new Login attempt exceeded.
     *
     * @param message the message
     */
    public LoginAttemptExceededException(final String message) {
        super(message);
    }

    /**
     * Instantiates a new Login attempt exceeded.
     *
     * @param message   the message
     * @param throwable the throwable
     */
    public LoginAttemptExceededException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}
