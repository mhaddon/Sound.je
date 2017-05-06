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
import Optional from "optional-js";
// Site Modules
import { Ajax } from "nestedbird/core/Ajax";
import { Util } from "nestedbird/core/Util";
import store from "nestedbird/vue/store";
import { Modal, Tag } from "nestedbird/vue/mixins";

/**
 * This class controls the artist modal
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 * @augments module:Vue/Mixins.Modal
 * @augments module:Vue/Mixins.Tag
 */
export const ArtistModal = {
    mixins:   [
        Modal,
        Tag
    ],
    /**
     * The module template
     * @member module:Vue/Components.ArtistModal#template
     * @default #artistModal-template
     * @type string
     */
    template: `#artistModal-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * The passed UUID from the Url, decoded to its normal UUID form
             * @member module:Vue/Components.ArtistModal#id
             * @type string
             */
            id:                store.getters.pathIdDecoded,
            /**
             * The artist name passed in the url
             * @member module:Vue/Components.ArtistModal#name
             * @type string
             */
            name:              store.getters.pathName,
            /**
             * The current page when scrolling through the artists music collection
             * @member module:Vue/Components.ArtistModal#mediaCurrentPage
             * @type number
             */
            mediaCurrentPage:  0,
            /**
             * The amount of media elements per page that we will load
             * @member module:Vue/Components.ArtistModal#mediaLimit
             * @type number
             */
            mediaLimit:        8,
            /**
             * The current page when scrolling through the artists upcoming events
             * @member module:Vue/Components.ArtistModal#eventsCurrentPage
             * @type number
             */
            eventsCurrentPage: 0,
            /**
             * The amount of event elements per page that we will load
             * @member module:Vue/Components.ArtistModal#eventsLimit
             * @type number
             */
            eventsLimit:       15
        };
    },
    created() {
        this.retrieveInitialMedia();
        this.retrieveInitialEvents();
    },
    mounted() {
        this.updateTags();
        this.getUpcomingEvents();
    },
    watch:    {
        Artist() {
            this.updateTags();
        }
    },
    computed: {
        /**
         * Gets the current page
         * @member module:Vue/Components.ArtistModal#page
         * @type string
         */
        page(): string {
            return store.getters.pathQuery.page;
        },
        /**
         * Does this artist have upcoming events
         * @member module:Vue/Components.ArtistModal#hasEvents
         * @type boolean
         */
        hasEvents(): boolean {
            return this.upcomingEvents && this.upcomingEvents.length;
        },
        /**
         * All the upcoming events the artist has
         * @member module:Vue/Components.ArtistModal#upcomingEvents
         * @type NBEvent[]
         */
        upcomingEvents(): NBEvent[] {
            return store.getters.occurrences
                .sort((a, b) => Math.sign(a.startTime - b.startTime))
                .map((e: Occurrence): NBEvent => {
                    const event = e.event;
                    event.startTime = Util.parseDateTime(e.startTime);
                    event.times = null;
                    return event;
                })
                .filter((e: NBEvent) => e.artists.some((a: Artist) => a.id === this.id));
        },
        /**
         * Does the current person viewing this page have the rights to edit this artist
         * @member module:Vue/Components.ArtistModal#editable
         * @type boolean
         */
        editable(): boolean {
            return store.getters.isLoggedIn &&
                store.getters.hasRole(`PRIV_GET_ENTITY_SCHEMA`) &&
                store.getters.hasRole(`PRIV_UPDATE_ENTITY`);
        },
        /**
         * This artists information
         * @member module:Vue/Components.ArtistModal#Artist
         * @type Artist
         */
        Artist(): Artist {
            return store.getters.getArtist(this.id);
        },
        /**
         * This artists parsed description
         * @member module:Vue/Components.ArtistModal#description
         * @type string
         */
        description(): string {
            return Util.parseMarkdown(this.Artist.description ||
                `Sorry, we do not have any further information about this artist.`);
        },
        /**
         * The facebook Url of this artist
         * @member module:Vue/Components.ArtistModal#fbUrl
         * @type string
         */
        fbUrl(): string {
            return `https://www.facebook.com/${this.Artist.facebookId}`;
        },
        /**
         * retrieves the currently downloaded artists media
         * @member module:Vue/Components.ArtistModal#mediaList
         * @type Medium[]
         */
        mediaList(): Medium[] {
            return store.getters.media.slice()
                .sort((a, b) => Math.sign(b.creationDateTime - a.creationDateTime))
                .filter(e => e.song.artist.id === this.Artist.id)
                .filter(e => store.getters.supportedMediaTypes.includes(e.type));
        }
    },
    methods:  {
        /**
         * Update meta tags
         * @member module:Vue/Components.ArtistModal#getMedia
         * @method
         */
        updateTags() {
            Optional.ofNullable(this.Artist.description || null)
                .ifPresent((description: string) => {
                    Ajax.setMetaTag(`og:description`, `${description.substr(0, 100)}...`);
                    Ajax.setMetaTag(`description`, `${description.substr(0, 100)}...`);
                });

            Optional.ofNullable(this.Artist.name || null)
                .ifPresent(name => Ajax.setMetaTag(`og:title`, name.substr(0, 100)));
        },
        /**
         * Downloads more artist media elements
         * @member module:Vue/Components.ArtistModal#getMedia
         * @method
         * @param {number} page         the page we have currently loaded up to
         * @param {number} limit        the amount of elements per page we will load
         * @returns {Promise<number>}
         */
        getMedia({ page = this.mediaCurrentPage, limit = this.mediaLimit }): Promise<number> {
            return store.dispatch(`getArtistMedia`, {
                page,
                limit,
                sort: `creationDateTime,desc`,
                id:   this.id
            }).then((): number => {
                this.mediaCurrentPage = page;
                return this.mediaCurrentPage;
            });
        },
        /**
         * Downloads more artist events elements
         * @member module:Vue/Components.ArtistModal#getEvents
         * @method
         * @param {number} page         the page we have currently loaded up to
         * @param {number} limit        the amount of elements per page we will load
         * @returns {Promise<number>}
         */
        getEvents({ page = this.eventsCurrentPage, limit = this.eventsLimit }): Promise<number> {
            return store.dispatch(`getArtistEvents`, {
                page,
                limit,
                id: this.id
            }).then((): number => {
                this.eventsCurrentPage = page;
                return this.eventsCurrentPage;
            });
        },
        /**
         * retrieves the initial load of media elements
         * @member module:Vue/Components.ArtistModal#retrieveInitialMedia
         * @method
         */
        retrieveInitialMedia() {
            this.$nextTick(() => {
                this.getMedia(this.mediaCurrentPage, this.mediaLimit).then(() => {
                    this.$forceUpdate();
                });
            });
        },
        /**
         * retrieves the initial load of events elements
         * @member module:Vue/Components.ArtistModal#retrieveInitialEvents
         * @method
         */
        retrieveInitialEvents() {
            this.$nextTick(() => {
                this.getEvents(this.eventsCurrentPage, this.eventsLimit).then(() => {
                    this.$forceUpdate();
                });
            });
        },
        /**
         * checks to see if a page is the current page
         * @member module:Vue/Components.ArtistModal#isPage
         * @method
         * @param {string} page the name of the page
         * @returns boolean
         */
        isPage(page: string): boolean {
            return this.page === page;
        }
    }
};

export const VueArtistModal = Vue.extend(ArtistModal);