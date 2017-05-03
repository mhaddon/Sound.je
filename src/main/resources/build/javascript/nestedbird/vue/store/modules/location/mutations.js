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
import { Modal, Page } from "nestedbird/core/Router";

export default {
    setPage(state: LocationState, { page = Page.EVENTS }) {
        const targetPage = Object.values(Page).includes(page) ? page : Page.EVENTS;

        const title = document.title; // TODO: get document title from somewhere else
        document.title = title;
        const pageUrl = {
            [`0`]: `/`,
            [`1`]: `/`,
            [`2`]: `/Media`,
            [`3`]: `/News`,
            [`4`]: `/About`
        };

        window.history.pushState({ pageTitle: title }, ``, pageUrl[targetPage]);
        state.currentPage = targetPage;
        state.currentModal = Modal.NONE;
        window.ga(`NestedBird.send`, `pageview`, document.location.pathname);
    },
    setModal(state: LocationState, { modal = Modal.NONE, id = 0 }) {
        state.currentModal = modal;
        state.modalItemId = id;
    },
    setPath(state: LocationState, path: Object) {
        state.path = path;
    },
    setQuery(state: LocationState, pathQuery: Object) {
        state.pathQuery = pathQuery;
    },
    setQueryField(state: LocationState, { name, value }) {
        Vue.set(state.pathQuery, name, value);
    },
    setLocationField(state: LocationState, { name, value }) {
        state[name] = value;
    },
    setMobileMode(state: LocationState, isMobile: boolean) {
        state.isMobile = isMobile;
    },
    setLastModallessUrl(state: LocationState, lastModallessUrl: string) {
        state.lastModallessUrl = lastModallessUrl;
    }
};