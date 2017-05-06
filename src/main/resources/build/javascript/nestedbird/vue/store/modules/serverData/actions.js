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
import { Util } from "nestedbird/core/Util";
import { Ajax } from "nestedbird/core/Ajax";

import {
    schemaArtistArray,
    schemaEventArray,
    schemaEventTimeArray,
    schemaLocationArray,
    schemaMediaArray,
    schemaOccurrenceArray,
    schemaPrivilegeArray,
    schemaRoleArray,
    schemaSongArray,
    schemaTagArray
} from "nestedbird/schemas";

declare class ServerDataRequestObject {
    apiurl: string;
    schema: Normalizr$Schema;
}

declare class ServerDataPaginatedRequestObject extends ServerDataRequestObject {
    page: number;
    limit: number;
    sort: string;
}

declare class ServerDataPaginatedRequestObjectOfSpecific extends ServerDataRequestObject {
    page: number;
    limit: number;
    sort: string;
    id: string;
}

declare class StatefulServerDataPaginatedRequestObject extends ServerDataPaginatedRequestObject {
    currentEntities: Object
}

export default {
    getData({ commit }: VuexActionOperations<ServerDataState>, { apiurl = ``, schema }: ServerDataRequestObject): Promise<
        string
        | Object> {
        return new Promise((resolve, reject) => {
            Ajax.create(apiurl, `GET`).onSuccess((response) => {
                const responseObject = JSON.parse(response);
                if (Object.getOwnPropertyNames(responseObject).length !== 0) {
                    resolve([Util.clean(responseObject)]);
                } else {
                    reject(response);
                }
            }).onFailure((response) => {
                console.log(`Failed to make ajax request with response:`, response);
                reject(response);
            });
        }).then((content) => {
            commit(`saveData`, {
                content,
                schema
            });
            return Util.parse(content);
        });
    },
    getPagedData({ commit }: VuexActionOperations<ServerDataState>, { apiurl = ``, page = 0, limit = 15, sort = ``, schema }: ServerDataPaginatedRequestObject): Promise<
        string
        | RESTObject<Object>> {
        return new Promise((resolve, reject) => {
            Ajax.create(`${apiurl}?page=${page}&limit=${limit}&sort=${sort}`, `GET`).onSuccess((response) => {
                const responseObject = JSON.parse(response);
                if (responseObject.content && responseObject.content.length) {
                    resolve(Util.clean(responseObject));
                } else {
                    reject(response);
                }
            }).onFailure((response) => {
                console.log(`Failed to make ajax request with response:`, response);
                reject(response);
            });
        }).then((response) => {
            commit(`saveData`, {
                content: response.content,
                schema
            });
            return Util.parse(response);
        });
    },
    getPagedDataContent({ dispatch }: VuexActionOperations<ServerDataState>, { apiurl = ``, page = 0, limit = 15, sort = ``, schema }: ServerDataPaginatedRequestObject): Promise<
        string
        | Object[]> {
        return dispatch(`getPagedData`, {
            apiurl,
            page,
            limit,
            sort,
            schema
        }).then(response => response.content);
    },
    getAllPagedDataContent({ dispatch }: VuexActionOperations<ServerDataState>, { apiurl = ``, page = 0, limit = 100, sort = ``, schema, currentEntities = {} }: StatefulServerDataPaginatedRequestObject): Promise<
        string
        | Object[]> {
        return dispatch(`getPagedData`, {
            apiurl,
            page,
            limit,
            sort,
            schema
        }).then((response) => {
            const maxPages = response.totalPages;
            const currentPage = response.number;
            let returnVar = Promise.resolve(currentEntities);

            if (response.totalElements > Object.keys(currentEntities).length) {
                const promiseList = [Promise.resolve(response)];

                for (let i = currentPage + 1; i < maxPages; i++) {
                    promiseList.push(dispatch(`getPagedDataContent`, {
                        apiurl,
                        page: i,
                        limit,
                        sort,
                        schema
                    }));
                }

                returnVar = Promise.all(promiseList)
                    .then(values => values.map(e => e.content).reduce((a, b) => a.concat(b)));
            }

            return returnVar;
        });
    },
    getMedia({ dispatch }: VuexActionOperations<ServerDataState>, { page = 0, limit = 15, sort = `` }: ServerDataPaginatedRequestObject): Promise<
        string
        | RESTObject<Medium>> {
        return dispatch(`getPagedDataContent`, {
            apiurl: `/api/v1/Media`,
            page,
            limit,
            sort,
            schema: schemaMediaArray
        });
    },
    getMediaHot({ dispatch, state }: VuexActionOperations<ServerDataState>, { page = 0, limit = 15, sort = `` }: ServerDataPaginatedRequestObject): Promise<
        string
        | RESTObject<Medium>> {
        return dispatch(`getPagedDataContent`, {
            apiurl: `/api/v1/Media/Hot`,
            page,
            limit,
            sort,
            schema: schemaMediaArray
        }).then((response) => {
            for (const medium of response) {
                if (!state.hotMedia.some(e => e.id === medium.id)) {
                    state.hotMedia.push({
                        id:               medium.id,
                        score:            medium.score,
                        scoreFinal:       medium.scoreFinal,
                        creationDateTime: medium.creationDateTime
                    });
                }
            }
            return Util.parse(state.hotMedia);
        });
    },
    getMedium({ dispatch }: VuexActionOperations<ServerDataState>, id: string): Promise<string | Medium> {
        return dispatch(`getData`, {
            apiurl: `/api/v1/Media/${id}`,
            schema: schemaMediaArray
        });
    },
    saveMedium({ commit }: VuexActionOperations<ServerDataState>, medium: Medium): Medium {
        const content = Util.clean(medium);
        commit(`saveData`, {
            content: [content],
            schema:  schemaMediaArray
        });
        return content;
    },
    getEvents({ dispatch }: VuexActionOperations<ServerDataState>, { page = 0, limit = 15, sort = `` }: ServerDataPaginatedRequestObject): Promise<
        string
        | RESTObject<NBEvent>> {
        return dispatch(`getPagedDataContent`, {
            apiurl: `/api/v1/Events/Upcoming`,
            page,
            limit,
            sort,
            schema: schemaOccurrenceArray
        });
    },
    getEvent({ dispatch }: VuexActionOperations<ServerDataState>, id: string): Promise<string | NBEvent> {
        return dispatch(`getData`, {
            apiurl: `/api/v1/Events/${id}`,
            schema: schemaEventArray
        });
    },
    saveEvent({ commit }: VuexActionOperations<ServerDataState>, event: NBEvent): NBEvent {
        const content = Util.clean(event);
        commit(`saveData`, {
            content: [content],
            schema:  schemaEventArray
        });
        return content;
    },
    getEventTime({ dispatch }: VuexActionOperations<ServerDataState>, id: string): Promise<string | EventTime> {
        return dispatch(`getData`, {
            apiurl: `/api/v1/EventTimes/${id}`,
            schema: schemaEventTimeArray
        });
    },
    getArtistMedia({ dispatch }: VuexActionOperations<ServerDataState>, { page = 0, limit = 15, id, sort = `` }: ServerDataPaginatedRequestObjectOfSpecific): Promise<
        string
        | RESTObject<Medium>> {
        return dispatch(`getPagedDataContent`, {
            apiurl: `/api/v1/Artists/${id}/Media`,
            page,
            limit,
            sort,
            schema: schemaMediaArray
        });
    },
    getArtistEvents({ dispatch }: VuexActionOperations<ServerDataState>, { page = 0, limit = 15, id, sort = `` }: ServerDataPaginatedRequestObjectOfSpecific): Promise<
        string
        | RESTObject<Occurrence>> {
        return dispatch(`getPagedDataContent`, {
            apiurl: `/api/v1/Artists/${id}/Events`,
            page,
            limit,
            sort,
            schema: schemaOccurrenceArray
        });
    },
    getArtist({ dispatch }: VuexActionOperations<ServerDataState>, id: string): Promise<string | Artist> {
        return dispatch(`getData`, {
            apiurl: `/api/v1/Artists/${id}`,
            schema: schemaArtistArray
        });
    },
    getArtists({ dispatch, state }: VuexActionOperations<ServerDataState>, { page = 0, limit = 100, sort = `` }: ServerDataPaginatedRequestObject): Promise<
        string
        | Artist[]> {
        return dispatch(`getAllPagedDataContent`, {
            apiurl:          `/api/v1/Artists/`,
            page,
            limit,
            sort,
            schema:          schemaArtistArray,
            currentEntities: state.entities.artist
        });
    },
    saveArtist({ commit }: VuexActionOperations<ServerDataState>, artist: Artist): Artist {
        const content = Util.clean(artist);
        commit(`saveData`, {
            content: [content],
            schema:  schemaArtistArray
        });
        return content;
    },
    getLocations({ dispatch, state }: VuexActionOperations<ServerDataState>, { page = 0, limit = 100, sort = `` }: ServerDataPaginatedRequestObject): Promise<
        string
        | Location[]> {
        return dispatch(`getAllPagedDataContent`, {
            apiurl:          `/api/v1/Locations/`,
            page,
            limit,
            sort,
            schema:          schemaLocationArray,
            currentEntities: state.entities.location
        });
    },
    getLocation({ dispatch }: VuexActionOperations<ServerDataState>, id: string): Promise<string | Location> {
        return dispatch(`getData`, {
            apiurl: `/api/v1/Locations/${id}`,
            schema: schemaLocationArray
        });
    },
    getLocationEvents({ dispatch }: VuexActionOperations<ServerDataState>, { page = 0, limit = 15, id, sort = `` }: ServerDataPaginatedRequestObjectOfSpecific): Promise<
        string
        | RESTObject<Occurrence>> {
        return dispatch(`getPagedDataContent`, {
            apiurl: `/api/v1/Locations/${id}/Events`,
            page,
            limit,
            sort,
            schema: schemaOccurrenceArray
        });
    },
    saveLocation({ commit }: VuexActionOperations<ServerDataState>, location: Location): Location {
        const content = Util.clean(location);
        commit(`saveData`, {
            content: [content],
            schema:  schemaLocationArray
        });
        return content;
    },
    getSongs({ dispatch, state }: VuexActionOperations<ServerDataState>, { page = 0, limit = 100, sort = `` }: ServerDataPaginatedRequestObject): Promise<
        string
        | Song[]> {
        return dispatch(`getAllPagedDataContent`, {
            apiurl:          `/api/v1/Songs/`,
            page,
            limit,
            sort,
            schema:          schemaSongArray,
            currentEntities: state.entities.song
        });
    },
    getSong({ dispatch }: VuexActionOperations<ServerDataState>, id: string): Promise<string | Song> {
        return dispatch(`getData`, {
            apiurl: `/api/v1/Songs/${id}`,
            schema: schemaSongArray
        });
    },
    saveSong({ commit }: VuexActionOperations<ServerDataState>, song: Song): Song {
        const content = Util.clean(song);
        commit(`saveData`, {
            content: [content],
            schema:  schemaSongArray
        });
        return content;
    },
    getRoles({ dispatch, state }: VuexActionOperations<ServerDataState>, { page = 0, limit = 100, sort = `` }: ServerDataPaginatedRequestObject): Promise<
        string
        | Role[]> {
        return dispatch(`getAllPagedDataContent`, {
            apiurl:          `/api/v1/Roles/`,
            page,
            limit,
            sort,
            schema:          schemaRoleArray,
            currentEntities: state.entities.role
        });
    },
    getRole({ dispatch }: VuexActionOperations<ServerDataState>, id: string): Promise<string | Role> {
        return dispatch(`getData`, {
            apiurl: `/api/v1/Roles/${id}`,
            schema: schemaRoleArray
        });
    },
    saveRole({ commit }: VuexActionOperations<ServerDataState>, role: Role): Role {
        const content = Util.clean(role);
        commit(`saveData`, {
            content: [content],
            schema:  schemaRoleArray
        });
        return content;
    },
    getPrivileges({ dispatch, state }: VuexActionOperations<ServerDataState>, { page = 0, limit = 100, sort = `` }: ServerDataPaginatedRequestObject): Promise<
        string
        | Privilege[]> {
        return dispatch(`getAllPagedDataContent`, {
            apiurl:          `/api/v1/Privileges/`,
            page,
            limit,
            sort,
            schema:          schemaPrivilegeArray,
            currentEntities: state.entities.privilege
        });
    },
    getPrivilege({ dispatch }: VuexActionOperations<ServerDataState>, id: string): Promise<string | Privilege> {
        return dispatch(`getData`, {
            apiurl: `/api/v1/Privileges/${id}`,
            schema: schemaPrivilegeArray
        });
    },
    savePrivilege({ commit }: VuexActionOperations<ServerDataState>, privilege: Privilege): Privilege {
        const content = Util.clean(privilege);
        commit(`saveData`, {
            content: [content],
            schema:  schemaPrivilegeArray
        });
        return content;
    },
    getTags({ dispatch, state }: VuexActionOperations<ServerDataState>, { page = 0, limit = 100, sort = `` }: ServerDataPaginatedRequestObject): Promise<
        string
        | Tag[]> {
        return dispatch(`getAllPagedDataContent`, {
            apiurl:          `/api/v1/Tags/`,
            page,
            limit,
            sort,
            schema:          schemaTagArray,
            currentEntities: state.entities.tag
        });
    },
    getTag({ dispatch }: VuexActionOperations<ServerDataState>, id: string): Promise<string | Tag> {
        return dispatch(`getData`, {
            apiurl: `/api/v1/Tags/${id}`,
            schema: schemaTagArray
        });
    },
    saveTag({ commit }: VuexActionOperations<ServerDataState>, tag: Tag): Tag {
        const content = Util.clean(tag);
        commit(`saveData`, {
            content: [content],
            schema:  schemaTagArray
        });
        return content;
    }
};