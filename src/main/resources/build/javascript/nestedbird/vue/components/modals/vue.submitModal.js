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
import { Router } from "nestedbird/core/Router";
import { Modal } from "nestedbird/vue/mixins";
import { Notification, Util } from "nestedbird/core/Util";

/**
 * This class controls the submit modal for exporting third party data
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 * @augments module:Vue/Mixins.Modal
 */
export const SubmitModal = {
    mixins:   [
        Modal
    ],
    /**
     * The module template
     * @member module:Vue/Components.SubmitModal#template
     * @default #submitModal-template
     * @type string
     */
    template: `#submitModal-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * If an error has occurred, its title is stored here
             * @member module:Vue/Components.SubmitModal#errorTitle
             * @type string
             */
            errorTitle: null,
            /**
             * If an error has occurred, its message is stored here
             * @member module:Vue/Components.SubmitModal#errorMsg
             * @type string
             */
            errorMsg:   null
        };
    },
    methods:  {
        /**
         * Generic On submit callback
         * @member module:Vue/Components.SubmitModal#onSubmit
         * @method
         * @param {Promise<string>} request request promise
         * @param {string} endpoint this url endpoint
         */
        onSubmit(request: Promise<string>, endpoint: string) {
            this.errorTitle = null;
            this.errorMsg = null;
            request
                .then((response: string) => {
                    Util.tryParseJSON(response).ifPresent((data: Object) => {
                        Notification.create(`Successfully Created`).record();
                        Router.redirect(`/records/${data.idBase64}/${endpoint}`);
                    });
                })
                .catch((response: string) => {
                    Util.tryParseJSON(response).ifPresent((data: Object) => {
                        this.errorTitle = data.error;
                        this.errorMsg = data.message;
                    });
                });
        },
        /**
         * On media submit callback
         * @member module:Vue/Components.SubmitModal#onMediaSubmit
         * @method
         * @param {Promise<string>} request request promise
         */
        onMediaSubmit(request: Promise<string>) {
            this.onSubmit(request, `Media`);
        },
        /**
         * On place submit callback
         * @member module:Vue/Components.SubmitModal#onPlaceSubmit
         * @method
         * @param {Promise<string>} request request promise
         */
        onPlaceSubmit(request: Promise<string>) {
            this.onSubmit(request, `Locations`);
        },
        /**
         * On page submit callback
         * @member module:Vue/Components.SubmitModal#onPageSubmit
         * @method
         * @param {Promise<string>} request request promise
         */
        onPageSubmit(request: Promise<string>) {
            this.onSubmit(request, `ScannedPages`);
        },
        /**
         * On event submit callback
         * @member module:Vue/Components.SubmitModal#onEventSubmit
         * @method
         * @param {Promise<string>} request request promise
         */
        onEventSubmit(request: Promise<string>) {
            this.onSubmit(request, `Events`);
        },
        /**
         * On artist submit callback
         * @member module:Vue/Components.SubmitModal#onArtistSubmit
         * @method
         * @param {Promise<string>} request request promise
         */
        onArtistSubmit(request: Promise<string>) {
            this.onSubmit(request, `Artists`);
        }
    }
};

export const VueSubmitModal = Vue.extend(SubmitModal);