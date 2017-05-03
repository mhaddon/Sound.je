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
import store from "nestedbird/vue/store";
import { InfiniteController } from "nestedbird/core/InfiniteController";

/**
 * This class controls the search page
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 */
export const SearchPage = {
    /**
     * The module template
     * @member module:Vue/Components.SearchPage#template
     * @default #searchPage-template
     * @type string
     */
    template: `#searchPage-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * The result of the main search query
             * @member module:Vue/Components.SearchPage#searchData
             * @type Object[]
             */
            searchData: [],
            /**
             * The result of the event search query
             * @member module:Vue/Components.SearchPage#eventData
             * @type Object[]
             */
            eventData:  [],
            /**
             * Current page we are on for infinite scrolling
             * @member module:Vue/Components.SearchPage#page
             * @type number
             */
            page:       0,
            /**
             * Amount of elements per page
             * @member module:Vue/Components.SearchPage#limit
             * @type number
             */
            limit:      15,
            /**
             * We want to cache the url query incase they open up a modal that also has a query
             * todo find a way that doesnt require a cache
             * @member module:Vue/Components.SearchPage#queryCache
             * @type string
             */
            queryCache: store.getters.path.query
        };
    },
    computed: {
        /**
         * Get the pages query
         * If the current page is not the search page, then return the cache
         * otherwise just return the query
         * @member module:Vue/Components.SearchPage#query
         * @type string
         */
        query() {
            return ((store.getters.path.url.includes(`/search`)) ? store.getters.path.query : this.queryCache) || ``;
        }
    },
    watch:    {
        /**
         * We need to watch incase they change whats in the search field
         */
        query() {
            this.page = 0;
            this.queryCache = this.query;
            this.searchData = [];
            this.eventData = [];
            this.firstLoad();
        }
    },
    mounted() {
        this.firstLoad();
    },
    methods:  {
        /**
         * Make the first query at page 0
         * @member module:Vue/Components.SearchPage#firstLoad
         * @method
         */
        firstLoad() {
            this.getSearchResults({})
                .then(this.resetInfinite);
            this.getEventResults();
        },
        /**
         * Reset any initialisers which may think the infinite scrolling has ended, back to their
         * default values
         * @member module:Vue/Components.SearchPage#resetInfinite
         * @method
         */
        resetInfinite() {
            InfiniteController.getContainer(window).resetListeners();
        },
        /**
         * Save the search response to searchData and cache the data to the vuex store
         * @member module:Vue/Components.SearchPage#processSearchResponse
         * @method
         */
        processSearchResponse(response: string) {
            Util.tryParseJSON(response).ifPresent((data: Object) => {
                this.searchData = this.searchData.concat(data.content
                    .map(e => Util.clean(e))
                    .map(e => {
                        e.entity = store.dispatch(`save${e.category}`, e.entity);
                        return e;
                    }));
            });
        },
        /**
         * Save the event search response to eventDat and cache the data to the vuex store
         * @member module:Vue/Components.SearchPage#processEventResponse
         * @method
         */
        processEventResponse(response: string) {
            Util.tryParseJSON(response).ifPresent((data: Object) => {
                this.eventData = this.eventData.concat(data.content
                    .filter(e => e.entity.inFuture)
                    .map(e => Util.clean(e))
                    .map(e => {
                        store.dispatch(`saveEvent`, e.entity);
                        return e;
                    }));
            });
        },
        /**
         * Query the server for the search results
         * @member module:Vue/Components.SearchPage#getSearchResults
         * @method
         */
        getSearchResults({ page = this.page, limit = this.limit }): Promise<number> {
            return Ajax.createPromise(`/api/v1/search/?query=${this.query}&page=${page}&limit=${limit}`)
                .then((response: string) => {
                    Util.tryParseJSON(response)
                        .filter(e => e.hasOwnProperty(`content`))
                        .filter(e => e.content.length > 0)
                        .orElseThrow(`End of content`);
                    // todo error is thrown to break promise, this needs to be changed

                    this.processSearchResponse(response);
                })
                .then((): number => {
                    this.page = page;
                    return this.page;
                });
        },
        /**
         * Query the server for the event search results
         * @member module:Vue/Components.SearchPage#getEventResults
         * @method
         */
        getEventResults(): Promise<*> {
            return Ajax.createPromise(`/api/v1/search/events/?query=${this.query}`)
                .then(this.processEventResponse);
        },
        /**
         * Gets the next occurrence of the event
         * @member module:Vue/Components.SearchPage#firstLoad
         * @method
         * @returns {string}
         */
        getNextOccurrence(eventTimes: EventTime[]): string {
            return Util.getNextEventTime(eventTimes)
                .map(e => Util.parseDateTime(e));
        },
        /**
         * Concatenates all the artist names fluently
         * @member module:Vue/Components.SearchPage#processArtistNames
         * @method
         * @returns {string}
         */
        processArtistNames(allArtistNames: string[]): string {
            const artistNames = Util.parse(allArtistNames.filter(n => n) || []);
            const lastName = artistNames.pop();
            return artistNames.length > 0 ? artistNames.join(`, `).concat(` and ${lastName}`) : lastName;
        }
    }
};

export const VueSearchPage = Vue.extend(SearchPage);