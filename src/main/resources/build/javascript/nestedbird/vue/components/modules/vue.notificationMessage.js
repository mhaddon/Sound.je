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

export const NotificationMessage = {
    template: `#notificationMessage-template`,
    props:    [
        `notification`
    ],
    data(): Object {
        return {
            ttl: 8500
        };
    },
    mounted() {
        setTimeout(() => {
            this.remove();
        }, this.ttl);
    },
    computed: {
        isError(): boolean {
            return this.notification.isError || false;
        },
        status(): string {
            return this.notification.status || ``;
        },
        title(): string {
            return this.notification.errorMessage || ``;
        },
        message(): string {
            return this.notification.message || this.title || this.status || ``;
        }
    },
    methods:  {
        remove() {
            this.notification.remove();
        }
    }
};

export const VueNotificationMessage = Vue.extend(NotificationMessage);