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

/**
 * This class handles an individual playlist, it is managed by PlaylistController
 * @see module:Core/MusicPlayer.PlaylistController
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @memberOf module:Core/MusicPlayer
 * @param {string} name         this playlists name/identifier
 */
export default class Playlist {
    /**
     * Array index of the currently playing element
     * @member module:Core/MusicPlayer.Playlist#id
     * @type number
     * @defaultvalue 0
     */
    id: number;

    /**
     * Array of all the vue elements in this playlist
     * @member module:Core/MusicPlayer.Playlist#elements
     * @type Object[]
     * @defaultvalue []
     */
    elements: Object[];

    /**
     * Name/Identifier of this playlist
     * @member module:Core/MusicPlayer.Playlist#name
     * @type string
     */
    name: string;

    /**
     * @constructor
     * @param {string} name     this playlists name/identifier
     */
    constructor(name: string) {
        this.id = 0;
        this.elements = [];
        this.name = name;
    }

    /**
     * Adds a new vue element to the playlist
     * @member module:Core/MusicPlayer.Playlist#add
     * @method
     * @param {object} element      vue element to add
     * @throws {TypeError} arguments must not be null
     */
    add(element: Object) {
        if (typeof element !== `object`) throw new TypeError(`element is not an object`);
        if (!this.doesElementExist(element)) {
            this.elements.push(element);
        }
    }

    /**
     * Removes a new vue element to the playlist
     * @member module:Core/MusicPlayer.Playlist#remove
     * @method
     * @param {object} element      vue element to add
     * @throws {TypeError} arguments must not be null
     */
    remove(element: Object) {
        if (typeof element !== `object`) throw new TypeError(`element is not an object`);
        this.elements = this.elements.filter(e => e !== element);
    }

    /**
     * Loops over all the playlist items, finds and record the item that is currently playing
     * @member module:Core/MusicPlayer.Playlist#setIdToPlayingElement
     * @method
     */
    setIdToPlayingElement() {
        this.id = (this.elements.findIndex(e => e.loaded) || 0).min(0);
    }

    /**
     * Gets a playlist item by its array index
     * @member module:Core/MusicPlayer.Playlist#get
     * @method
     * @param {object} id   array index
     * @returns {object} vue item
     * @throws {TypeError} arguments must not be null
     */
    get(id: number): Object {
        if (typeof id !== `number`) throw new TypeError(`id is not a valid index`);
        return this.elements[id];
    }

    /**
     * Gets the currently playing vue element
     * @member module:Core/MusicPlayer.Playlist#getPlayingElement
     * @method
     * @returns {object} vue item
     */
    getPlayingElement(): Object {
        this.setIdToPlayingElement();
        return this.get(this.id);
    }

    /**
     * Gets the index of the next playlist element, wraps around to the start if at the end
     * @member module:Core/MusicPlayer.Playlist#getNextId
     * @method
     * @returns {number}
     */
    getNextId(): number {
        let id = this.id + 1;
        if (id >= this.elements.length) {
            id = 0;
        }
        return id.max(this.elements.length - 1).min(0);
    }

    /**
     * Gets the index of the previous playlist element, wraps around to the end if at the start
     * @member module:Core/MusicPlayer.Playlist#getPreviousId
     * @method
     * @returns {number}
     */
    getPreviousId(): number {
        let id = this.id - 1;
        if (id < 0) {
            id = this.elements.length - 1;
        }
        return id.max(this.elements.length - 1).min(0);
    }

    /**
     * Gets the next vue element in the playlist
     * @member module:Core/MusicPlayer.Playlist#getNextElement
     * @method
     * @returns {object}
     */
    getNextElement(): Object {
        return this.get(this.getNextId());
    }

    /**
     * Gets the previous vue element in the playlist
     * @member module:Core/MusicPlayer.Playlist#getPreviousElement
     * @method
     * @returns {object}
     */
    getPreviousElement(): Object {
        return this.get(this.getPreviousId());
    }

    /**
     * Does the playlist already have this vue element?
     * @member module:Core/MusicPlayer.Playlist#doesElementExist
     * @param {object} element  vue element to check
     * @method
     * @returns {boolean}
     */
    doesElementExist(element: Object): boolean {
        return this.elements.some(e => e === element);
    }
};
