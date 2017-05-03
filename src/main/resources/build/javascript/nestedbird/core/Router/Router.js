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
import Optional from "optional-js";
// Site Modules
import store from "nestedbird/vue/store";
import { Ajax } from "nestedbird/core/Ajax";
import { Notification } from "nestedbird/core/Util";
import href from "./Href";
import { Modal } from "./Enum";
import Route from "./Route";
import Routes from "./Routes";

/**
 * The router parses pathnames by finding a matching route for the pathname.
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @memberOf module:Core/Router
 * @param {Object} [routes] Object with all possible routes defined
 */
class Router {
    /**
     * Array of all possible routes
     * @member module:Core/Router.Router#routes
     * @type Route[]
     */
    routes: Route[];

    /**
     * Creates all the route objects from a route defining object.
     * see routes.js
     *
     * @param {object} [routes]   Object with all possible routes defined
     * @throws {TypeError} arguments must not be null
     */
    constructor(routes: Object = {}) {
        if (typeof routes !== `object`) throw new TypeError(`routes is not an object`);
        this.routes = [];
        for (const routeKey of Object.keys(routes)) {
            const routeValue = routes[routeKey];
            this.routes.push(new Route(routeKey, routeValue));
        }
    }

    /**
     * Loops over every route and if they match it processes the route
     *
     * @member module:Core/Router.Route#parse
     * @method
     * @param {string} pathname
     * @throws {TypeError} arguments must not be null
     */
    parse(pathname: string) {
        if (typeof pathname !== `string`) throw new TypeError(`pathname is not a string`);

        const firstRoute = this.routes
            .find(route => route.match(pathname));

        const response = Optional.ofNullable(firstRoute)
            .map(route => route.parse(pathname))
            .orElse(404);

        if (response === 404) {
            Ajax.setMetaTag(`robots`, `noindex`);
            Notification.create(`Resource Not Found`).record();
        } else if ((response === 403) && (pathname !== `/login`)) {
            Ajax.setMetaTag(`robots`, `noindex`);
            Notification.create(`Insufficient permissions`).record();
            this.parse(`/login`);
        }

        store.commit(`setQuery`, this.parseQuery());
        Ajax.setMetaTag(`og:url`, `${document.location.origin}${document.location.pathname}`);
    }

    /**
     * parses a HrefDirectiveObject
     * @member module:Core/Router.Route#parseObject
     * @method
     * @param {hrefDirectiveObject} object
     */
    parseObject(object: hrefDirectiveObject) {
        this.parse(this.hrefDirectiveObjectToUrl(object));
    }

    /**
     * parses a HrefDirectiveObject to its url
     * @member module:Core/Router.Route#hrefDirectiveObjectToUrl
     * @method
     * @param {hrefDirectiveObject} object
     * @returns string
     */
    hrefDirectiveObjectToUrl(object: hrefDirectiveObject): string {
        return href.calculate(object);
    }

    /**
     * redirects the user to a url and parses this url
     * @member module:Core/Router.Route#redirect
     * @method
     * @param {string} pathname
     * @throws {TypeError} arguments must not be null
     */
    redirect(pathname: string) {
        if (typeof pathname !== `string`) throw new TypeError(`pathname is not a string`);
        window.history.pushState({}, ``, pathname); // pushstate must come BEFORE we define the document.title
        this.parse(pathname.split(`?`, 1)[0]);
        window.ga(`NestedBird.send`, `pageview`, document.location.pathname);

        if (store.getters.currentModal === Modal.NONE) {
            store.commit(`setLastModallessUrl`, window.location.pathname);
        }
    }

    /**
     * redirects the url to a path query, but does not process it
     * @member module:Core/Router.Route#redirectQuery
     * @method
     * @param {string} query
     * @throws {TypeError} arguments must not be null
     */
    redirectQuery(query: string) {
        // pushstate must come BEFORE we define the document.title
        window.history.pushState({}, ``, `${window.location.pathname}?${query}`);
        this.updateQuery();
        window.ga(`NestedBird.send`, `pageview`, document.location.pathname);
    }

    /**
     * Updates the current query information
     * @member module:Core/Router.Route#updateQuery
     * @method
     * @param {string} location [null]    query info to parse, prefix with ?
     */
    updateQuery(location: ?string = null) {
        store.commit(`setQuery`, this.parseQuery(location));
    }

    /**
     * Retrieves the query values from the current url
     * @member module:Core/Router.Route#parseQuery
     * @method
     * @param {string} location [window.location.search]    query info to parse, prefix with ?
     * @returns {Object}
     */
    parseQuery(location: ?string): Object {
        return Object.assign(...(location || window.location.search)
            .substr(1).split(`&`)
            .map((e: string): Object => {
                const queryPieces = e.split(`=`);
                return {
                    [queryPieces[0]]: decodeURIComponent(queryPieces[1])
                };
            }));
    }

    /**
     * redirects the user to a url and parses this url
     * @member module:Core/Router.Route#redirectObject
     * @method
     * @param {hrefDirectiveObject} object
     */
    redirectObject(object: hrefDirectiveObject) {
        this.redirect(this.hrefDirectiveObjectToUrl(object));
    }

    /**
     * Binds event listeners to the page to listen to page events
     * @member module:Core/Router.Route#bindListeners
     * @method
     */
    bindListeners() {
        if (typeof window.onpopstate !== `function`) {
            window.onpopstate = () => {
                this.parse(document.location.pathname);
                ga(`NestedBird.send`, `pageview`, document.location.pathname);
            };
        }
    }

    /**
     * initialises the Router for first page load
     * @member module:Core/Router.Route#processFirstPageLoad
     * @method
     */
    processFirstPageLoad() {
        this.parse(document.location.pathname);
        this.bindListeners();
    }
}

export default new Router(Routes);