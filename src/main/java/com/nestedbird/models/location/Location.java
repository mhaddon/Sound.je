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

package com.nestedbird.models.location;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nestedbird.components.bridges.EventBridge;
import com.nestedbird.models.core.Tagged.TaggedEntity;
import com.nestedbird.models.event.Event;
import com.nestedbird.models.tag.Tag;
import com.nestedbird.modules.entitysearch.SearchAnalysers;
import com.nestedbird.modules.schema.Schema;
import com.nestedbird.modules.schema.annotations.SchemaRepository;
import com.nestedbird.modules.schema.annotations.SchemaView;
import lombok.*;
import org.hibernate.search.annotations.*;
import org.hibernate.search.bridge.builtin.LongBridge;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The type Location.
 */
@Entity
@Table(name = "locations")
@Cacheable
@Indexed
@SchemaRepository(LocationRepository.class)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"events"})
@NoArgsConstructor(force = true)
public class Location extends TaggedEntity implements Serializable {

    /**
     * all events associated to the location
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "location")
    //    @JsonManagedReference(value = "locationParent")
    @JsonBackReference(value = "locationParent")
    @Field(bridge = @FieldBridge(impl = EventBridge.class),
            analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    private Set<Event> events = new HashSet<>(0);

    /**
     * name of the location
     */
    @Column(name = "name", nullable = false, length = 100)
    @Field(boost = @Boost(2f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @NotNull
    @Size(min = 2, max = 140)
    @SchemaView
    private String name;

    /**
     * the id of the facebook page for this location
     */
    @Column(name = "facebook_id", length = 20)
    @Field(bridge = @FieldBridge(impl = LongBridge.class))
    @SchemaView(Schema.FACEBOOKID)
    private Long facebookId;

    /**
     * the postcode of this location
     */
    @Column(name = "postcode", length = 100)
    @Field
    @Size(max = 100)
    @SchemaView
    private String postcode;

    /**
     * the city of this location
     */
    @Column(name = "city", length = 100)
    @Field(analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 100)
    @SchemaView
    private String city;

    /**
     * the country of this location
     */
    @Column(name = "country", length = 100)
    @Field(analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 100)
    @SchemaView
    private String country;

    /**
     * the street of this location
     */
    @Column(name = "street", length = 100)
    @Field(analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 100)
    @SchemaView
    private String street;

    /**
     * the coordinates, this value is seperated by a comma
     * longitude,latitude
     */
    @Column(name = "coordinates")
    @Size(max = 255)
    @SchemaView
    private String coordinates;

    /**
     * the image associated to this location
     */
    @Column(name = "image_url", length = 250)
    @Size(max = 250)
    @SchemaView(Schema.IMAGEURL)
    private String imageUrl;

    /**
     * description about this location
     */
    @Column(name = "description", columnDefinition = "TEXT", length = 25000)
    @Lob
    @Field(boost = @Boost(0.5f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 25000)
    @SchemaView(Schema.MARKDOWN)
    private String description;

    @Builder
    private Location(final String id,
                     final Set<Event> events,
                     final String name,
                     final Long facebookId,
                     final String postcode,
                     final String city,
                     final String country,
                     final String street,
                     final String coordinates,
                     final String imageUrl,
                     final String description,
                     final Boolean active,
                     final Set<Tag> tags) {
        super(id);

        // Null safe
        this.events = Optional.ofNullable(events).orElse(new HashSet<>());
        this.name = Optional.ofNullable(name).orElse("");
        this.postcode = Optional.ofNullable(postcode).orElse("");
        this.city = Optional.ofNullable(city).orElse("");
        this.country = Optional.ofNullable(country).orElse("");
        this.street = Optional.ofNullable(street).orElse("");
        this.coordinates = Optional.ofNullable(coordinates).orElse("0,0");
        this.imageUrl = Optional.ofNullable(imageUrl).orElse("");
        this.description = Optional.ofNullable(description).orElse("");
        setActive(Optional.ofNullable(active).orElse(false));
        setTags(Optional.ofNullable(tags).orElse(new HashSet<>()));

        // Not null safe
        this.facebookId = facebookId;
    }

    @Override
    public String getUrl() {
        return String.format(
                "/Locations/%s/%s",
                this.getIdBase64(),
                this.getName().replace(" ", "_")
        );
    }

    @Override
    public String getDefiningName() {
        return name;
    }
}
