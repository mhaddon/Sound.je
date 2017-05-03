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
import { Notification, Util } from "nestedbird/core/Util";
import store from "nestedbird/vue/store";
import { Modal } from "nestedbird/vue/mixins";
import { Router } from "nestedbird/core/Router";

/**
 * This class controls the Login Password Modal
 * This appears after we know the users email address, and when we know they are registered
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 * @augments module:Vue/Mixins.Modal
 */
export const LoginPasswordModal = {
    mixins:   [
        Modal
    ],
    /**
     * The module template
     * @member module:Vue/Components.LoginPasswordModal#template
     * @default #loginPasswordModal-template
     * @type string
     */
    template: `#loginPasswordModal-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * If there is an error, this is the title of the error
             * @member module:Vue/Components.LoginPasswordModal#errorTitle
             * @type string
             */
            errorTitle: null,
            /**
             * If there is an error, this is the message of the error
             * @member module:Vue/Components.LoginPasswordModal#errorMsg
             * @type string
             */
            errorMsg:   null
        };
    },
    computed: {
        /**
         * The users email address
         * @member module:Vue/Components.LoginPasswordModal#email
         * @type string
         */
        email(): string {
            return store.getters.pathQuery.email;
        }
    },
    methods:  {
        /**
         * On login submission
         * @member module:Vue/Components.LoginPasswordModal#onLogin
         * @method
         * @param {Promise<string>} request the request promise
         */
        onLogin(request: Promise<string>) {
            this.errorTitle = null;
            this.errorMsg = null;
            request
                .then((response: string) => {
                    store.dispatch(`processSession`, response);
                    Router.redirect(`/`);
                })
                .catch((response: string) => {
                    Util.tryParseJSON(response).ifPresent((data: Object) => {
                        this.errorTitle = data.error;
                        this.errorMsg = data.message;
                    });
                });
        },
        /**
         * On password reset request
         * @member module:Vue/Components.LoginPasswordModal#onRequestPasswordReset
         * @method
         * @param {Promise<string>} request the request promise
         */
        onRequestPasswordReset(request: Promise<string>) {
            this.errorTitle = null;
            this.errorMsg = null;
            request
                .then(() => {
                    Notification.create(`Request submitted`);
                })
                .catch((response: string) => {
                    Util.tryParseJSON(response).ifPresent((data: Object) => {
                        this.errorTitle = data.error;
                        this.errorMsg = data.message;
                    });
                });
        }
    }
};

export const VueLoginPasswordModal = Vue.extend(LoginPasswordModal);