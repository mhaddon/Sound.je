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
// Node modules
import Optional from "optional-js";
// Site Modules
import { Util } from "nestedbird/core/Util";

/**
 * Saves the current IE version information as a class to the body, so we can do css tricks around this
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Mixins
 */
export const IeVersion = {
    /**
     * When the element is mounted we need to record the IE version
     * @member module:Vue/Mixins.IeVersion#created
     * @method
     */
    created() {
        this.getIEVersion().ifPresent((version: number) => {
            Util.OptionalDOM(`body`)
                .ifPresent(body => body.addClass(`ie ie--${version}`).setAttribute(`ie`, version));
        });
    },
    methods: {
        /**
         * Gets the current Internet Explorer version as an Optional
         * @member module:Vue/Mixins.IeVersion#getIEVersion
         * @method
         * @returns Optional<number>
         */
        getIEVersion(): Optional<number> {
            let version: number = 0;
            const ua = window.navigator.userAgent;
            const msie = ua.indexOf(`MSIE `);
            const trident = ua.indexOf(`Trident/`);
            const edge = ua.indexOf(`Edge/`);

            if (msie > 0) {
                version = parseInt(ua.substring(msie + 5, ua.indexOf(`.`, msie)), 10);
            } else if (trident > 0) {
                version = 11;
            } else if (edge > 0) {
                version = 12;
            }

            return Optional.ofNullable(version || null);
        }
    }
};