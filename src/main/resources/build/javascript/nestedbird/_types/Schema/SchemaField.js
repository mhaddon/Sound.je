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

/**
 * This type holds the information for a SchemaField
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @static
 * @memberOf module:Types
 */
class SchemaField {
    /**
     * The Java datatype of the field
     * @member module:Types.SchemaField#type
     * @type string
     */
    type: string;

    /**
     * The name of the field
     * @member module:Types.SchemaField#name
     * @type string
     */
    name: string;

    /**
     * The category that will define how the data will be rendered
     * @member module:Types.SchemaField#view
     * @type string
     */
    view: string;

    /**
     * An object that lists all the validations that need to be checked to see if the data is valid
     * @member module:Types.SchemaField#validations
     * @type Object
     */
    validations: Object;

    /**
     * Whether or not the data is editable
     * @member module:Types.SchemaField#enabled
     * @type boolean
     */
    enabled: boolean;

    constructor() {
    }
}