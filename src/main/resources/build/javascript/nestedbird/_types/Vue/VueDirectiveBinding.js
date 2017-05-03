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

/**
 * https://vuejs.org/v2/guide/custom-directive.html
 */
declare class VueDirectiveBinding {
    /**
     * The name of the directive, without the v- prefix.
     */
        name: string;

    /**
     * The value passed to the directive.
     * For example in v-my-directive="1 + 1", the value would be 2.
     */
        value: mixed;

    /**
     * The expression of the binding as a string.
     * For example in v-my-directive="1 + 1", the expression would be "1 + 1".
     */
        expression: string;

    /**
     * The argument passed to the directive, if any.
     * For example in v-my-directive:foo, the arg would be "foo".
     */
        arg: string;

    /**
     * An object containing modifiers, if any.
     * For example in v-my-directive.foo.bar, the modifiers object would be { foo: true, bar: true }.
     */
        modifiers: Object;

    /**
     * Reference the bindings scope
     */
        def: Object;
}
