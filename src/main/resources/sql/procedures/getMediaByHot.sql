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

-- This procedure gets all media by how hot they are.
-- It calculates their score and then sorts them

CREATE DEFINER=`root`@`localhost` PROCEDURE `getMediaByHot`()
BEGIN

-- This query requires you to disable something probably very important:
-- in my.conf add:
-- sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION
--
-- Probably will rewrite in the future to not require the above problem.
-- Something to do with not knowing what row i want when i do GROUP BY


	SELECT SQL_CACHE
		InnerQuery.score as 'score_final',
		InnerQuery.*
	FROM
		-- Calculate the score of each song, the total sum of all their medias score
		-- Then we order it by a preference of which media we prefer
		(SELECT
			getScore(ScoreQuery.source_playback_count,
					 ScoreQuery.source_comment_count,
					 ScoreQuery.source_favourite_count,
					 songs.release_date_time) as 'score',
			`media`.`id`,
            `media`.`art_url`,
            `media`.`blocked`,
            `media`.`cover`,
            `media`.`creation_date_time`,
            `media`.`data`,
            `media`.`demo`,
            `media`.`live`,
            `media`.`preview`,
            `media`.`source_comment_count`,
            `media`.`source_favourite_count`,
            `media`.`source_id`,
            `media`.`source_playback_count`,
            `media`.`submission_date_time`,
            `media`.`type`,
            `media`.`url`,
            `media`.`song_id`,
            `media`.`created_by`,
            `media`.`created_date`,
            `media`.`last_modified_by`,
            `media`.`last_modified_date`,
            `media`.`active`
		FROM
			songs
		JOIN
			media ON media.song_id = songs.id
		JOIN
			-- To calculate a songs score we must add up all its medias scores
			-- This join returns all of a songs media with their score counts 
			-- being the sum of all of their sibling media 
			(SELECT
				song_id,
				IFNULL(SUM(source_playback_count), 0) as 'source_playback_count',
				IFNULL(SUM(source_comment_count), 0) as 'source_comment_count',
				IFNULL(SUM(source_favourite_count), 0) as 'source_favourite_count'
				FROM
					media
				GROUP BY
					song_id
			) as ScoreQuery ON ScoreQuery.song_id=songs.id
		ORDER BY
			media.type='0', 		-- Our native songs are first
			media.type='1', 		-- Then Youtube videos
			media.type='3', 		-- Then soundcloud songs
			media.type='2', 		-- Then facebook videos
			media.live='0', 		-- Then songs that are not live
			media.demo='0', 		-- Then songs that are not demos
			media.preview='0', 		-- Then songs that are not previews
			`score` ASC 			-- Then settle differences by score
		) as InnerQuery
	GROUP BY
		InnerQuery.song_id
	HAVING 
		score_final > 0
	ORDER BY
		score_final ASC;
		
END