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

package com.nestedbird.models.event;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.PeriodDeserializer;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import com.fasterxml.jackson.datatype.joda.ser.PeriodSerializer;
import com.nestedbird.models.core.DataObject;
import lombok.*;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.io.Serializable;
import java.util.Optional;

/**
 * The type Parsed event data.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class ParsedEventData extends DataObject implements Serializable {
    private String eventId;

    @JsonSerialize(using = DateTimeSerializer.class)
    //    @JsonDeserialize(using= DateTimeDeserializer.class)
    private DateTime startTime;

    @JsonSerialize(using = PeriodSerializer.class)
    @JsonDeserialize(using = PeriodDeserializer.class)
    private Period duration;

    @Builder
    private ParsedEventData(final String eventId, final DateTime startTime, final Period duration) {

        // Null safe
        this.eventId = Optional.ofNullable(eventId).orElse("");
        this.startTime = Optional.ofNullable(startTime).orElse(new DateTime());
        this.duration = Optional.ofNullable(duration).orElse(new Period(0));
    }

    @Override
    public String toString() {
        return toJSON();
    }
}
