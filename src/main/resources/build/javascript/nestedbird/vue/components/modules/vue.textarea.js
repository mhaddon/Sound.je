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
 * This controller handles the song modal
 * @type {Vue}
 */
export const TextArea = {
    /**
     * The components design template, see the "text/x-template" tags in
     * index.html
     */
    template: `#textarea-template`,
    /**
     * The components variables that it inherits from its parent
     */
    props:    [
        `id`,
        `name`,
        `value`
    ],
    /**
     * Every component requires a data feild, but i do not need it as
     * i inherit all the relevent data. So it is filled with nonsense.
     */
    data(): Object {
        return {
            textarea:         null,
            lastScrollHeight: 0,
            limitRows:        15
        };
    },
    mounted() {
        this.$nextTick(() => {
            this.textarea = this.$el.querySelector(`textarea`);

            this.textarea.addEventListener(`input`, this.resize);
            this.resize();
        });
    },
    methods:  {
        /**
         * Calculates the height of the textarea then resizes the form height appropriately.
         */
        resize() {
            this.textarea.setAttribute(`rows`, 1);

            const style = window.getComputedStyle(this.textarea);

            const innerScrollHeight = parseInt(this.textarea.scrollHeight || 0, 10) - parseInt(style.paddingTop || 0, 10) - parseInt(style.paddingBottom || 0, 10);
            const lineHeight = parseInt(style.lineHeight, 10);

            const rows = Math.ceil(innerScrollHeight / lineHeight);

            this.textarea.setAttribute(`rows`, rows.max(this.limitRows).min(1));
        }
    }
};

export const VueTextArea = Vue.extend(TextArea);