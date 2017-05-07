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
import slugid from "slugid";
// Site Modules
/**
 * Showdown`s Extension boilerplate
 *
 * A boilerplate from where you can easily build extensions
 * for showdown
 * https://github.com/showdownjs/extension-boilerplate/blob/master/src/showdown-extension-boilerplate.js
 */
(function (extension) {
    `use strict`;

    // UML - Universal Module Loader
    // This enables the extension to be loaded in different environments
    if (typeof showdown !== `undefined`) {
        // global (browser or nodejs global)
        extension(showdown);
    } else if (typeof define === `function` && define.amd) {
        // AMD
        define([`showdown`], extension);
    } else if (typeof exports === `object`) {
        // Node, CommonJS-like
        module.exports = extension(require(`showdown`));
    } else {
        // showdown was not found so we throw
        throw Error(`Could not find showdown library`);
    }

}(function (showdown) {
    'use strict';

    // This is the extension code per se

    // Here you have a safe sandboxed environment where you can use "static code"
    // that is, code and data that is used accros instances of the extension itself
    // If you have regexes or some piece of calculation that is immutable
    // this is the best place to put them.

    // The following method will register the extension with showdown
    showdown.extension(`showdown-extension-entitylink`, function () {
        'use strict';

        return [
            {
                type:  `lang`,
                regex: `\\@([0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12})\\[(.*)\\]`,
                replace(match, uuidMatch, text) {
                    return `<a href="/resolve/${uuidMatch}">${text}</a>`;
                }
            }, {
                type:  `lang`,
                regex: `\\@([0-9a-zA-Z_]{22})\\[(.*)\\]`,
                replace(match, uuidBase64, text) {
                    const uuidMatch = slugid.decode(uuidBase64);
                    return `<a href="/resolve/${uuidMatch}">${text}</a>`;
                }
            }, {
                type:  `lang`,
                regex: `\\@([0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12})`,
                replace(match, uuidMatch) {
                    return `<a href="/resolve/${uuidMatch}">@${uuidMatch}</a>`;
                }
            }, {
                type:  `lang`,
                regex: `\\@([0-9a-zA-Z_]{22})`,
                replace(match, uuidBase64) {
                    const uuidMatch = slugid.decode(uuidBase64);
                    return `<a href="/resolve/${uuidMatch}">@${uuidMatch}</a>`;
                }
            }];
    });
}));