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

/**
 * Controller for a Textarea for editing Markdown
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 */
export const MarkdownTextarea = {
    /**
     * The module template
     * @member module:Vue/Components.MarkdownTextarea#template
     * @default #markdownTextarea-template
     * @type string
     */
    template: `#markdownTextarea-template`,
    props:    {
        /**
         * HTML Id of the textarea
         * @member module:Vue/Components.MarkdownTextarea#id
         * @type string
         */
        id:    {
            type:     String,
            required: true
        },
        /**
         * The value of the textarea
         * @member module:Vue/Components.MarkdownTextarea#value
         * @type string
         */
        value: {
            type:     String,
            required: false
        },
        /**
         * HTML form name of the textarea
         * @member module:Vue/Components.MarkdownTextarea#name
         * @type string
         */
        name:  {
            type:     String,
            required: false
        }
    },
    data(): Object {
        return {
            /**
             * The local copy of the value, to not edit the passed value
             * @member module:Vue/Components.MarkdownTextarea#text
             * @type string
             */
            text:      this.value || ``,
            /**
             * The textarea element
             * @member module:Vue/Components.MarkdownTextarea#textarea
             * @type Element
             */
            textarea:  null,
            /**
             * The maximum rows the textarea can have
             * @member module:Vue/Components.MarkdownTextarea#limitRows
             * @type number
             */
            limitRows: 20
        };
    },
    computed: {
        /**
         * The parsed version of the text
         * @member module:Vue/Components.MarkdownTextarea#markdownParsedText
         * @type string
         */
        markdownParsedText() {
            return Util.parseMarkdown(this.text);
        }
    },
    mounted() {
        this.$nextTick(() => {
            this.bindEvents();
            this.resize();
        });
    },
    methods:  {
        /**
         * Binds events to the textarea
         * @member module:Vue/Components.MarkdownTextarea#bindEvents
         * @method
         */
        bindEvents() {
            this.textarea = this.$el.querySelector(`textarea`);

            this.textarea.addEventListener(`input`, this.resize);
        },
        /**
         * Calculates the height of the textarea then resizes the form height appropriately.
         * @member module:Vue/Components.MarkdownTextarea#resize
         * @method
         */
        resize() {
            this.textarea.setAttribute(`rows`, 1);

            const style = window.getComputedStyle(this.textarea);

            const innerScrollHeight =
                parseInt(this.textarea.scrollHeight || 0, 10)
                - parseInt(style.paddingTop || 0, 10)
                - parseInt(style.paddingBottom || 0, 10);

            const lineHeight = parseInt(style.lineHeight, 10);

            const rows = Math.ceil(innerScrollHeight / lineHeight);

            this.textarea.setAttribute(`rows`, rows.max(this.limitRows).min(1));
        }
    }
};

export const VueMarkdownTextarea = Vue.extend(MarkdownTextarea);