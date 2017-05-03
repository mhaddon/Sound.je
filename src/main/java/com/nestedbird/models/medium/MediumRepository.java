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

package com.nestedbird.models.medium;


import com.nestedbird.models.core.Audited.AuditedRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * The interface Medium repository.
 */
@Repository
public interface MediumRepository extends AuditedRepository<Medium> {
    /**
     * Sets medium score by id.
     *
     * @param score      the score
     * @param scoreFinal the score final
     * @param id         the id
     */
    @Modifying
    @Transactional
    @Query("UPDATE Medium m SET m.score = :score, m.scoreFinal = :scoreFinal WHERE m.id = :id")
    void setMediumScoreById(@Param("score") final double score, @Param("scoreFinal") final double scoreFinal, @Param("id") final String id);

    /**
     * Find first by source id and type medium.
     *
     * @param sourceId the source id
     * @param type     the type
     * @return the medium
     */
    Medium findFirstBySourceIdAndType(final String sourceId, final MediumType type);
}