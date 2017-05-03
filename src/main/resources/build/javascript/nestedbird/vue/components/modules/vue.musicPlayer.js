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
import { MusicPlayer as MusicPlayerController } from "nestedbird/core/musicplayer";
import { VueMusicFSMP } from "./vue.musicFSMP";

export const MusicPlayer = {
    /**
     * The components design template, see the "text/x-template" tags in
     * index.html
     */
    template:   `#musicPlayer-template`,
    /**
     * The components variables that it inherits from its parent
     */
    props:      [],
    /**
     * Every component requires a data feild, but i do not need it as
     * i inherit all the relevent data. So it is filled with nonsense.
     */
    data(): Object {
        return {
            songIndex:     0,
            playlist:      [],
            playlistGroup: `null`
        };
    },
    /**
     * Every component requires a data feild, but i do not need it as
     * i inherit all the relevent data. So it is filled with nonsense.
     */
    components: {
        "musicfsmp-component": VueMusicFSMP
    },
    created() {
        // this.musicPlayer.setVolume(0.75);
    },
    computed:   {
        paused(): boolean {
            return store.getters.paused;
        },
        songName(): string {
            return store.getters.songName;
        },
        artistName(): string {
            return store.getters.artistName;
        },
        sliderCSS(): string {
            const percentage = ((store.getters.currentTime / store.getters.duration) * 100);

            return `width:${percentage}%;`;
        },
        currentTime(): string {
            const minutes = Math.floor(store.getters.currentTime / 60);
            // this is floored so it doesnt show 60 as a possible output
            const seconds = Math.floor(store.getters.currentTime % 60);

            return `${minutes.pad(1)}:${seconds.pad(2)}`;
        },
        duration(): string {
            const minutes = Math.floor(store.getters.duration / 60);
            // this is floored so it doesnt show 60 as a possible output
            const seconds = Math.floor(store.getters.duration % 60);

            return `${minutes.pad(1)}:${seconds.pad(2)}`;
        }
    },
    methods:    {
        play() {
            if (MusicPlayerController.isPaused()) {
                // this.musicPlayer.play();
                // const media = this.musicPlayer.getMedia().orElseGet(getFirstMediaOnPage);
                // media.play();

                MusicPlayerController.play();

                // this.musicPlayer.getMedia()
                //     .ifPresent(() => { this.musicPlayer.play() });
            } else {
                MusicPlayerController.pause();
            }
        },
        next() {
            MusicPlayerController.next();
        },
        back() {
            MusicPlayerController.previous();
        },
        changeVolume(event: Object) {
            MusicPlayerController.volumeChange(event.target.value / 1000);
        },
        openFSMP() {

        },
        seek(e: Element) {
            const left = e.pageX - (e.currentTarget.getBoundingClientRect().left + document.body.scrollLeft);
            const width = this.$el.querySelector(`.musicplayer__seeker`).offsetWidth;
            const percent = left / width;
            MusicPlayerController.seek(percent);
        }
    }
};

export const VueMusicPlayer = Vue.extend(MusicPlayer);