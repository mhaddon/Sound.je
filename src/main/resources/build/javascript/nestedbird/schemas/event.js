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
import { schema } from "normalizr";
// Site Modules
import { schemaArtistArray } from "./artist";
import { schemaEventTimeArray } from "./eventtime";
import { schemaLocation } from "./location";

export const schemaEvent = new schema.Entity(`event`, {
    location: schemaLocation,
    times:    schemaEventTimeArray,
    artists:  schemaArtistArray
}, {
    idAttribute: `id`
});

export const schemaEventArray = new schema.Array(schemaEvent);