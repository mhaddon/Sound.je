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
import { SchemaReader } from "nestedbird/core/SchemaReader";
import { Util } from "nestedbird/core/Util";

/**
 * Vue wrapper for flatpickr, or any later alternative datetime pickers.
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Components
 */
export const EditableFormData = {
    /**
     * The module template
     * @member module:Vue/Components.EditableFormData#template
     * @default #editableFormData-template
     * @type string
     */
    template: `#editableFormData-template`,
    props:    {
        /**
         * the form data we are editing, this is a vue property
         * @member module:Vue/Components.EditableFormData#formData
         * @default []
         * @type Object[]
         */
        formData:       {
            type:    Array,
            default: []
        },
        /**
         * will uneditable elements be hidden
         * @member module:Vue/Components.EditableFormData#hideuneditable
         * @default false
         * @type boolean
         */
        hideuneditable: {
            type:    Boolean,
            default: false
        },
        /**
         * how will this data be arranged, this is a flex-direction value, column, row, column-reverse, row-reverse
         * @member module:Vue/Components.EditableFormData#direction
         * @default column
         * @type string
         */
        direction:      {
            type:    String,
            default: `column`
        },
        /**
         * this elements name prefix
         * this is only used if this is a nested element
         * @member module:Vue/Components.EditableFormData#prefix
         * @default null
         * @type string | null
         */
        prefix:         {
            type:    String,
            default: null
        },
        /**
         * this elements array index
         * this is only used if this is a nested element
         * @member module:Vue/Components.EditableFormData#index
         * @default null
         * @type number | null
         */
        index:          {
            type:    Number,
            default: null
        }
    },
    data(): Object {
        return {
            /**
             * a clone of formData that contains the edits, this is because we dont want the changes to bubble up
             * @member module:Vue/Components.EditableFormData#editedFormData
             * @type Object[]
             */
            editedFormData:  this.formData,
            /**
             * whether the data this is associated with has loaded yet
             * @member module:Vue/Components.EditableFormData#loadedData
             * @type boolean
             */
            loadedData:      this.formData.length > 0,
            /**
             * records whether the ajax data has loaded
             * @member module:Vue/Components.EditableFormData#loadedData
             * @type boolean
             */
            loadedPages:     {},
            /**
             * contains the data for loading entitiy field data elements
             * @member module:Vue/Components.EditableFormData#entityFieldData
             * @type Object
             */
            entityFieldData: {
                Location:  {
                    type:     `Locations`,
                    elements: () => this.locations
                },
                Event:     {
                    type:     `Events`,
                    elements: () => this.events
                },
                Medium:    {
                    type:     `Media`,
                    elements: () => this.media
                },
                Song:      {
                    type:     `Songs`,
                    elements: () => this.songs
                },
                Artist:    {
                    type:     `Artists`,
                    elements: () => this.artists
                },
                User:      {
                    type:     `Users`,
                    elements: () => this.users
                },
                Role:      {
                    type:     `Roles`,
                    elements: () => this.roles
                },
                Privilege: {
                    type:     `Privileges`,
                    elements: () => this.privileges
                },
                Tag:       {
                    type:     `Tags`,
                    elements: () => this.tags
                }
            }
        };
    },
    watch:    {
        formData() {
            if (!this.loadedData) {
                this.editedFormData = Util.parse(this.formData);
                this.loadedData = true;
            }
        }
    },
    computed: {
        /**
         * retrieves all locations
         * @member module:Vue/Components.EditableFormData#locations
         * @type Location[]
         */
        locations(): Location[] {
            return this.getAllLocations() || store.getters.locations;
        },
        /**
         * retrieves all artists
         * @member module:Vue/Components.EditableFormData#artists
         * @type Artist[]
         */
        artists(): Artist[] {
            return this.getAllArtists() || store.getters.artists;
        },
        /**
         * retrieves all roles
         * @member module:Vue/Components.EditableFormData#roles
         * @type Role[]
         */
        roles(): Role[] {
            return this.getAllRoles() || store.getters.roles;
        },
        /**
         * retrieves all privileges
         * @member module:Vue/Components.EditableFormData#privileges
         * @type Privilege[]
         */
        privileges(): Privilege[] {
            return this.getAllPrivileges() || store.getters.privileges;
        },
        /**
         * retrieves all locations
         * @member module:Vue/Components.EditableFormData#events
         * @type NBEvent[]
         */
        events(): NBEvent[] {
            return this.getAllEvents() || store.getters.events;
        },
        /**
         * retrieves all tags
         * @member module:Vue/Components.EditableFormData#tags
         * @type Tag[]
         */
        tags(): Tag[] {
            return this.getAllTags() || store.getters.tags;
        },
        /**
         * retrieves all songs
         * @member module:Vue/Components.EditableFormData#songs
         * @type Song[]
         */
        songs(): Song[] {
            return this.getAllSongs() || store.getters.songs;
        }
    },
    methods:  {
        /**
         * if this page has not been recorded, we record it, then return true
         * otherwise return false
         * @member module:Vue/Components.EditableFormData#recordLoadedPage
         * @method
         * @param {string} page       page to record
         * @return {boolean}
         */
        recordLoadedPage(page: string): boolean {
            if (!this.loadedPages.hasOwnProperty(page)) {
                this.loadedPages[page] = true;
                return true;
            }
            return false;
        },
        /**
         * Retrieve all roles from the database, if we havnt already tried
         * @member module:Vue/Components.EditableFormData#getAllRoles
         * @method
         */
        getAllRoles() {
            if (this.recordLoadedPage(`roles`)) {
                store.dispatch(`getRoles`, {}).then(() => {
                    this.$forceUpdate();
                });
            }
        },
        /**
         * Retrieve all tags from the database, if we havnt already tried
         * @member module:Vue/Components.EditableFormData#getAllTags
         * @method
         */
        getAllTags() {
            if (this.recordLoadedPage(`tags`)) {
                store.dispatch(`getTags`, {}).then(() => {
                    this.$forceUpdate();
                });
            }
        },
        /**
         * Retrieve all privileges from the database, if we havnt already tried
         * @member module:Vue/Components.EditableFormData#getAllPrivileges
         * @method
         */
        getAllPrivileges() {
            if (this.recordLoadedPage(`privileges`)) {
                store.dispatch(`getPrivileges`).then(() => {
                    this.$forceUpdate();
                });
            }
        },
        /**
         * Retrieve all locations from the database, if we havnt already tried
         * @member module:Vue/Components.EditableFormData#getAllLocations
         * @method
         */
        getAllLocations() {
            if (this.recordLoadedPage(`locations`)) {
                store.dispatch(`getLocations`, {}).then(() => {
                    this.$forceUpdate();
                });
            }
        },
        /**
         * Retrieve all artists from the database, if we havnt already tried
         * @member module:Vue/Components.EditableFormData#getAllArtists
         * @method
         */
        getAllArtists() {
            if (this.recordLoadedPage(`artists`)) {
                store.dispatch(`getArtists`, {}).then(() => {
                    this.$forceUpdate();
                });
            }
        },
        /**
         * Retrieve all events from the database, if we havnt already tried
         * @member module:Vue/Components.EditableFormData#getAllEvents
         * @method
         */
        getAllEvents() {
            if (this.recordLoadedPage(`events`)) {
                store.dispatch(`getEvents`, {}).then(() => {
                    this.$forceUpdate();
                });
            }
        },
        /**
         * Retrieve all songs from the database, if we havnt already tried
         * @member module:Vue/Components.EditableFormData#getAllSongs
         * @method
         */
        getAllSongs() {
            if (this.recordLoadedPage(`songs`)) {
                store.dispatch(`getSongs`, {}).then(() => {
                    this.$forceUpdate();
                });
            }
        },
        /**
         * Generates the html form name for this element
         * @member module:Vue/Components.EditableFormData#getObjectName
         * @method
         * @param {string} name current name
         * @returns string
         */
        getObjectName(name: string): string {
            let returnVar = name;

            if (this.prefix && !isNaN(this.index)) {
                returnVar = `${this.prefix}[${this.index}].${name}`;
            } else if (this.prefix) {
                returnVar = `${this.prefix}.${name}`;
            }

            return returnVar;
        },
        /**
         * retrieves the objects id safely
         * @member module:Vue/Components.EditableFormData#getObjectId
         * @method
         * @param {Object} object  object we want id from
         * @returns string | null
         */
        getObjectId(object: Object): string | null {
            return (typeof object === `object`) ? object.id : null;
        },
        /**
         * Shallow copies the form data
         * @member module:Vue/Components.EditableFormData#shallowFormData
         * @method
         * @returns Object
         */
        shallowFormData(): Object {
            return Object.assign(...Util.parse(this.editedFormData)
                .map(e => ({ [e.name]: e.value })));
        },
        /**
         * Adds a new element
         * @member module:Vue/Components.EditableFormData#addNew
         * @method
         * @param {Object} column new item we are adding
         */
        addNew(column: Object) {
            const parsedSchemaField = Util.parse(this.editedFormData).find(e => e.name === column.name);
            const shallowFormData = this.shallowFormData();
            const newItem = SchemaReader.bindSchemaToData({}, column.additionalSchemaData)
                .map(e => SchemaReader.parseDataMappings(e, parsedSchemaField, shallowFormData));

            this.getFormElement(column.name).value
                .push(newItem);
            this.$forceUpdate();
        },
        /**
         * removes a child element
         * @member module:Vue/Components.EditableFormData#remove
         * @method
         * @param {number} index index of element we are removing
         * @param {Object} column item we are removing
         */
        remove(index: number, column: Object) {
            this.getFormElement(column.name).value.splice(index, 1);
            this.$forceUpdate();
        },
        /**
         * Retrieves a specific element of the form by name
         * @member module:Vue/Components.EditableFormData#getFormElement
         * @method
         * @param {string} columnName name of form element we want
         * @returns Object
         */
        getFormElement(columnName: string): Object {
            return this.editedFormData
                .find(e => e.name === columnName);
        },
        /**
         * Is this element visible
         * @member module:Vue/Components.EditableFormData#isVisible
         * @method
         * @param {Object} column item we want to check
         */
        isVisible(column: Object): boolean {
            return !this.hideuneditable ? true : !(!column.enabled || column.name === `id`);
        },
        /**
         * Converts a singular object to plural to database requests
         * @member module:Vue/Components.EditableFormData#getEntityType
         * @method
         * @param {string} name     name to get type from
         * @return {boolean}
         */
        getEntityType(name: string): string {
            return this.entityFieldData[name].type;
        },
        /**
         * Retrieves an entities elements
         * @member module:Vue/Components.EditableFormData#getEntityElements
         * @method
         * @param {string} name     name to get data from
         * @return {Object[]}
         */
        getEntityElements(name: string): Object[] {
            return this.entityFieldData[name].elements();
        }
    }
};

export const VueEditableFormData = Vue.extend(EditableFormData);