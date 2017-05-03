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

type MusicPlayerReactiveData = {
    /**
     * percentage of how much the media has buffered
     */
        bufferPercent: number,
    /**
     * current place in the media item
     */
        currentTime: number,
    /**
     * duration of the media item
     */
        duration: number,
    /**
     * Volume of the media element, 0 to 1
     */
        volume: number,
    /**
     * Is the media paused
     */
        paused: boolean,
    /**
     * The currently playing songs name
     */
        songName: string,
    /**
     * The currently playing artists name
     */
        artistName: string,
    /**
     * Is the media in a playable state?
     */
        playable: boolean,
    /**
     * Currently loaded playlists name
     */
        playlistName: string,
    /**
     * What type is the currently playing media item
     * @see MediaType
     */
        type: number
}

type MusicPlayerState = {
    musicPlayer: MusicPlayerReactiveData,
    supportedMediaTypes: string[]
}