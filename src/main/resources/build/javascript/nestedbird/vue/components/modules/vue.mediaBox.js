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
import { MusicPlayer, PlaylistController } from "nestedbird/core/musicplayer";
import { VueFacebookBox } from "./vue.facebookBox";
import { VueSoundcloudBox } from "./vue.soundcloudBox";
import { VueYoutubeBox } from "./vue.youtubeBox";

/**
 * Controller for playing a Medium item.
 * It embeds either a YoutubeBox, FacebookBox or SoundcloudBox into the page.
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 */
export const MediaBox = {
    /**
     * The module template
     * @member module:Vue/Components.MediaBox#template
     * @default #mediaBox-template
     * @type string
     */
    template:   `#mediaBox-template`,
    props:      {
        /**
         * the medium element that this mediabox controls
         * @member module:Vue/Components.MediaBox#Medium
         * @type string
         */
        Medium: {
            type:     Object,
            required: true
        },
        /**
         * What group of media elements does this box belong to, this is for playlists
         * @member module:Vue/Components.MediaBox#group
         * @type string
         */
        group:  {
            type:     String,
            required: false
        }
    },
    components: {
        "youtubebox-component":    VueYoutubeBox,
        "soundcloudbox-component": VueSoundcloudBox,
        "facebookbox-component":   VueFacebookBox
    },
    data(): Object {
        return {
            /**
             * Is the embedded mediabox loaded
             * It only shows the media element if its loaded
             * @member module:Vue/Components.MediaBox#loaded
             * @type boolean
             */
            loaded:  false,
            /**
             * Is the mediabox currently visible on the page
             * If its not we hide most of the element
             * @member module:Vue/Components.MediaBox#visible
             * @type boolean
             */
            visible: true
        };
    },
    created() {
        this.$nextTick(() => this.addToPlaylist());
    },
    beforeDestroy() {
        this.removeFromPlaylist();
    },
    computed:   {
        /**
         * If if loaded, what type of media box is this
         * @member module:Vue/Components.MediaBox#type
         * @type number | boolean
         */
        type(): number | boolean {
            return (this.loaded === true) ? this.Medium.type : false;
        },
        /**
         * Is the header information visible
         * @member module:Vue/Components.MediaBox#isHeaderVisible
         * @type boolean
         */
        isHeaderVisible(): boolean {
            return this.visible && !this.loaded;
        }
    },
    methods:    {
        /**
         * On visibility change we update the visible flag
         * @member module:Vue/Components.MarkdownTextarea#visibilityChanged
         * @method
         */
        visibilityChanged(isVisible: boolean) {
            this.visible = isVisible;
        },
        /**
         * Record this media element in its playlist
         * @member module:Vue/Components.MarkdownTextarea#addToPlaylist
         * @method
         */
        addToPlaylist() {
            if (this.group) {
                PlaylistController.get(this.group).add(this);
            }
        },
        /**
         * Removes this item from its playlist
         * @member module:Vue/Components.MarkdownTextarea#removeFromPlaylist
         * @method
         */
        removeFromPlaylist() {
            if (this.$el.getAttribute(`data-group`)) {
                PlaylistController.get(this.group).remove(this);
            }
        },
        /**
         * Play the embedded media element
         * Sets a callback that will unload the mediabox upon completion
         * @member module:Vue/Components.MarkdownTextarea#play
         * @method
         */
        play() {
            MusicPlayer.unload();
            this.loaded = true;
            MusicPlayer.setPlaylist(this.group);
            MusicPlayer.unload = () => {
                this.loaded = false;
            };
        }
    }
};

export const VueMediaBox = Vue.extend(MediaBox);