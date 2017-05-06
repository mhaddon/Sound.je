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

package com.nestedbird.models.core.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nestedbird.models.core.Base.BaseEntity;
import com.nestedbird.models.user.User;
import com.nestedbird.modules.schema.Schema;
import com.nestedbird.modules.schema.annotations.SchemaView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.search.annotations.Field;
import org.joda.time.DateTime;
import org.joda.time.base.AbstractInstant;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.Optional;

/**
 * This abstract class is for entities that extra transactional information stored about them
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"createdBy", "lastModifiedBy"})
public abstract class AuditedEntity extends BaseEntity {

    /**
     * When was this entity originally created
     */
    @CreatedDate
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private final Date createdDate;

    /**
     * When was this entity last updated
     */
    @LastModifiedDate
    @Column(name = "last_modified_date")
    @Temporal(TemporalType.TIMESTAMP)
    //    @Version
    @JsonIgnore
    private Date lastModifiedDate;

    /**
     * Who created this entity
     */
    @CreatedBy
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "created_by")
    private User createdBy;

    /**
     * Who last updated this entity
     */
    @LastModifiedBy
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @Fetch(FetchMode.SELECT)
    @JoinColumn(name = "last_modified_by")
    private User lastModifiedBy;

    @Column(name = "active")
    @Field
    @SchemaView(Schema.BOOLEAN)
    private Boolean active;

    /**
     * Instantiates a new Base entity with a random UUID
     */
    protected AuditedEntity() {
        super();
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
    }

    /**
     * Instantiates a new Base entity.
     *
     * @param id the id
     */
    protected AuditedEntity(final String id) {
        super(id);
        this.createdDate = new Date();
        this.lastModifiedDate = new Date();
    }

    /**
     * Gets created by.
     *
     * @return the created by
     */
    public Optional<User> getCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    /**
     * Gets created date.
     *
     * @return the created date
     */
    public DateTime getCreatedDate() {
        return Optional.ofNullable(createdDate)
                .map(DateTime::new)
                .orElse(null);
    }

    /**
     * Gets last modified by.
     *
     * @return the last modified by
     */
    public Optional<User> getLastModifiedBy() {
        return Optional.ofNullable(lastModifiedBy);
    }

    /**
     * Gets last modified date.
     *
     * @return the last modified date
     */
    public DateTime getLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate)
                .map(DateTime::new)
                .orElse(null);
    }

    /**
     * Sets last modified date.
     *
     * @param lastModifiedDate the last modified date
     */
    public void setLastModifiedDate(final DateTime lastModifiedDate) {
        this.lastModifiedDate = Optional.ofNullable(lastModifiedDate)
                .map(AbstractInstant::toDate)
                .orElse(null);
    }

    /**
     * Gets active.
     *
     * @return the active
     */
    public boolean getActive() {
        return Optional.ofNullable(active).orElse(false);
    }
}
