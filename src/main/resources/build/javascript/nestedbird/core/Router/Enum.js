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
// @flow
// Node Modules

// Site Modules

/**
 * An enum for the different possible modals
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @enum {number}
 * @class
 * @readonly
 * @memberOf module:Core/Router
 */
export const Modal = {
    NONE:          0,
    LOCATION:      1,
    EVENT:         2,
    ARTIST:        3,
    SONG:          4,
    MEDIUM:        5,
    LOGIN:         6,
    RECORD:        7,
    EDITRECORD:    8,
    CREATERECORD:  9,
    PASSWORDRESET: 10,
    REGISTER:      11,
    LOGINPASSWORD: 12,
    SUBMIT:        13,
    NOTFOUND:      404
};

/**
 * An enum for the different possible pages
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @enum {number}
 * @class
 * @readonly
 * @memberOf module:Core/Router
 */
export const Page = {
    NONE:         0,
    EVENTS:       1,
    MUSIC:        2,
    NEWS:         3,
    ABOUT:        4,
    ADMIN:        5,
    RECORDS:      6,
    VERIFYEVENTS: 7,
    SEARCH:       8
};