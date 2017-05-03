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
import Vue from "vue/dist/vue";
// Site Modules
import { Util } from "nestedbird/core/Util";

export const PeriodField = {
    template: `#periodField-template`,
    props:    [
        `id`,
        `name`,
        `value`
    ],
    data(): Object {
        return {
            elements:        [
                {
                    name: `Seconds`,
                    id:   1
                }, {
                    name: `Minutes`,
                    id:   60
                }, {
                    name: `Hours`,
                    id:   60 * 60
                }, {
                    name: `Days`,
                    id:   60 * 60 * 24
                }, {
                    name: `Weeks`,
                    id:   60 * 60 * 24 * 7
                }
            ],
            currentEntityId: 1,
            currentValue:    1
        };
    },
    mounted() {
        this.currentEntityId = this.getDivisable(this.value).id;
        this.currentValue = this.getDivisableValue(this.value);
    },
    computed: {
        summedValue(): number {
            return parseInt(this.currentValue, 10) * this.currentEntityId;
        }
    },
    methods:  {
        updateSelected(name: string, entity: Object) {
            this.currentEntityId = entity.id;
        },
        getDivisable(value: number): Object {
            return Util.parse(this.elements)
                    .reverse()
                    .find((e: Object) => e.id <= value) || this.elements[0];
        },
        getDivisableValue(value: number): number {
            return (value / this.getDivisable(value).id) || 0;
        }
    }
};

export const VuePeriodField = Vue.extend(PeriodField);