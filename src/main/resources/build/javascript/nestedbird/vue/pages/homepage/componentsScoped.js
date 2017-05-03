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
import {
    VueAboutPage,
    VueAdminPage,
    VueArtistModal,
    VueCreateRecordModal,
    VueEditRecordModal,
    VueEventList,
    VueEventModal,
    VueLocationModal,
    VueLoginModal,
    VueLoginPasswordModal,
    VueMediaList,
    VueMediumModal,
    VueNewsPage,
    VueNotFoundModal,
    VuePasswordResetModal,
    VueRecordModal,
    VueRecordsPage,
    VueRegisterModal,
    VueSearchPage,
    VueSongModal,
    VueSubmitModal
} from "nestedbird/vue/components";

/**
 * This object contains the list of all the components used that are direct children of the homepage element
 */
export default {
    "notfoundmodal-component":      VueNotFoundModal,
    "locationmodal-component":      VueLocationModal,
    "eventmodal-component":         VueEventModal,
    "mediummodal-component":        VueMediumModal,
    "recordmodal-component":        VueRecordModal,
    "songmodal-component":          VueSongModal,
    "artistmodal-component":        VueArtistModal,
    "loginmodal-component":         VueLoginModal,
    "aboutpage-component":          VueAboutPage,
    "newspage-component":           VueNewsPage,
    "recordspage-component":        VueRecordsPage,
    "editrecordmodal-component":    VueEditRecordModal,
    "createrecordmodal-component":  VueCreateRecordModal,
    "adminpage-component":          VueAdminPage,
    "medialist-component":          VueMediaList,
    "eventlist-component":          VueEventList,
    "passwordresetmodal-component": VuePasswordResetModal,
    "loginpasswordmodal-component": VueLoginPasswordModal,
    "submitmodal-component":        VueSubmitModal,
    "registermodal-component":      VueRegisterModal,
    "searchpage-component":         VueSearchPage
};