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
 * This component is responsible for controlling Soundcloud songs
 * @type {Vue}
 */
export const SoundcloudBox = {
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
     * When the element is created we need to load the soundcloud media and bind all the events that this
     * object controls
     */
    created() {
        this.$nextTick(() => {
            const apiurl = encodeURIComponent([
                `https://api.soundcloud.com/tracks/${this.id}`,
                `?theme_color=161616`,
                `&color=3E74A1`,
                `&auto_play=true`,
                `&hide_related=true`,
                `&show_user=true`,
                `&show_reposts=false`,
                `&visual=true`,
                `&sharing=false`,
                `&show_playcount=false`,
                `&show_comments=false`,
                `&buying=false`,
                `&liking=false`
            ].join(``));

            const url = [
                `https://w.soundcloud.com/player/`,
                `?url=${apiurl}`
            ].join(``);

            const node = document.createElement(`iframe`);
            node.setAttribute(`id`, `SC_${this.id}`);
            node.setAttribute(`width`, `100%`);
            node.setAttribute(`height`, `100%`);
            node.setAttribute(`scrolling`, `no`);
            node.setAttribute(`frameborder`, `no`);
            node.setAttribute(`src`, url);

            this.$el.querySelector(`#${this.getElementId()}`).appendChild(node);

            const media = new SC.Widget(node);
            media.TargetElement = node;
            MusicPlayer.setMedia(media);
            MusicPlayer.setType(MediaType.SoundCloud);

            media.bind(SC.Widget.Events.FINISH, this.onFinish);
            media.bind(SC.Widget.Events.PLAY, this.onPlay);
            media.bind(SC.Widget.Events.PAUSE, this.onPause);
            media.bind(SC.Widget.Events.READY, this.onReady);
            media.bind(SC.Widget.Events.PLAY_PROGRESS, this.onTick);


            MusicPlayer.setSongName(this.Medium.song.name);
            MusicPlayer.setArtistName(this.Medium.song.artist.name);
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
                    media.play();
                });
            });

            MusicPlayer.onSeek(() => {
                MusicPlayer.getMedia().ifPresent((media: Object) => {
                    media.seekTo(MusicPlayer.getCurrentTime() * 1000);
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
            });

            MusicPlayer.onTick((e: Object) => {
                this.updateStore(e);
            });

            MusicPlayer.onLoad((e: Object) => {
                this.updateStore(e);
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
        onFinish() {
            MusicPlayer.end();
        },
        onPlay() {
            MusicPlayer.setPaused(false);
            // MusicPlayer.play();
        },
        onPause() {
            MusicPlayer.setPaused(true);
            // MusicPlayer.pause();
        },
        onReady(e: Object) {
            this.setEvents();
            MusicPlayer.volumeChange(MusicPlayer.getVolume());
            MusicPlayer.load(e);
        },
        onTick(e: Object) {
            MusicPlayer.tick(e);
        },
        updateStore() {
            MusicPlayer.getMedia().ifPresent((media: Object) => {
                media.getPosition((position: number) => {
                    MusicPlayer.setCurrentTime(position / 1000);
                });
                media.getDuration((duration: number) => {
                    MusicPlayer.setDuration(duration / 1000);
                });
            });
        }
    }
};

export const VueSoundcloudBox = Vue.extend(SoundcloudBox);