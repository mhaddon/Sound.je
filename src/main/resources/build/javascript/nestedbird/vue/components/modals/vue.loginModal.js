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
import { Util } from "nestedbird/core/Util";
import { Modal } from "nestedbird/vue/mixins";
import { Router } from "nestedbird/core/Router";

/**
 * This class controls the Login Modal
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 * @augments module:Vue/Mixins.Modal
 */
export const LoginModal = {
    mixins:   [
        Modal
    ],
    /**
     * The module template
     * @member module:Vue/Components.LoginModal#template
     * @default #loginModal-template
     * @type string
     */
    template: `#loginModal-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * If there is an error, this is the title of the error
             * @member module:Vue/Components.LoginModal#errorTitle
             * @type string
             */
            errorTitle: null,
            /**
             * If there is an error, this is the message of the error
             * @member module:Vue/Components.LoginModal#errorMsg
             * @type string
             */
            errorMsg:   null,
            /**
             * The inserted email of the user
             * @member module:Vue/Components.LoginModal#errorMsg
             * @type string
             */
            email:      null
        };
    },
    methods:  {
        /**
         * On email submission
         * @member module:Vue/Components.LoginModal#onEmailSubmitted
         * @method
         * @param {Promise<string>} request the request promise
         */
        onEmailSubmitted(request: Promise<string>) {
            this.errorTitle = null;
            this.errorMsg = null;
            request
                .then((response: string) => {
                    if (response === `1`) {
                        Router.parse(`/login/fast`);
                        Router.updateQuery(`?email=${this.email}`);
                    } else {
                        Router.redirect(`/login/register`);
                        Router.updateQuery(`?email=${this.email}`);
                    }
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

export const VueLoginModal = Vue.extend(LoginModal);