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
import uuid from "uuid";
// Site Modules
import store from "nestedbird/vue/store";
import Util from "./Util";

/**
 * Simple object for recording errors
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @memberOf module:Core/Util
 */
export default class Notification {
    /**
     * Creates a new Notification
     * @member module:Core/Util.Notification#create
     * @method
     * @param {string} message                  Message
     * @param {number} status []                http status message
     * @param {string} errorMessage []          Error message
     * @returns {module:Core/Util.Notification}
     */
    static create(message: string, status: ?number, errorMessage: ?string): Notification {
        return new Notification(message, status, errorMessage);
    }

    /**
     * Creates a new Notification from an error JSON response
     * @member module:Core/Util.Notification#createFromJSON
     * @method
     * @param {string} json     JSON response
     * @returns {module:Core/Util.Notification}
     */
    static createFromJSON(json: string): Notification {
        let error = new Notification(``);

        Util.tryParseJSON(json)
            .ifPresent((object: Object) => {
                error = new Notification(object.message, object.status, object.error, object.path, object.timestamp);
            });

        return error;
    }

    /**
     * Removes all notifications
     * @member module:Core/Util.Notification#clear
     * @method
     */
    static clear() {
        store.commit(`clearNotifications`);
    }

    /**
     * The unique ID of this error. This is so vue knows which element its deleting.
     * @member module:Core/Util.Notification#id
     * @type string
     */
    id: string;

    /**
     * HTTP status code
     * @member module:Core/Util.Notification#id
     * @type number
     */
    status: ?number;

    /**
     * Error information
     * @member module:Core/Util.Notification#errorMessage
     * @type string
     */
    errorMessage: ?string;

    /**
     * Message
     * @member module:Core/Util.Notification#message
     * @type string
     */
    message: string;

    /**
     * Error path
     * @member module:Core/Util.Notification#path
     * @type string
     */
    path: ?string;

    /**
     * server recorded timestamp of the error
     * @member module:Core/Util.Notification#timestamp
     * @type number
     */
    timestamp: ?number;

    /**
     * client recorded timestamp of the error
     * @member module:Core/Util.Notification#recordedTime
     * @type number
     */
    recordedTime: ?number;

    /**
     * Is this an error?
     */
    isError: ?boolean;

    /**
     * @constructor
     * @param {string} message
     * @param {number} status
     * @param {string} errorMessage
     * @param {string} path
     * @param {number} timestamp
     */
    constructor(message: string, status: ?number, errorMessage: ?string, path: ?string, timestamp: ?number) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.recordedTime = (new Date()).getTime();
        this.id = uuid();
        this.isError = false;
    }

    /**
     * Sets the notification to be an error
     * @member module:Core/Util.Notification#error
     * @method
     * @returns {module:Core/Util.Notification}
     */
    error() {
        this.isError = true;
        return this;
    }

    /**
     * Records the error
     * @member module:Core/Util.Notification#record
     * @method
     */
    record() {
        store.commit(`addNotification`, this);
    }

    /**
     * Removes the error
     * @member module:Core/Util.Notification#remove
     * @method
     */
    remove() {
        store.commit(`removeNotification`, this);
    }
};
