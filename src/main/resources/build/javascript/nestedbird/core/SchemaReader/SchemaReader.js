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
import { parse, toSeconds } from "iso8601-duration";
import Optional from "optional-js";
// Site Modules
import SchemaData from "./SchemaData";
import { Util } from "nestedbird/core/Util";
import { Ajax } from "nestedbird/core/Ajax";

/**
 * This is the class that reads and parses the schema
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @memberOf module:Core/SchemaReader
 * @param {string} endpointUrl      The REST endpoint of the schema
 * @param {string} apiQuery         The URL Query that the schema will use to retrieve its information
 */
export default class SchemaReader {
    /**
     * The REST endpoint of the schema
     *
     * @member module:Core/SchemaReader.SchemaReader#endpointUrl
     * @type string
     */
    endpointUrl: string;

    /**
     * The url query for the REST endpoint
     *
     * @member module:Core/SchemaReader.SchemaReader#endpointUrl
     * @type string
     */
    apiQuery: string;

    /**
     * The promise of the currently downloaded schema to stop any race conditions
     *
     * @member module:Core/SchemaReader.SchemaReader#schemaPromise
     * @type Promise<SchemaField[]>
     */
    schemaPromise: Promise<SchemaField[]>;

    /**
     * @constructor
     * @param endpointUrl           The REST endpoint of the schema
     * @param apiQuery              The URL Query that the schema will use to retrieve its information
     */
    constructor(endpointUrl: string, apiQuery: string = ``) {
        this.endpointUrl = endpointUrl;
        this.apiQuery = apiQuery;
        this.schemaPromise = this.saveSchema();
    }

    /**
     * Retrieves the currently loaded schema data
     * It returns it inside a promise, Meaning the response is async.
     *
     * @member module:Core/SchemaReader.SchemaReader#getSchema
     * @method
     * @returns Promise<SchemaField[]>
     */
    getSchema(): Promise<SchemaField[]> {
        return this.schemaPromise;
    }

    /**
     * Retrieves the URL of the REST endpoint that retrieves a single item
     *
     * @member module:Core/SchemaReader.SchemaReader#getSingleItemUrl
     * @method
     * @param {string} id       id of the specific element you want
     * @returns {string}
     */
    getSingleItemUrl(id: string): string {
        return `${this.endpointUrl}/${id}?${this.apiQuery}`;
    }

    /**
     * Retrieves the URL of the REST endpoint that list all of the data
     *
     * @member module:Core/SchemaReader.SchemaReader#getListUrl
     * @method
     * @returns {string}
     */
    getListUrl(): string {
        return `${this.endpointUrl}?${this.apiQuery}`;
    }

    /**
     * Retrieves the URL of the schema
     *
     * @member module:Core/SchemaReader.SchemaReader#getSchemaUrl
     * @method
     * @returns {string}
     */
    getSchemaUrl(): string {
        return `${this.endpointUrl}/schema`;
    }

    /**
     * Retrieves the schema from the REST endpoint
     *
     * @member module:Core/SchemaReader.SchemaReader#retrieveSchema
     * @method
     * @returns {Promise<string>}
     */
    retrieveSchema(): Promise<string> {
        return Ajax.createPromise(this.getSchemaUrl(), `GET`);
    }

    /**
     * retrieves a schema then saves it to the object.
     *
     * @member module:Core/SchemaReader.SchemaReader#saveSchema
     * @method
     * @returns {Promise<SchemaField>}
     */
    saveSchema(): Promise<SchemaField[]> {
        return this.retrieveSchema()
            .then((response: string): SchemaField[] => Util.clean(JSON.parse(response)));
    }

    /**
     * Retrieves a list of elements then parses them
     *
     * @member module:Core/SchemaReader.SchemaReader#retrieveList
     * @method
     * @returns {Promise<Optional<SchemaData>>}
     */
    retrieveList(): Promise<Optional<SchemaData>> {
        return this.getSchema().then((schema: SchemaField[]): Promise<Optional<SchemaData>> =>
            Ajax.createPromise(this.getListUrl(), `GET`).then((response: string): Optional<SchemaData> => {
                const responseObject = JSON.parse(response);
                let schemaData: SchemaData | null = null;

                if (Object.getOwnPropertyNames(responseObject).length !== 0) {
                    const data = Util.clean(responseObject);

                    schemaData = new SchemaData(
                        data.totalPages,
                        data.totalElements,
                        SchemaReader.parseData(data.content, schema),
                        schema
                    );
                }
                return Optional.ofNullable(schemaData || null);
            })
        );
    }

    /**
     * Retrieves a single element and parses it.
     *
     * @member module:Core/SchemaReader.SchemaReader#retrieveSingle
     * @method
     * @param {string} id           id of specific element to retrieve
     * @returns {Promise<Optional<SchemaData>>}
     */
    retrieveSingle(id: string): Promise<Optional<SchemaData>> {
        return this.getSchema().then((schema: SchemaField): Promise<Optional<SchemaData>> =>
            Ajax.createPromise(this.getSingleItemUrl(id), `GET`).then((response: string): Optional<SchemaData> => {
                const responseObject = JSON.parse(response);
                let schemaData: SchemaData | null = null;

                if (Object.getOwnPropertyNames(responseObject).length !== 0) {
                    const data = Util.clean(responseObject);

                    schemaData = new SchemaData(
                        1,
                        1,
                        SchemaReader.parseData([data], schema),
                        schema
                    );
                }
                return Optional.ofNullable(schemaData || null);
            })
        );
    }

    /**
     * Parses an entities schema by saving its data to it
     *
     * First we loop over each possible array element and map the schemaData to this element.
     * Then for each mapped schemaData we set the value field
     *
     * @member module:Core/SchemaReader.SchemaReader#parseData
     * @method
     * @param {Object} elements               data elements to parse
     * @param {SchemaField} schemaData        schema to attach to data
     * @returns {ParsedSchemaField[]}
     */
    static parseData(elements: Object[], schemaData: SchemaField): ParsedSchemaField[][] {
        return elements.map((entity: Object): ParsedSchemaField[] => this.bindSchemaToData(entity, schemaData));
    }

    /**
     *
     * @member module:Core/SchemaReader.SchemaReader#bindSchemaToData
     * @method
     * @param {Object} entityData             data elements to parse
     * @param {SchemaField} schemaData        schema to attach to data
     * @returns {ParsedSchemaField[]}
     */
    static bindSchemaToData(entityData: Object, schemaData: SchemaField): ParsedSchemaField[] {
        return Util.parse(schemaData)
            .map((schemaField: SchemaField): ParsedSchemaField =>
                this.bindSchemaFieldToDataField(entityData, schemaField));
    }

    /**
     *
     * @member module:Core/SchemaReader.SchemaReader#bindSchemaFieldToDataField
     * @method
     * @param {Object} entityData               field to parse
     * @param {SchemaField} schemaField         schema to attach to data
     * @returns {ParsedSchemaField}
     */
    static bindSchemaFieldToDataField(entityData: Object, schemaField: SchemaField): ParsedSchemaField {
        const parsedSchemaField: ParsedSchemaField = (schemaField: Object);

        parsedSchemaField.value = entityData[parsedSchemaField.name];

        if (parsedSchemaField.view === `Array`) {
            parsedSchemaField.value = SchemaReader.parseData(parsedSchemaField.value,
                parsedSchemaField.additionalSchemaData)
                .map(e => this.parseArrayDataMappings(e, parsedSchemaField, entityData));
        } else if ((parsedSchemaField.view === `Period`) && (parsedSchemaField.value)) {
            parsedSchemaField.value = toSeconds(parse(parsedSchemaField.value));
        }

        return parsedSchemaField;
    }

    /**
     * Parses an array of SchmeaFields
     * Searches the parsedSchemaField for mapping references and maps the data.
     * A mapping reference is where a child element might have its value bound to that if its parent.
     * For example in a One to Many relationship, the Many are bound to the id field of the one.
     * @member module:Core/SchemaReader.SchemaReader#parseArrayDataMappings
     * @method
     * @param {SchemaField[]} schemaFields              - child schemaFields
     * @param {ParsedSchemaField} parsedSchemaField     - parent fields schema of child
     * @param {Object} entityData                       - parents data
     * @returns {ParsedSchemaField[]}
     */
    static parseArrayDataMappings(schemaFields: SchemaField[], parsedSchemaField: ParsedSchemaField, entityData: Object): ParsedSchemaField[] {
        return schemaFields.map((schemaField: SchemaField) => this.parseDataMappings(schemaField, parsedSchemaField, entityData));
    }

    /**
     * Parses a single SchemaField
     * Searches the parsedSchemaField for mapping references and maps the data.
     * A mapping reference is where a child element might have its value bound to that if its parent.
     * For example in a One to Many relationship, the Many are bound to the id field of the one.
     * @member module:Core/SchemaReader.SchemaReader#parseDataMappings
     * @method
     * @param {SchemaField} schemaField                 - child schemaFields
     * @param {ParsedSchemaField} parsedSchemaField     - parent fields schema of child
     * @param {Object} entityData                       - parents data
     * @returns {ParsedSchemaField[]}
     */
    static parseDataMappings(schemaField: SchemaField, parsedSchemaField: ParsedSchemaField, entityData: Object): ParsedSchemaField {
        parsedSchemaField.mappings.forEach((mapping: string) => {
            const parsedMapping = mapping.split(`:`);

            if (schemaField.name === parsedMapping[0]) {
                schemaField.value = entityData[parsedMapping[1]];
                schemaField.enabled = false;
            }
        });
        return schemaField;
    }

    /**
     * Creates a new SchemaReader object
     * @param parameters
     * @returns {SchemaReader}
     */
    static create(...parameters): SchemaReader {
        return new SchemaReader(...parameters);
    }
};
