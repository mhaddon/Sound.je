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
import store from "nestedbird/vue/store";
import { Modal } from "nestedbird/vue/mixins";
import { Util } from "nestedbird/core/Util";

/**
 * This class controls the Location Modal
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 * @augments module:Vue/Mixins.Modal
 * @augments module:Vue/Mixins.Tag
 */
export const LocationModal = {
    mixins:   [
        Modal
    ],
    /**
     * The module template
     * @member module:Vue/Components.LocationModal#template
     * @default #locationModal-template
     * @type string
     */
    template: `#locationModal-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * The id of the location
             * @member module:Vue/Components.LocationModal#id
             * @type string
             */
            id:   store.getters.pathIdDecoded,
            /**
             * The artist name passed in the url
             * @member module:Vue/Components.LocationModal#name
             * @type string
             */
            name: store.getters.pathName
        };
    },
    mounted() {
        this.updateTags();
    },
    watch:    {
        Location() {
            this.updateTags();
        }
    },
    computed: {
        /**
         * Gets the current page
         * @member module:Vue/Components.LocationModal#page
         * @type string
         */
        page(): string {
            return store.getters.pathQuery.page;
        },
        /**
         * Does this user have permission to edit this element
         * @member module:Vue/Components.LocationModal#editable
         * @type boolean
         */
        editable(): boolean {
            return store.getters.isLoggedIn &&
                store.getters.hasRole(`PRIV_GET_ENTITY_SCHEMA`) &&
                store.getters.hasRole(`PRIV_UPDATE_ENTITY`);
        },
        /**
         * This location element
         * @member module:Vue/Components.LocationModal#Location
         * @type Location
         */
        Location(): Location {
            return store.getters.getLocation(this.id);
        },
        /**
         * This google maps image url of this event
         * @member module:Vue/Components.LocationModal#mapUrl
         * @type string
         */
        mapUrl(): string {
            return [
                `https://maps.google.com/maps/api/staticmap`,
                `?center=${this.Location.coordinates}`,
                `&zoom=13`,
                `&size=600x400`,
                `&maptype=roadmap`,
                `&sensor=false`,
                `&scale=2`,
                `&markers=color:blue%7Clabel:${this.Location.name}%7C${this.Location.coordinates}`,
                `&key=AIzaSyAPTF7YjFmUJKcX552G7drMMPq8_p1yC08`
            ].join(``);
        },
        /**
         * This google maps url of this event
         * @member module:Vue/Components.LocationModal#mapLinkUrl
         * @type string
         */
        mapLinkUrl(): string {
            return `https://www.google.com/maps/place/${this.Location.name}/@${this.Location.coordinates},12.5z`;
        },
        /**
         * The description of this location
         * @member module:Vue/Components.LocationModal#description
         * @type string
         */
        description(): string {
            return Util.parseMarkdown(this.Location.description ||
                `Sorry, we do not have any further information about this location.`);
        },
        /**
         * The url of this facebook page
         * @member module:Vue/Components.LocationModal#fbUrl
         * @type string
         */
        fbUrl(): string {
            return `https://www.facebook.com/${this.Location.facebookId}`;
        }
    },
    methods:  {
        /**
         * checks to see if a page is the current page
         * @member module:Vue/Components.LocationModal#isPage
         * @method
         * @param {string} page the name of the page
         * @returns boolean
         */
        isPage(page: string): boolean {
            return this.page === page;
        },
        /**
         * Update meta tags
         * @member module:Vue/Components.LocationModal#updateTags
         * @method
         */
        updateTags() {
            Optional.ofNullable(this.Location.description || null)
                .ifPresent((description: string) => {
                    Ajax.setMetaTag(`og:description`, `${description.substr(0, 100)}...`);
                    Ajax.setMetaTag(`description`, `${description.substr(0, 100)}...`);
                });

            Optional.ofNullable(this.Location.name || null)
                .ifPresent((name: string) => {
                    Ajax.setMetaTag(`og:title`, name.substr(0, 100));
                });
        },
        /**
         * Retrieves all events associated with this location
         * @member module:Vue/Components.LocationModal#getEventList
         * @method
         * @returns NBEvent[]
         */
        getEventList(): NBEvent[] {
            return store.getters.events
                .slice()
                .sort((a, b) => Math.sign(b.startTime - a.startTime))
                .filter(e => e.location.id === this.Location.id)
                .flatMap((e) => {
                    const names = Util.stripArray(e.name.split(`;`)
                        .concat(e.artists.map((a: NBEvent) => a.name)));
                    return names.map((name) => Util.mergeDeep(e, { name }));
                })
                .map((e: NBEvent): EventParsed => {
                    const parsedEvent: EventParsed = (e: Object);
                    if (parsedEvent.times && parsedEvent.times.length > 0) {
                        Util.getNextEventTime(parsedEvent.times).ifPresent((time: number) => {
                            parsedEvent.nextOccurrence = Util.parseDateTime(time);
                        });
                    }
                    return parsedEvent;
                });
        }
    }
};

export const VueLocationModal = Vue.extend(LocationModal);