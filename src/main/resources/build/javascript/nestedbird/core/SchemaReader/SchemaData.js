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

/**
 * This class holds the schema information and parsed data of an entity
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @memberOf module:Core/SchemaReader
 * @param {number} pageCount                    The amount of pages of data there is
 * @param {number} totalElements                The amount of total elements there is
 * @param {ParsedSchemaField[]} elements        An array of processed schema elements
 * @param {SchemaField} schema                  A record of the schema before it was parsed
 */
export default class SchemaData {
    /**
     * An array of processed schema elements
     *
     * @member module:Core/SchemaReader.schemaData#elements
     * @type ParsedSchemaField[]
     */
    elements: ParsedSchemaField[];

    /**
     * The amount of pages of data there is
     *
     * @member module:Core/SchemaReader.schemaData#pageCount
     * @type number
     */
    pageCount: number;

    /**
     * The amount of total elements there is
     *
     * @member module:Core/SchemaReader.schemaData#totalElements
     * @type number
     */
    totalElements: number;

    /**
     * A record of the schema before it was parsed
     *
     * @member module:Core/SchemaReader.schemaData#schema
     * @type SchemaField
     */
    schema: SchemaField;

    /**
     * @constructor
     * @param {number} pageCount                    The amount of pages of data there is
     * @param {number} totalElements                The amount of total elements there is
     * @param {ParsedSchemaField[]} elements        An array of processed schema elements
     * @param {SchemaField} schema                  A record of the schema before it was parsed
     */
    constructor(pageCount: number, totalElements: number, elements: ParsedSchemaField[], schema: SchemaField) {
        this.pageCount = pageCount;
        this.totalElements = totalElements;
        this.elements = elements;
        this.schema = schema;
    }
};
