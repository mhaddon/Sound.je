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
// Site Modules
import store from "nestedbird/vue/store";
import { Util } from "nestedbird/core/Util";
import { Page } from "./Enum";

/**
 * This singleton class is responsible for processing href requests made by the v-href directive
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @singleton
 * @memberOf module:Core/Router
 */
class Href {
    /**
     * The base root path, this is used if the v-href path cant be determined or is not set
     * @member module:Core/Router.Href#_rootPath
     * @defaultvalue /
     * @type RegExp
     * @private
     */
    _rootPath: RegExp;

    /**
     * This matches to a page without additional information
     * @member module:Core/Router.Href#_pagePath
     * @defaultvalue /:page
     * @type RegExp
     * @private
     */
    _pagePath: RegExp;

    /**
     * This matches to a page with additional information
     * @member module:Core/Router.Href#_pagePathX
     * @defaultvalue /:page/*
     * @type RegExp
     * @private
     */
    _pagePathX: RegExp;

    /**
     * This matches to a page about a specific item
     * @member module:Core/Router.Href#_itemPath
     * @defaultvalue /:page/:id/:name?
     * @type RegExp
     * @private
     */
    _itemPath: RegExp;

    constructor() {
        this._rootPath = pathToRegexp.compile(`/`);
        this._pagePath = pathToRegexp.compile(`/:page`);
        this._pagePathX = pathToRegexp(`/:page/*`);
        this._itemPath = pathToRegexp.compile(`/:page/:id/:name?`);
    }

    /**
     * Does the passed data use a magic reference
     * a magic reference is a page reference which has a special method attached to it
     * @member module:Core/Router.Href#_isMagicPageReference
     * @method
     * @private
     * @param {hrefDirectiveObject} data        The request data
     * @returns {boolean}
     */
    _isMagicPageReference(data: hrefDirectiveObject): boolean {
        return [`$parent`, `$modalless`].includes(data.page);
    }

    /**
     * Gets the current page parent based on the URL
     * for example:
     * /Locations/uN5scUsAFcHDYa.t.0c1hV/The_Green_Rooster
     * Will see:
     * Locations
     * as its parent
     * @member module:Core/Router.Href#_getPageParent
     * @method
     * @private
     * @returns {string}
     */
    _getPageParent(): string {
        const matches = this._pagePathX.exec(document.location.pathname);
        return (matches) ? matches[1] : ``;
    }

    /**
     * Gets the current page based on the store references.
     * If someone is on the /Media page, and loads an artist, they will load
     * /Artist/.... in the url bar.
     *
     * However, in the store it still knows they were on /Media previously,
     * this method retrieves the /Media from the store
     * @member module:Core/Router.Href#_getModallessPage
     * @method
     * @private
     * @returns {string}
     */
    _getModallessPage(): string {
        const pageReferences = {
            [Page.EVENTS]:  ``,
            [Page.ABOUT]:   `About`,
            [Page.MUSIC]:   `Media`,
            [Page.NEWS]:    `News`,
            [Page.RECORDS]: `Records`,
            [Page.ADMIN]:   `ADMIN`,
            [Page.NONE]:    ``
        };
        return pageReferences[store.getters.currentPage];
    }

    /**
     * convert magic references to their desired values
     * @member module:Core/Router.Href#_parseMagicPageReference
     * @method
     * @private
     * @param {hrefDirectiveObject} data        The request data
     * @returns {string}
     */
    _parseMagicPageReference(data: hrefDirectiveObject): string {
        let returnVar = data.page;
        if (data.page === `$parent`) {
            returnVar = this._getPageParent();
        } else if (data.page === `$modalless`) {
            returnVar = this._getModallessPage();
        }

        return returnVar;
    }

    /**
     * convert a v-href object to a href string
     * @member module:Core/Router.Href#calculate
     * @method
     * @param {hrefDirectiveObject} passedData        The request data
     * @returns {string}
     */
    calculate(passedData: hrefDirectiveObject): string {
        let returnVar = `#`;
        const data: hrefDirectiveObject = Util.parse(passedData);
        if (this._isMagicPageReference(data)) {
            data.page = this._parseMagicPageReference(data);
        }

        if (data.page && data.id) {
            returnVar = this._itemPath(this._parseItemPathData(data));
        } else if (data.page) {
            returnVar = this._pagePath(data);
        } else {
            returnVar = this._rootPath(data);
        }

        if (data.query) {
            returnVar += `?${this._calculateQuery(data.query)}`;
        }

        return returnVar;
    }

    /**
     * An item path request may need its data mutated under certain conditions.
     * For example having a non-uuid id or a cleaned up name.
     * @member module:Core/Router.Href#_parseItemPathData
     * @method
     * @private
     * @param {hrefDirectiveObject} data        The request data
     * @returns {hrefDirectiveObject}
     */
    _parseItemPathData(data: hrefDirectiveObject): hrefDirectiveObject {
        if (data.name) {
            data.name = data.name
                .substr(0, 40)
                .trim()
                .replace(/ /g, `_`)
                .replace(/-/g, `_`)
                .replace(/\\/g, `_`)
                .replace(/\//g, `_`)
                .replace(/\W/g, ``)
                .replace(/([_])\1+/g, `_`);
        }

        if (!data.unencode) {
            data.id = slugid.encode(data.id);
        }
        return data;
    }

    /**
     * Convert an object to a query string
     * @member module:Core/Router.Href#_calculateQuery
     * @method
     * @private
     * @param {Object} query        Query object to be turned into a url query string
     * @returns {string}
     */
    _calculateQuery(query: Object): string {
        return Object.entries(query).map(e => `${[e[0]]}=${encodeURIComponent(e[1])}`).join(`&`);
    }
}

export default new Href();