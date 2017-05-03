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
import { Util } from "nestedbird/core/Util";

/**
 * This class is responsible for controlling an infinite scroller listener
 * The listener waits until it hears the signal that its parent element has scrolled to the bottom
 * Then it loads any extra data it wants
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @memberOf module:Core/InfiniteController
 * @param {boolean} active             Whether or not the listener is active
 * @param {Element} el                 dom element the listener refers to
 * @param {ListenerBinding} binding    v-infinite binding
 */
export default class Listener {
    /**
     * Is this Listener active
     *
     * @member module:Core/InfiniteController.Listener#active
     * @type boolean
     */
    active: boolean;

    /**
     * Is this Listener currently loading something
     * While its loading its temporarily disabled
     *
     * @member module:Core/InfiniteController.Listener#loading
     * @defaultvalue false
     * @type boolean
     */
    loading: boolean;

    /**
     * The element this is bound to
     *
     * @member module:Core/InfiniteController.Listener#el
     * @type Element
     */
    el: Element;

    /**
     * Data sent to us from v-infinite
     *
     * @member module:Core/InfiniteController.Listener#binding
     * @type ListenerBinding
     */
    binding: ListenerBinding;


    /**
     * @constructor
     * @param {boolean} active             Whether or not the listener is active
     * @param {Element} el                 dom element the listener refers to
     * @param {ListenerBinding} binding    v-infinite binding
     */
    constructor(active: boolean, el: Element, binding: ListenerBinding) {
        this.active = active;
        this.el = el;
        this.binding = binding;
        this.loading = false;
    }

    /**
     * Is the Listener currently active
     *
     * @member module:Core/InfiniteController.Listener#isActive
     * @method
     * @returns {boolean}
     */
    isActive(): boolean {
        return this.active &&
            this.loading === false &&
            this.el.getAttribute(`data-active`) !== `false` &&
            this.el.offsetParent !== null;
    }

    /**
     * Load new content for the listener
     *
     * @member module:Core/InfiniteController.Listener#load
     * @method
     * @returns {Promise<null>}
     */
    load(): Promise<null> {
        return new Promise((resolve, reject) => {
            this.loading = true; // We are currently loading

            /**
             * Create HTML elements that we will show the user
             */
            const nodeText = document.createElement(`P`);
            nodeText.innerHTML = `Loading...`;
            nodeText.className = `loadmore__text`;

            const nodeLoading = document.createElement(`I`);
            nodeLoading.className = `loadmore__img fa fa-refresh fa-spin`;

            let node = this.el.querySelector(`.loadmore`);
            if (!node) {
                node = document.createElement(`DIV`);
                node.className = `loadmore`;
            }
            while (node.firstChild) node.removeChild(node.firstChild);
            node.style.display = ``;

            node.appendChild(nodeText);
            node.appendChild(nodeLoading);
            this.el.appendChild(node);

            /**
             * Request the data from the request function which is a promise
             */
            this.binding.value.fn({
                page: parseInt(this.el.getAttribute(`data-page`), 10) + 1
            }).then(() => {
                node.style.display = `none`;
                this.loading = false;
                resolve();
            }).catch(() => {
                nodeText.innerHTML = `Sorry, we ran out of content!`;
                nodeLoading.className = `loadmore__img fa fa-frown-o`;
                reject();
            });
        });
    }

    /**
     * Gets the bottom of this bound element
     *
     * @member module:Core/InfiniteController.Listener#getBottomOfElement
     * @method
     * @returns number
     */
    getBottomOfElement(): number {
        return this.el.clientHeight; // + this.el.offsetTop
    }

    /**
     * Resets the Listener to its default state
     *
     * @member module:Core/InfiniteController.Listener#reset
     * @method
     */
    reset() {
        this.loading = false;
        Util.OptionalDOM(`.loadmore`)
            .ifPresent((element: Element) => {
                element.style.display = `none`;
            });
    }
};