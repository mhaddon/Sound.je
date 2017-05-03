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
import { Modal, Page } from "./Enum";

/**
 * These routes are read to determine which content the user sees depending on the url they loaded
 */
export default {
    [`/search/:query`]:        {
        state: {
            currentModal: Modal.NONE,
            currentPage:  Page.SEARCH,
            title:        `Sound of Jersey | Search`
        }
    },
    [`/submit/:type`]:         {
        state: {
            currentModal:         Modal.SUBMIT,
            requireAuthorisation: `PRIV_MODERATOR`,
            title:                `Sound of Jersey | Submit`
        }
    },
    [`/login/fast`]:           {
        state: {
            currentModal:         Modal.LOGINPASSWORD,
            requireAuthorisation: false,
            title:                `Sound of Jersey | Login`
        }
    },
    [`/login/register`]:       {
        state: {
            currentModal:         Modal.REGISTER,
            requireAuthorisation: false,
            title:                `Sound of Jersey | Register`
        }
    },
    [`/login/reset`]:          {
        state: {
            currentModal:         Modal.PASSWORDRESET,
            requireAuthorisation: false,
            title:                `Sound of Jersey | Password Reset`
        }
    },
    [`/VerifyEvents`]:         {
        state: {
            currentModal:         Modal.NONE,
            currentPage:          Page.VERIFYEVENTS,
            requireAuthorisation: `PRIV_MODERATOR`,
            title:                `Sound of Jersey | VerifyEvents`
        }
    },
    [`/Records/:name/create`]: {
        state: {
            currentModal:         Modal.CREATERECORD,
            requireAuthorisation: `PRIV_MODERATOR`,
            title:                `Sound of Jersey | Create Record | $1`
        }
    },
    [`/Records/:id/:name`]:    {
        state: {
            currentModal:         Modal.EDITRECORD,
            requireAuthorisation: `PRIV_MODERATOR`,
            title:                `Sound of Jersey | Edit Record | $2`
        }
    },
    [`/Records/:name`]:        {
        state: {
            currentModal:         Modal.RECORD,
            requireAuthorisation: `PRIV_MODERATOR`,
            title:                `Sound of Jersey | Record | $1`
        }
    },
    [`/Records`]:              {
        state: {
            currentModal:         Modal.NONE,
            currentPage:          Page.RECORDS,
            requireAuthorisation: `PRIV_MODERATOR`,
            title:                `Sound of Jersey | Records`
        }
    },
    [`/Admin`]:                {
        state: {
            currentModal:         Modal.NONE,
            currentPage:          Page.ADMIN,
            requireAuthorisation: `PRIV_MODERATOR`,
            title:                `Sound of Jersey | Admin`
        }
    },
    [`/Events`]:               {
        state: {
            currentModal:         Modal.NONE,
            currentPage:          Page.EVENTS,
            requireAuthorisation: false,
            title:                `Sound of Jersey | Events`
        }
    },
    [`/News`]:                 {
        state: {
            currentModal:         Modal.NONE,
            currentPage:          Page.NEWS,
            requireAuthorisation: false,
            title:                `Sound of Jersey | News`
        }
    },
    [`/About`]:                {
        state: {
            currentModal:         Modal.NONE,
            currentPage:          Page.ABOUT,
            requireAuthorisation: false,
            title:                `Sound of Jersey | About`
        }
    },
    [`/Media`]:                {
        state: {
            currentModal:         Modal.NONE,
            currentPage:          Page.MUSIC,
            requireAuthorisation: false,
            title:                `Sound of Jersey | Media`
        }
    },
    [`/login`]:                {
        state: {
            currentModal:         Modal.LOGIN,
            requireAuthorisation: false,
            title:                `Sound of Jersey | Login`
        }
    },
    [`/Locations/:id/:name?`]: {
        state: {
            currentModal:         Modal.LOCATION,
            requireAuthorisation: false,
            title:                `Sound of Jersey | Location | $2`
        }
    },
    [`/Events/:id/:name?`]:    {
        state: {
            currentModal:         Modal.EVENT,
            requireAuthorisation: false,
            title:                `Sound of Jersey | Event | $2`
        }
    },
    [`/Artists/:id/:name?`]:   {
        state: {
            currentModal:         Modal.ARTIST,
            requireAuthorisation: false,
            title:                `Sound of Jersey | Artist | $2`
        }
    },
    [`/Song/:id/:name?`]:      {
        state: {
            currentModal:         Modal.SONG,
            requireAuthorisation: false,
            title:                `Sound of Jersey | Song | $2`
        }
    },
    [`/Medium/:id/:name?`]:    {
        state: {
            currentModal:         Modal.MEDIUM,
            requireAuthorisation: false,
            title:                `Sound of Jersey | Medium | $2`
        }
    },
    [`/`]:                     {
        state: {
            currentModal:         Modal.NONE,
            currentPage:          Page.EVENTS,
            requireAuthorisation: false,
            title:                `Sound of Jersey`
        }
    },
    [`*`]:                     {
        state: {
            currentModal:         Modal.NOTFOUND,
            requireAuthorisation: false,
            title:                `Sound of Jersey | 404`
        }
    }
};
