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
import { Notification, Util } from "nestedbird/core/Util";

/**
 * Callback method
 * @callback AjaxCallback
 * @param {string} response
 * @memberOf module:Core/Util
 */
type AjaxCallback = (response: string) => void;

/**
 * A simple AJAX class that creates an AJAX request and has jquery-like methods for handling the response.
 * This class is wrapped in Util.ajax
 *
 * For example:
 * const ajaxRequest = new Ajax(url);
 * ajaxRequest.onSuccess(callbackFunction).onFailure(callbackFunction).onComplete(callbackFunction);
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @memberOf module:Core/Ajax
 * @param {string} [url='/']        ajax endpoint
 * @param {string} [method='get']   http request method to use, get, post, put, delete....
 * @param {Object} [data={}]        if we need to send additional data, like a post, then the data goes in here
 * @returns this
 */
export default class Ajax {

    /**
     * Update the CSRF Meta Tags in the DOM
     *
     * @member module:Core/Ajax.Ajax#setCSRFMetaTags
     * @method
     * @static
     * @param {string} header        the http request header that the CSRF tag uses
     * @param {string} value         the value of the CSRF token
     */
    static setCSRFMetaTags(header: string, value: string) {
        this.setMetaTag(`_csrf_header`, header);
        this.setMetaTag(`_csrf`, value);
    }

    /**
     * Sets a meta tag
     * @member module:Core/Ajax.Ajax#setCSRFMetaTags
     * @method
     * @static
     * @param {string} tag          the tag
     * @param {string} value        the value
     */
    static setMetaTag(tag: string, value: string) {
        Util.OptionalDOM(`meta[name='${tag}']`)
            .ifPresent(element => element.setAttribute(`content`, value));
    }

    /**
     * Converts an object hashmap to a query string
     *
     * @member module:Core/Ajax.Ajax#convertDataToString
     * @method
     * @static
     * @param {Object} data         object to convert
     * @returns string
     */
    static convertDataToString(data: Object): string {
        const stringElements = [];
        for (const elementName in data) {
            if (data.hasOwnProperty(elementName)) {
                stringElements.push(`${elementName}=${encodeURIComponent(data[elementName])}`);
            }
        }

        return stringElements.join(`&`);
    }

    /**
     * Creates a new AJAX request with the AJAX class
     * @member module:Core/Ajax.Ajax#create
     * @method
     * @static
     * @param {string} [url='/']        ajax endpoint
     * @param {string} [method='get']   http request method to use, get, post, put, delete....
     * @param {Object} [data={}]        if we need to send additional data, like a post, then the data goes in here
     * @returns Ajax
     */
    static create(url: string = `/`, method: string = `get`, data: ?Object = {}): Ajax {
        return new Ajax(url, method, data);
    }

    /**
     * Creates a new AJAX request with the AJAX class and wraps it in a Promise
     * On error it will report to the GUI the error
     * @member module:Core/Ajax.Ajax#createPromise
     * @method
     * @static
     * @param {string} [url='/']        ajax endpoint
     * @param {string} [method='get']   http request method to use, get, post, put, delete....
     * @param {Object} [data={}]        if we need to send additional data, like a post, then the data goes in here
     * @returns Promise<string>
     */
    static createPromise(url: string = `/`, method: string = `get`, data: ?Object = {}): Promise<string> {
        return new Promise((resolve, reject) => {
            this.create(url, method, data).onSuccess((response: string) => {
                resolve(response);
            }).onFailure((response: string) => {
                reject(response);
            }).outputErrors();
        });
    }

    /**
     * Creates a new AJAX request with the AJAX class and wraps it in a Promise
     * @member module:Core/Ajax.Ajax#createPromiseWithoutError
     * @method
     * @static
     * @param {string} [url='/']        ajax endpoint
     * @param {string} [method='get']   http request method to use, get, post, put, delete....
     * @param {Object} [data={}]        if we need to send additional data, like a post, then the data goes in here
     * @returns Promise<string>
     */
    static createPromiseWithoutError(url: string = `/`, method: string = `get`, data: ?Object = {}): Promise<string> {
        return new Promise((resolve, reject) => {
            this.create(url, method, data).onSuccess((response: string) => {
                resolve(response);
            }).onFailure((response: string) => {
                reject(response);
            });
        });
    }

    /**
     * An array of the success callback functions
     *
     * @member module:Core/Ajax.Ajax#_successEvents
     * @type AjaxCallback
     * @private
     */
    _successEvents: AjaxCallback[];

    /**
     * An array of the failure callback functions
     *
     * @member module:Core/Ajax.Ajax#_failureEvents
     * @type AjaxCallback
     * @private
     */
    _failureEvents: AjaxCallback[];

    /**
     * An array of the complete callback functions
     *
     * @member module:Core/Ajax.Ajax#_completeEvents
     * @type AjaxCallback
     * @private
     */
    _completeEvents: AjaxCallback[];

    /**
     * Whether or not the request has completed
     *
     * @member module:Core/Ajax.Ajax#_complete
     * @type boolean
     * @private
     */
    _complete: boolean;

    /**
     * The http status code of the request
     *
     * @member module:Core/Ajax.Ajax#_status
     * @type boolean
     * @private
     */
    _status: number;

    /**
     * The text response of the request
     *
     * @member module:Core/Ajax.Ajax#_response
     * @type string
     * @private
     */
    _response: string;

    /**
     * On construction we immediately start processing the request
     *
     * @constructor
     * @param {string} [url='/']        ajax endpoint
     * @param {string} [method='get']   http request method to use, get, post, put, delete....
     * @param {Object} [data={}]        if we need to send additional data, like a post, then the data goes in here
     * @returns this
     */
    constructor(url: string = `/`, method: string = `get`, data: ?Object = {}) {
        this._successEvents = [];
        this._failureEvents = [];
        this._completeEvents = [];
        this._complete = false;
        this._status = 200;
        this._response = ``;

        this._sendRequest(url, method, data);
    }

    /**
     * Creates the url string for the request, appends get data if applicable
     * @member module:Core/Ajax.Ajax#_createUrlString
     * @method
     * @param {string} url              ajax endpoint
     * @param {string} method           http request method
     * @param {Object} data             payload data
     * @private
     */
    _createUrlString(url: string, method: string, data: ?Object) {
        if ((method.toUpperCase() === `GET`) && data && (Object.keys(data).length)) {
            return `${url}?${Ajax.convertDataToString(data)}`;
        }

        return url;
    }

    /**
     * Sends the request to the url endpoint
     * @member module:Core/Ajax.Ajax#_sendRequest
     * @method
     * @private
     * @param {string} [url='/']        ajax endpoint
     * @param {string} [method='get']   http request method to use, get, post, put, delete....
     * @param {Object} [data={}]        if we need to send additional data, like a post, then the data goes in here
     */
    _sendRequest(url: string = `/`, method: string = `get`, data: ?Object = {}) {
        const request = this._createRequestObject(this._createUrlString(url, method, data), method);

        /**
         * If the request is POST then we also bundle the passed data too
         */
        if (method.toUpperCase() === `POST`) {
            request.setRequestHeader(`Content-Type`, `application/x-www-form-urlencoded`);

            const token = document.querySelector(`meta[name='_csrf']`).getAttribute(`content`);
            const header = document.querySelector(`meta[name='_csrf_header']`).getAttribute(`content`);

            request.setRequestHeader(header, token);

            if (data !== null) {
                request.send(Ajax.convertDataToString(data));
            } else {
                request.send();
            }
        } else {
            request.send();
        }
    }

    /**
     * Attach a callback function to be called when the request is completed successfully
     * @member module:Core/Ajax.Ajax#onSuccess
     * @method
     * @param {AjaxCallback} fn         callback function
     * @returns this
     */
    onSuccess(fn: AjaxCallback): Ajax {
        if (this.isSuccess() && this._complete) {
            fn(this._response);
        } else {
            this._successEvents.push(fn);
        }
        return this;
    }

    /**
     * Attach a callback function to be called when the request is completed unsuccessfully
     * @member module:Core/Ajax.Ajax#onFailure
     * @method
     * @param {AjaxCallback} fn         callback function
     * @returns this
     */
    onFailure(fn: AjaxCallback): Ajax {
        if (!this.isSuccess() && this._complete) {
            fn(this._response);
        } else {
            this._failureEvents.push(fn);
        }
        return this;
    }

    /**
     * Attach a callback function to be called when the request is completed, regardless of success or failure
     * @member module:Core/Ajax.Ajax#onFailure
     * @method
     * @param {AjaxCallback} fn         callback function
     * @returns this
     */
    onComplete(fn: AjaxCallback): Ajax {
        if (this._complete) {
            fn(this._response);
        } else {
            this._completeEvents.push(fn);
        }
        return this;
    }

    /**
     * Is the status code of the http response considered valid or not
     * @member module:Core/Ajax.Ajax#isSuccess
     * @method
     * @returns {boolean}
     */
    isSuccess(): boolean {
        return (this._status >= 200 && this._status < 400);
    }

    /**
     * Is the status code of the http response a client error
     * @member module:Core/Ajax.Ajax#isClientError
     * @method
     * @returns {boolean}
     */
    isClientError(): boolean {
        return (this._status >= 400 && this._status < 500);
    }

    /**
     * Is the status code of the http response a server error
     * @member module:Core/Ajax.Ajax#isServerError
     * @method
     * @returns {boolean}
     */
    isServerError(): boolean {
        return (this._status >= 500 && this._status < 600);
    }

    /**
     * Create AJAX request object and define its callbacks and properties
     *
     * @member module:Core/Ajax.Ajax#_createRequestObject
     * @method
     * @private
     * @param {string} url       url to request from
     * @param {string} method    method of sending data (get or post)
     * @returns {XMLHttpRequest}
     */
    _createRequestObject(url: string, method: string): XMLHttpRequest {
        const request = new XMLHttpRequest();
        request.open(method.toUpperCase(), url, true);

        /**
         * When the request errors
         */
        request.onerror = () => {
            this._response = request.response;
            for (const event of this._failureEvents) {
                event(this._response);
            }
            this._status = request.status || 408;
        };

        /**
         * When the request loads
         */
        request.onload = () => {
            this._status = request.status;
            this._response = request.responseText;
            if (this.isSuccess()) {
                for (const event of this._successEvents) {
                    event(this._response);
                }
            } else {
                request.onerror();
            }
            for (const event of this._completeEvents) {
                event(this._response);
            }
            this._complete = true;
        };

        return request;
    }

    /**
     * Creates an additional failure event that outputs errors
     *
     * @member module:Core/Ajax.Ajax#outputErrors
     * @method
     * @returns {Ajax}
     */
    outputErrors(): Ajax {
        this.onFailure((response: string) => {
            if (this._status === 408) {
                Notification.create(`Connection Timed Out`).error().record();
            }
            if (!response) {
                Notification.create(`Unknown Connection Error`).error().record();
            } else {
                Notification.createFromJSON(response).error().record();
            }
        });

        return this;
    }
};