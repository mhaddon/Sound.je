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

package com.nestedbird.models.occurrence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import com.nestedbird.components.bridges.JodaDateTimeSplitBridge;
import com.nestedbird.components.bridges.JodaPeriodSplitBridge;
import com.nestedbird.models.core.Base.BaseEntity;
import com.nestedbird.models.event.Event;
import com.nestedbird.modules.schema.Schema;
import com.nestedbird.modules.schema.annotations.SchemaRepository;
import com.nestedbird.modules.schema.annotations.SchemaView;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.annotations.*;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;

/**
 * The type Occurrence.
 */
@Entity
@Table(name = "occurrences")
@Cacheable
@Indexed
//@Cache(region="common", usage = CacheConcurrencyStrategy.READ_WRITE)
@SchemaRepository(OccurrenceRepository.class)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"event"})
@NoArgsConstructor(force = true)
@Slf4j
public class Occurrence extends BaseEntity implements Serializable {
    /**
     * what event is this assosciated to
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonManagedReference(value = "occurrencetoevent")
    @IndexedEmbedded
    @NotNull
    @SchemaView(value = Schema.BaseEntity, type = Event.class)
    @JsonProperty("event")
    private Event event;

    /**
     * start time of this occurrence
     */
    @Column(name = "start_time", length = 200)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(store = Store.YES, bridge = @FieldBridge(impl = JodaDateTimeSplitBridge.class))
    @SchemaView(Schema.DATETIME)
    private DateTime startTime;

    /**
     * duration of this occurrence
     */
    @Column(name = "duration", length = 200)
    @Field(store = Store.YES, bridge = @FieldBridge(impl = JodaPeriodSplitBridge.class))
    @SchemaView(Schema.PERIOD)
    private Period duration;

    /**
     * facebook post id of this occurrence, for example, if we auto post this to FB, this is the record
     */
    @Column(name = "facebook_post_id", length = 20)
    @Field
    @Size(max = 20)
    @SchemaView(Schema.FACEBOOKID)
    private String facebookPostId;

    @Builder
    private Occurrence(final String id,
                       final Event event,
                       final DateTime startTime,
                       final Period duration,
                       final String facebookPostId) {
        super(id);

        // Null safe
        this.startTime = Optional.ofNullable(startTime).orElse(new DateTime());
        this.duration = Optional.ofNullable(duration).orElse(new Period());

        // Not null safe
        this.event = event;
        this.facebookPostId = facebookPostId;
    }

    @Override
    public String getUrl() {
        String name = "e";
        try {
            name = URLEncoder.encode(getEvent().map(Event::getName)
                    .map(newName -> newName.replace(" ", "_"))
                    .map(newName -> newName.replace("/", ""))
                    .map(newName -> Strings.padStart(newName, 1, 'e'))
                    .orElse("#"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.info("[Occurrence] [getUrl] Failure To Encode URL", e);
        }

        return String.format(
                "/Events/%s/%s?startTime=%s",
                getEvent().map(Event::getIdBase64).orElse(""),
                name, // uuidToBase64(getEvent().getId()),
                String.valueOf(getStartTime().getMillis())
        );
    }

    /**
     * Gets event.
     *
     * @return the event
     */
    @JsonIgnore
    public Optional<Event> getEvent() {
        return Optional.ofNullable(event);
    }

    @Override
    public String getDefiningName() {
        return getIdBase64();
    }

    @Override
    public String getId() {
        return event.getId() + "-" + this.startTime.getMillis() + "-" + this.getDuration().getMillis();
    }
}
