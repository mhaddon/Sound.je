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
import { Modal, Tag } from "nestedbird/vue/mixins";
import { Util } from "nestedbird/core/Util";
import store from "nestedbird/vue/store";

/**
 * This class controls the event modal
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 * @augments module:Vue/Mixins.Modal
 * @augments module:Vue/Mixins.Tag
 */
export const EventModal = {
    mixins:   [
        Modal,
        Tag
    ],
    /**
     * The module template
     * @member module:Vue/Components.EventModal#template
     * @default #eventModal-template
     * @type string
     */
    template: `#eventModal-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * The id of the event
             * @member module:Vue/Components.EventModal#id
             * @type string
             */
            id:   store.getters.pathIdDecoded,
            /**
             * The artist name passed in the url
             * @member module:Vue/Components.EventModal#name
             * @type string
             */
            name: store.getters.pathName
        };
    },
    watch:    {
        Event() {
            this.updateTags();
        }
    },
    computed: {
        /**
         * Gets the current page
         * @member module:Vue/Components.EventModal#page
         * @type string
         */
        page(): string {
            return store.getters.pathQuery.page;
        },
        /**
         * Gets the events image
         * @member module:Vue/Components.EventModal#imageUrl
         * @type string
         */
        imageUrl(): string {
            return this.Event.imageUrl || this.Event.location.imageUrl;
        },
        /**
         * Does this event have any recorded artists playing at it
         * @member module:Vue/Components.EventModal#hasArtists
         * @type boolean
         */
        hasArtists(): boolean {
            return this.Event.artists && this.Event.artists.length;
        },
        /**
         * Does this user have permission to edit this element
         * @member module:Vue/Components.EventModal#editable
         * @type boolean
         */
        editable(): boolean {
            return store.getters.isLoggedIn &&
                store.getters.hasRole(`PRIV_GET_ENTITY_SCHEMA`) &&
                store.getters.hasRole(`PRIV_UPDATE_ENTITY`);
        },
        /**
         * This Event element
         * @member module:Vue/Components.EventModal#Event
         * @type NBEvent
         */
        Event(): NBEvent {
            return store.getters.getEvent(this.id);
        },
        /**
         * Gets the next start time in the future, if there is one
         * @member module:Vue/Components.EventModal#calculatedStartTime
         * @type number
         */
        calculatedStartTime(): number | null {
            let calculatedTime = null;
            Util.getNextEventTime(this.Event.times).ifPresent((e: number) => {
                calculatedTime = e;
            });
            return calculatedTime;
        },
        /**
         * Gets the start time of this element, it checks if theres a start time in the url
         * @member module:Vue/Components.EventModal#startTime
         * @type number
         */
        startTime: {
            get(): number | null {
                return store.getters.pathQuery.startTime ||
                    this.calculatedStartTime;
            },
            set(startTime: string) {
                store.commit(`setQuery`, {
                    name:  `startTime`,
                    value: startTime
                });
            }
        },
        /**
         * Retrieves the google maps image URL
         * @member module:Vue/Components.EventModal#mapUrl
         * @type string
         */
        mapUrl(): string {
            return [
                `https://maps.google.com/maps/api/staticmap`,
                `?center=${this.Event.location.coordinates}`,
                `&zoom=13`,
                `&size=600x400`,
                `&maptype=roadmap`,
                `&sensor=false`,
                `&scale=2`,
                `&markers=color:blue%7Clabel:${this.Event.location.name}%7C${this.Event.location.coordinates}`,
                `&key=AIzaSyAPTF7YjFmUJKcX552G7drMMPq8_p1yC08`
            ].join(``);
        },
        /**
         * Retrieves the google maps URL
         * @member module:Vue/Components.EventModal#mapLinkUrl
         * @type string
         */
        mapLinkUrl(): string {
            return [
                `https://www.google.com/maps/place/`,
                `${this.Event.location.name}/@${this.Event.location.coordinates},12.5z`
            ].join(``);
        },
        /**
         * Retrieves the events description, if exists, it parses the markdown
         * @member module:Vue/Components.EventModal#description
         * @type string
         */
        description(): string {
            return Util.parseMarkdown(this.Event.description ||
                `Sorry, we do not have any further information about this event.`);
        },
        /**
         * Retrieves the url to facebook
         * @member module:Vue/Components.EventModal#fbUrl
         * @type string
         */
        fbUrl(): string {
            return `https://www.facebook.com/${this.Event.facebookId}`;
        },
        /**
         * Generates the name of this event
         * @member module:Vue/Components.EventModal#name
         * @type string
         */
        artistNames(): string {
            let returnVar = this.Event.name || ``;
            if (this.Event.hasOwnProperty(`name`)) {
                const strippedArr = Util.stripArray(returnVar.split(`;`).concat(this.Event.artists.map((a) => a.name)));
                if (strippedArr.length > 1) {
                    returnVar = `${strippedArr.slice(0, -1).join(`, `)} and ${strippedArr.slice(-1)}`;
                } else {
                    returnVar = strippedArr[0];
                }
            }
            return returnVar;
        }
    },
    methods:  {
        /**
         * checks to see if a page is the current page
         * @member module:Vue/Components.EventModal#isPage
         * @method
         * @param {string} page the name of the page
         * @returns boolean
         */
        isPage(page: string): boolean {
            return this.page === page;
        },
        /**
         * Update meta tags
         * @member module:Vue/Components.EventModal#updateTags
         * @method
         */
        updateTags() {
            Optional.ofNullable(this.Event.description || null)
                .ifPresent((description: string) => {
                    Ajax.setMetaTag(`og:description`, `${description.substr(0, 100)}...`);
                    Ajax.setMetaTag(`description`, `${description.substr(0, 100)}...`);
                });
            Ajax.setMetaTag(`og:title`, this.artistNames.substr(0, 100));
        },
        /**
         * Converts a datetime to a timestamp
         * @member module:Vue/Components.EventModal#getTime
         * @method
         * @returns string
         */
        getTime(): string | null {
            let returnVar = null;
            if (this.startTime) {
                const date = Util.parseTime(parseInt(this.startTime, 10));
                const time = Util.parseDateTime(parseInt(this.startTime, 10));
                returnVar = `${date} ${time}`;
            }
            return returnVar;
        }
    }
};

export const VueEventModal = Vue.extend(EventModal);