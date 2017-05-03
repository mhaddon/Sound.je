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
import Optional from "optional-js";

// Site Modules

export default {
    isLoggedIn(state: AuthState): boolean {
        return !!state.email && !!state.userId && state.authorities.length > 0;
    },
    isAdmin(state: AuthState, getters: Object): boolean {
        return getters.isLoggedIn && getters.hasRole(`PRIV_ADMIN`);
    },
    isModerator(state: AuthState, getters: Object): boolean {
        return getters.isLoggedIn && getters.hasRole(`PRIV_MODERATOR`);
    },
    isUser(state: AuthState, getters: Object): boolean {
        return getters.isLoggedIn;
    },
    hasRole: (state: AuthState) => (role: string): boolean => state.authorities.some(e => e === role),
    authorities(state: AuthState): Optional<string[]> {
        return Optional.ofNullable(state.authorities.length ? state.authorities : null);
    }
};