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
const Playlist = require(__base + `core/musicplayer/Playlist`);
const PlaylistController = require(__base + `core/musicplayer/PlaylistController`);
const Util = require(__base + `core/Util`);

describe(`PlaylistController`, function () {
    describe(`#add`, function () {
        it(`can add playlist item`, function () {
            const id = 1;
            playlist.add(id);
            const length = (playlist.playlists.length === 1);
            expect(length).to.be.true;
        });
        it(`should fail to add same playlist again`, function () {
            const id = 1;
            playlist.add(id);
            const length = (playlist.playlists.length === 1);
            expect(length).to.be.true;
        });
    });
    describe(`#get`, function () {
        it(`should be able to retrieve previously added playlist item`, function () {
            const id = 1;
            const response = playlist.get(id);
            expect(response.name).to.equal(id);
        });
        it(`should be able to create and get playlist item`, function () {
            const id = 2;
            const response = playlist.get(id);
            expect(response.name).to.equal(id);
        });
        it(`should only have two items so far stored`, function () {
            const id = 2;
            const response = playlist.playlists.length;
            expect(response).to.equal(2);
        });
    });
    describe(`#addAndGet`, function () {
        it(`should be able to create and get playlist item`, function () {
            const id = 3;
            const response = playlist.addAndGet(id);
            expect(response.name).to.equal(id);
        });
    });
    describe(`#getLastElement`, function () {
        it(`should be able to retrieve the array index of the last added item`, function () {
            const response = playlist.getLastElement();
            expect(response.name).to.equal(3);
        });
    });
    describe(`#doesPlaylistExist`, function () {
        it(`should find our playlists`, function () {
            const response = playlist.doesPlaylistExist(1);
            expect(response).to.be.true;
        });
        it(`should find our playlists`, function () {
            const response = playlist.doesPlaylistExist(100);
            expect(response).to.be.false;
        });
    });
});


const exampleItem = {
    name: `test`,
    loaded: false
};

describe(`Playlist`, function () {
    describe(`#add`, function () {
        it(`can add item to playlist`, function () {
            playlist.get(1).add(exampleItem);

            const length = (playlist.get(1).elements.length === 1);
            expect(length).to.be.true;
        });
        it(`should fail to add same item again`, function () {
            playlist.get(1).add(exampleItem);

            const length = (playlist.get(1).elements.length === 1);
            expect(length).to.be.true;
        });
        it(`should succeed on object of different memory address`, function () {
            playlist.get(1).add(Util.parse(exampleItem));

            const length = (playlist.get(1).elements.length === 2);
            expect(length).to.be.true;
        });
    });
    describe(`#doesElementExist`, function () {
        it(`should find out if element exists`, function () {
            const response = playlist.get(1).doesElementExist(exampleItem);

            expect(response).to.be.true;
        });
        it(`should not find out if element exists if it has different memory address`, function () {
            const response = playlist.get(1).doesElementExist(Util.parse(exampleItem));

            expect(response).to.be.false;
        });
    });
    describe(`#remove`, function () {
        it(`should be able to remove an object`, function () {
            playlist.get(1).remove(exampleItem);

            const length = (playlist.get(1).elements.length === 1);
            expect(length).to.be.true;
        });
    });
    describe(`#setIdToPlayingElement`, function () {
        it(`should be able to find out which element is playing`, function () {
            playlist.get(1).add({
                name: `test1`,
                loaded: false
            });
            playlist.get(1).add({
                name: `test2`,
                loaded: false
            });
            playlist.get(1).add({
                name: `test3`,
                loaded: true
            });
            playlist.get(1).add({
                name: `test4`,
                loaded: false
            });
            playlist.get(1).add({
                name: `test5`,
                loaded: false
            });

            playlist.get(1).setIdToPlayingElement();

            const id = playlist.get(1).id;
            expect(id).to.equal(3);
        });
        it(`if no element is playing, then the first one is`, function () {
            playlist.get(1).get(3).loaded = false;

            playlist.get(1).setIdToPlayingElement();

            const id = playlist.get(1).id;
            expect(id).to.equal(0);
        });
    });
    describe(`#get`, function () {
        it(`should be able to retrieve an item`, function () {
            expect(playlist.get(1).get(3).name).to.equal(`test3`);
        });
        it(`should fail to retrieve an unknown item`, function () {
            expect(playlist.get(1).get(3000)).to.be.undefined;
        });
    });
    describe(`#getPlayingElement`, function () {
        it(`should be able to retrieve the currently playing element`, function () {
            playlist.get(1).get(3).loaded = true;
            const response = playlist.get(1).getPlayingElement().name;
            expect(response).to.equal(`test3`);
        });
        it(`if no element is playing, then the first is`, function () {
            playlist.get(1).get(3).loaded = false;
            const response = playlist.get(1).getPlayingElement().name;
            expect(response).to.equal(`test`);
        });
    });
    describe(`#getNextId`, function () {
        it(`should find next id`, function () {
            const response = playlist.get(1).getNextId();
            expect(response).to.equal(1);
        });
        it(`should find next id by looping around`, function () {
            playlist.get(1).id = playlist.get(1).elements.length - 1;
            const response = playlist.get(1).getNextId();
            expect(response).to.equal(0);
        });
    });
    describe(`#getPreviousId`, function () {
        it(`should find previous id`, function () {
            const response = playlist.get(1).getPreviousId();
            expect(response).to.equal(playlist.get(1).elements.length - 2);
        });
        it(`should find previous id by looping around`, function () {
            playlist.get(1).id = 0;
            const response = playlist.get(1).getPreviousId();
            expect(response).to.equal(playlist.get(1).elements.length - 1);
        });
    });
});