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
import slugid from "slugid";

// Site Modules

export default {
    currentPage(state: LocationState): number {
        return state.currentPage;
    },
    currentModal(state: LocationState): number {
        return state.currentModal;
    },
    path(state: LocationState): Object {
        return state.path;
    },
    title(state: LocationState): string {
        return state.title;
    },
    pathQuery(state: LocationState): string {
        return state.pathQuery;
    },
    pathName(state: LocationState): string {
        return state.path.name;
    },
    pathId(state: LocationState): number {
        return state.path.id;
    },
    pathIdDecoded(state: LocationState, getters): string {
        return slugid.decode(getters.pathId || ``);
    },
    isMobile(state: LocationState): boolean {
        return state.isMobile;
    },
    lastModallessUrl(state: LocationState): string {
        return state.lastModallessUrl;
    }
};