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
import Optional from "optional-js";
// Site Modules
import store from "nestedbird/vue/store";
import { Util } from "nestedbird/core/Util";
import { Router } from "nestedbird/core/Router";
import { SchemaData, SchemaReader } from "nestedbird/core/SchemaReader";

class PageObject {
    value: number;
    colour: string;
    active: boolean;

    constructor(value: number, colour: string, active: boolean) {
        this.value = value;
        this.colour = colour;
        this.active = active;
    }
}

export const SmartTable = {
    /**
     * The components design template, see the "text/x-template" tags in
     * index.html
     */
    template: `#smartTable-template`,
    /**
     * The components variables that it inherits from its parent
     */
    props:    [
        `APIUrl`
    ],
    /**
     * Every component requires a data feild, but i do not need it as
     * i inherit all the relevent data. So it is filled with nonsense.
     */
    data(): Object {
        return {
            name:            store.getters.pathName,
            pageCount:       1,
            totalElements:   0,
            dataStore:       [],
            schemaData:      [],
            sortTimeout:     0,
            searchTimeout:   0,
            currentFormData: []
        };
    },
    mounted() {
        this.width = this.$el.getBoundingClientRect().width;

        this.getData();
    },
    beforeDestroy() {

    },
    computed: {
        shownElements(): number {
            return this.dataStore.length;
        },
        search: {
            get(): string {
                return store.getters.pathQuery.query || ``;
            },
            set(search: string) {
                store.commit(`setQueryField`, {
                    name:  `query`,
                    value: search
                });
            }
        },
        sort:   {
            get(): string {
                return store.getters.pathQuery.sort || ``;
            },
            set(sort: string) {
                store.commit(`setQueryField`, {
                    name:  `sort`,
                    value: sort
                });
            }
        },
        page:   {
            get(): number {
                return parseInt(store.getters.pathQuery.page || 0, 10);
            },
            set(page: number) {
                store.commit(`setQueryField`, {
                    name:  `page`,
                    value: page
                });
            }
        },
        limit:  {
            get(): number {
                return parseInt(store.getters.pathQuery.limit || 15, 10);
            },
            set(limit: number) {
                store.commit(`setQueryField`, {
                    name:  `limit`,
                    value: limit
                });
            }
        }
    },
    watch:    {
        search() {
            this.searchData();
        },
        sort() {
            this.searchData();
        },
        page() {
            this.getData();
        },
        limit() {
            this.searchData();
        }
    },
    methods:  {
        getObjectId(object: Object): string | null {
            return (typeof object === `object`) ? object.id : null;
        },
        getObjectName(object: Object): string | null {
            let value = null;
            if (typeof object === `object`) {
                value = object.name || object.id || null;
            }
            return value;
        },
        parseObject(object: Object): string {
            return JSON.stringify(Util.clean(object), null, 2);
        },
        parseDateTime(dateTime: string): string {
            return `${Util.parseDateTime(dateTime)} ${Util.parseTime(dateTime)}`;
        },
        getFacebookUrl(facebookId: string): string {
            return `https://www.facebook.com/${facebookId}`;
        },
        getPages(): PageObject[] {
            const maxPages = Math.floor(this.width / 75);
            const pageArray: PageObject[] = [];

            for (let i = 0; i < maxPages; i++) {
                let number = i;
                if (maxPages < this.pageCount) {
                    number = (this.page - Math.floor(maxPages / 2)).min(0) + i;
                }

                if (number < this.pageCount) {
                    pageArray.push(new PageObject(
                        number + 1,
                        number === this.page ? `#376890` : `grey`,
                        number === this.page));
                }
            }

            return pageArray;
        },
        changePage(e) {
            this.page = parseInt(e.target.innerText, 10) - 1;
        },
        getApiQuery(): string {
            return [
                (this.search) ? `query=${this.search}&` : ``,
                (this.sort) ? `sort=${this.sort}&` : ``,
                (this.page && this.page > 0) ? `page=${this.page}&` : ``,
                (this.size && this.size > 0) ? `limit=${this.size}&` : ``
            ].join(``);
        },
        getAPIUrl(): string {
            return `${this.APIUrl}?${this.getAPIUrl()}`;
        },
        searchData() {
            window.clearTimeout(this.searchTimeout);
            this.searchTimeout = window.setTimeout(() => {
                this.page = 0;
                this.getData();
            }, 250);
        },
        getData(): Promise<Optional<SchemaData>> {
            Router.redirectQuery(this.getApiQuery());
            return SchemaReader.create(this.APIUrl, this.getApiQuery()).retrieveList()
                .then((schemaDataOptional: Optional<SchemaData>) => {
                    schemaDataOptional.ifPresent((schemaData: SchemaData) => {
                        this.dataStore = schemaData.elements;
                        this.pageCount = schemaData.pageCount;
                        this.totalElements = schemaData.totalElements;
                        this.schemaData = schemaData.schema;
                    });
                });
        }
    }
};

export const VueSmartTable = Vue.extend(SmartTable);