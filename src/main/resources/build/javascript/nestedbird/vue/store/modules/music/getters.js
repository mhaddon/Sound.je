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

// Site Modules

export default {
    supportedMediaTypes(state: MusicPlayerState): string[] {
        return state.supportedMediaTypes;
    },
    bufferPercent(state: MusicPlayerState): number {
        return state.musicPlayer.bufferPercent;
    },
    currentTime(state: MusicPlayerState): number {
        return state.musicPlayer.currentTime;
    },
    duration(state: MusicPlayerState): number {
        return state.musicPlayer.duration;
    },
    volume(state: MusicPlayerState): number {
        return state.musicPlayer.volume;
    },
    paused(state: MusicPlayerState): boolean {
        return state.musicPlayer.paused;
    },
    songName(state: MusicPlayerState): string {
        return state.musicPlayer.songName;
    },
    artistName(state: MusicPlayerState): string {
        return state.musicPlayer.artistName;
    },
    playable(state: MusicPlayerState): boolean {
        return state.musicPlayer.playable;
    },
    playlistName(state: MusicPlayerState): string {
        return state.musicPlayer.playlistName;
    },
    type(state: MusicPlayerState): number {
        return state.musicPlayer.type;
    }
};