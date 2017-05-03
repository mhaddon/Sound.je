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
import { Router } from "nestedbird/core/Router";
import { Notification, Util } from "nestedbird/core/Util";

/**
 * A Vue mixin that adds user session/Authorisation checking
 * @copyright Copyright (C) 2017  Michael Haddon
 * @Author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Mixins
 */
export const Auth = {
    data(): Object {
        return {
            /**
             * How often in ms will we poll the server for session information
             * @member module:Vue/Mixins.Auth#pollFrequency
             * @type number
             * @default 30000
             */
            pollFrequency: 30000,

            /**
             * Current permissions so we can check to see if permissions change
             * @member module:Vue/Mixins.Auth#currentPermissions
             * @type string[] | null
             * @default null
             */
            currentPermissions: null
        };
    },
    created() {
        this.pollSession();
    },
    methods: {
        /**
         * Continuously polls the server for session changes
         * @member module:Vue/Mixins.Auth#pollSession
         * @method
         */
        pollSession() {
            store.dispatch(`updateSession`)
                .then(() => {
                    this.checkAuthorities();
                    window.setTimeout(() => {
                        /**
                         * requestAnimationFrame ensures we only poll when the window has focus
                         */
                        window.requestAnimationFrame(this.pollSession.bind(this));
                    }, this.pollFrequency);
                });
        },
        /**
         * If the users Authorities changes, we need to reprocess the page through the router
         * @member module:Vue/Mixins.Auth#checkAuthorities
         * @method
         */
        checkAuthorities() {
            store.getters.authorities.ifPresent((Authorities: string[]) => {
                if (!Util.is(this.currentPermissions, Authorities)) {
                    this.currentPermissions = Authorities;
                    Notification.clear();
                    Router.parse(window.location.pathname);
                }
            });
        }
    }
};