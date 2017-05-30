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
import store from "nestedbird/vue/store";
import { Modal } from "nestedbird/vue/mixins";
import { SchemaData, SchemaReader } from "nestedbird/core/SchemaReader";
import { Notification, Util } from "nestedbird/core/Util";

/**
 * This class controls the modal for editing a record
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 * @augments module:Vue/Mixins.Modal
 */
export const EditRecordModal = {
    mixins:   [
        Modal
    ],
    /**
     * The module template
     * @member module:Vue/Components.EditRecordModal#template
     * @default #editRecordModal-template
     * @type string
     */
    template: `#editRecordModal-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * The id we are editing
             * @member module:Vue/Components.EditRecordModal#id
             * @type string
             */
            id:         store.getters.pathIdDecoded,
            /**
             * The record type we are editing
             * @member module:Vue/Components.EditRecordModal#name
             * @type string
             */
            name:       store.getters.pathName,
            /**
             * The records parsed form data
             * @member module:Vue/Components.EditRecordModal#formData
             * @type Object[]
             */
            formData:   [],
            /**
             * If there is an error, this is the title of the error
             * @member module:Vue/Components.EditRecordModal#errorTitle
             * @type string
             */
            errorTitle: null,
            /**
             * If there is an error, this is the message of the error
             * @member module:Vue/Components.EditRecordModal#errorMsg
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
         * The api endpoint url of the specific element
         * @member module:Vue/Components.EditRecordModal#endpointUrl
         * @type string
         */
        endpointUrl(): string {
            return `${this.APIUrl}/${this.id}`;
        },
        /**
         * The api endpoint url
         * @member module:Vue/Components.EditRecordModal#APIUrl
         * @type string
         */
        APIUrl(): string {
            return `/api/v1/${this.name}`;
        }
    },
    methods:  {
        /**
         * Retrieve the schema data from the API endpoint and save it to formData
         * Returns a promise containing the results
         * @member module:Vue/Components.EditRecordModal#getData
         * @method
         * @returns Promise<Object>
         */
        getData(): Promise<Object> {
            return SchemaReader.create(this.APIUrl).retrieveSingle(this.id)
                .then((schemaDataOptional: Optional<SchemaData>) => {
                    schemaDataOptional.ifPresent((schemaData: SchemaData) => {
                        this.formData = schemaData.elements[0];
                        this.$forceUpdate();
                    });
                });
        },
        /**
         * Save the record, to the database
         * @member module:Vue/Components.EditRecordModal#onSave
         * @method
         * @param {Promise<string>} request the request promise
         */
        onSave(request: Promise<string>) {
            this.errorTitle = null;
            this.errorMsg = null;
            request
                .then(() => {
                    Notification.create(`Successfully Saved`).record();
                    this.getData();
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

export const VueEditRecordModal = Vue.extend(EditRecordModal);
