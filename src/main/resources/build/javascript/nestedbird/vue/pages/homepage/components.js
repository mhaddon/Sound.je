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
import Vue from "vue/dist/vue";
import VueTouch from "vue-touch";
import VueMultiSelect from "vue-multiselect";
import VueObserveVisibility from "vue-observe-visibility";
// Site Modules
import {
    VueDateTimePicker,
    VueEditableFormData,
    VueEntityField,
    VueMarkdownTextarea,
    VueMediaBox,
    VueMultiEntityField,
    VueMusicPlayer,
    VueNotificationMessage,
    VuePeriodField,
    VueSmartTable,
    VueTextarea
} from "nestedbird/vue/components";

import "nestedbird/core/KeyController";
import "nestedbird/vue/directives/href";
import "nestedbird/vue/directives/src";
import "nestedbird/vue/directives/infinite";
import "nestedbird/vue/directives/form";

/**
 * Turns off Hammers (VueTouchs) feature which disables selecting of text
 */
delete Hammer.defaults.cssProps.userSelect;

Vue.use(VueTouch);
Vue.use(VueObserveVisibility);
// Vue.use(VueMultiSelect);
// Vue.use(VueFlatpickr);

VueTouch.config.swipe = {
    direction: `horizontal`,
    threshold: 50
};

/**
 * Register components globally
 */
Vue.component(`textarea-component`, VueTextarea);
Vue.component(`markdowntextarea-component`, VueMarkdownTextarea);
Vue.component(`entityfield-component`, VueEntityField);
Vue.component(`multientityfield-component`, VueMultiEntityField);
Vue.component(`periodfield-component`, VuePeriodField);
Vue.component(`editableformdata-component`, VueEditableFormData);
Vue.component(`mediabox-component`, VueMediaBox);
Vue.component(`musicplayer-component`, VueMusicPlayer);
Vue.component(`smarttable-component`, VueSmartTable);
Vue.component(`datetimepicker-component`, VueDateTimePicker);
Vue.component(`notificationmessage-component`, VueNotificationMessage);
Vue.component(`multiselect`, VueMultiSelect);
