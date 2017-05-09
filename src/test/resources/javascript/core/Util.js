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
/* eslint func-names:0 */
/* eslint prefer-arrow-callback:0 */
// Node Modules

// Site Modules
const Util = require(`${__base}core/Util`);

describe(`Util`, function () {
    describe(`#parseDateTime`, function () {
        it(`time should convert to Thursday 24th of Nov`, function () {
            const response = Util.parseDateTime(1480006377283);
            expect(response).to.equal(`Thursday 24th of Nov`);
        });
    });
    describe(`#parseTime`, function () {
        it(`time should convert to 04:52pm`, function () {
            const response = Util.parseTime(1480006377283);
            expect(response).to.equal(`04:52pm`);
        });
    });
    describe(`#cleanObject`, function () {
        it(`shallow object cleans`, function () {
            const obj = {
                test:  `&amp;`,
                test2: `&#8364;test&#8364;`
            };
            const expectedresponse = {
                test:  `&`,
                test2: `€test€`
            };
            const response = Util.clean(obj);
            expect(response).to.deep.equal(expectedresponse);
        });
        it(`deep object cleans`, function () {
            const obj = {
                deep: {
                    test: `&amp;cat`
                }
            };
            const expectedresponse = {
                deep: {
                    test: `&cat`
                }
            };
            const response = Util.clean(obj);
            expect(response).to.deep.equal(expectedresponse);
        });
        it(`shallow object cleans with array`, function () {
            const obj = {
                array: [
                    `&amp;`,
                    `&pound;`
                ]
            };
            const expectedresponse = {
                array: [
                    `&`,
                    `£`
                ]
            };
            const response = Util.clean(obj);
            expect(response).to.deep.equal(expectedresponse);
        });
        it(`deep object cleans with array`, function () {
            const obj = {
                deep: {
                    array: [
                        `&amp;`,
                        `&pound;`
                    ]
                }
            };
            const expectedresponse = {
                deep: {
                    array: [
                        `&`,
                        `£`
                    ]
                }
            };
            const response = Util.clean(obj);
            expect(response).to.deep.equal(expectedresponse);
        });
    });
    describe(`#cleanArray`, function () {
        it(`shallow array clean`, function () {
            const arr = [
                `&amp;`,
                `&#8364;test&#8364;`
            ];
            const expectedresponse = [
                `&`,
                `€test€`
            ];
            const response = Util.cleanArray(arr);
            expect(response).to.deep.equal(expectedresponse);
        });
        it(`deep array clean`, function () {
            const arr = [{
                test: `&amp;`
            }];
            const expectedresponse = [{
                test: `&`
            }];
            const response = Util.cleanArray(arr);
            expect(response).to.deep.equal(expectedresponse);
        });
    });
    describe(`#clean`, function () {
        it(`string clean`, function () {
            const string = `&amp;`;
            const expectedresponse = `&`;
            const response = Util.clean(string);
            expect(response).to.deep.equal(expectedresponse);
        });
        it(`deep clean`, function () {
            const obj = {
                test:  `&amp;`,
                test2: 1,
                test3: null,
                test4: {
                    test:  `&amp;`,
                    test2: [
                        `&amp;`,
                        `&amp;`
                    ]
                }
            };
            const expectedresponse = {
                test:  `&`,
                test2: 1,
                test3: null,
                test4: {
                    test:  `&`,
                    test2: [
                        `&`,
                        `&`
                    ]
                }
            };
            const response = Util.clean(obj);
            expect(response).to.deep.equal(expectedresponse);
        });
    });
    describe(`#is`, function () {
        it(`strings match`, function () {
            const response = Util.is(`test`, `test`);
            expect(response).to.be.true;
        });
        it(`strings do not match`, function () {
            const response = Util.is(`test`, `test2`);
            expect(response).to.be.false;
        });
        it(`numbers match`, function () {
            const response = Util.is(1, 1);
            expect(response).to.be.true;
        });
        it(`numbers do not match strings`, function () {
            const response = Util.is(1, `1`);
            expect(response).to.be.false;
        });
        it(`arrays match`, function () {
            const array1 = [`1`, 2, `3`];
            const array2 = [`1`, 2, `3`];
            const response = Util.is(array1, array2);
            expect(response).to.be.true;
        });
        it(`objects match`, function () {
            const obj1 = {
                a: `1`
            };
            const obj2 = {
                a: `1`
            };
            const response = Util.is(obj1, obj2);
            expect(response).to.be.true;
        });
        it(`objects do not match`, function () {
            const obj1 = {
                a: `1`
            };
            const obj2 = {
                a: `1`,
                b: 1
            };
            const response = Util.is(obj1, obj2);
            expect(response).to.be.false;
        });
    });
});