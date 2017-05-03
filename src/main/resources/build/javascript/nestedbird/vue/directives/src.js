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
import Vue from "vue/dist/vue";
import uuid from "uuid";
// Site Modules
import { Util } from "nestedbird/core/Util";

/**
 * This class adds helper functions to the src directive
 * This is separate as "this" keyword in directives is often undefined
 *
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Directives
 */
class SrcDirectiveHelper {
    /**
     * Higher order function that sets the background of an image.
     * If no background is detected then it defaults to the HOF background
     *
     * Sets the src tag to the URL of the downloaded image and resets the background to the state it
     * was in prior to this method running
     * @param {Element} el                              element we are changing
     * @param {string} background                       the original background
     * @param {string} id                               the id of this request
     * @member module:Vue/Directives.SrcDirectiveHelper#setBackground
     * @method
     */
    static setBackground(el: Element, background: string, id: string) {
        return (newSrc: string) => {
            if (el.getAttribute(`data-srcid`) === id) {
                el.style.background = background;
                el.src = newSrc;
                el.removeAttribute(`data-srcid`);
            }
        };
    }
}

/**
 * This directive loads a src attribute async
 * Simple add v-src="", to an image tag instead of src, and it will load the image, then load the image.
 * After loading the image it just adds a src tag linked to the image.
 *
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Directives
 */
const src = {
    /**
     * Update elements src tag async
     * @param {Element} el                              Element this is bound to
     * @param {VueDirectiveBinding} binding             The binding information
     * @member module:Vue/Directives.src#bind
     * @method
     */
    inserted(el: Element, binding: VueDirectiveBinding) {
        // Record background of element for later as we overwrite it to show the spinner
        const background: string = el.style.background;
        const src: string = binding.value;
        const image: Image = new Image();
        const id: string = uuid();
        const setBackground = SrcDirectiveHelper.setBackground(el, background, id);

        // Save to id to DOM to avoid loading multiple images at same time and having race conditions
        el.setAttribute(`data-srcid`, id);
        el.style.background = `url('/images/loading.gif') no-repeat center`;

        /**
         * Timeout to make sure this does not interrupt any current processing.
         * Puts this at the bottom of the queue.
         */
        window.setTimeout(() => {
            image.onload = () => {
                setBackground(src);
            };
            image.onerror = () => {
                setBackground(`/images/NoImage.png`);
            };
            image.src = src;
        }, 15);
    },
    /**
     * When the v-src attribute is changed, we need to download the new picture
     * @param {Element} el                              Element this is bound to
     * @param {VueDirectiveBindingUpdate} binding       The binding information
     * @member module:Vue/Directives.src#update
     * @method
     */
    update(el: Element, binding: VueDirectiveBindingUpdate) {
        if (!Util.is(binding.value, binding.oldValue)) {
            binding.def.inserted.bind(this, el, binding);
        }
    }
};

Vue.directive(`src`, src);