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
import { SchemaReader } from "nestedbird/core/SchemaReader";
import { Util } from "nestedbird/core/Util";

/**
 * Vue wrapper for flatpickr, or any later alternative datetime pickers.
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 */
export const MultiEntityField = {
    /**
     * The components design template, see the "text/x-template" tags in
     * index.html
     */
    template: `#multiEntityField-template`,
    /**
     * The components variables that it inherits from its parent
     */
    props:    {
        datasets: {
            type:    Array,
            default: []
        }
    },
    /**
     * Every component requires a data feild, but i do not need it as
     * i inherit all the relevent data. So it is filled with nonsense.
     */
    data(): Object {
        return {};
    },
    computed: {},
    methods:  {}
};

export const VueMultiEntityField = Vue.extend(MultiEntityField);