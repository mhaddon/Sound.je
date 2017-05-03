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
// Site Modules
import { InfiniteController, Listener } from "nestedbird/core/InfiniteController";

InfiniteController.addContainer(window);

/**
 * This directive is responsible for handling infinite page scrolling
 * After a container has scrolled past a certain point, this directive will call a callback function to load more
 * data.
 * If no more data can be loaded, then the specific listener is silenced and a message is shown to the user.
 *
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Directives
 */
const infinite = {
    /**
     * When the page is loaded we must define the attributes with the default values
     * @param {Element} el                              Element this is bound to
     * @param {VueDirectiveBinding} binding             The binding information
     * @member module:Vue/Directives.infinite#inserted
     * @method
     */
    inserted(el: Element, binding: VueDirectiveBinding) {
        const { page = 0, limit = 15, active = true } = binding.value;
        // TODO: Remove HTML Attribute Pollution
        el.setAttribute(`data-page`, page);
        el.setAttribute(`data-limit`, limit);
        el.setAttribute(`data-active`, active);
    },
    /**
     * Create a listener for this element which will bind the events needed
     * @param {Element} el                              Element this is bound to
     * @param {VueDirectiveBinding} binding             The binding information
     * @member module:Vue/Directives.infinite#bind
     * @method
     */
    bind(el: Element, binding: VueDirectiveBinding) {
        setTimeout(() => {
            let container = window;
            if (binding.value.parent) {
                container = el.closest(binding.value.parent) || window;
            }
            const listener = new Listener(true, el, binding);
            InfiniteController.addListener(container, listener);
        }, 15);
    },
    /**
     * When the element is updated then we need to reprocess the bindings
     * @param {Element} el                              Element this is bound to
     * @param {VueDirectiveBindingUpdate} binding       The binding information
     * @member module:Vue/Directives.infinite#update
     * @method
     */
    update(el: Element, binding: VueDirectiveBindingUpdate) {
        binding.def.inserted(el, binding);
    },
    /**
     * When the element is destroyed we need to remove it from the list of listeners
     * @param {Element} el                              Element this is bound to
     * @member module:Vue/Directives.infinite#unbind
     * @method
     */
    unbind(el: Element) {
        InfiniteController.removeListener(el);
    }
};

Vue.directive(`infinite`, infinite);