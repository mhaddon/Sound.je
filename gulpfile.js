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

/**
 * This file configures and brings together all the various gulp modules.
 * For a list of possible gulp commands look at the modules inside ./gulp
 */

// ==========================================================================
// VARIABLES
// ==========================================================================

// JS
global.jsSourceDirectory = `src/main/resources/build/javascript/`;
global.jsSourceFile = `nestedbird/main.js`;
global.jsDistDirectory = `src/main/resources/static/`;
global.jsDistFile = `main.js`;
global.jsBuildDirectory = `target/classes/static/`;
global.jsTestDirectory = `src/test/resources/javascript/`;
global.jsTestFile = `main.js`;

// SCSS
global.scssSourceDirectory = `src/main/resources/build/stylesheet/`;
global.scssSourceFile = `core.scss`;
global.scssDistDirectory = jsDistDirectory;
global.scssDistFile = `core.css`;
global.scssBuildDirectory = jsBuildDirectory;


// ==========================================================================
// REQUIRE GULP MODULES
// ==========================================================================

require(`require-dir`)(`./gulp`, { recurse: true });