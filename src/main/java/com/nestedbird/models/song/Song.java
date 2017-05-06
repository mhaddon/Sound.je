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

package com.nestedbird.models.song;

import com.fasterxml.jackson.annotation.*;
import com.nestedbird.components.bridges.JodaDateTimeSplitBridge;
import com.nestedbird.models.artist.Artist;
import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.medium.Medium;
import com.nestedbird.modules.entitysearch.SearchAnalysers;
import com.nestedbird.modules.schema.Schema;
import com.nestedbird.modules.schema.annotations.SchemaRepository;
import com.nestedbird.modules.schema.annotations.SchemaView;
import lombok.*;
import org.hibernate.search.annotations.*;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The type Song.
 */
@Entity
@Table(name = "songs")
@Cacheable
@Indexed
@SchemaRepository(SongRepository.class)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"artist", "media"})
@NoArgsConstructor(force = true)
public class Song extends AuditedEntity implements Serializable {

    /**
     * what artist this song belongs to
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "artist_id")
    @JsonManagedReference(value = "songToArtist")
    @IndexedEmbedded
    @SchemaView(value = Schema.BaseEntity, type = Artist.class)
    @JsonProperty("artist")
    private Artist artist;

    /**
     * what media this song has
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "song")
    @JsonBackReference(value = "mediaToSong")
    private Set<Medium> media = new HashSet<>(0);

    /**
     * the name of this song
     */
    @Column(name = "name", nullable = false, length = 100)
    @Field(boost = @Boost(2.0f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @NotNull
    @Size(max = 100)
    @SchemaView
    private String name;

    /**
     * the release time of this song
     */
    @Column(name = "release_date_time", length = 200)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //    @DateBridge(resolution = Resolution.SECOND)
    @Field(store = Store.YES, bridge = @FieldBridge(impl = JodaDateTimeSplitBridge.class))
    @SchemaView(Schema.DATETIME)
    private DateTime releaseDateTime;

    /**
     * this songs lyrics
     */
    @Column(name = "lyrics", nullable = true, columnDefinition = "LONGTEXT")
    @Field(boost = @Boost(0.5f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Lob
    private String lyrics;

    @Builder
    private Song(final String id,
                 final Artist artist,
                 final Set<Medium> media,
                 final String name,
                 final DateTime releaseDateTime,
                 final String lyrics,
                 final Boolean active) {
        super(id);

        // Null safe
        this.media = Optional.ofNullable(media).orElse(new HashSet<>());
        this.name = Optional.ofNullable(name).orElse("");
        this.releaseDateTime = Optional.ofNullable(releaseDateTime).orElse(new DateTime());
        this.lyrics = Optional.ofNullable(lyrics).orElse("");
        setActive(Optional.ofNullable(active).orElse(false));

        // Not null safe
        this.artist = artist;
    }

    /**
     * Gets artist.
     *
     * @return the artist
     */
    @JsonIgnore
    public Optional<Artist> getArtist() {
        return Optional.ofNullable(artist);
    }

    /**
     * Sets release time.
     *
     * @param releaseDateTime the release time
     */
    public void setReleaseDateTime(final DateTime releaseDateTime) {
        this.releaseDateTime = releaseDateTime;
    }

    /**
     * Sets release time.
     *
     * @param releaseDateTime the release time
     */
    public void setReleaseDateTime(final String releaseDateTime) {
        setReleaseDateTime(releaseDateTime, "dd/MM/yyyy HH:mm:ss");
    }

    /**
     * Sets release time.
     *
     * @param releaseDateTime the release time
     * @param pattern         the pattern
     */
    public void setReleaseDateTime(final String releaseDateTime, final String pattern) {
        this.releaseDateTime = org.joda.time.format.DateTimeFormat
                .forPattern(pattern)
                .parseDateTime(releaseDateTime);
    }

    @Override
    public String getUrl() {
        return String.format(
                "/Songs/%s/%s",
                this.getIdBase64(),
                this.getName().replace(" ", "_")
        );
    }

    @Override
    public String getDefiningName() {
        return name;
    }
}
