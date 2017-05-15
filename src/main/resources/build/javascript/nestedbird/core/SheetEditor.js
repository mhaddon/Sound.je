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
import Optional from "optional-js";

// Site Modules

/**
 * This file is deprecated,
 * will either phase out or rework
 * @deprecated
 */
class SheetEditor {
    /**
     * The index of this stylesheet
     */
    index: number;

    constructor(index: number) {
        this.index = index;
    }

    getSheet(): CSSStyleSheet {
        return document.styleSheets[this.index];
    }

    /**
     * Add a new css rule to the style sheet
     * http://stackoverflow.com/a/16507264
     * @param selector
     * @param styles
     */
    addRule(selector: string, styles: string = ``): Optional<CSSRule> {
        const sheet: CSSStyleSheet = this.getSheet();
        let id: ?number = null;
        if (sheet.insertRule) {
            id = sheet.insertRule(`${selector}{${styles}}`, sheet.cssRules.length);
        } else if (sheet.addRule) {
            id = sheet.addRule(selector, styles);
        }

        return this.getRule(id);
    }

    getRule(id: ?number): Optional<CSSRule> {
        return Optional.ofNullable(document.styleSheets[this.index].cssRules[id] || null);
    }
}

export default new SheetEditor(1);

// const sheetEditor = new SheetEditor(1);
// module.exports = new SheetEditor(1);

// const ruleId = sheetEditor.addRule("body:before", `background-position-y:${1}px`);
// const rule = sheetEditor.getRule(ruleId);
//
// rule.style.backgroundPositionY = "1px";