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
import { Ajax } from "nestedbird/core/Ajax";
import store from "nestedbird/vue/store";
import { Modal } from "nestedbird/vue/mixins";

/**
 * This class controls the song modal
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 * @augments module:Vue/Mixins.Modal
 */
export const SongModal = {
    mixins:   [
        Modal
    ],
    /**
     * The module template
     * @member module:Vue/Components.SongModal#template
     * @default #songModal-template
     * @type string
     */
    template: `#songModal-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * The passed UUID from the Url, decoded to its normal UUID form
             * @member module:Vue/Components.SongModal#id
             * @type string
             */
            id: store.getters.pathIdDecoded
        };
    },
    watch:    {
        Song() {
            this.updateTags();
        }
    },
    computed: {
        /**
         * this Song
         * @member module:Vue/Components.SongModal#Song
         * @type Song
         */
        Song(): Song {
            return store.getters.getSong(this.id);
        },
        /**
         * The songs description
         * @member module:Vue/Components.SongModal#description
         * @type string
         */
        description(): string {
            return this.Song.description ||
                this.Song.artist.description ||
                `Sorry, we do not have any further information about this artist.`;
        },
        /**
         * The facebook url
         * @member module:Vue/Components.SongModal#fbUrl
         * @type string
         */
        fbUrl(): string {
            return `https://www.facebook.com/${this.Song.artist.facebookId}`;
        }
    },
    methods:  {
        /**
         * Update meta tags
         * @member module:Vue/Components.SongModal#updateTags
         * @method
         */
        updateTags() {
            Optional.ofNullable(this.Song.description || null)
                .ifPresent((description: string) => {
                    Ajax.setMetaTag(`og:description`, `${description.substr(0, 100)}...`);
                    Ajax.setMetaTag(`description`, `${description.substr(0, 100)}...`);
                });

            Optional.ofNullable(this.Song.name || null)
                .ifPresent((name: string) => {
                    Ajax.setMetaTag(`og:title`, name.substr(0, 100));
                });
        }
    }
};

export const VueSongModal = Vue.extend(SongModal);