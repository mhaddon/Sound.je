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

/**
 * A container holds many listeners.
 * For example, an element with a scrollbar is the container, the content being
 * scrolled is the listener.
 *
 * Containers can have many listeners, the window is a common container
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @memberOf module:Core/InfiniteController
 * @param {Element} el          The DOM element this container is bound to
 */
export default class ScrollContainer {
    /**
     * The DOM element this container is bound to
     *
     * @member module:Core/InfiniteController.ScrollContainer#_el
     * @type Element
     * @private
     */
    _el: Element;

    /**
     * An array of listeners listening to this scrollcontainer
     *
     * @member module:Core/InfiniteController.ScrollContainer#_listeners
     * @type Listener[]
     * @defaultvalue []
     * @private
     */
    _listeners: Listener[];

    /**
     * @constructor
     * @param {Element} el          The DOM element this container is bound to
     */
    constructor(el: Element) {
        this._listeners = [];
        this._el = el;

        this._el.onscroll = () => {
            this.onScroll();
        };
    }

    /**
     * Add a new Listener
     *
     * @member module:Core/InfiniteController.ScrollContainer#addListener
     * @method
     * @param {Listener} listener           adds an additional Listener to this ScrollContainer
     */
    addListener(listener: Listener) {
        if (!this.getListener(listener)) {
            this._listeners.push(listener);
        }
    }

    /**
     * Remove listener based on the element it is linked to
     *
     * @member module:Core/InfiniteController.ScrollContainer#removeListener
     * @method
     * @param {Element} el          Element to remove
     */
    removeListener(el: Element) {
        this._listeners = this._listeners.filter(e => e.el !== el);
    }

    /**
     * Resets all listeners so they can be used again
     *
     * @member module:Core/InfiniteController.ScrollContainer#resetListeners
     * @method
     */
    resetListeners() {
        this._listeners.forEach(e => e.reset());
    }

    /**
     * Get a listener
     *
     * @member module:Core/InfiniteController.ScrollContainer#getListener
     * @method
     * @param {Listener} listener           Listener to find
     */
    getListener(listener: Listener): Listener {
        return this._listeners.find(e => e.el === listener.el);
    }

    /**
     * On container scroll we must loop over each listener and update it
     *
     * @member module:Core/InfiniteController.ScrollContainer#onScroll
     * @method
     */
    onScroll() {
        for (const listener: Listener of this._listeners) {
            if (listener.isActive()) {
                if (this.getBottomOfElement() > listener.getBottomOfElement() - 20) {
                    listener.load().then(() => {
                        this.onLoad();
                    });
                }
            }
        }
    }

    /**
     * Gets the bottom of this bound element
     *
     * @member module:Core/InfiniteController.ScrollContainer#getBottomOfElement
     * @method
     * @returns number
     */
    getBottomOfElement(): number {
        let currentBottom = this._el.scrollTop + this._el.clientHeight;
        if (this._el === window) {
            currentBottom = (document.body.scrollTop ||
                document.documentElement.scrollTop) + document.documentElement.clientHeight;
        }
        return currentBottom;
    }

    /**
     * On data load we check the scroller again
     *
     * @member module:Core/InfiniteController.ScrollContainer#onLoad
     * @method
     */
    onLoad() {
        this.onScroll();
    }

    /**
     * Gets the current bound Element
     * @member module:Core/InfiniteController.ScrollContainer#getElement
     * @method
     * @returns {Element}
     */
    getElement(): Element {
        return this._el;
    }
};