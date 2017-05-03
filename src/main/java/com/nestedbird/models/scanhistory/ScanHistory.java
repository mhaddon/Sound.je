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

import com.nestedbird.models.core.Base.BaseEntity;
import com.nestedbird.modules.schema.annotations.SchemaRepository;
import lombok.*;
import org.hibernate.search.annotations.Indexed;
import org.joda.time.DateTime;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Optional;

/**
 * The type Scan history.
 */
@Entity
@Table(name = "scan_history")
@Cacheable
@Indexed
@SchemaRepository(ScanHistoryRepository.class)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class ScanHistory extends BaseEntity implements Serializable {
    private String url;
    private DateTime scanTime;

    @Builder
    private ScanHistory(final String id,
                        final String url,
                        final DateTime scanTime) {
        super(id);

        // Null safe
        this.url = Optional.ofNullable(url).orElse("");
        this.scanTime = Optional.ofNullable(scanTime).orElse(new DateTime());
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getDefiningName() {
        return getIdBase64();
    }
}
