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
import ComponentsScoped from "./componentsScoped";
import mixins from "./mixins";
import store from "nestedbird/vue/store";
import { Modal, Page, Router } from "nestedbird/core/Router";
import { MusicPlayer } from "nestedbird/core/musicplayer";
import { Notification } from "nestedbird/core/Util";
import TriggerManager from "nestedbird/core/TriggerManager";
import "./components";

/**
 * Homepage vue model controller
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Pages
 */
export const ViewModel = {
    el:         `#vueController`,
    /**
     * The page template
     * @member module:Vue/Pages.ViewModel#template
     * @default #homepage-template
     * @type string
     */
    template:   `#homepage-template`,
    mixins,
    components: ComponentsScoped,
    created() {
        Router.processFirstPageLoad();

        this.$nextTick(() => {
            TriggerManager.trigger(`onLoad`);
            setTimeout(() => {
                TriggerManager.trigger(`onLoadDelay`);
            }, 250);
        });
    },
    data(): Object {
        return {
            /**
             * current search query
             * @member module:Vue/Pages.ViewModel#searchQuery
             * @type string
             */
            searchQuery: ``
        };
    },
    computed:   {
        /**
         * Retrieves all new notifications
         * @member module:Vue/Pages.ViewModel#notifications
         * @returns {string}
         */
        notifications(): Notification[] {
            return store.getters.notifications;
        },
        /**
         * Retrieves the currently loaded modal id
         * @member module:Vue/Pages.ViewModel#curentModal
         * @returns {number}
         */
        currentModal(): number {
            return store.getters.currentModal;
        },
        /**
         * Checks if the user is an admin
         * @member module:Vue/Pages.ViewModel#isModerator
         * @returns {boolean}
         */
        isModerator(): boolean {
            return store.getters.isModerator;
        }
    },
    methods:    {
        /**
         * Is this page the current opened page?
         * @member module:Vue/Pages.ViewModel#isPage
         * @method
         * @param {string} page        page to check
         * @returns {boolean}
         */
        isPage(page: string): boolean {
            return store.getters.currentPage === Page[page];
        },
        /**
         * Is this modal the current opened modal?
         * @member module:Vue/Pages.ViewModel#isModal
         * @method
         * @param {string} modal        modal to check
         * @returns {boolean}
         */
        isModal(modal: string): boolean {
            return store.getters.currentModal === Modal[modal];
        },
        /**
         * Sets the current page
         * @member module:Vue/Pages.ViewModel#setPage
         * @method
         * @param {string} page         new page
         */
        setPage(page: string) {
            store.commit(`setPage`, { page: Page[page] });
        },
        /**
         * on page swipe left
         * @member module:Vue/Pages.ViewModel#onSwipeLeft
         * @method
         * @param {Object} e            swipe event
         */
        onSwipeLeft(e: Object) {
            this.onSwipe(e, 1);
        },
        /**
         * on page swipe right
         * @member module:Vue/Pages.ViewModel#onSwipeRight
         * @method
         * @param {Object} e            swipe event
         */
        onSwipeRight(e: Object) {
            this.onSwipe(e, -1);
        },
        /**
         * on page swipe, we change the current page
         * @member module:Vue/Pages.ViewModel#onSwipe
         * @method
         * @param {Object} e            swipe event
         * @param {number} movement     direction and speed of swipe, negative numbers go back
         */
        onSwipe(e: Object, movement: number) {
            if (store.getters.isMobile) {
                const container = e.target.closest(`[data-SwipePage]`) || e.target;
                const elements = [].slice.call(container.parentElement.querySelectorAll(`[data-SwipePage]`));
                const currentIndex = elements.findIndex(element => element === container);
                const newIndex = (currentIndex + movement).max(elements.length - 1).min(0);
                store.commit(`setPage`, {
                    page: parseInt(elements[newIndex].getAttribute(`data-SwipePage`), 10)
                });
            }
        },
        /**
         * Is the medialist currently visible?
         * @member module:Vue/Pages.ViewModel#isMediaListVisible
         * @method
         * @returns {boolean}
         */
        isMediaListVisible(): boolean {
            return store.getters.currentPage === Page.MUSIC ||
                (store.getters.currentPage === Page.EVENTS && !store.getters.isMobile);
        },
        /**
         * Is the musicplayer currently visible?
         * @member module:Vue/Pages.ViewModel#isMusicPlayerVisible
         * @method
         * @returns {boolean}
         */
        isMusicPlayerVisible(): boolean {
            return this.isMediaListVisible() ||
                MusicPlayer.isPlayable() ||
                !MusicPlayer.isPaused();
        },
        /**
         * On logout we redirect the user
         * @member module:Vue/Pages.ViewModel#onLogout
         * @method
         * @param {Promise<string>} request
         */
        onLogout(request: Promise<string>) {
            request
                .then((response: string) => {
                    store.dispatch(`processSession`, response);
                    Router.redirect(`/`);
                });
        },
        /**
         * On search request, we redirect to search page
         * @member module:Vue/Pages.ViewModel#searchSite
         * @method
         * @param {Event} e     form submission event
         */
        searchSite(e: Event) {
            e.preventDefault();
            Router.redirect(`/search/${this.searchQuery}`);
        }
    }
};

export const VueViewModal = new Vue(ViewModel);