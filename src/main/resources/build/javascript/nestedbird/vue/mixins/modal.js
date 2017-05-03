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
import { Util } from "nestedbird/core/Util";

/**
 * A Vue mixin giving default events for modals
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @namespace
 * @readonly
 * @memberOf module:Vue/Mixins
 */
export const Modal = {
    /**
     * When a modal is created we need to disable page scrolling
     * @member module:Vue/Mixins.Modal#mounted
     * @method
     */
    mounted() {
        Util.OptionalDOM(`body`).ifPresent(body => body.addClass(`modalopen`));
    },
    /**
     * When a modal is destroyed we need to reenable page scrolling
     * @member module:Vue/Mixins.Modal#beforeDestroy
     * @method
     */
    beforeDestroy() {
        Util.OptionalDOM(`body`).ifPresent(body => body.removeClass(`modalopen`));
    }
};