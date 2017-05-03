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
 * This file contains the various gulp tasks related to CSS/SASS.
 * View the specific task for more information
 * Tasks:
 * ├── Single Purpose Tasks
 * |   └── sass
 * └── Watch Tasks
 *     └── sass:watch
 */

const gulp = require(`gulp`);
const sass = require(`gulp-sass`);
const postcss = require(`gulp-postcss`);
const rename = require(`gulp-rename`);

// ==========================================================================
// Single Purpose Tasks
// ==========================================================================

/**
 * This is the standard build task for the stylesheets
 * First it transpiles the sass to css
 * then it applies postcss postprocessors to the css (such as auto prefixer)
 */
gulp.task(`sass`, () => {
    const postprocessors = [
        require(`autoprefixer`),
        require(`css-mqpacker`),
        require(`cssnano`)
    ];

    return gulp.src(`${scssSourceDirectory}/${scssSourceFile}`)
        .pipe(sass().on(`error`, sass.logError))
        .pipe(postcss(postprocessors))
        .pipe(rename(scssDistFile))
        .pipe(gulp.dest(scssDistDirectory))
        .pipe(gulp.dest(scssBuildDirectory));
});

// ==========================================================================
// Watch Tasks
// ==========================================================================

/**
 * Watches the source code for changes and automatically builds the css
 */
gulp.task(`sass:watch`, () => {
    gulp.watch(`${scssSourceDirectory}/**/*.scss`, [`sass`]);
});

