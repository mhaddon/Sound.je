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
/* eslint-disable */
// Node Modules

// Site Modules

/**
 * This code enables and configures Google Analytics
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @member module:Core/GlobalJS.Google_Analytics
 */
const Google_Analytics = {};

(function (i, s, o, g, r, a, m) {
    i[`GoogleAnalyticsObject`] = r;
    i[r] = i[r] || function () {
            (i[r].q = i[r].q || []).push(arguments);
        }, i[r].l = 1 * new Date();
    a = s.createElement(o),
        m = s.getElementsByTagName(o)[0];
    a.async = 1;
    a.src = g;
    m.parentNode.insertBefore(a, m);
})(window, document, `script`, `//www.google-analytics.com/analytics.js`, `ga`);

window.ga(`create`, `UA-94003577-1`, `auto`, {
    name: `SoundJe`
});

window.ga(`SoundJe.send`, `pageview`, document.location.pathname);