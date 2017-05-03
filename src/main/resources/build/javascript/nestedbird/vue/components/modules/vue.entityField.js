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
import { Ajax } from "nestedbird/core/Ajax";

/**
 * Controller for entityfields
 * an entity field is a dropdown where you can select one or multiple elements
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 */
export const EntityField = {
    /**
     * The module template
     * @member module:Vue/Components.EntityField#template
     * @default #entityField-template
     * @type string
     */
    template: `#entityField-template`,
    props:    {
        /**
         * Data about the Schema Element
         * @member module:Vue/Components.EntityField#data
         * @default {}
         * @type Object
         */
        data:     {
            type:    Object,
            default: {}
        },
        /**
         * Array of options
         * @member module:Vue/Components.EntityField#elements
         * @default []
         * @type Object[]
         */
        elements: {
            type:    Array,
            default: []
        },
        /**
         * the name of the type of object this is
         * @member module:Vue/Components.EntityField#type
         * @default ``
         * @type string
         */
        type:     {
            type:    String,
            default: ``
        },
        /**
         * html form name
         * @member module:Vue/Components.EntityField#name
         * @default ``
         * @type string
         */
        name:     {
            type:    String,
            default: ``
        },
        /**
         * will this dropdown allow multiple elements selected
         * @member module:Vue/Components.EntityField#multi
         * @default false
         * @type boolean
         */
        multi:    {
            type:    Boolean,
            default: false
        },
        /**
         * Can extra tags be created
         * @member module:Vue/Components.EntityField#taggable
         * @default false
         * @type boolean
         */
        taggable: {
            type:    Boolean,
            default: false
        }
    },
    data(): Object {
        return {
            /**
             * The currently selected data
             * @member module:Vue/Components.EntityField#selectedData
             * @default false
             * @type array | object
             */
            selectedData: this.data.value
        };
    },
    computed: {
        /**
         * Value of input field
         * @member module:Vue/Components.EntityField#queryValue
         * @type string
         */
        queryValue() {
            return this.getValue(this.selectedData);
        }
    },
    methods:  {
        /**
         * Retrieves the value from the dropdowns object
         * The dropdown can return a simple string as its value, or an entire object that maps the result
         * This function retrieves the value from either
         * @member module:Vue/Components.EntityField#getSelectedValue
         * @method
         * @param {Object} entity the current dropdown value container
         * @returns string
         */
        getSelectedValue(entity: [] | {}): string {
            let returnVar = ``;

            if (Array.isArray(entity) && entity.length) {
                returnVar = entity.reduce((a, b) => `${a}${b.id},`, ``);
            } else if (Util.isObject(entity) && Object.keys(entity).length) {
                returnVar = entity.id;
            } else {
                returnVar = entity || ``;
            }

            return returnVar;
        },
        /**
         * Retrieves the initial value of the input
         * @member module:Vue/Components.EntityField#getValue
         * @method
         * @param {Object | string} value the current value
         * @returns string
         */
        getValue(value: Object | string): string {
            if (!value) {
                return ``;
            }

            let returnVar = value.toString();

            if ((typeof value === `object`) && (value)) {
                returnVar = this.getSelectedValue(value);
            }

            return returnVar;
        },
        /**
         * Create a new tag element
         * todo this is hardcoded for just tags, this needs to work for any element... somehow
         * @member module:Vue/Components.EntityField#addTag
         * @method
         * @param {string} value new tag to add
         */
        addTag(value: string) {
            const data = {
                name: value
            };

            Ajax.createPromise(`/api/v1/Tags/`, `POST`, data)
                .then((response: string) =>
                    Util.tryParseJSON(response)
                        .ifPresent((object) => {
                            this.elements.push(object);
                            this.selectedData.push(object);

                            this.updateSelected();
                        })
                );
        }
    }
};

export const VueEntityField = Vue.extend(EntityField);