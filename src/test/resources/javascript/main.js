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
// Node Modules
require(`jsdom-global`)();
require(`babel-polyfill`);
const { should, expect, assert } = require(`chai`);
const XMLHttpRequest = require(`xmlhttprequest`).XMLHttpRequest;

// Node Globals
global.__base = `${__dirname}/../../../main/resources/build/javascript/`; // this feels so hacky


global.XMLHttpRequest = XMLHttpRequest;
global.should = should;
global.expect = expect;
global.assert = assert;

// Site Modules


// Tests
require(`./core/Ajax`);
require(`./core/Util`);
require(`./core/KeyController`);
require(`./music/Playlist`);
