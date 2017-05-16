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

-- This procedure gets all events that COULD be in the future.
-- It may not know exactly when they are, as it does not parse the event times in detail

CREATE PROCEDURE `getUpcomingEvents` ()
BEGIN
  SELECT DISTINCT SQL_CACHE
    e.*
  FROM
    NestedBird.events AS e
  JOIN
    NestedBird.event_times AS t ON e.id = t.event_id
  WHERE (
    e.active = 1
  ) AND (
    (
        t.start_time >= CURDATE()
      OR
        t.start_time >= t.repeat_end
    ) OR (
        t.repeat_end >= CURDATE()
    )
  );
END
