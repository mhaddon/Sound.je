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
import store from "nestedbird/vue/store";

export default class OptionalEntity {
    static of(entity) {
        return new OptionalEntity(entity);
    }

    _entity: Optional<BaseEntity>;

    constructor(entity: BaseEntity) {
        this._entity = Optional.ofNullable(entity);
    }

    get(): BaseEntity {
        return this._entity.get();
    }

    isPresent(): boolean {
        return this._entity.isPresent() && this._entity.filter(e => !!e.id).isPresent();
    }

    ifPresent(fn: Function) {
        if (this.isPresent()) {
            fn();
        }
    }

    orElse(val: Object): Object {
        return this.isPresent() ? this.get() : val;
    }

    orElseGet(fn: Function): Object {
        return this.isPresent() ? this.get() : fn();
    }

    orElseRetrieve(dispatch: string, id: string): Object {
        if (!this.isPresent()) {
            store.dispatch(dispatch, id);
        }
        return this.get();
    }
};