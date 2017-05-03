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
import { default as Playlist } from "./Playlist";

/**
 * This singleton class manages all the different potential playlists on the website
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @memberOf module:Core/MusicPlayer
 */
class PlaylistController {
    /**
     * The collection of all playlists this controller manages
     * @member module:Core/MusicPlayer.PlaylistController#playlists
     * @type Playlist[]
     * @defaultvalue []
     */
    playlists: Playlist[];

    /**
     * @constructor
     */
    constructor() {
        this.playlists = [];
    }

    /**
     * Gets a playlist by its name, if it doesnt exist, it creates a new one
     * @member module:Core/MusicPlayer.PlaylistController#get
     * @method
     * @param {string} name     name/identifier of playlist
     * @returns {Playlist}
     */
    get(name: string): Playlist {
        return this.playlists.find(e => e.name === name) || this.addAndGet(name);
    }

    /**
     * Creates a new playlist item
     * @member module:Core/MusicPlayer.PlaylistController#add
     * @method
     * @param {string} name     name/identifier of playlist
     */
    add(name: string) {
        if (!this.doesPlaylistExist(name)) {
            this.playlists.push(new Playlist(name));
        }
    }

    /**
     * Adds a new playlist item then retrieves the item it just added
     * @member module:Core/MusicPlayer.PlaylistController#addAndGet
     * @method
     * @param {string} name     name/identifier of playlist
     * @returns {Playlist}
     */
    addAndGet(name: string): Playlist {
        this.add(name);
        return this.getLastElement();
    }

    /**
     * Gets the last playlist element
     * @member module:Core/MusicPlayer.PlaylistController#getLastElement
     * @method
     * @returns {Playlist}
     */
    getLastElement(): Playlist {
        return this.playlists[this.playlists.length - 1];
    }

    /**
     * Does the playlist already exist?
     * @member module:Core/MusicPlayer.PlaylistController#doesPlaylistExist
     * @method
     * @param {string} name     name/identifier of playlist
     * @returns {boolean}
     */
    doesPlaylistExist(name: string): boolean {
        return this.playlists.some(e => e.name === name);
    }
}

export default new PlaylistController();
