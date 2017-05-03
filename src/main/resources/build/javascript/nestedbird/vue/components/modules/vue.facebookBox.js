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
import { MediaType, MusicPlayer } from "nestedbird/core/musicplayer";

/**
 * Controller for playing facebook videos
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 */
export const FacebookBox = {
    /**
     * The module template
     * @member module:Vue/Components.FacebookBox#template
     * @default #facebookBox-template
     * @type string
     */
    template: `#facebookBox-template`,
    props:    {
        /**
         * Id of this media elements source
         * @member module:Vue/Components.FacebookBox#id
         * @type string
         */
        id:     {
            type:     [String, Number],
            required: true
        },
        /**
         * The medium object
         * @member module:Vue/Components.FacebookBox#Medium
         * @type string | number
         */
        Medium: {
            type:     Object,
            required: true
        }
    },
    data(): Object {
        return {};
    },
    /**
     * When the element is created we need to load the facebook media and bind all the events that this
     * object controls
     */
    created() {
        this.$nextTick(() => {
            const url = `https://www.facebook.com/video.php?v=${this.id}`;
            const node = document.createElement(`div`);
            node.id = `FB_${this.id}`;
            node.addClass(`fb-video`);
            node.setAttribute(`data-href`, url);
            node.setAttribute(`data-show-captions`, `false`);
            node.setAttribute(`data-allowfullscreen`, `false`);
            node.setAttribute(`data-autoplay`, `false`);

            this.$el.querySelector(`#${this.getElementId()}`).appendChild(node);

            // window.FB.XFBML.parse(node);
            // FB.Event.unsubscribe('xfbml.ready');
            FB.Event.subscribe(`xfbml.ready`, this.onReady);
            window.FB.XFBML.parse();
        });
    },
    /**
     * Unload the element from the page
     */
    beforeDestroy() {
        MusicPlayer.reset();
    },
    methods:  {
        /**
         * Define all the MusicPlayer event callbacks, so the musicplayer knows how to talk to the media element.
         * In the future this may be changed to use an inheritance system instead
         * @member module:Vue/Components.FacebookBox#setEvents
         * @method
         */
        setEvents() {
            MusicPlayer.onPlay(() => {
                MusicPlayer.getMedia().ifPresent((media: Object) => {
                    media.play();
                    MusicPlayer.videoPoll = window.setInterval(MusicPlayer.tick.bind(MusicPlayer), 25);
                });
            });

            MusicPlayer.onSeek(() => {
                MusicPlayer.getMedia().ifPresent((media: Object) => {
                    media.seek(MusicPlayer.getCurrentTime());
                });
            });

            MusicPlayer.onVolumeChange((volume: number) => {
                MusicPlayer.getMedia().ifPresent((media: Object) => {
                    media.setVolume(volume);
                });
            });

            MusicPlayer.onPause(() => {
                MusicPlayer.getMedia().ifPresent((media: Object) => {
                    media.pause();
                });
                window.clearInterval(MusicPlayer.videoPoll);
            });

            MusicPlayer.onTick(() => {
                MusicPlayer.getMedia().ifPresent((media: Object) => {
                    MusicPlayer.setCurrentTime(media.getCurrentPosition());
                    MusicPlayer.setDuration(media.getDuration());
                });
            });

            MusicPlayer.onLoad(() => {
                MusicPlayer.getMedia().ifPresent((media: Object) => {
                    MusicPlayer.setDuration(media.getDuration());
                });
                MusicPlayer.play();
            });

            MusicPlayer.onReset(() => {
                // this.MusicPlayer.media.destroy();
                MusicPlayer.media = null;
                MusicPlayer.setType(MediaType.None);
                window.clearInterval(MusicPlayer.videoPoll);
            });
        },
        /**
         * Retrieves the elements dom id
         * @member module:Vue/Components.FacebookBox#getElementId
         * @returns {string}
         */
        getElementId(): string {
            return `player_${this.id}`;
        },
        /**
         * Callback when the media element is ready. Finish initialising the element
         * @member module:Vue/Components.FacebookBox#onReady
         */
        onReady(videoinfo: Object) {
            if (videoinfo.type === `video`) {

                MusicPlayer.pause();
                MusicPlayer.setType(MediaType.Facebook);

                MusicPlayer.setMedia(videoinfo.instance);
                MusicPlayer.media.targetElement = document.getElementById(`FB_${this.id}`);
                this.setEvents();

                MusicPlayer.getMedia().ifPresent((media: Object) => {
                    media.subscribe(`startedPlaying`, () => {
                        MusicPlayer.setPaused(false);
                        // this.MusicPlayer.play();
                    });
                    media.subscribe(`paused`, () => {
                        MusicPlayer.setPaused(false);
                        // this.MusicPlayer.pause();
                    });
                    media.subscribe(`finishedPlaying`, () => {
                        MusicPlayer.end();
                    });
                    media.subscribe(`startedBuffering`, () => {
                        // Video started playing ...
                    });
                    media.subscribe(`finishedBuffering`, () => {
                        // Video started playing ...
                    });

                    media.unmute();
                });

                MusicPlayer.volumeChange(MusicPlayer.getVolume());
                MusicPlayer.load();


                MusicPlayer.setSongName(this.Medium.song.name);
                MusicPlayer.setArtistName(this.Medium.song.artist.name);
            }
        }
    }
};

export const VueFacebookBox = Vue.extend(FacebookBox);