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
// Site Modules
import store from "nestedbird/vue/store";
import TriggerManager from "nestedbird/core/TriggerManager";

/**
 * A Vue mixin that adds support for Mobile checking
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Mixins
 */
export const Mobile = {
    data:     {
        /**
         * The identifier of the perspective swap timeout
         * @member module:Vue/Mixins.Mobile#reloadTimeout
         * @type number | null
         * @default null
         */
        reloadTimeout: null
    },
    created() {
        store.commit(`setMobileMode`, this.isMobile);

        window.onresize = () => {
            this.checkPerspective();
            TriggerManager.trigger(`onResize`);
        };
    },
    computed: {
        /**
         * Calculates whether or not the client is a Mobile
         * @member module:Vue/Mixins.Mobile#isMobile
         * @type boolean
         */
        isMobile: {
            cache: false,
            get(): boolean {
                return document.body.clientWidth < 1250;
            }
        }
    },
    methods:  {
        /**
         * Checks the perspective to see if its Mobile or not.
         * This updates the store
         * @member module:Vue/Mixins.Mobile#checkPerspective
         * @method
         */
        checkPerspective() {
            if (store.getters.isMobile !== this.isMobile) {
                store.commit(`setMobileMode`, this.isMobile);

                window.clearTimeout(this.reloadTimeout);
                this.reloadTimeout = window.setTimeout(() => {
                    this.$forceUpdate();
                    TriggerManager.trigger(`onPerspectiveSwap`);
                }, 250);
            }
        }
    }
};