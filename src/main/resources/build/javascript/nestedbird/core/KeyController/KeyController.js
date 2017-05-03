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
import Trigger from "./Trigger";
import Triggers from "./Triggers";
import { TriggerCallback } from "./_types";

/**
 * Singleton class that attaches to the DOM and listens for and manages KeyPress events
 * See [Triggers]{@link module:Core/KeyController.Triggers} for the implementation
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @memberOf module:Core/KeyController
 * @param {Object} [triggers] prebuilt list of triggers
 */
class KeyController {
    /**
     * The current state of all recorded keys, it only records keys it has seen being pressed
     *
     * @member module:Core/KeyController.KeyController#states
     * @type Object
     * @defaultvalue {}
     */
    states: { [key: number]: boolean };

    /**
     * The current triggers bound to each key
     *
     * @member module:Core/KeyController.KeyController#triggers
     * @type Object
     * @defaultvalue {}
     */
    triggers: { [key: number]: Trigger[] };

    /**
     * Builds an empty KeyController
     * @constructor
     */
    constructor(triggers: ?{ [key: number]: TriggerCallback }) {
        this.states = {};
        this.triggers = {};

        if (typeof triggers === `object`) {
            this.importTriggers(triggers);
        }
    }

    /**
     * Imports triggers from an Object
     * @member module:Core/KeyController.KeyController#importTriggers
     * @method
     * @param {Object} triggers
     * @throws {TypeError} arguments must not be null
     */
    importTriggers(triggers: { [key: number]: TriggerCallback }) {
        if (typeof triggers !== `object`) throw new TypeError(`triggers is not an object`);
        Object.keys(triggers).forEach((index) => {
            this.addTrigger(index, triggers[index]);
        });
    }

    /**
     * When a key is released this event fires to record that the button is not being pressed
     *
     * @member module:Core/KeyController.KeyController#up
     * @method
     * @param {number} code - keycode
     * @throws {TypeError} arguments must not be null
     */
    up(code: number) {
        if (!code) throw new TypeError(`code is empty`);
        this.states[code] = false;
    }

    /**
     * When the key is pressed down we trigger the event and update the record
     *
     * @member module:Core/KeyController.KeyController#down
     * @method
     * @param {number} code - keycode
     * @param {KeyboardEvent} keyboardEvent
     * @throws {TypeError} arguments must not be null
     */
    down(code: number, keyboardEvent: KeyboardEvent) {
        if (!code) throw new TypeError(`code is empty`);
        if (!keyboardEvent) throw new TypeError(`keyboardEvent is empty`);

        if (!this.isDown(code)) {
            this.trigger(code, keyboardEvent);
        }
        this.states[code] = true;
    }

    /**
     * Is the key recorded as being pressed
     * Returns false if the key has not been recorded yet
     *
     * @member module:Core/KeyController.KeyController#isDown
     * @method
     * @param {number} code - keycode
     * @returns {boolean}
     * @throws {TypeError} arguments must not be null
     */
    isDown(code: number): boolean {
        if (!code) throw new TypeError(`code is empty`);

        return !!this.states[code];
    }

    /**
     * Trigger a KeyPress by looping over each of the keys trigger and calling each function
     *
     * @member module:Core/KeyController.KeyController#trigger
     * @method
     * @param {number} code - keycode
     * @param {KeyboardEvent} keyboardEvent
     * @throws {TypeError} arguments must not be null
     */
    trigger(code: number, keyboardEvent: KeyboardEvent) {
        if (!code) throw new TypeError(`code is empty`);
        if (!keyboardEvent) throw new TypeError(`keyboardEvent is empty`);

        if (Array.isArray(this.triggers[code])) {
            for (const trigger of this.triggers[code]) {
                trigger.call(keyboardEvent);
            }
        }
    }

    /**
     * Add a trigger to the list of triggers
     *
     * @member module:Core/KeyController.KeyController#addTrigger
     * @method
     * @param {number} code - keycode
     * @param {TriggerCallback} fn - callback function
     * @param {boolean} [strictFocus=true] - Ensure that the users keypress is not intended for an input/textarea element
     * @throws {TypeError} arguments must not be null
     */
    addTrigger(code: number, fn: TriggerCallback, strictFocus: boolean = true) {
        if (!fn) throw new TypeError(`fn is empty`);
        if (!code) throw new TypeError(`code is empty`);

        if (!Array.isArray(this.triggers[code])) {
            this.triggers[code] = [];
        }

        this.triggers[code].push(new Trigger(fn, strictFocus));
    }
}

// Init singleton

const keyController = new KeyController();

keyController.importTriggers(Triggers);

/**
 * The below events update the keyControllers key states and trigger the events
 */

window.addEventListener(`keyup`, (e: KeyboardEvent) => {
    keyController.up(e.keyCode);
});

window.addEventListener(`keydown`, (e: KeyboardEvent) => {
    if (!keyController.isDown(e.keyCode)) {
        keyController.down(e.keyCode, e);
    }
});

export default keyController;