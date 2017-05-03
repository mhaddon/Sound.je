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
 * The different states that Youtube videos can have
 */
const YTState = {
    UNSTARTEDVIDEO: -1,
    END:            0,
    ONPLAY:         1,
    ONPAUSE:        2,
    BUFFERING:      3,
    VIDEOQUEUED:    4
};

/**
 * This component is responsible for controlling Youtube videos
 * @type {Vue}
 */
export const YoutubeBox = {
    /**
     * The components design template, see the "text/x-template" tags in
     * index.html
     */
    template: `#youtubeBox-template`,
    /**
     * The components variables that it inherits from its parent
     */
    props:    [`id`, `Medium`],
    /**
     * Every component requires a data feild, but i do not need it as
     * i inherit all the relevent data. So it is filled with nonsense.
     */
    data(): Object {
        return {};
    },
    /**
     * When the element is created we need to load the youtube media and bind all the events that this
     * object controls
     */
    created() {
        this.$nextTick(() => {
            new YT.Player(this.getElementId(), {
                height:     `480`,
                width:      `853`,
                videoId:    this.id,
                playerVars: {
                    autoplay:       1,
                    controls:       0,
                    modestbranding: 1,
                    rel:            0,
                    showinfo:       1,
                    iv_load_policy: 3
                },
                events:     {
                    onStateChange: this.onStateChange,
                    onReady:       this.onReady
                }
            });
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
         */
        setEvents() {
            MusicPlayer.onPlay(() => {
                MusicPlayer.getMedia().ifPresent((media: Object) => {
                    media.playVideo();
                    MusicPlayer.videoPoll = window.setInterval(MusicPlayer.tick.bind(MusicPlayer), 25);
                });
            });

            MusicPlayer.onSeek(() => {
                MusicPlayer.getMedia().ifPresent((media: Object) => {
                    media.seekTo(MusicPlayer.getCurrentTime(), true);
                });
            });

            MusicPlayer.onVolumeChange((volume: number) => {
                MusicPlayer.getMedia().ifPresent((media: Object) => {
                    media.setVolume(volume * 100);
                });
            });

            MusicPlayer.onPause(() => {
                MusicPlayer.getMedia().ifPresent((media: Object) => {
                    media.pauseVideo();
                });
                window.clearInterval(MusicPlayer.videoPoll);
            });

            MusicPlayer.onTick(() => {
                MusicPlayer.getMedia().ifPresent((media: Object) => {
                    MusicPlayer.setBufferPercent(Math.floor(media.getVideoLoadedFraction() * 100));
                    MusicPlayer.setCurrentTime(media.getCurrentTime());
                });
            });

            MusicPlayer.onLoad(() => {
                MusicPlayer.getMedia().ifPresent((media: Object) => {
                    MusicPlayer.setDuration(media.getDuration());
                });
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
         * @returns {string}
         */
        getElementId(): string {
            return `player_${this.id}`;
        },
        /**
         * When the youtube media changes its state, we will process the result below
         * @param state
         */
        onStateChange(state: Object) {
            const stateData = state[`data`];
            if (stateData === YTState.END) {
                MusicPlayer.end();
            } else if (stateData === YTState.ONPLAY) {
                MusicPlayer.play();
            } else if (stateData === YTState.ONPAUSE) {
                MusicPlayer.pause();
            } else if (stateData === YTState.BUFFERING) {
                // buffering
            } else if (stateData === YTState.VIDEOQUEUED) {
                // video cued
            } else if (stateData === YTState.UNSTARTEDVIDEO) {
                // unstarted video
            }
        },
        /**
         * When the media is ready to play we need to prepare the MusicPlayer, then start playing the media
         * @param videoinfo
         */
        onReady(videoinfo: Object) {
            MusicPlayer.pause();
            MusicPlayer.setType(MediaType.YouTube);

            MusicPlayer.setMedia(videoinfo.target);
            MusicPlayer.media.targetElement = videoinfo.target.a;
            this.setEvents();

            MusicPlayer.volumeChange(MusicPlayer.getVolume());

            MusicPlayer.setSongName(this.Medium.song.name);
            MusicPlayer.setArtistName(this.Medium.song.artist.name);

            MusicPlayer.load();
        }
    }
};

export const VueYoutubeBox = Vue.extend(YoutubeBox);