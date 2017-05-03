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
import { TriggerCallback } from "./_types";

/**
 * This class holds the
 * See [Triggers]{@link module:Core/KeyController.Triggers} for the implementation
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @memberOf module:Core/KeyController
 * @param {TriggerCallback} callback [more info]{@link module:Core/KeyController.Trigger#callback}
 * @param {boolean} strictFocus [more info]{@link module:Core/KeyController.Trigger#strictFocus}
 * @throws {TypeError} callback needs to be a function
 */
export default class Trigger {

    /**
     * Callback function that will be executed when this trigger is called
     *
     * @member module:Core/KeyController.Trigger#callback
     * @type TriggerCallback
     */
    callback: TriggerCallback;

    /**
     * If this is turned on then the callback will not be called if an input or textarea field currently has focus
     * @member module:Core/KeyController.Trigger#strictFocus
     */
    strictFocus: boolean;

    /**
     * @constructor
     */
    constructor(callback: TriggerCallback, strictFocus: boolean) {
        if (typeof callback !== `function`) throw new TypeError(`callback is not a function`);

        this.callback = callback;
        this.strictFocus = strictFocus;
    }

    /**
     * Is the given trigger callable?
     *
     * @member module:Core/KeyController.Trigger#isCallable
     * @method
     * @returns {boolean}
     */
    isCallable(): boolean {
        return !this.strictFocus || !document.querySelectorAll(`input:focus,textarea:focus`).length;
    }

    /**
     * Executes the callback function if {@link module:Core/KeyController.Trigger#isCallable} returns true
     * @member module:Core/KeyController.Trigger#call
     * @method
     * @param {KeyboardEvent} e - The keyboard event that triggered the event to fire
     * @throws {TypeError} arguments must not be null
     */
    call(e: KeyboardEvent) {
        if (!e) throw new TypeError(`e is empty`);

        if (this.isCallable()) {
            this.callback(e);
        }
    }
};