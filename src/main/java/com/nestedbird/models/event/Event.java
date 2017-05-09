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

import com.fasterxml.jackson.annotation.*;
import com.nestedbird.components.bridges.ArtistBridge;
import com.nestedbird.components.bridges.EventTimeBridge;
import com.nestedbird.components.bridges.JodaDateTimeSplitBridge;
import com.nestedbird.models.artist.Artist;
import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.eventtime.EventTime;
import com.nestedbird.models.location.Location;
import com.nestedbird.models.occurrence.Occurrence;
import com.nestedbird.modules.entitysearch.SearchAnalysers;
import com.nestedbird.modules.schema.Schema;
import com.nestedbird.modules.schema.annotations.SchemaRepository;
import com.nestedbird.modules.schema.annotations.SchemaView;
import lombok.*;
import org.hibernate.search.annotations.*;
import org.hibernate.search.bridge.builtin.LongBridge;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Event.
 */
@Entity
@Table(name = "events")
@Cacheable
@Indexed
//@Cache(region="common", usage = CacheConcurrencyStrategy.READ_WRITE)
@SchemaRepository(EventRepository.class)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"artists", "location", "recordedOccurrences", "times"})
@NoArgsConstructor(force = true)
public class Event extends AuditedEntity implements Serializable {

    /**
     * The name of this event
     */
    @Column(name = "name", length = 100)
    @Field(boost = @Boost(2f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 100)
    @SchemaView
    private String name;

    /**
     * What artists are at this event
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "events_artists",
            joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id", referencedColumnName = "id"))
    @JsonManagedReference(value = "eventArtistParent")
    @SchemaView(value = Schema.BaseEntityArray, type = Artist.class)
    @Field(bridge = @FieldBridge(impl = ArtistBridge.class),
            analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    private Set<Artist> artists = new HashSet<>(0);

    /**
     * raw information about the event
     */
    @Column(name = "raw", nullable = true, columnDefinition = "LONGTEXT")
    @Lob
    private transient String raw;

    /**
     * old raw information, for checking changes
     */
    @Column(name = "old_raw", nullable = true, columnDefinition = "LONGTEXT")
    @Lob
    private transient String oldRaw;

    /**
     * the last time this event was updated
     */
    @Column(name = "updated_time", length = 200)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(store = Store.YES, bridge = @FieldBridge(impl = JodaDateTimeSplitBridge.class))
    @SchemaView(value = Schema.DATETIME, locked = true)
    private DateTime updatedTime;

    /**
     * the time this event was processed
     */
    @Column(name = "processed_time", length = 200)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(store = Store.YES, bridge = @FieldBridge(impl = JodaDateTimeSplitBridge.class))
    @SchemaView(value = Schema.DATETIME, locked = true)
    private DateTime processedDate;

    /**
     * the facebook id of this event
     */
    @Column(name = "facebook_id", length = 20)
    @Field
    @FieldBridge(impl = LongBridge.class)
    @SchemaView(Schema.FACEBOOKID)
    private Long facebookId;

    /**
     * the image associated to this event
     */
    @Column(name = "image_url", length = 250)
    @Field(store = Store.NO)
    @Size(max = 250)
    @SchemaView(Schema.IMAGEURL)
    private String imageUrl;

    /**
     * this events description
     */
    @Column(name = "description", columnDefinition = "TEXT", length = 25000)
    @Lob
    @Field(boost = @Boost(0.5f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 25000)
    @SchemaView(Schema.MARKDOWN)
    private String description;

    /**
     * the location of this event
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", nullable = false)
    //    @JsonBackReference(value = "locationParent")
    //    @JsonIdentityReference(alwaysAsId=true)
    @JsonManagedReference(value = "locationParent")
    @IndexedEmbedded
    @SchemaView(value = Schema.BaseEntity, type = Location.class)
    @JsonProperty("location")
    private Location location;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event")
    @JsonBackReference(value = "occurrencetoevent")
    @SchemaView(visible = false)
    private Set<Occurrence> recordedOccurrences = new HashSet<>(0);

    /**
     * The amount of eventtimes that are associated to this event
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event")
    @JsonManagedReference(value = "eventParent")
    @SchemaView(value = "Array", type = EventTime.class, mappings = {"event:id"})
    @Field(bridge = @FieldBridge(impl = EventTimeBridge.class),
            analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    //    @AnalyzerDiscriminator(impl = EventTimeBridge.class)
    private Set<EventTime> times = new HashSet<>(0);

    @Builder
    private Event(final String id,
                  final String name,
                  final Set<Artist> artists,
                  final String raw,
                  final String oldRaw,
                  final DateTime updatedTime,
                  final DateTime processedDate,
                  final Long facebookId,
                  final String imageUrl,
                  final String description,
                  final Location location,
                  final Set<Occurrence> recordedOccurrences,
                  final Set<EventTime> times,
                  final Boolean active) {
        super(id);

        // Fatal if null
        if (location == null) throw new NullPointerException("An event must have a location");
        this.location = location;

        // Null Safe
        this.name = Optional.ofNullable(name).orElse("");
        this.artists = Optional.ofNullable(artists).orElse(new HashSet<>());
        this.raw = Optional.ofNullable(raw).orElse("");
        this.oldRaw = Optional.ofNullable(oldRaw).orElse("");
        this.updatedTime = Optional.ofNullable(updatedTime).orElse(new DateTime());
        this.processedDate = Optional.ofNullable(processedDate).orElse(new DateTime());
        this.imageUrl = Optional.ofNullable(imageUrl).orElse("");
        this.description = Optional.ofNullable(description).orElse("");
        this.recordedOccurrences = Optional.ofNullable(recordedOccurrences).orElse(new HashSet<>());
        this.times = Optional.ofNullable(times).orElse(new HashSet<>());
        setActive(Optional.ofNullable(active).orElse(false));

        // Not null safe
        this.facebookId = facebookId;
    }

    /**
     * Sets updated time.
     *
     * @param updatedTime the updated time
     * @return the updated time
     */
    public Event setUpdatedTime(final DateTime updatedTime) {
        this.updatedTime = updatedTime;
        return this;
    }

    /**
     * Sets updated time.
     *
     * @param updatedTime the updated time
     * @return the updated time
     */
    public Event setUpdatedTime(final String updatedTime) {
        setUpdatedTime(updatedTime, "dd/MM/yyyy HH:mm:ss");
        return this;
    }

    /**
     * Sets updated time.
     *
     * @param updatedTime the updated time
     * @param pattern     the pattern
     * @return the updated time
     */
    public Event setUpdatedTime(final String updatedTime, final String pattern) {
        this.updatedTime = org.joda.time.format.DateTimeFormat
                .forPattern(pattern)
                .parseDateTime(updatedTime);
        return this;
    }

    /**
     * Does this event have any occurrences in the future
     *
     * @return does this occur in the future
     */
    @JsonIgnore
    public boolean isInFuture() {
        return getAllOccurrences().stream()
                .anyMatch(e -> e.getStartTime().isAfterNow());
    }

    /**
     * Retrieves a list of all occurrences of this event
     *
     * @return list of occurrences
     */
    @JsonIgnore
    public List<ParsedEventData> getAllOccurrences() {
        return times.stream()
                .map(EventTime::getOccurrences)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Gets all artist names.
     *
     * @return the all artist names
     */
    @JsonIgnore
    public List<String> getAllArtistNames() {
        final Stream<String> artistNames = getArtists().stream()
                .map(Artist::getName);

        return Stream.concat(artistNames, Arrays.stream(name.split(";")))
                .collect(Collectors.toList());
    }

    /**
     * Gets all artist names encoded.
     *
     * @return the all artist names encoded
     */
    @JsonIgnore
    public List<String> getAllArtistNamesEncoded() {
        final Stream<String> artistNames = getArtists().stream()
                .map(e -> {
                    if (e.getFacebookId().length() > 0) {
                        return "@[" + e.getFacebookId() + ":1:" + e.getName() + "]";
                    }
                    return e.getName();
                });

        return Stream.concat(artistNames, Arrays.stream(name.split(";")))
                .collect(Collectors.toList());
    }

    @Override
    public String getUrl() {
        return String.format(
                "/Events/%s/%s",
                this.getIdBase64(),
                this.getName().replace(" ", "_")
        );
    }

    @Override
    public String getDefiningName() {
        return "Event at: ".concat(getLocation().map(Location::getName).orElse("unknown")).trim();
    }

    /**
     * Gets location.
     *
     * @return the location
     */
    @JsonIgnore
    public Optional<Location> getLocation() {
        return Optional.ofNullable(location);
    }
}
