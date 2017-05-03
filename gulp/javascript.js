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
 * This file contains the various gulp tasks related to Javascript.
 * View the specific task for more information
 * Tasks:
 * ├── Single Purpose Tasks
 * |   ├── scripts
 * |   ├── scripts:light
 * |   ├── scripts:analyse
 * |   ├── scripts:lint
 * |   ├── scripts:test
 * |   └── scripts:doc
 * ├── Build Tasks
 * |   ├── scripts:build:light
 * |   └── scripts:build
 * └── Watch Tasks
 *     └── scripts:watch
 */

// gulp modules
const gulp = require(`gulp`);
const addSrc = require(`gulp-add-src`);
const uglify = require(`gulp-uglify`);
const rename = require(`gulp-rename`);
const webpack = require(`webpack-stream`);
const notify = require(`gulp-notify`);
const mocha = require(`gulp-mocha`);
const jsdoc = require(`gulp-jsdoc`);
const babel = require(`gulp-babel`);
const path = require(`path`);
const stripDebug = require(`gulp-strip-debug`);
const BundleAnalyzerPlugin = require(`webpack-bundle-analyzer`).BundleAnalyzerPlugin;
const gulpsync = require(`gulp-sync`)(gulp);

// ==========================================================================
// Single Purpose Tasks
// ==========================================================================

/**
 * This is the standard build task for the javascript
 * First it transpiles the ES6 code to ES5 with babel, stripping flowjs,
 * then it joins the modules together with webpack.
 * It creates both an uglified/minified and a normal version.
 * The normal version contains sourcemap information
 */
gulp.task(`scripts`, () => gulp
    .src(`${jsSourceDirectory}/${jsSourceFile}`)
    .pipe(webpack({
        devtool: `module-inline-source-map`,
        module:  {
            noParse: [
                /node_modules\/flexibility\/flexibility.js/
            ],
            loaders: [{
                test:    /.jsx?$/,
                loader:  `babel-loader`,
                exclude: /node_modules/
            }]
        },
        resolve: {
            root: [
                path.resolve(jsSourceDirectory)
            ]
        }
    }))
    .pipe(rename(jsDistFile))
    .pipe(gulp.dest(`${jsDistDirectory}/`))
    .pipe(gulp.dest(`${jsBuildDirectory}`))
    .pipe(notify({ message: `JS Transpiled` }))
    .pipe(rename({ suffix: `.min` }))
    .pipe(stripDebug())
    .pipe(uglify())
    .pipe(gulp.dest(`${jsDistDirectory}/`))
    .pipe(gulp.dest(`${jsBuildDirectory}`))
    .pipe(notify({ message: `JS Minified` }))
);

/**
 * This is a lighter version of the standard build task.
 * It does not create the uglified/minified version
 */
gulp.task(`scripts:light`, () => gulp
    .src(`${jsSourceDirectory}/${jsSourceFile}`)
    .pipe(webpack({
        devtool: `module-inline-source-map`,
        module:  {
            noParse: [
                /node_modules\/flexibility\/flexibility.js/
            ],
            loaders: [{
                test:    /.jsx?$/,
                loader:  `babel-loader`,
                exclude: /node_modules/
            }]
        },
        resolve: {
            root: [
                path.resolve(jsSourceDirectory)
            ]
        }
    }))
    .pipe(rename(jsDistFile))
    .pipe(gulp.dest(`${jsDistDirectory}/`))
    .pipe(gulp.dest(`${jsBuildDirectory}`))
    .pipe(notify({ message: `JS Transpiled` }))
);

/**
 * This task scans the normal output of the scripts task and analyses the file sizes of every contained module
 */
gulp.task(`scripts:analyse`, () => gulp
    .src(`${jsSourceDirectory}/${jsSourceFile}`)
    .pipe(webpack({
        devtool: `module-inline-source-map`,
        module:  {
            noParse: [
                /node_modules\/flexibility\/flexibility.js/
            ],
            loaders: [{
                test:    /.jsx?$/,
                loader:  `babel-loader`,
                exclude: /node_modules/
            }]
        },
        resolve: {
            root: [
                path.resolve(jsSourceDirectory)
            ]
        },
        plugins: [new BundleAnalyzerPlugin()]
    }))
);

/**
 * This task runs a linter against our source code checking for common linting errors
 */
gulp.task(`scripts:lint`, () => gulp
    .src(`${jsSourceDirectory}/**/*.js`)
    .pipe(webpack({
        module: {
            preLoaders: [
                {
                    test:    /\.js$/,
                    exclude: /node_modules/,
                    loader:  `eslint-loader`
                }
            ],
            loaders:    [{
                test:    /.jsx?$/,
                loader:  `babel-loader`,
                exclude: /node_modules/,
                query:   {
                    presets: [`latest`, `es2017`]
                }
            }]
        }
    }))
    .pipe(notify({ message: `JS Linted` }))
);

/**
 * This task runs tests against the source code
 */
gulp.task(`scripts:test`, () => gulp
    .src(`${jsTestDirectory}/${jsTestFile}`, { read: false })
    .pipe(mocha({
        reporter:  `spec`,
        compilers: {
            js: require(`babel-core/register`)
        }
    }))
    .pipe(notify({ message: `JS Tested` }))
);

/**
 * This task produces the jsdoc
 */
gulp.task(`scripts:doc`, () => gulp
    .src([`${jsSourceDirectory}/**/*.js`])
    .pipe(babel())
    .pipe(addSrc(`README.md`))
    .pipe(addSrc(`Credits.md`))
    .pipe(jsdoc.parser())
    .pipe(jsdoc.generator(`./docs/js`))
);

// ==========================================================================
// Build Tasks
// ==========================================================================

/**
 * Checks for errors with the tests and linter then it builds without minified version
 */
gulp.task(`scripts:build:light`, gulpsync.sync([`scripts:test`, `scripts:lint`, `scripts:light`]));

/**
 * Checks for errors with the tests and linter then it builds
 */
gulp.task(`scripts:build`, gulpsync.sync([`scripts:test`, `scripts:lint`, `scripts`]));

// ==========================================================================
// Watch Tasks
// ==========================================================================

/**
 * Watches the source code for changes and automatically builds the light version
 */
gulp.task(`scripts:watch`, () => {
    gulp.watch(`${jsSourceDirectory}/**/*.js`, [`scripts:build:light`]);
});