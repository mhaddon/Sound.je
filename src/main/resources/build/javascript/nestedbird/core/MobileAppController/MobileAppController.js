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
import Optional from "optional-js";

// Site Modules

/**
 * This singleton controller is for controlling mobile apps
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @singleton
 * @memberOf module:Core/MobileAppController
 */
class MobileAppController {
    /**
     * Are we currently loading the page from a mobile app?
     *
     * @member module:Core/MobileAppController.MobileAppController#_isMobileApp
     * @type boolean
     * @defaultvalue false
     * @private
     */
    _isMobileApp: boolean;

    /**
     * @constructor
     */
    constructor() {
        this._isMobileApp = window.isMobileApp || false;
    }

    /**
     * Are we currently viewing the page in a mobile app
     * @member module:Core/MobileAppController.MobileAppController#isMobileApp
     * @method
     * @return {boolean}
     */
    isMobileApp() {
        return this._isMobileApp;
    }

    /**
     * Returns an optional of whether or not we are a mobile app
     * @member module:Core/MobileAppController.MobileAppController#whenMobileApp
     * @method
     * @returns Optional<number>
     */
    whenMobileApp() {
        return Optional.ofNullable(this.isMobileApp() || null);
    }
}

export default new MobileAppController();