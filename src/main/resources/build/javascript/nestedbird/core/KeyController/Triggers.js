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
import { MusicPlayer } from "nestedbird/core/musicplayer";

import KeyCode from "./KeyCode";
import KeyController from "./KeyController";

/**
 * This is a configuration file that saves all the important trigger events to the KeyController
 * If you want to add a new Key Event add it here
 * @see module:Core/KeyController.KeyController
 * @copyright Copyright (C) 2017  Michael Haddon
 * @author Michael Haddon
 * @since 23 Feb 2017
 * @class
 * @member module:Core/KeyController.triggers
 */
export default {
    // ==============================================================
    // SONG STATE CONTROL
    // ==============================================================
    // When the space key is pressed and no input field is selected we play/pause the song
    [KeyCode.SPACE]:       (e: KeyboardEvent) => {
        if (MusicPlayer.isPaused()) {
            MusicPlayer.play();
        } else {
            MusicPlayer.pause();
        }
        e.preventDefault();
    },
    // ==============================================================
    // SONG NAVIGATION CONTROL
    // ==============================================================
    // When the arrow keys are pressed we go to the next or previous song
    [KeyCode.RIGHT_ARROW]: (e: KeyboardEvent) => {
        if (!KeyController.isDown(KeyCode.SHIFT)) {
            MusicPlayer.next();
            e.preventDefault();
        }
    },
    [KeyCode.LEFT_ARROW]:  (e: KeyboardEvent) => {
        if (!KeyController.isDown(KeyCode.SHIFT)) {
            MusicPlayer.previous();
            e.preventDefault();
        }
    },
    // ==============================================================
    // SONG VOLUME CONTROL
    // ==============================================================
    // When the Add or Minus keys are pressed we change the volume
    [KeyCode.ADD]:         (e: KeyboardEvent) => {
        MusicPlayer.setVolume(MusicPlayer.getVolume() + 0.1);
        e.preventDefault();
    },
    [KeyCode.SUBTRACT]:    (e: KeyboardEvent) => {
        MusicPlayer.setVolume(MusicPlayer.getVolume() - 0.1);
        e.preventDefault();
    }
};