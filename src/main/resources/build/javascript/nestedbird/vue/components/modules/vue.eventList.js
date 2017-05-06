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
import store from "nestedbird/vue/store";
import TriggerManager from "nestedbird/core/TriggerManager";

/**
 * Controller for the event listings
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 */
export const EventList = {
    /**
     * The module template
     * @member module:Vue/Components.EventList#template
     * @default #eventList-template
     * @type string
     */
    template: `#eventList-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * A list of the parsed events
             * @member module:Vue/Components.EventList#parsedEvents
             * @type EventBlock[]
             */
            parsedEvents:      [],
            /**
             * The offset of the current event block
             * @member module:Vue/Components.EventList#eventOffset
             * @type number
             */
            eventOffset:       0,
            /**
             * The amount of event blocks shown at once
             * @member module:Vue/Components.EventList#eventCount
             * @type number
             */
            eventCount:        2,
            /**
             * Amount of events loaded per query
             * @member module:Vue/Components.EventList#eventsPerRequest
             * @type number
             */
            eventsPerRequest:  25,
            /**
             * The current amount of pages we have downloaded
             * @member module:Vue/Components.EventList#eventsRequestPage
             * @type number
             */
            eventsRequestPage: 0

        };
    },
    created() {
        this.getEvents({})
            .then(() => this.checkNextEvents());

        TriggerManager.addTrigger({
            events: [`onPerspectiveSwap`],
            action: () => this.checkNextEvents()
        });
    },
    computed: {
        /**
         * Are there previous events to those being shown?
         * @member module:Vue/Components.EventList#isPreviousEvents
         * @type boolean
         */
        isPreviousEvents(): boolean {
            return !this.isMobile && (this.eventOffset > 0);
        },
        /**
         * Are there next events to those being shown?
         * @member module:Vue/Components.EventList#isNextEvents
         * @type boolean
         */
        isNextEvents(): boolean {
            return !this.isMobile && (this.parsedEvents.length - this.eventOffset - this.eventCount > 0);
        },
        /**
         * Is the window mobile sized
         * @member module:Vue/Components.EventList#isMobile
         * @type boolean
         */
        isMobile: () => store.getters.isMobile,
        /**
         * Gets the upcoming events
         * @member module:Vue/Components.EventList#currentEvents
         * @type EventBlock[]
         */
        currentEvents(): EventBlock[] {
            let currentEvents = this.parsedEvents.slice()
                .sort((a, b) => Math.sign(a.dateTime - b.dateTime));
            if (!this.isMobile) {
                currentEvents = currentEvents
                    .slice(this.eventOffset, this.eventOffset + this.eventCount);
            }
            return currentEvents;
        }
    },
    methods:  {
        /**
         * Retrieves new events
         * @member module:Vue/Components.EventList#getEvents
         * @method
         * @param {number} page     current page
         * @param {number} limit    limit of items per page
         * @returns {Promise.<number>}
         */
        getEvents({ page = this.eventsRequestPage, limit = this.eventsPerRequest }): Promise<number> {
            return store
                .dispatch(`getEvents`, {
                    page,
                    limit
                })
                .then((): number => {
                    this.parseEvents();
                    this.eventsRequestPage = page;
                    return this.eventsRequestPage;
                });
        },
        /**
         * Converts the start time in ms to a timestamp string
         * @member module:Vue/Components.EventList#parseStartTime
         * @method
         * @param {number} startTime     start time in ms
         * @returns string
         */
        parseStartTime(startTime: number): string {
            return Util.parseTime(startTime);
        },
        /**
         * Records a block of parsed events
         * @member module:Vue/Components.EventList#addEvents
         * @method
         * @param {Object} events     block of events organised by time and location
         */
        addEvents(events: Object) {
            if (typeof events !== `undefined`) {
                this.parsedEvents.push(events);
            }
        },
        /**
         * Goes to the previous block of events
         * @member module:Vue/Components.EventList#previousEvents
         * @method
         */
        previousEvents() {
            this.eventOffset -= this.eventCount;

            this.eventOffset = this.eventOffset.min(0);
        },
        /**
         * Goes to the next block of events
         * @member module:Vue/Components.EventList#previousEvents
         * @method
         */
        nextEvents() {
            this.eventOffset += this.eventCount;

            this.eventOffset = this.eventOffset.max(this.parsedEvents.length - this.eventsCount).min(0);

            this.checkNextEvents();
        },
        /**
         * Checks if theres any next events in parsedEvents, if not, then we try downloading more
         * @member module:Vue/Components.EventList#checkNextEvents
         * @method
         */
        checkNextEvents() {
            if (!this.isNextEvents && !this.isMobile) {
                this.getEvents({ page: this.eventsRequestPage + 1 })
                    .then(() => this.checkNextEvents());
            }
        },
        /**
         * Gets the upcoming events
         * @member module:Vue/Components.EventList#upcomingEvents
         * @method
         * @returns EventParsed[]
         */
        upcomingEvents(): EventParsed[] {
            return store.getters.occurrences
                .sort((a, b) => Math.sign(a.startTime - b.startTime))
                .map((e: Occurrence): EventParsed => {
                    const event: EventParsed = (e.event: Object);
                    event.startTime = e.startTime;
                    event.times = null;
                    return event;
                });
        },
        /**
         * Parses the upcoming events converting them to EventBlocks
         * @member module:Vue/Components.EventList#parseEvents
         * @method
         */
        parseEvents() {
            this.parsedEvents = [];

            const data = new Map();

            for (const unparsedEvent of this.upcomingEvents()) {
                const event = Util.parse(unparsedEvent); // Weird bug with object references, so we deepclone
                const daysTime = new Date(event.startTime).setHours(0, 0, 0);
                if (!data.has(daysTime)) {
                    data.set(daysTime, {
                        dateTime:        event.startTime,
                        displayDateTime: Util.parseDateTime(event.startTime),
                        displayTime:     Util.parseTime(event.startTime),
                        Places:          new Map()
                    });
                }

                const currentDay = data.get(daysTime);
                if (!currentDay.Places.has(event.location.id)) {
                    currentDay.Places.set(event.location.id, Object.assign(event.location, {
                        Events:      new Map(),
                        facebookUrl: `https://www.facebook.com/${event.location.facebookId}`
                    }));
                }

                const currentPlace = currentDay.Places.get(event.location.id);

                const eventNames: string[] = Util.stripArray(event.name.split(`;`)
                    .concat(event.artists.map((a: Artist): string => a.name)));

                for (let i = 0; i < eventNames.length; i++) {
                    const eventName = eventNames[i];
                    if (eventName.trim()) {
                        const newEvent = JSON.parse(JSON.stringify(event));
                        newEvent.name = eventName;
                        delete newEvent.location;
                        currentPlace.Events.set(`${event.id}#${i}`, Object.assign(newEvent, {
                            facebookUrl: `https://www.facebook.com/${event.location.facebookId}`
                        }));
                    }
                }
            }

            Util.convertMapToArray(data)
                .forEach(e => this.addEvents(e));
        }
    }
};

export const VueEventList = Vue.extend(EventList);