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
import { Modal, Tag } from "nestedbird/vue/mixins";

/**
 * This class controls the Medium modal
 * This appears after we know the users email address, and when we know they are registered
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 * @augments module:Vue/Mixins.Modal
 * @augments module:Vue/Mixins.Tag
 */
export const MediumModal = {
    mixins:   [
        Modal,
        Tag
    ],
    /**
     * The module template
     * @member module:Vue/Components.MediumModal#template
     * @default #mediumModal-template
     * @type string
     */
    template: `#mediumModal-template`,
    props:    [],
    data(): Object {
        return {
            /**
             * The id of the event
             * @member module:Vue/Components.MediumModal#id
             * @type string
             */
            id: store.getters.pathIdDecoded
        };
    },
    watch:    {
        Medium() {
            this.updateTags();
        }
    },
    computed: {
        /**
         * This Medium element
         * @member module:Vue/Components.MediumModal#Medium
         * @type Medium
         */
        Medium(): Medium {
            return store.getters.getMedium(this.id);
        },
        /**
         * This mediums description
         * @member module:Vue/Components.MediumModal#description
         * @type string
         */
        description(): string {
            return this.Medium.song.description ||
                this.Medium.song.artist.description ||
                `Sorry, we do not have any further information about this song.`;
        },
        /**
         * The url to this facebook page
         * @member module:Vue/Components.MediumModal#fbUrl
         * @type string
         */
        fbUrl(): string {
            return `https://www.facebook.com/${this.Medium.song.artist.facebookId}`;
        }
    },
    methods:  {
        /**
         * Update meta tags
         * @member module:Vue/Components.vueArtistModal#getMedia
         * @method
         */
        updateTags() {
            Optional.ofNullable(this.Medium.song || null)
                .ifPresent((song: Song) => {
                    Ajax.setMetaTag(`og:description`, `${song.description.substr(0, 100)}...`);
                    Ajax.setMetaTag(`description`, `${song.description.substr(0, 100)}...`);
                    Ajax.setMetaTag(`og:title`, song.name.substr(0, 100));
                });
        }
    }
};

export const VueMediumModal = Vue.extend(MediumModal);