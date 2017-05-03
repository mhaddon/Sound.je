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
// Node Modules

// Site Modules
// import sheetEditor from "nestedbird/core/SheetEditor";

/**
 * This file is deprecated
 * Will either phase out or rework
 * @deprecated
 */
class TriggerManager {
    triggers: Object[];

    constructor() {
        this.triggers = [];
    }

    addTrigger(trigger: Object) {
        this.triggers.push(trigger);
    }

    trigger(eventname: String) {
        this.triggers
            .filter(trigger => trigger.events.indexOf(eventname) > -1)
            .forEach((trigger) => {
                trigger.action();
            });
    }
}

export default new TriggerManager();


// const vueController = sheetEditor.addRule(`.ie--respmainlayout .vueController`);
// const layoutMainDesktop = sheetEditor.addRule(`.ie--respmainlayout .l-main--desktop`);
// triggerManager.addTrigger({
//     events: [`onLoadDelay`, `onResize`],
//     action: () => {
//         if (document.body.hasClass(`ie`) && parseInt(document.body.getAttribute(`ie`), 10) <= 11) {
//
//             layoutMainDesktop.ifPresent((rule: CssRule) => {
//                 rule.style.position = `initial`;
//             });
//
//             const header = document.querySelector(`.vueController > .header`);
//             const layoutMain = document.querySelector(`.l-main`);
//             let height = header.outerSize().height;
//             for (const node of layoutMain.childNodes) {
//                 if (node.childNodes.length) {
//                     height += node.outerSize().height;
//                 }
//             }
//
//             if (height < window.innerHeight) {
//                 document.body.addClass(`ie--respmainlayout`);
//             } else {
//                 document.body.removeClass(`ie--respmainlayout`);
//             }
//
//             const musicPlayer = document.querySelector(`.musicplayer`);
//
//             vueController.ifPresent((rule: CssRule) => {
//                 rule.style.position = `relative`;
//             });
//
//             Object.assign(layoutMainDesktop.style, {
//                 position: `absolute`,
//                 top: `${header.offsetHeight + 20}px`,
//                 left: 0,
//                 right: 0,
//                 bottom: layoutMain.hasClass(`l-main--musicplayer`) ? `${musicPlayer.offsetHeight}px` : 0
//             });
//         }
//     }
// });