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
import store from "nestedbird/vue/store";
import { Modal } from "nestedbird/vue/mixins";

/**
 * This class controls the record modal
 * The record modal is the scaffolded data structure
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 * @augments module:Vue/Mixins.Modal
 */
export const RecordModal = {
    mixins:   [
        Modal
    ],
    /**
     * The module template
     * @member module:Vue/Components.RecordModal#template
     * @default #recordModal-template
     * @type string
     */
    template: `#recordModal-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * The name of the record modal database entity
             * @member module:Vue/Components.RecordModal#name
             * @type string
             */
            name:   store.getters.pathName,
            /**
             * The api endpoint url
             * @member module:Vue/Components.RecordModal#APIUrl
             * @type string
             */
            APIUrl: `/`
        };
    },
    created() {
        this.APIUrl = `/api/v1/${this.name}`;
    }
};

export const VueRecordModal = Vue.extend(RecordModal);