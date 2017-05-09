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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nestedbird.components.bridges.JodaDateTimeSplitBridge;
import com.nestedbird.models.core.Audited.AuditedEntity;
import com.nestedbird.models.song.Song;
import com.nestedbird.modules.entitysearch.SearchAnalysers;
import com.nestedbird.modules.schema.Schema;
import com.nestedbird.modules.schema.annotations.SchemaRepository;
import com.nestedbird.modules.schema.annotations.SchemaView;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.search.annotations.*;
import org.hibernate.search.bridge.builtin.IntegerBridge;
import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Optional;

/**
 * The type Medium.
 */
@Entity
@Table(name = "media")
@Cacheable
@Indexed
@SchemaRepository(MediumRepository.class)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"song"})
@NoArgsConstructor(force = true)
public class Medium extends AuditedEntity implements Serializable {
    /**
     * the song this medium belongs to
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "song_id")
    @JsonManagedReference(value = "mediaToSong")
    @IndexedEmbedded
    @SchemaView(value = Schema.BaseEntity, type = Song.class)
    @JsonProperty("song")
    private Song song;

    /**
     * when was this media element first created
     */
    @Column(name = "creation_date_time", length = 200)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(store = Store.YES, bridge = @FieldBridge(impl = JodaDateTimeSplitBridge.class))
    @SchemaView(Schema.DATETIME)
    //    @DateBridge(resolution=Resolution.SECOND)
    private DateTime creationDateTime;

    /**
     * when did we first record this media element
     */
    @Column(name = "submission_date_time", nullable = true, length = 200)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(store = Store.YES, bridge = @FieldBridge(impl = JodaDateTimeSplitBridge.class))
    @SchemaView(Schema.DATETIME)
    //    @DateBridge(resolution=Resolution.SECOND)
    private DateTime submissionDateTime;

    /**
     * is this live
     */
    @Column(name = "live")
    @Field
    @SchemaView(Schema.BOOLEAN)
    private Boolean live;

    /**
     * is this a demo
     */
    @Column(name = "demo")
    @Field
    @SchemaView(Schema.BOOLEAN)
    private Boolean demo;

    /**
     * is this a preview
     */
    @Column(name = "preview")
    @Field
    @SchemaView(Schema.BOOLEAN)
    private Boolean preview;

    /**
     * is this blocked
     */
    @Column(name = "blocked")
    @Field
    @SchemaView(Schema.BOOLEAN)
    private Boolean blocked;

    /**
     * is this a cover
     */
    @Column(name = "cover")
    @Field
    @SchemaView(Schema.BOOLEAN)
    private Boolean cover;

    /**
     * additional data
     */
    @Column(name = "data", columnDefinition = "LONGTEXT")
    @Lob
    private String data;

    /**
     * what type of media element is this, ie what website is it hosted on
     */
    @Column(name = "type")
    @Field
    //    @Enumerated(EnumType.ORDINAL)
    @SchemaView(value = Schema.ENUMERATION, type = MediumType.class)
    private MediumType type;

    /**
     * the id for this element on the site that hosts it
     */
    @Column(name = "source_id", length = 100)
    @Field
    @Size(max = 100)
    @SchemaView
    private String sourceId;

    /**
     * the full url to get to the media
     */
    @Column(name = "url", length = 100)
    @Size(max = 100)
    @Field(analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @SchemaView
    private String sourceUrl;

    /**
     * the url of the art associated to the media
     */
    @Column(name = "art_url", length = 200)
    @Size(max = 200)
    @Field
    @SchemaView
    private String artUrl;

    /**
     * the playback count on the original source
     */
    @Column(name = "source_playback_count")
    @Field(bridge = @FieldBridge(impl = IntegerBridge.class))
    private Integer sourcePlaybackCount;

    /**
     * the comment count on the original source
     */
    @Column(name = "source_comment_count")
    @Field(bridge = @FieldBridge(impl = IntegerBridge.class))
    private Integer sourceCommentCount;

    /**
     * the favourite count on the original source
     */
    @Column(name = "source_favourite_count")
    @Field(bridge = @FieldBridge(impl = IntegerBridge.class))
    private Integer sourceFavouriteCount;

    /**
     * what score does this media have, this is how we rank it, it is based on the age and its sources score
     */
    @Column(name = "score")
    @Field(bridge = @FieldBridge(impl = IntegerBridge.class))
    private Double score;

    /**
     * additional score calculations
     */
    @Column(name = "score_final")
    @Field(bridge = @FieldBridge(impl = IntegerBridge.class))
    private Double scoreFinal;

    //    @Formula("getScore(source_playback_count, source_comment_count, source_favourite_count, creation_date_time)")
    //    private Double score;

    //@Transient
    //private Double score;
    //
    //    @JsonProperty("score")
    //    public Double onPostLoad() {
    //        Double sqrtPlayBackCount = Math.sqrt((Optional.ofNullable(sourcePlaybackCount).orElse(0) + 1) / 250.0);
    //        Double sqrtCommentCount = Math.sqrt((Optional.ofNullable(sourceCommentCount).orElse(0) + 1) / 1.0);
    //        Double sqrtFavouriteCount = Math.sqrt((Optional.ofNullable(sourceFavouriteCount).orElse(0) + 1) / 1.0);
    //        Double sqrtTotal = sqrtPlayBackCount + sqrtCommentCount + sqrtFavouriteCount;
    //        Period p = new Period(Optional.ofNullable(creationDateTime).orElse(new DateTime(2000, 1, 1, 1, 1)), new DateTime());
    //        Double ageScore = (p.getHours() / 100.0) + 1;
    //
    //        return Math.pow(ageScore, 2) / (sqrtTotal + 1);
    //    }


    @Builder
    private Medium(final String id,
                   final Song song,
                   final DateTime creationDateTime,
                   final DateTime submissionDateTime,
                   final Boolean live,
                   final Boolean demo,
                   final Boolean preview,
                   final Boolean blocked,
                   final Boolean cover,
                   final String data,
                   final MediumType type,
                   final String sourceId,
                   final String sourceUrl,
                   final String artUrl,
                   final Integer sourcePlaybackCount,
                   final Integer sourceCommentCount,
                   final Integer sourceFavouriteCount,
                   final Double score,
                   final Double scoreFinal,
                   final Boolean active) {
        super(id);

        // Null safe
        this.creationDateTime = Optional.ofNullable(creationDateTime).orElse(new DateTime());
        this.submissionDateTime = Optional.ofNullable(submissionDateTime).orElse(new DateTime());
        this.live = Optional.ofNullable(live).orElse(false);
        this.demo = Optional.ofNullable(demo).orElse(false);
        this.preview = Optional.ofNullable(preview).orElse(false);
        this.blocked = Optional.ofNullable(blocked).orElse(false);
        this.cover = Optional.ofNullable(cover).orElse(false);
        this.data = Optional.ofNullable(data).orElse("");
        this.type = Optional.ofNullable(type).orElse(MediumType.UNKNOWN);
        this.sourceId = Optional.ofNullable(sourceId).orElse("");
        this.sourceUrl = Optional.ofNullable(sourceUrl).orElse("");
        this.artUrl = Optional.ofNullable(artUrl).orElse("");
        this.sourcePlaybackCount = Optional.ofNullable(sourcePlaybackCount).orElse(0);
        this.sourceCommentCount = Optional.ofNullable(sourceCommentCount).orElse(0);
        this.sourceFavouriteCount = Optional.ofNullable(sourceFavouriteCount).orElse(0);
        this.score = Optional.ofNullable(score).orElse(0.0);
        this.scoreFinal = Optional.ofNullable(scoreFinal).orElse(0.0);
        setActive(Optional.ofNullable(active).orElse(false));

        // Not null safe
        this.song = song;
    }

    /**
     * Sets submission date time.
     *
     * @param submissionDateTime the submission date time
     * @return the submission date time
     */
    public Medium setSubmissionDateTime(final DateTime submissionDateTime) {
        this.submissionDateTime = submissionDateTime;
        return this;
    }

    /**
     * Sets submission date time.
     *
     * @param submissionDateTime the submission date time
     * @return the submission date time
     */
    public Medium setSubmissionDateTime(final String submissionDateTime) {
        return setSubmissionDateTime(submissionDateTime, "dd/MM/yyyy HH:mm:ss");
    }

    /**
     * Sets submission date time.
     *
     * @param submissionDateTime the submission date time
     * @param pattern            the pattern
     * @return the submission date time
     */
    public Medium setSubmissionDateTime(final String submissionDateTime, final String pattern) {
        this.submissionDateTime = org.joda.time.format.DateTimeFormat
                .forPattern(pattern)
                .parseDateTime(submissionDateTime);
        return this;
    }

    @Override
    public String getUrl() {
        return String.format(
                "/Medium/%s/%s",
                this.getIdBase64(),
                this.getSong().map(Song::getName).map(StringEscapeUtils::escapeHtml4)
                        .orElse("unnamed")
                        .replace(" ", "_")
        );
    }

    /**
     * Gets song.
     *
     * @return the song
     */
    @JsonIgnore
    public Optional<Song> getSong() {
        return Optional.ofNullable(song);
    }

    @Override
    public String getDefiningName() {
        return getSong().map(Song::getName).orElse("").concat(" Media").trim();
    }
}
