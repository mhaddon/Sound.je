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
import { Util } from "nestedbird/core/Util";
import { Router } from "nestedbird/core/Router";
import store from "nestedbird/vue/store";

/**
 * This directive automatically updates the href attribute.
 * Options include:
 * page: page of redirection
 * name: name of the page
 * id: id of the element that the page refers to
 * query: an object with the query parameters that will be passed into the new url
 * unencode: true/false, whether or not the id will be base64 encoded/decoded
 *
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Directives
 * @see module:Core/Router.Href
 */
const href = {
    /**
     * Update the href tag with the result of v-href
     * @param {Element} el                              Element this is bound to
     * @param {VueDirectiveBinding} binding             The binding information
     * @member module:Vue/Directives.href#inserted
     * @method
     */
    inserted(el: Element, binding: VueDirectiveBinding) {
        if (typeof binding === `object`) {
            if (typeof binding.value === `object`) {
                el.setAttribute(`href`, Router.hrefDirectiveObjectToUrl(binding.value));
            } else {
                el.setAttribute(`href`, binding.value);
            }
        }
    },
    /**
     * On element creation we must bind relevent callbacks
     * @param {Element} el                              Element this is bound to
     * @member module:Vue/Directives.href#bind
     * @method
     */
    bind(el: Element) {
        /**
         * On click we want to navigate to this page
         */
        el.addEventListener(`click`, (e: Event) => {
            e.preventDefault();
            if (el.hasClass(`modal__remove`)) {
                // window.history.back();
                // store.commit(`setModal`, {});
                Router.redirect(store.getters.lastModallessUrl);
            } else {
                Router.redirect(el.getAttribute(`href`));
            }
        });
    },
    /**
     * When the v-href attribute has changed we need to regenerate the href tag
     * @param {Element} el                              Element this is bound to
     * @param {VueDirectiveBindingUpdate} binding       The binding information
     * @member module:Vue/Directives.href#update
     * @method
     */
    update(el: Element, binding: VueDirectiveBindingUpdate) {
        if (!Util.is(binding.value, binding.oldValue)) {
            binding.def.inserted(el, binding);
        }
    }
};

Vue.directive(`href`, href);