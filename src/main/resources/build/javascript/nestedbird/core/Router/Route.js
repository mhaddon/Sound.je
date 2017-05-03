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
// Node_Modules
import pathToRegexp from "path-to-regexp";
import slugid from "slugid";
import Optional from "optional-js";
// Site Modules
import store from "nestedbird/vue/store";

/**
 * A route is a controller for a specific route. The routes are defined in routes.js and they are controlled by
 * the Router class below.
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @memberOf module:Core/Router
 * @param {string} name      name of the route (a path-to-regexp expression)
 * @param {RouteInfo} data      data of the route, usually an object of data it changes in the store
 */
export default class Route {
    /**
     * name of the route (a path-to-regexp expression)
     *
     * @member module:Core/Router.Route#name
     * @type string
     */
    name: string;

    /**
     * data of the route, usually an object of data it changes in the store
     *
     * @member module:Core/Router.Route#data
     * @type RouteInfo
     */
    data: RouteInfo;

    /**
     * a cached compiled version of the path-to-regexp expression
     *
     * @member module:Core/Router.Route#regexp
     * @type string
     */
    regexp: Object;

    /**
     * Stores the elements that make up the regex query
     *
     * @member module:Core/Router.Route#keys
     * @type string
     * @default []
     */
    keys: Object[];

    /**
     * Define routes properties
     * @param name      name of the route (a path-to-regexp expression)
     * @param data      data of the route, usually an object of data it changes in the store
     * @throws {TypeError} arguments must not be null
     */
    constructor(name: string, data: RouteInfo) {
        if (typeof name !== `string` || !name) throw new TypeError(`name needs to be a string`);
        if (!data) throw new TypeError(`data is empty`);

        this.name = name;
        this.data = data;
        this.keys = [];
        this.regexp = pathToRegexp(name, this.keys);
    }

    /**
     * Does the pathname match this routes name?
     *
     * @member module:Core/Router.Route#match
     * @method
     * @param {string} pathname
     * @returns boolean
     * @throws {TypeError} arguments must not be null
     */
    match(pathname: string): boolean {
        if (typeof pathname !== `string`) throw new TypeError(`pathname is not a string`);
        return this.regexp.test(pathname);
    }

    /**
     * Get all match occurrences
     * @member module:Core/Router.Route#getMatches
     * @method
     * @param {string} pathname
     * @returns {Array|{index: number, input: string}|*}
     * @throws {TypeError} arguments must not be null
     */
    getMatches(pathname: string) {
        if (typeof pathname !== `string`) throw new TypeError(`pathname is not a string`);
        return Optional.ofNullable(this.regexp.exec(pathname) || null);
    }

    /**
     * saves the path info to the store
     * @member module:Core/Router.Route#savePathInfoToStore
     * @method
     * @param {Array} routeMatches
     */
    savePathInfoToStore(routeMatches: Array<T>) {
        const pathInfo = {};

        pathInfo.url = routeMatches[0];

        for (let i = 1; i < routeMatches.length; i++) {
            pathInfo[this.keys[i - 1].name || i - 1] = routeMatches[i];
        }

        store.commit(`setPath`, pathInfo);
    }

    /**
     * parse the routes information
     * returns http status code of result
     * @member module:Core/Router.Route#parse
     * @method
     * @param pathname
     * @returns number
     */
    parse(pathname: string): number {
        return this.getMatches(pathname)
            .map((routeMatches) => {
                this.savePathInfoToStore(routeMatches);

                if (this._checkAuthorisation()) {
                    if (this._hasStateData()) {
                        this._saveRouteData(routeMatches);
                    }
                    document.title = store.getters.title.substr(0, 60).replace(/_/g, ` `);

                    if (typeof this.data.fn === `function`) {
                        this.data.fn();
                    }
                    return 200;
                }
                return 403;
            })
            .orElse(404);
    }

    /**
     * Determines whether or not the user has the permission to view this page.
     * @member module:Core/Router.Route#_checkAuthorisation
     * @method
     * @returns {boolean}
     * @private
     */
    _checkAuthorisation(): boolean {
        return !(this._hasStateData() &&
        this.data.state.requireAuthorisation &&
        !store.getters.hasRole(this.data.state.requireAuthorisation));
    }

    /**
     * Determines whether or not this state has any data
     * @member module:Core/Router.Route#_hasStateData
     * @method
     * @returns {boolean}
     * @private
     */
    _hasStateData(): boolean {
        return !!this.data.state;
    }

    /**
     * Saves this route data
     * @member module:Core/Router.Route#_saveRouteData
     * @method
     * @param routeMatches
     * @private
     */
    _saveRouteData(routeMatches: Object[]) {
        for (const stateKey of Object.keys(this.data.state)) {
            const stateValue = this.data.state[stateKey];
            store.commit(`setLocationField`, {
                name:  stateKey,
                value: Route.parseStateValue(routeMatches, stateValue)
            });
        }
    }

    /**
     * This method converts special characters in stateValue.
     * For example:
     * $1 becomes index 1 in routeMatches
     * @1 becomes index 1 in routeMatches and converted to base64
     * #1 becomes index 1 in routeMatches and converted to an integer
     *
     * @member module:Core/Router.Route#parseStateValue
     * @method
     * @param routeMatches
     * @param stateValue
     * @returns {*}
     */
    static parseStateValue(routeMatches, stateValue) {
        let i = 0;
        const regex = /([$|@|#])([0-9]+)/g; // Regex expression that matches special characters
        while (regex.test(stateValue)) {
            regex.exec(stateValue); // TODO: <- This code does nothing but app wont run without it... wtf?
            const [match, matchType, matchId] = regex.exec(stateValue);

            const routeMatch = routeMatches[matchId];

            if (matchType === `$`) {
                stateValue = stateValue.replace(match, routeMatch);
            } else if (matchType === `@`) {
                stateValue = stateValue.replace(match, slugid.decode(routeMatch));
            } else if (matchType === `#`) {
                stateValue = stateValue.replace(match, parseInt(routeMatch, 10));
            }

            /**
             * A simple catch to stop any infinite loops
             * TODO: replace this with a better solution
             */
            if (i++ > 5) {
                break;
            }
        }
        return stateValue;
    }
};