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
 * This file contains the various gulp tasks for temporary servers
 * View the specific task for more information
 * Tasks:
 * └── Single Purpose Tasks
 *     └── server:browsersync
 */

const gulp = require(`gulp`);
const browserSync = require(`browser-sync`).create();
const reload = browserSync.reload;

// ==========================================================================
// Single Purpose Tasks
// ==========================================================================

/**
 * Starts a browser sync server to watch for changes
 */
gulp.task(`server:browsersync`, () => {
    browserSync.init({
        proxy: `localhost:8081`
    });
    gulp.watch([`${jsBuildDirectory}${jsDistFile}`, `${scssBuildDirectory}${scssDistFile}`]).on(`change`, reload);
});
