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
import { denormalize } from "normalizr";
// Site Modules
import { OptionalEntity, Util } from "nestedbird/core/Util";

import {
    schemaArtist,
    schemaArtistArray,
    schemaEvent,
    schemaEventArray,
    schemaEventTime,
    schemaEventTimeArray,
    schemaLocation,
    schemaLocationArray,
    schemaMedia,
    schemaMediaArray,
    schemaOccurrence,
    schemaOccurrenceArray,
    schemaPrivilege,
    schemaPrivilegeArray,
    schemaRole,
    schemaRoleArray,
    schemaSong,
    schemaSongArray,
    schemaTag,
    schemaTagArray
} from "nestedbird/schemas";

type ServerDataGetters = {
    hotMedia: Object[];
    media: Medium[];
    songs: Song[];
    artists: Artist[];
    locations: Location[];
    roles: Role[];
    privileges: Privilege[];
    events: NBEvent[];
    eventtimes: EventTime[];
    occurrences: Occurrence[];
    getEntity(id: string, schema: Normalizr$SchemaOrObject, dispatch: string, defaultobj: Object): BaseEntity;
    getMedium(id: string): Medium;
    getEvent(id: string): NBEvent;
    getSong(id: string): Song;
    getArtist(id: string): Artist;
    getLocation(id: string): Location;
    getEventTime(id: string): EventTime;
    getEventUrl(e: NBEvent | string): string;
}

export default ({
    hotMedia(state: ServerDataState): Object[] {
        return state.hotMedia;
    },
    media(state: ServerDataState): Medium[] {
        const entities = Util.parse(state.entities);
        return Util.parse(denormalize(
            Object.values(entities.media),
            schemaMediaArray,
            entities));
    },
    songs(state: ServerDataState): Song[] {
        const entities = Util.parse(state.entities);
        return denormalize(
            Object.values(entities.song),
            schemaSongArray,
            entities);
    },
    artists(state: ServerDataState): Artist[] {
        const entities = Util.parse(state.entities);
        return Util.parse(denormalize(
            Object.values(entities.artist),
            schemaArtistArray,
            entities));
    },
    locations(state: ServerDataState): Location[] {
        const entities = Util.parse(state.entities);
        return Util.parse(denormalize(
            Object.values(entities.location),
            schemaLocationArray,
            entities));
    },
    tags(state: ServerDataState): Tag[] {
        const entities = Util.parse(state.entities);
        return Util.parse(denormalize(
            Object.values(entities.tag),
            schemaTagArray,
            entities));
    },
    roles(state: ServerDataState): Role[] {
        const entities = Util.parse(state.entities);
        return Util.parse(denormalize(
            Object.values(entities.role),
            schemaRoleArray,
            entities));
    },
    privileges(state: ServerDataState): Privilege[] {
        const entities = Util.parse(state.entities);
        return Util.parse(denormalize(
            Object.values(entities.privilege),
            schemaPrivilegeArray,
            entities));
    },
    events(state: ServerDataState): NBEvent[] {
        const entities = Util.parse(state.entities);
        return Util.parse(denormalize(
            Object.values(entities.event),
            schemaEventArray,
            entities));
    },
    eventtimes(state: ServerDataState): EventTime[] {
        const entities = Util.parse(state.entities);
        return Util.parse(denormalize(
            Object.values(entities.eventtime),
            schemaEventTimeArray,
            entities));
    },
    occurrences(state: ServerDataState): Occurrence[] {
        const entities = Util.parse(state.entities);
        return Util.parse(denormalize(
            Object.values(entities.occurrence),
            schemaOccurrenceArray,
            entities));
    },
    getEntity: (state: ServerDataState) =>
                   (id: string, schema: Normalizr$SchemaOrObject, dispatch: string, defaultobj: Object = {}): BaseEntity => {
                       const entities = Util.parse(state.entities);
                       const object = Object.assign(defaultobj, Util.parse(denormalize(
                           id,
                           schema,
                           entities)));

                       return OptionalEntity.of(object).orElseRetrieve(dispatch, id);
                   },
    getMedium: (state: ServerDataState, getters: ServerDataGetters) => (id: string): Medium => getters
        .getEntity(id, schemaMedia, `getMedium`, {
            song: {
                artist: {}
            }
        }),
    getEvent:  (state: ServerDataState, getters: ServerDataGetters) => (id: string): NBEvent => getters
        .getEntity(id, schemaEvent, `getEvent`, {
            location: {}
        }),
    getSong:   (state: ServerDataState, getters: ServerDataGetters) => (id: string): Song => getters
        .getEntity(id, schemaSong, `getSong`, {
            artist: {}
        }),

    getArtist: (state: ServerDataState, getters: ServerDataGetters) => (id: string): Artist => getters
        .getEntity(id, schemaArtist, `getArtist`),

    getTag: (state: ServerDataState, getters: ServerDataGetters) => (id: string): Tag => getters
        .getEntity(id, schemaTag, `getTag`),

    getLocation: (state: ServerDataState, getters: ServerDataGetters) => (id: string): Location => getters
        .getEntity(id, schemaLocation, `getLocation`),

    getEventTime: (state: ServerDataState, getters: ServerDataGetters) => (id: string): EventTime => getters
        .getEntity(id, schemaEventTime, `getEventTime`),

    getEventUrl: (state: ServerDataState, getters: ServerDataGetters) => (e: NBEvent | string): string => {
        if (typeof e !== `object`) {
            e = getters.getEvent(e);
        }

        return `/Event/${encodeURIComponent(e.name.replace(/ /g, `_`))}-${e.id}`;
    }
});