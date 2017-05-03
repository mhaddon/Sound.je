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
 * Enum of the different types of supported services and their respective identities.
 * @class
 * @member module:Core/MusicPlayer.MediaType
 * @type {{None: number, Native: number, YouTube: number, Facebook: number, SoundCloud: number}}
 */
export const MediaType = {
    None:       -1,
    Native:     0,
    YouTube:    1,
    Facebook:   2,
    SoundCloud: 3
};
