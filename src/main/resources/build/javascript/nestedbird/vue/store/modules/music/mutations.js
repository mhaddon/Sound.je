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
    setBufferPercent(state: MusicPlayerState, percentage: number) {
        state.musicPlayer.bufferPercent = percentage;
    },
    setCurrentTime(state: MusicPlayerState, currentTime: number) {
        state.musicPlayer.currentTime = currentTime;
    },
    setDuration(state: MusicPlayerState, duration: number) {
        state.musicPlayer.duration = duration;
    },
    setVolume(state: MusicPlayerState, volume: number) {
        state.musicPlayer.volume = volume;
    },
    setPaused(state: MusicPlayerState, paused: boolean) {
        state.musicPlayer.paused = paused;
    },
    setSongName(state: MusicPlayerState, songName: string) {
        state.musicPlayer.songName = songName;
    },
    setArtistName(state: MusicPlayerState, artistName: string) {
        state.musicPlayer.artistName = artistName;
    },
    setPlayable(state: MusicPlayerState, playable: boolean) {
        state.musicPlayer.playable = playable;
    },
    setPlaylistName(state: MusicPlayerState, playlistName: string) {
        state.musicPlayer.playlistName = playlistName;
    },
    setType(state: MusicPlayerState, type: number) {
        state.musicPlayer.type = type;
    }
};