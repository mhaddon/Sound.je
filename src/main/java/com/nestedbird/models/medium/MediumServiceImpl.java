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

import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.core.Audited.AuditedRepository;
import com.nestedbird.models.core.Audited.AuditedServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * The type Medium service.
 */
@Service
@Transactional
class MediumServiceImpl extends AuditedServiceImpl<Medium> implements MediumService {

    private final MediumRepository mediumRepository;

    /**
     * Instantiates a new Medium service.
     *
     * @param mediumRepository the medium repository
     */
    @Autowired
    MediumServiceImpl(final MediumRepository mediumRepository) {
        this.mediumRepository = mediumRepository;
    }

    @Override
    protected AuditedRepository<Medium> getRepository() {
        return mediumRepository;
    }

    @Override
    public Optional<Medium> findFirstBySourceIdAndType(final String sourceId, final MediumType type) {
        return Optional.ofNullable(mediumRepository.findFirstBySourceIdAndType(sourceId, type))
                .filter(AuditedEntity::getActive);
    }
}