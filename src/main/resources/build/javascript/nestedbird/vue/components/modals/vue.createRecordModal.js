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
import { SchemaReader } from "nestedbird/core/SchemaReader";
import { Router } from "nestedbird/core/Router";

/**
 * This class controls the create record modal
 * The create record modal is used for creating new database records
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 * @augments module:Vue/Mixins.Modal
 */
export const CreateRecordModal = {
    mixins:   [
        Modal
    ],
    /**
     * The module template
     * @member module:Vue/Components.CreateRecordModal#template
     * @default #createRecordModal-template
     * @type string
     */
    template: `#createRecordModal-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * The API class endpoint for the record we want to create
             * @member module:Vue/Components.CreateRecordModal#name
             * @type string
             */
            name:       store.getters.pathName,
            /**
             * The forms schemafield information
             * @member module:Vue/Components.CreateRecordModal#formData
             * @type SchemaField[]
             */
            formData:   [],
            /**
             * If an error has occurred, its title is stored here
             * @member module:Vue/Components.CreateRecordModal#errorTitle
             * @type string
             */
            errorTitle: null,
            /**
             * If an error has occurred, its message is stored here
             * @member module:Vue/Components.CreateRecordModal#errorMsg
             * @type string
             */
            errorMsg:   null
        };
    },
    created() {
        this.getData();
    },
    computed: {
        /**
         * The Url of the API Endpoint
         * @member module:Vue/Components.CreateRecordModal#endpointUrl
         * @type string
         */
        endpointUrl(): string {
            return `/api/v1/${this.name}/`;
        }
    },
    methods:  {
        /**
         * Downloads and processes the forms schema information, then saves it to
         * [formData]{@link module:Vue/Components.CreateRecordModal#formData}
         * @member module:Vue/Components.CreateRecordModal#getData
         * @method
         * @returns Promise<SchemaField[]>
         */
        getData(): Promise<SchemaField[]> {
            return SchemaReader.create(this.endpointUrl).getSchema().then((schemaFields: SchemaField[]) => {
                Vue.set(this, `formData`, schemaFields);
            });
        },
        /**
         * This method processes the response from the server after the data has been saved
         * @member module:Vue/Components.CreateRecordModal#onSave
         * @method
         * @param {Promise<string>} request         the promise that has sent the data to the server
         */
        onSave(request: Promise<string>) {
            this.errorTitle = null;
            this.errorMsg = null;
            request
                .then((response: string) => {
                    Util.tryParseJSON(response).ifPresent((data: Object) => {
                        Notification.create(`Successfully Created`).record();
                        Router.redirect(`/records/${data.idBase64}/${this.name}`);
                    });
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

export const VueCreateRecordModal = Vue.extend(CreateRecordModal);