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
import { Ajax } from "nestedbird/core/Ajax";

/**
 * This class adds helper functions to the form directive
 * This is separate as "this" keyword in directives is often undefined
 *
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Directives
 */
class FormDirectiveHelper {
    /**
     * Scans for the forms name elements and generates the query data in an object
     * @param {Element} el                          form element
     * @member module:Vue/Directives.FormDirectiveHelper#generateQueryData
     * @method
     */
    static generateQueryData(el: Element): Object {
        const data = {};
        el.querySelectorAll(`[name]`).forEach((fieldElement: Element) => {
            if (fieldElement.type && fieldElement.type.toLowerCase() === `checkbox`) {
                data[fieldElement.name] = fieldElement.checked;
            } else if (fieldElement.contentEditable === `true`) {
                data[fieldElement.name] = fieldElement.innerText;
            } else {
                data[fieldElement.name] = fieldElement.value;
            }
        });
        return data;
    }

    /**
     * Higher order function that toggles a form
     * @param {Element} el                              element we are toggling
     * @param {boolean} enabled                         is this form enabled
     * @member module:Vue/Directives.FormDirectiveHelper#toggleForm
     * @method
     */
    static toggleForm(el: Element, enabled: boolean): Function {
        return (response: ?string) => {
            el.querySelectorAll(`button, input[type='submit']`)
                .forEach((button: Element) => {
                    button.disabled = !enabled;

                    if (enabled) {
                        button.removeClass(`button--loading`);
                    } else {
                        button.addClass(`button--loading`);
                    }
                });
            el.disabled = !enabled;
            return response;
        };
    }
}

/**
 * A Vue directive that hooks into a forms submit and sends it async with AJAX instead
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Directives
 */
const form = {
    /**
     * On element creation we must bind relevant callbacks
     * @param {Element} el                              Element this is bound to
     * @param {VueDirectiveBinding} binding             The binding information
     * @member module:Vue/Directives.form#bind
     * @method
     */
    bind(el: Element, binding: VueDirectiveBinding) {
        /**
         * on submit event we want to send the data via ajax
         */
        el.addEventListener(`submit`, (e: Event) => {
            e.preventDefault();

            if (!el.disabled) {
                const data = FormDirectiveHelper.generateQueryData(el);
                const toggleFormOn = FormDirectiveHelper.toggleForm(el, true);
                const toggleFormOff = FormDirectiveHelper.toggleForm(el, false);

                toggleFormOff();

                if (typeof binding.value === `function`) {
                    const promise = Ajax
                        .createPromiseWithoutError(
                            el.getAttribute(`action`),
                            el.getAttribute(`method`),
                            data
                        )
                        .then(toggleFormOn);
                    binding.value(promise);
                } else {
                    Ajax.createPromise(el.getAttribute(`action`), el.getAttribute(`method`), data)
                        .then(toggleFormOn);
                }
            }
        });
    }
};

Vue.directive(`form`, form);