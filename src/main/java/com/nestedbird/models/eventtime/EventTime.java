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

package com.nestedbird.models.eventtime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nestedbird.components.bridges.EventTimeBridge;
import com.nestedbird.components.bridges.JodaDateTimeSplitBridge;
import com.nestedbird.components.bridges.JodaPeriodSplitBridge;
import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.event.Event;
import com.nestedbird.models.event.ParsedEventData;
import com.nestedbird.modules.schema.Schema;
import com.nestedbird.modules.schema.annotations.SchemaRepository;
import com.nestedbird.modules.schema.annotations.SchemaView;
import lombok.*;
import org.hibernate.search.annotations.*;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Event time.
 */
@Entity
@Table(name = "event_times")
@Cacheable
@Indexed
@SchemaRepository(EventTimeRepository.class)
@ClassBridge(impl = EventTimeBridge.class)
@AnalyzerDiscriminator(impl = EventTimeBridge.class)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"event"})
@NoArgsConstructor(force = true)
public class EventTime extends AuditedEntity implements Serializable {
    /**
     * the associated event
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonBackReference(value = "eventParent")
    @IndexedEmbedded
    @SchemaView(value = Schema.BaseEntity, type = Event.class, locked = true)
    @JsonProperty("event")
    private Event event;

    /**
     * the start time of the event
     */
    @Column(name = "start_time", nullable = true, length = 200)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(store = Store.YES, bridge = @FieldBridge(impl = JodaDateTimeSplitBridge.class))
    @SchemaView(Schema.DATETIME)
    private DateTime startTime;

    /**
     * the duration of the event
     */
    @Column(name = "duration", nullable = true, length = 200)
    @Field(store = Store.YES, bridge = @FieldBridge(impl = JodaPeriodSplitBridge.class))
    @SchemaView(Schema.PERIOD)
    private Period duration;

    /**
     * the repeat time of the event,
     * ie how much time passes between each occurrence
     */
    @Column(name = "repeat_time", nullable = true, length = 200)
    @Field(store = Store.YES, bridge = @FieldBridge(impl = JodaPeriodSplitBridge.class))
    @SchemaView(Schema.PERIOD)
    private Period repeatTime;

    /**
     * the final repeat time of the event
     */
    @Column(name = "repeat_end", nullable = true, length = 200)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(store = Store.YES, bridge = @FieldBridge(impl = JodaDateTimeSplitBridge.class))
    @SchemaView(Schema.DATETIME)
    private DateTime repeatEnd;

    @Builder
    private EventTime(final String id,
                      final Event event,
                      final DateTime startTime,
                      final Period duration,
                      final Period repeatTime,
                      final DateTime repeatEnd,
                      final Boolean active) {
        super(id);

        // Fatal if null
        if (event == null) throw new NullPointerException("A time must have an event");
        this.event = event;

        // Null Safe
        this.startTime = Optional.ofNullable(startTime).orElse(new DateTime());
        this.duration = Optional.ofNullable(duration).orElse(new Period());
        this.repeatTime = Optional.ofNullable(repeatTime).orElse(new Period());
        this.repeatEnd = Optional.ofNullable(repeatEnd).orElse(new DateTime());
        setActive(Optional.ofNullable(active).orElse(false));
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

    /**
     * Sets start time.
     *
     * @param startTime the start time
     * @return the start time
     */
    public EventTime setStartTime(final DateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * Sets start time.
     *
     * @param startTime the start time
     * @return the start time
     */
    public EventTime setStartTime(final String startTime) {
        setStartTime(startTime, "dd/MM/yyyy HH:mm:ss");
        return this;
    }

    /**
     * Sets start time.
     *
     * @param startTime the start time
     * @param pattern   the pattern
     * @return the start time
     */
    public EventTime setStartTime(final String startTime, final String pattern) {
        this.startTime = org.joda.time.format.DateTimeFormat
                .forPattern(pattern)
                .parseDateTime(startTime);
        return this;
    }

    /**
     * Sets duration.
     *
     * @param duration the duration
     * @return the duration
     */
    public EventTime setDuration(final Integer duration) {
        this.duration = Period.seconds(duration);
        return this;
    }

    /**
     * Sets duration.
     *
     * @param duration the duration
     * @return the duration
     */
    public EventTime setDuration(final Period duration) {
        this.duration = duration;
        return this;
    }

    /**
     * Sets repeat time.
     *
     * @param repeatTime the repeat time
     * @return the repeat time
     */
    public EventTime setRepeatTime(final Integer repeatTime) {
        this.repeatTime = Period.seconds(repeatTime);
        return this;
    }

    /**
     * Sets repeat time.
     *
     * @param repeatTime the repeat time
     * @return the repeat time
     */
    public EventTime setRepeatTime(final Period repeatTime) {
        this.repeatTime = repeatTime;
        return this;
    }

    /**
     * Sets repeat end.
     *
     * @param repeatEnd the repeat end
     * @return the repeat end
     */
    public EventTime setRepeatEnd(final DateTime repeatEnd) {
        this.repeatEnd = repeatEnd;
        return this;
    }

    /**
     * Sets repeat end.
     *
     * @param repeatEnd the repeat end
     * @return the repeat end
     */
    public EventTime setRepeatEnd(final String repeatEnd) {
        setRepeatEnd(repeatEnd, "dd/MM/yyyy HH:mm:ss");
        return this;
    }

    /**
     * Sets repeat end.
     *
     * @param repeatEnd the repeat end
     * @param pattern   the pattern
     * @return the repeat end
     */
    public EventTime setRepeatEnd(final String repeatEnd, final String pattern) {
        this.repeatEnd = org.joda.time.format.DateTimeFormat
                .forPattern(pattern)
                .parseDateTime(repeatEnd);
        return this;
    }

    /**
     * retrieves future occurrences of this event time
     *
     * @return list of occurrences
     */
    public List<ParsedEventData> getFutureOccurrences() {
        DateTime thisMorning = DateTime.now().withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);
        return getOccurrences(thisMorning.getMillis());
    }

    /**
     * Gets all occurrences of an event time
     *
     * @param fromTime the from time
     * @return list of occurrences
     */
    public List<ParsedEventData> getOccurrences(final long fromTime) {
        List<ParsedEventData> parsedResults = new ArrayList<>();

        if (getStartTime() == null) {
            return parsedResults;
        }

        final long startTime = getStartTime().getMillis();

        if (startTime >= fromTime) {
            parsedResults.add(generateParsedEventData(startTime, getDuration(), event.getId()));
        }

        if ((getRepeatTime() != null) && (getRepeatTime().normalizedStandard(PeriodType.hours()).getHours() >= 24)) {
            final long finalTime = DateTime.now().plusWeeks(6).getMillis();
            final long lastTime = (getRepeatEnd() != null) ? getRepeatEnd().getMillis() : finalTime;
            final long newLastTime = lastTime <= finalTime ? lastTime : finalTime;
            final long timeDifference = newLastTime - startTime;

            if (timeDifference > 0) {
                final long repeatTime = getRepeatTime().normalizedStandard(PeriodType.millis()).getMillis();
                final int repeatOccurrences = (int) Math.floor((double) timeDifference / (double) repeatTime);

                for (int i = 1; i <= Math.min(repeatOccurrences, 10); i++) {
                    final long occurrenceTime = startTime + (repeatTime * i);
                    if (occurrenceTime >= fromTime) {
                        parsedResults.add(generateParsedEventData(
                                occurrenceTime,
                                getDuration(),
                                event.getId()));
                    }
                }
            }
        }

        return parsedResults;
    }

    /**
     * Create new Parsed Event Data object
     *
     * @param startTime start time of occurrence
     * @param duration  duration of occcurrence
     * @param eventId   event id
     * @return new parsed event data
     */
    private ParsedEventData generateParsedEventData(final long startTime, final Period duration, final String eventId) {
        final ParsedEventData parsedEventData = new ParsedEventData();
        parsedEventData.setEventId(eventId);
        parsedEventData.setStartTime(new DateTime(startTime));
        parsedEventData.setDuration(duration);
        return parsedEventData;
    }

    /**
     * Get all occurrences of this event time
     *
     * @return list of occurrences
     */
    public List<ParsedEventData> getOccurrences() {
        return getOccurrences(1L);
    }

    @Override
    public String getUrl() {
        return "#";
    }

    @Override
    public String getDefiningName() {
        return getIdBase64();
    }
}
