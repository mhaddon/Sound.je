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
import Listener from "./Listener";
import ScrollContainer from "./ScrollContainer";
import { Util } from "nestedbird/core/Util";

/**
 * This singleton controller handles all the containers that feature infinite scrolling
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @singleton
 * @memberOf module:Core/InfiniteController
 */
class InfiniteController {
    /**
     * Stores an array of all scroll containers
     *
     * @member module:Core/InfiniteController.InfiniteController#_scrollContainers
     * @type ScrollContainer[]
     * @defaultvalue []
     * @private
     */
    _scrollContainers: ScrollContainer[];

    /**
     * @constructor
     */
    constructor() {
        this._scrollContainers = [];
    }

    /**
     * Add a container which holds listeners
     * @member module:Core/InfiniteController.InfiniteController#addContainer
     * @method
     * @param {Element} container       DOM element to attach to
     * @throws {TypeError} arguments must not be null
     */
    addContainer(container: Element): ScrollContainer {
        if (!Util.isElement(container) && container !== window) throw new TypeError(`container is not a DOM Element`);

        if (!this.getContainer(container)) {
            this._scrollContainers.push(new ScrollContainer(container));
            const newContainer = this._scrollContainers[this._scrollContainers.length - 1];

            // TODO: Recode this, very bodgely loads additional content if the first load didnt load enough
            setTimeout(() => {
                newContainer.onLoad();
            }, 2000);
            return newContainer;
        }
        return this.getContainer(container);
    }

    /**
     * Add new listener to a container
     * @member module:Core/InfiniteController.InfiniteController#addListener
     * @method
     * @param {Element} container       DOM Element of container to attach to
     * @param {Listener} listener       Listener to attach
     * @throws {TypeError} arguments must not be null
     */
    addListener(container: Element, listener: Listener) {
        if (!Util.isElement(container) && container !== window) throw new TypeError(`container is not a DOM Element`);

        let scrollContainer = this.getContainer(container);
        if (!scrollContainer) {
            scrollContainer = this.addContainer(container);
        }
        if (scrollContainer) {
            scrollContainer.addListener(listener);
        }
    }

    /**
     * Removes a listener from all containers
     * @member module:Core/InfiniteController.InfiniteController#removeListener
     * @method
     * @param {Element} el       DOM Element
     * @throws {TypeError} arguments must not be null
     */
    removeListener(el: Element) {
        if (!Util.isElement(el) && el !== window) throw new TypeError(`el is not a DOM Element`);

        for (const scrollContainer of this._scrollContainers) {
            scrollContainer.removeListener(el);
        }
    }

    /**
     * Retrieves a container by DOM Element
     * @member module:Core/InfiniteController.InfiniteController#getContainer
     * @method
     * @param {Element} container       DOM Element
     * @returns {ScrollContainer}
     * @throws {TypeError} arguments must not be null
     */
    getContainer(container: Element): ScrollContainer {
        if (!Util.isElement(container) && container !== window) throw new TypeError(`container is not a DOM Element`);

        return this._scrollContainers.find(e => e.getElement() === container);
    }
}

export default new InfiniteController();
