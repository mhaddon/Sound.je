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
 * This exception is ran when an ip has been blocked from doing an action
 */
public class IPTemporarilyBlockedException extends Exception {

    /**
     * Instantiates a new Ip temporarily blocked.
     *
     * @param message the message
     */
    public IPTemporarilyBlockedException(final String message) {
        super(message);
    }


    /**
     * Instantiates a new Ip temporarily blocked.
     *
     * @param message   the message
     * @param throwable the throwable
     */
    public IPTemporarilyBlockedException(final String message, final Throwable throwable) {
        super(message, throwable);
    }
}