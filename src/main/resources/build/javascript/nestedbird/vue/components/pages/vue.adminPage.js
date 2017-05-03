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
 * This class controls the admin page
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 */
export const AdminPage = {
    /**
     * The module template
     * @member module:Vue/Components.AdminPage#template
     * @default #adminPage-template
     * @type string
     */
    template: `#adminPage-template`,
    props:    [],
    data(): Object {
        return {};
    },
    computed: {
        /**
         * Returns the query string to get futur2e unauthorised events
         * @member module:Vue/Components.AdminPage#futureEventsQuery
         * @type string
         */
        futureEventsQuery(): string {
            const date = new Date();
            return `[times.ms:>${date.getTime()}] AND NOT active:TRUE`;
        }
    }
};

export const VueAdminPage = Vue.extend(AdminPage);