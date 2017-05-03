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

/**
 * Vue wrapper for flatpickr, or any later alternative datetime pickers.
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 */
export const DateTimePicker = {
    /**
     * The module template
     * @member module:Vue/Components.DateTimePicker#template
     * @default #dateTimePicker-template
     * @type string
     */
    template: `#dateTimePicker-template`,
    props:    {
        /**
         * The form input name of this field
         * @member module:Vue/Components.DateTimePicker#name
         * @type string
         */
        name:  {
            type:     String,
            required: true
        },
        /**
         * The initial datetime of the picker
         * @member module:Vue/Components.DateTimePicker#value
         * @type string | number
         */
        value: {
            type:     [String, Number],
            required: false,
            default:  0
        }
    },
    data(): Object {
        return {
            /**
             * The date version of the datetime value
             * @member module:Vue/Components.DateTimePicker#dateTime
             * @type Date
             */
            dateTime: new Date(this.value),
            /**
             * Flatpickr configuration object
             * @member module:Vue/Components.DateTimePicker#config
             * @type Object
             */
            config:   {
                enableTime: true,
                altInput:   true,
                altFormat:  `l J \\o\\f F, h:i K`,
                dateFormat: `Y-m-d H:i:S`
            }
        };
    },
    computed: {
        /**
         * The DateTime in a human readable format
         * @member module:Vue/Components.DateTimePicker#parsedDateTime
         * @type string
         */
        parsedDateTime(): string | null {
            let returnVar = null;

            if (this.dateTime.getTime() > 0) {
                returnVar = [
                    this.dateTime.getFullYear(),
                    `-`,
                    (this.dateTime.getMonth() + 1).pad(2),
                    `-`,
                    this.dateTime.getDate().pad(2),
                    ` `,
                    this.dateTime.getHours().pad(2),
                    `:`,
                    this.dateTime.getMinutes().pad(2),
                    `:`,
                    this.dateTime.getSeconds().pad(2)
                ].join(``);
            }

            return returnVar;
        }
    },
    mounted() {
        // Initialise flatpickr
        this.$nextTick(() => {
            setTimeout(() => {
                this.$el.querySelector(`.dateTimePicker`).flatpickr(this.config);
            }, 1000);
        });
    }
};

export const VueDateTimePicker = Vue.extend(DateTimePicker);