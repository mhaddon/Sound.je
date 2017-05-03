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
// Node modules
// Site Modules
import { Ajax } from "nestedbird/core/Ajax";

/**
 * A Vue mixin that gives defaults to meta tags
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Mixins
 */
export const Tag = {
    created() {
        Ajax.setMetaTag(`og:description`, `Sound of Jersey is a website designed to help support musicians in the Channel Islands.`);
        Ajax.setMetaTag(`description`, `Sound of Jersey is a website designed to help support musicians in the Channel Islands.`);
        Ajax.setMetaTag(`og:title`, `Support Jersey Music`);
    }
};