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
import store from "nestedbird/vue/store";
import { Page } from "nestedbird/core/Router";
import { Util } from "nestedbird/core/Util";
import { MusicPlayer } from "nestedbird/core/musicplayer";

export const MediaList = {
    /**
     * The components design template, see the "text/x-template" tags in
     * index.html
     */
    template: `#mediaList-template`,
    /**
     * The components variables that it inherits from its parent
     */
    props:    [],
    /**
     * Every component requires a data feild, but i do not need it as
     * i inherit all the relevent data. So it is filled with nonsense.
     */
    data(): Object {
        return {
            mediaOffset: 0,
            mediaCount:  3,
            currentPage: 0,
            limit:       15
        };
    },
    created() {
        this.$nextTick(() => {
            this.$el.style.opacity = `0`;
            this.$el.style.height = `196px`;
            store.dispatch(`getMediaHot`, {
                page:  this.currentPage,
                limit: this.limit
            }).then(() => {
                setTimeout(() => {
                    this.getMediaList({});
                    this.$el.style.opacity = ``;
                    this.$el.style.height = ``;
                    MusicPlayer.onNext(() => {
                        setTimeout(() => {
                            this.getMediaList();
                        }, 15);
                    });
                    MusicPlayer.onPrevious(() => {
                        setTimeout(() => {
                            this.getMediaList();
                        }, 15);
                    });
                }, 15);
            });
        });
    },
    computed: {
        /**
         * Are we showing the smaller versions
         * @returns {boolean}
         */
        smallBoxes(): boolean {
            return store.getters.currentPage !== Page.MUSIC;
        }
    },
    methods:  {
        getMedia({ page = this.currentPage, limit = this.limit }) {
            return store.dispatch(`getMediaHot`, {
                page,
                limit
            }).then((): number => {
                this.currentPage = page;
                return this.currentPage;
            });
        },
        getMediaList(): Medium[] {
            const sortedList = store.getters.hotMedia
                .slice()
                .sort((a, b) => Math.sign(a.scoreFinal - b.scoreFinal))
                .map(e => store.getters.getMedium(e.id))
                .filter(e => store.getters.supportedMediaTypes.includes(e.type));

            this.$nextTick(() => {
                this.updateMediaVisibility();
            });

            return sortedList;
        },
        updateMediaVisibility() {
            if (store.getters.currentPage === Page.MUSIC) {
                if (this.$el && Util.isElement(this.$el)) {
                    const elements = this.$el.querySelectorAll(`.medium`);
                    if (elements.length) {
                        for (let i = elements.length - 1; i >= 0; i--) {
                            const element = elements[i];
                            element.parentElement.style.display = ``;
                        }
                    }
                }
            } else if (this.$el && Util.isElement(this.$el)) {
                this.mediaCount = Math.floor(this.$el.offsetWidth / 350).min(1);

                const elements = this.$el.querySelectorAll(`.medium`);
                if (elements.length) {
                    let loadedId = 0;
                    for (let i = 0; i < elements.length; i++) {
                        const element = elements[i];
                        element.parentElement.style.display = `none`;
                        if (element && Util.isElement(element) && /medium--loaded/g.exec(element.className)) {
                            loadedId = i - 1;
                        }
                    }
                    this.mediaOffset = loadedId.max(elements.length - this.mediaCount).min(0);

                    for (let i = this.mediaOffset; i < this.mediaOffset + this.mediaCount; i++) {
                        const element = elements[i];
                        if (element && Util.isElement(element)) {
                            element.parentElement.style.display = ``;
                        }
                    }
                }
            }
        }
    }
};

export const VueMediaList = Vue.extend(MediaList);