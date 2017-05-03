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
import Optional from "optional-js";
// Site Modules
import { Notification, Util } from "nestedbird/core/Util";
import store from "nestedbird/vue/store";
import { Modal } from "nestedbird/vue/mixins";

/**
 * This class controls the password reset modal
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 * @augments module:Vue/Mixins.Modal
 */
export const PasswordResetModal = {
    mixins:   [
        Modal
    ],
    /**
     * The module template
     * @member module:Vue/Components.PasswordResetModal#template
     * @default #passwordResetModal-template
     * @type string
     */
    template: `#passwordResetModal-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * If there is an error, this is the title of the error
             * @member module:Vue/Components.PasswordResetModal#errorTitle
             * @type string
             */
            errorTitle: null,
            /**
             * If there is an error, this is the message of the error
             * @member module:Vue/Components.PasswordResetModal#errorMsg
             * @type string
             */
            errorMsg:   null
        };
    },
    computed: {
        /**
         * Retrieves the query tokenId as an optional
         * @member module:Vue/Components.PasswordResetModal#tokenId
         * @type Optional<string>
         */
        tokenId(): Optional<string> {
            return Optional.ofNullable(store.getters.pathQuery.t || null);
        },
        /**
         * Retrieves the query userId as an optional
         * @member module:Vue/Components.PasswordResetModal#userId
         * @type Optional<string>
         */
        userId(): Optional<string> {
            return Optional.ofNullable(store.getters.pathQuery.u || null);
        }
    },
    methods:  {
        /**
         * on password reset callback
         * @member module:Vue/Components.EditRecordModal#onPasswordReset
         * @method
         * @param {Promise<string>} request the request promise
         */
        onPasswordReset(request: Promise<string>) {
            this.errorTitle = null;
            this.errorMsg = null;
            request
                .then(() => {
                    Notification.create(`Password Reset`).record();
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

export const VuePasswordResetModal = Vue.extend(PasswordResetModal);