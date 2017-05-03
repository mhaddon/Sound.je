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
 * This code enables and configures Facebooks API, this is used to load the various facebook videos on the page
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @member module:Core/GlobalJS.Facebook_API
 */
const Facebook_API = {};

(function (d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id))
        return;
    js = d.createElement(s);
    js.id = id;
    js.src = `https://connect.facebook.net/en_GB/all.js#xfbml=1&amp;version=v2.3`;
    fjs.parentNode.insertBefore(js, fjs);
}(document, `script`, `facebook-jssdk`));

window.fbAsyncInit = () => {
    FB.init({
        appId:   `875488569232346`,
        xfbml:   true,
        version: `v2.5`
    });
};