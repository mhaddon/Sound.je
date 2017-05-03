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

package com.nestedbird.models.scanhistory;

import com.nestedbird.models.core.Base.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Scan history repository.
 */
@Repository
public interface ScanHistoryRepository extends BaseRepository<ScanHistory> {
    /**
     * Find first by url scan history.
     *
     * @param url the url
     * @return the scan history
     */
    ScanHistory findFirstByUrl(final String url);
}
