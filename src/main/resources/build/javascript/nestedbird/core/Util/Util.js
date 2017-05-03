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
import Optional from "optional-js";
import showdown from "showdown";
import htmlentities from "he";
import { parse, toSeconds } from "iso8601-duration";

// Site Modules
/**
 * This class is used in so many modules that it is unwise for it to require modules globally
 * as it may get circular requires
 */

/**
 * This class is a utility class that holds various useful methods
 * This class holds no state and all the methods are static
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @memberOf module:Core/Util
 */
export default class Util {

    /**
     * Calculates the most appropriate starttime for an event when a start time is unknown.
     * It will either return the next occurrence of the event in the future, or if it has no future occurrences
     * it will return the last previous occurrence.
     * Returns an optional incase this event never has had or will have an occurrence
     *
     * @member module:Core/Util.Util#getNextEventTime
     * @method
     * @static
     * @param {EventTime[]} eventTimes          array of event times to scan for next occurrence
     * @returns Optional<number>
     */
    static getNextEventTime(eventTimes: EventTime[]): Optional<number> {
        let nextEventTime = null;
        let previousEventTime = null;
        const currentTime = (new Date()).getTime();

        if (eventTimes && eventTimes.length > 0) {
            const potentialTimes = [];
            eventTimes.forEach((time: EventTime) => {
                potentialTimes.push(time.startTime);

                const startTime = time.startTime;

                if ((time.repeatTime) && (toSeconds(parse(time.repeatTime)) >= 60 * 60 * 24)) {
                    const finalTime = currentTime + (60 * 60 * 24 * 7 * 6 * 1000); // 6 weeks
                    const lastTime = time.repeatEnd ? time.repeatEnd : finalTime;
                    const newLastTime = lastTime <= finalTime ? lastTime : finalTime;

                    const timeDifference = newLastTime - startTime;

                    if (timeDifference > 0) {
                        const repeatTime = toSeconds(parse(time.repeatTime)) * 1000;
                        const occurrences = Math.floor(timeDifference / repeatTime, 10);

                        for (let i = 1; i <= occurrences; i++) {
                            potentialTimes.push(startTime + (repeatTime * i));
                        }
                    }
                }
            });
            nextEventTime = Math.min(...potentialTimes.filter(e => e > currentTime));
            previousEventTime = Math.max(...potentialTimes.filter(e => e <= currentTime));
        }

        return Optional.ofNullable(isFinite(nextEventTime) ? nextEventTime : previousEventTime);
    }

    /**
     * Deep cleans an object, string or array of htmlentities
     * @param {*} e
     * @returns {*}
     */

    /**
     * Deep cleans an object, string, array or number
     * It processes htmlentities
     *
     * @member module:Core/Util.Util#clean
     * @method
     * @static
     * @param {T} e      entity you want to clean
     * @returns T
     */
    static clean<T>(e: T): T {
        let returnVar = e;

        if (Array.isArray(e)) {
            returnVar = this._cleanArray(e);
        } else if (typeof e === `object`) {
            returnVar = this._cleanObject(e);
        } else if (typeof e === `string`) {
            returnVar = this._htmlDecode(e);
        }

        return returnVar;
    }

    /**
     * Deep cleans an object
     * It removes processes htmlentities
     *
     * @see module:Core/Util.Util#clean
     * @member module:Core/Util.Util#_cleanObject
     * @method
     * @private
     * @static
     * @param {Object} e      Object you want to clean
     * @returns Object
     */
    static _cleanObject(e: Object): Object {
        for (const property in e) {
            // eslint-disable-next-line
            if (e.hasOwnProperty(property)) {
                e[property] = this.clean(e[property]);
            }
        }
        return e;
    }

    /**
     * Deep cleans an array
     * It processes htmlentities
     *
     * @see module:Core/Util.Util#clean
     * @member module:Core/Util.Util#_cleanArray
     * @method
     * @static
     * @private
     * @param {T[]} array      Array you want to clean
     * @returns T[]
     */
    static _cleanArray<T>(array: T[]): T[] {
        return array.map(e => this.clean(e));
    }

    /**
     * cleans a string using the textarea trick
     * It processes htmlentities
     *
     * @see module:Core/Util.Util#clean
     * @member module:Core/Util.Util#_htmlDecode
     * @method
     * @static
     * @private
     * @param {string} value      String you want to clean
     * @returns string
     */
    static _htmlDecode(value: string): string {
        const elem = document.createElement(`textarea`);
        elem.innerHTML = value;
        return elem.value;
    }

    /**
     * Converts a Map to an array of objects
     *
     * @member module:Core/Util.Util#convertMapToArray
     * @method
     * @static
     * @param {Map} e      Map to convert to an array
     * @returns Array<mixed>
     */
    static convertMapToArray(e: Map): Array<mixed> {
        const arr = [];
        for (const key of e.keys()) {
            if (e.has(key)) {
                const newObject = {};
                const objectElement = e.get(key);

                for (const objectKey in objectElement) {
                    if (objectElement.hasOwnProperty(objectKey)) {
                        if ((typeof objectElement[objectKey] === `object`) &&
                            objectElement[objectKey] !== null && !Array.isArray(objectElement[objectKey])) {
                            newObject[objectKey] = Util.convertMapToArray(objectElement[objectKey]);
                        } else {
                            newObject[objectKey] = objectElement[objectKey];
                        }
                    }
                }

                arr.push(newObject);
            }
        }
        return arr;
    }

    /**
     * Parses a unix timestamp to a time string in the format HH:MM am/pm
     *
     * @member module:Core/Util.Util#parseTime
     * @method
     * @static
     * @param {number} time      time since unix epoch
     * @returns string
     */
    static parseTime(time: number): string {
        const date = new Date(time);
        const hour = ((date.getHours() % 12) === 0) ? `12` : (date.getHours() % 12);
        const postfix = ((date.getHours() <= 12) ? `am` : `pm`);
        return `${hour.pad(2)}:${date.getMinutes().pad(2)}${postfix}`;
    }

    /**
     * Parses a unix timestamp to a date string in the format:
     * Day Date of Month
     *
     * @member module:Core/Util.Util#parseDateTime
     * @method
     * @static
     * @param {number} time      time since unix epoch
     * @returns string
     */
    static parseDateTime(time: number): string {
        const date = new Date(time);
        const days = [`Sunday`, `Monday`, `Tuesday`, `Wednesday`, `Thursday`, `Friday`, `Saturday`];
        const months = [`Jan`, `Feb`, `Mar`, `Apr`, `May`, `Jun`, `Jul`, `Aug`, `Sep`, `Oct`, `Nov`, `Dec`];
        const postfix = Util.getDatePostFix(date.getDate());

        return `${days[date.getDay()]} ${date.getDate()}${postfix} of ${months[date.getMonth()]}`;
    }

    /**
     * Retrieves the postfix of a given date.
     * IE: st, nd, rd, th
     *
     * @member module:Core/Util.Util#getDatePostFix
     * @method
     * @static
     * @param {number} date      date in the current month
     * @returns string
     */
    static getDatePostFix(date: number): string {
        let postFix = `th`;
        date = date.toString();
        if (date < 10 || date > 20) {
            /*eslint-disable */
            switch (parseInt(date.substr(-1), 10)) {
                case 1:
                    postFix = `st`;
                    break;
                case 2:
                    postFix = `nd`;
                    break;
                case 3:
                    postFix = `rd`;
                    break;
                default:
                    postFix = `th`;
                    break;
            }
            /*eslint-enable */
        }
        return postFix;
    }

    /**
     * Deep clones an object with JSON.
     * In theory would also work with other datatypes
     * Also parses vue objects to a format with just their data
     * Returns a blank object if the passed in value is falsey
     *
     * @member module:Core/Util.Util#parse
     * @method
     * @static
     * @param {T} e             entity to parse
     * @returns T | Object
     */
    static parse<T>(e: T): T | Object {
        return e ? JSON.parse(JSON.stringify(e)) : {};
    }

    /**
     * Strips array of falsey values while also trimming string values
     *
     * @member module:Core/Util.Util#stripArray
     * @method
     * @static
     * @param {mixed[]} array       array to strip
     * @returns mixed[]
     */
    static stripArray(array: mixed[]): mixed[] {
        return array.filter(e => !!e).map((e) => {
            let returnVar = e;
            if (typeof e === `string`) {
                returnVar = e.trim();
            }
            return returnVar;
        });
    }

    /**
     * A deep object scan that uses json to check similarity.
     *
     * @member module:Core/Util.Util#is
     * @method
     * @static
     * @param {Object} obj1         object to compare
     * @param {Object} obj2         object to compare
     * @returns boolean
     */
    static is(obj1: Object, obj2: Object): boolean {
        return Object.is(JSON.stringify(obj1), JSON.stringify(obj2));
    }

    /**
     * Is the value a html element
     *
     * @member module:Core/Util.Util#isElement
     * @method
     * @static
     * @param {Object} obj         object to compare
     * @returns boolean
     */
    static isElement(obj: Object): boolean {
        try {
            // Using W3 DOM2 (works for FF, Opera and Chrome)
            return obj instanceof HTMLElement;
        } catch (e) {
            // Browsers not supporting W3 DOM2 don't have HTMLElement and
            // an exception is thrown and we end up here. Testing some
            // properties that all elements have. (works on IE7)
            return (typeof obj === `object`) &&
                (obj.nodeType === 1) && (typeof obj.style === `object`) &&
                (typeof obj.ownerDocument === `object`);
        }
    }

    /**
     * Is the value an object and also not an array
     *
     * @member module:Core/Util.Util#isObject
     * @method
     * @static
     * @param {mixed} item         object to compare
     * @returns boolean
     */
    static isObject(item: mixed): boolean {
        return (!!item && typeof item === `object` && !Array.isArray(item) && item !== null);
    }

    /**
     * Merges two objects deeply
     *
     * @member module:Core/Util.Util#mergeDeep
     * @method
     * @static
     * @param {Object} target         object to merge into
     * @param {Object} source         object to merge from
     * @returns boolean
     */
    static mergeDeep(target: Object, source: Object): Object {
        const output = Object.assign({}, target);
        if (Util.isObject(target) && Util.isObject(source)) {
            Object.keys(source).forEach((key) => {
                if (Util.isObject(source[key])) {
                    if (!(key in target)) {
                        Object.assign(output, { [key]: source[key] });
                    } else {
                        output[key] = Util.mergeDeep(target[key], source[key]);
                    }
                } else {
                    Object.assign(output, { [key]: source[key] });
                }
            });
        }
        return output;
    }

    /**
     * Runs a querySelector against the DOM and returns an Optional rather than an element.
     * The reason being is that an element unfound returns null
     *
     * @member module:Core/Util.Util#OptionalDOM
     * @method
     * @static
     * @param {string} querySelector        query selector to search the DOM for
     * @returns Optional<Element>
     */
    static OptionalDOM(querySelector: string): Optional<Element> {
        return Optional.ofNullable(document.querySelector(querySelector));
    }

    /**
     * Runs a querySelectorAll against the DOM and returns an Optional rather than an element.
     * The reason being is that an element unfound returns null
     *
     * @member module:Core/Util.Util#OptionalDOMAll
     * @method
     * @static
     * @param {string} querySelector        query selector to search the DOM for
     * @returns Optional<Element[]>
     */
    static OptionalDOMAll(querySelector: string): Optional<Element[]> {
        return Optional.ofNullable(document.querySelectorAll(querySelector));
    }

    /**
     * Attempts to parse a JSON string to an object without errors
     * @member module:Core/Util.Util#tryParseJSON
     * @method
     * @static
     * @param {string} jsonString           json of object
     * @source http://stackoverflow.com/a/20392392/1507692
     * @returns Optional<Object>
     */
    static tryParseJSON(jsonString): Optional<Object> {
        try {
            const o = JSON.parse(jsonString);

            // Handle non-exception-throwing cases:
            // Neither JSON.parse(false) or JSON.parse(1234) throw errors, hence the type-checking,
            // but... JSON.parse(null) returns null, and typeof null === "object",
            // so we must check for that, too. Thankfully, null is falsey, so this suffices:
            if (o && typeof o === `object`) {
                return Optional.ofNullable(o);
            }
        } catch (e) {
        }

        return Optional.empty();
    }

    /**
     * Converts a Markdown encoded text to HTML.
     * This also strips any HTML tags originally to pretend dodgy tags.
     * @member module:Core/Util.Util#parseMarkdown
     * @method
     * @static
     * @param value
     * @returns {*}
     */
    static parseMarkdown(value: string): string {
        const converter = new showdown.Converter();
        converter.setFlavor(`github`);
        converter.setOption(`simpleLineBreaks`, true);
        converter.setOption(`simplifiedAutoLink`, true);
        converter.setOption(`tables`, true);
        converter.setOption(`tasklists`, true);
        converter.setOption(`openLinksInNewWindow`, true);
        converter.setOption(`parseImgDimensions`, true);
        converter.setOption(`ghMentions`, false);
        return converter.makeHtml(htmlentities.encode(value));
    }
};