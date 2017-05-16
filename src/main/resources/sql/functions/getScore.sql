--  NestedBird  Copyright (C) 2016-2017  Michael Haddon
--
--  This program is free software: you can redistribute it and/or modify
--  it under the terms of the GNU Affero General Public License version 3
--  as published by the Free Software Foundation.
--
--  This program is distributed in the hope that it will be useful,
--  but WITHOUT ANY WARRANTY; without even the implied warranty of
--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--  GNU Affero General Public License for more details.
--
--  You should have received a copy of the GNU Affero General Public License
--  along with this program.  If not, see <http://www.gnu.org/licenses/>.

-- This function retrieves the score of a media

DELIMITER $$
CREATE FUNCTION `getScore`(sourcePlayBackCount INTEGER, sourceCommentCount INTEGER, sourceFavouriteCount INTEGER,
                           creationDateTime    DATETIME)
  RETURNS DOUBLE
  BEGIN
    DECLARE sqrtPlayBackCount DOUBLE;
    DECLARE sqrtCommentCount DOUBLE;
    DECLARE sqrtFavouriteCount DOUBLE;
    DECLARE sqrtTotal DOUBLE;
    DECLARE age DOUBLE;
    DECLARE returnVar DOUBLE;

    SET sqrtPlayBackCount = SQRT((IFNULL(sourcePlayBackCount, 0.0) + 1.0) / 250.0);
    SET sqrtCommentCount = SQRT((IFNULL(sourceCommentCount, 0.0) + 1.0) / 1.0);
    SET sqrtFavouriteCount = SQRT((IFNULL(sourceFavouriteCount, 0.0) + 1.0) / 1.0);
    SET sqrtTotal = sqrtPlayBackCount + sqrtCommentCount + sqrtFavouriteCount;
    SET age = IFNULL(TIMESTAMPDIFF(HOUR, creationDateTime, NOW()) / 100.0, 0.0) + 1.0;
    SET returnVar = POW(age, 3) / (sqrtTotal + 1.0);
    RETURN returnVar;
  END$$

DELIMITER ;

