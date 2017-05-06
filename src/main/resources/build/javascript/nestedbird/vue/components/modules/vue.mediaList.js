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
import store from "nestedbird/vue/store";
import { Page } from "nestedbird/core/Router";
import { PlaylistController } from "nestedbird/core/musicplayer";
import TriggerManager from "nestedbird/core/TriggerManager";

/**
 * Controller for listing media items
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 */
export const MediaList = {
    /**
     * The module template
     * @member module:Vue/Components.MediaList#template
     * @default #mediaList-template
     * @type string
     */
    template: `#mediaList-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * Current page for infinite scroll
             * @member module:Vue/Components.MediaList#currentPage
             * @type string
             */
            currentPage: 0,
            /**
             * Amount of elements per page for infinite scroll
             * @member module:Vue/Components.MediaList#limit
             * @type string
             */
            limit:       15
        };
    },
    created() {
        this.$nextTick(() => {
            this.$el.style.opacity = `0`;
            this.$el.style.height = `196px`;
            store.dispatch(`getMediaHot`, {
                page:  this.currentPage,
                limit: this.limit
            }).then(() => {
                setTimeout(() => {
                    this.$el.style.opacity = ``;
                    this.$el.style.height = ``;
                }, 15);
            });

            TriggerManager.addTrigger({
                events: [`onSongChange`],
                action: () => this.$forceUpdate()
            });
        });
    },
    computed: {
        /**
         * Are we showing the smaller versions
         * @returns {boolean}
         */
        smallBoxes(): boolean {
            return store.getters.currentPage !== Page.MUSIC;
        },
        /**
         * If this is the small version, how many media elements will be shown
         * @member module:Vue/Components.MediaList#mediaCount
         * @type number
         */
        mediaCount(): number {
            return Math.floor(this.$el.offsetWidth / 350).min(1);
        },
        /**
         * Retrieve all the hot media
         * @member module:Vue/Components.MediaList#hotMedia
         * @type Medium[]
         */
        hotMedia(): Medium[] {
            return store.getters.hotMedia
                .slice()
                .sort((a, b) => Math.sign(a.scoreFinal - b.scoreFinal))
                .map(e => store.getters.getMedium(e.id))
                .filter(e => store.getters.supportedMediaTypes.includes(e.type));
        }
    },
    methods:  {
        /**
         * Is the medium element visible by its index
         * @member module:Vue/Components.MarkdownTextarea#isMediumVisible
         * @method
         * @param {number} index    the index of this medium element as it occurs on the page
         * @returns boolean
         */
        isMediumVisible(index: number): boolean {
            const isPlaying = PlaylistController.get(`Media-HPTicker`).getId() === index;
            const isMusicPage = store.getters.currentPage === Page.MUSIC;
            const isAroundCurrentElement = index >= this.getMediaOffset() && index < this.getMediaOffset() + this.mediaCount;

            return isMusicPage || isPlaying || isAroundCurrentElement;
        },
        /**
         * If this is the small version, we calculate where the media offset begins
         * @member module:Vue/Components.MarkdownTextarea#getMediaOffset
         * @method
         * @returns number
         */
        getMediaOffset(): number {
            return (PlaylistController.get(`Media-HPTicker`).getId() - Math.floor(this.mediaCount / 2))
                .max(this.$el.querySelectorAll(`.medium`).length - this.mediaCount)
                .min(0);
        },
        /**
         * Retrieves additional medium elements
         * @member module:Vue/Components.MarkdownTextarea#getMedia
         * @method
         * @returns Promise<number>
         */
        getMedia({ page = this.currentPage, limit = this.limit }): Promise<number> {
            return store.dispatch(`getMediaHot`, {
                page,
                limit
            }).then((): number => {
                this.currentPage = page;
                return this.currentPage;
            });
        }
    }
};

export const VueMediaList = Vue.extend(MediaList);