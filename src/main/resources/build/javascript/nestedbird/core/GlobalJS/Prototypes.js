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

/**
 * This code contains native class prototypes that has been extended with new functionality
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @member module:Core/GlobalJS.Prototypes
 */
const Prototypes = {};

if (typeof Array.prototype.chain === `undefined`) {
    /**
     *
     * @source https://github.com/hemanth/functional-programming-jargon#monad
     * @param {function} fn
     */
    // eslint-disable-next-line no-extend-native
    (Array: any).prototype.chain = function chain(fn) {
        return this.reduce((a, b) => a.concat(fn(b)), []);
    };
}

if (typeof Array.prototype.flatMap === `undefined`) {
    /**
     * flatmaps an array
     * @source https://gist.github.com/samgiles/762ee337dff48623e729
     * @param {function} fn
     * @returns {*}
     */
    (Array: any).prototype.flatMap = function flatMap(fn) {
        return Array.prototype.concat.apply([], this.map(fn));
    };
}

if (typeof Number.prototype.pad === `undefined`) {
    /**
     * pads a number to a string
     * @param {number} size     minimum string length
     * @returns {string}
     */
    // eslint-disable-next-line no-extend-native
    (Number: any).prototype.pad = function pad(size): string {
        return this.toString().pad(size);
    };
}

if (typeof String.prototype.pad === `undefined`) {
    /**
     * returns a padded string
     * @param {number} size     minimum string length
     * @returns {string}
     */
    // eslint-disable-next-line no-extend-native
    (String: any).prototype.pad = function pad(size): string {
        let s = this;
        while (s.length < (size || 2)) {
            s = `0${s}`;
        }
        return s;
    };
}

if (typeof Number.prototype.min === `undefined`) {
    /**
     * Defines the minimum value this variable should be.
     * For example:
     * (100).min(200)
     * Will return 200 because 200 is the minimum amount and 100 is smaller than that
     * (200).min(100)
     * will return 200 because it exceeds the minimum
     * @param {number} num      minimum value
     * @returns {number}
     */
    // eslint-disable-next-line no-extend-native
    (Number: any).prototype.min = function min(num): number {
        return (this < num) ? num : this;
    };
}

if (typeof Number.prototype.max === `undefined`) {
    /**
     * Defines the maximum value this variable should be.
     * For example:
     * (200).max(100)
     * Will return 100 because 100 is the maximum amount and 200 is greater than that
     * (100).max(200)
     * will return 100 because it is smaller than the maximum
     * @param {number} num      maximum value
     * @returns {number}
     */
    // eslint-disable-next-line no-extend-native
    (Number: any).prototype.max = function max(num: number): number {
        return (this > num) ? num : this;
    };
}

if (typeof Element.prototype.hasClass === `undefined`) {
    /**
     * Does an element have a specific class
     * @param {string} className        Classname to search
     * @returns {boolean}
     */
    // eslint-disable-next-line no-extend-native
    (Element: any).prototype.hasClass = function hasClass(className): boolean {
        let classList;
        if (this.classList) {
            classList = this.classList.contains(className);
        } else {
            classList = new RegExp(`(^| )${className}( |$)`, `gi`).test(this.className);
        }
        return classList;
    };
}

if (typeof Element.prototype.outerSize === `undefined`) {
    /**
     * Calculate the outsize of an element
     * @source http://stackoverflow.com/a/23270007
     * @returns {{width: (Number|number), height: (Number|number)}}
     */
    // eslint-disable-next-line no-extend-native
    (Element: any).prototype.outerSize = function outerSize() {
        const style = this.currentStyle || window.getComputedStyle(this);
        const width = this.offsetWidth || 0;
        const height = this.offsetHeight || 0;
        const marginWidth = (parseFloat(style.marginLeft) || 0) + (parseFloat(style.marginRight) || 0);
        const marginHeight = (parseFloat(style.marginTop) || 0) + (parseFloat(style.marginBottom) || 0);
        const paddingWidth = (parseFloat(style.paddingLeft) || 0) + (parseFloat(style.paddingRight) || 0);
        const paddingHeight = (parseFloat(style.paddingTop) || 0) + (parseFloat(style.paddingBottom) || 0);
        const borderWidth = (parseFloat(style.borderLeftWidth) || 0) + (parseFloat(style.borderRightWidth) || 0);
        const borderHeight = (parseFloat(style.borderTopWidth) || 0) + (parseFloat(style.borderBottomWidth) || 0);

        return {
            width:  parseInt((width + marginWidth) - (paddingWidth + borderWidth), 10) || 0,
            height: parseInt((height + marginHeight) - (paddingHeight + borderHeight), 10) || 0
        };
    };
}

if (typeof Element.prototype.addClass === `undefined`) {
    /**
     * Add a class to an element
     * @param {string} className    class to add
     * @return {this|Element}
     */
    // eslint-disable-next-line no-extend-native
    (Element: any).prototype.addClass = function addClass(className): Element {
        if (this.classList) {
            this.classList.add(className);
        } else {
            this.className += ` ${className}`;
        }
        return this;
    };
}

if (typeof Element.prototype.removeClass === `undefined`) {
    /**
     * remove a class from an element
     * @param {string} className    class to remove
     * @returns {this|Element}
     */
    // eslint-disable-next-line no-extend-native
    (Element: any).prototype.removeClass = function removeClass(className): Element {
        if (this.classList) {
            this.classList.remove(className);
        } else {
            const regex = new RegExp(`(^|\\b)${className.split(` `).join(`|`)}(\\b|$)`, `gi`);
            this.className = this.className.replace(regex, ` `);
        }

        return this;
    };
}