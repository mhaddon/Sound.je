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

package com.nestedbird.models.artist;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.nestedbird.components.bridges.ArtistBridge;
import com.nestedbird.components.bridges.EventBridge;
import com.nestedbird.components.bridges.SongBridge;
import com.nestedbird.models.core.Tagged.TaggedEntity;
import com.nestedbird.models.event.Event;
import com.nestedbird.models.song.Song;
import com.nestedbird.models.tag.Tag;
import com.nestedbird.modules.entitysearch.SearchAnalysers;
import com.nestedbird.modules.schema.Schema;
import com.nestedbird.modules.schema.annotations.SchemaRepository;
import com.nestedbird.modules.schema.annotations.SchemaView;
import lombok.*;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The type Artist.
 */
@Entity
@Table(name = "artists")
@Cacheable
@Indexed
@SchemaRepository(ArtistRepository.class)
@Boost(2.0f)
@AnalyzerDiscriminator(impl = ArtistBridge.class)
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"events", "songs"})
@NoArgsConstructor(force = true)
public class Artist extends TaggedEntity implements Serializable {

    /**
     * All events the artist is participating in
     */
    @ManyToMany(mappedBy = "artists")
    @ContainedIn
    @JsonBackReference(value = "eventArtistParent")
    @Field(bridge = @FieldBridge(impl = EventBridge.class),
            analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    private Set<Event> events = new HashSet<>(0);

    /**
     * All the songs the artist owns
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "artist")
    @JsonBackReference(value = "songToArtist")
    @ContainedIn
    @Field(bridge = @FieldBridge(impl = SongBridge.class),
            analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    private Set<Song> songs = new HashSet<>(0);

    /**
     * The name of the artist
     */
    @Column(name = "name", nullable = false, length = 50)
    @Field(boost = @Boost(2f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @NotNull
    @Size(min = 2, max = 50)
    @SchemaView
    private String name;

    /**
     * the description about the artist
     */
    @Column(name = "description", columnDefinition = "LONGTEXT")
    @Lob
    @Field(boost = @Boost(0.5f), analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @SchemaView(Schema.MARKDOWN)
    private String description;

    /**
     * the facebook id of the artist
     */
    @Column(name = "facebook_id", length = 20)
    @Field
    @SchemaView(Schema.FACEBOOKID)
    private String facebookId;

    /**
     * the artists website
     */
    @Column(name = "website_url", length = 100)
    @Field(analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 100)
    @SchemaView(Schema.URL)
    private String websiteUrl;

    /**
     * the artists youtube account url
     */
    @Column(name = "youtube_url", length = 100)
    @Field(analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 100)
    @SchemaView(Schema.URL)
    private String youtubeUrl;

    /**
     * the artists tumblr account url
     */
    @Column(name = "tumblr_url", length = 100)
    @Field(analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 100)
    @SchemaView(Schema.URL)
    private String tumblrUrl;

    /**
     * the artists twitter account url
     */
    @Column(name = "twitter_url", length = 100)
    @Field(analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 100)
    @SchemaView(Schema.URL)
    private String twitterUrl;

    /**
     * the artists soundcloud account url
     */
    @Column(name = "soundcloud_url", length = 100)
    @Field(analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 100)
    @SchemaView(Schema.URL)
    private String soundcloudUrl;

    /**
     * An image associated to the artist
     */
    @Column(name = "image_url", length = 250)
    @Field(analyzer = @Analyzer(definition = SearchAnalysers.ENGLISH_WORD_ANALYSER))
    @Size(max = 250)
    @SchemaView(Schema.URL)
    private String imageUrl;

    @Builder
    private Artist(final String id,
                   final Set<Event> events,
                   final Set<Song> songs,
                   final String name,
                   final String description,
                   final String facebookId,
                   final String websiteUrl,
                   final String youtubeUrl,
                   final String tumblrUrl,
                   final String twitterUrl,
                   final String soundcloudUrl,
                   final String imageUrl,
                   final Boolean active,
                   final Set<Tag> tags) {
        super(id);

        // Null Safe
        this.events = Optional.ofNullable(events).orElse(new HashSet<>());
        this.songs = Optional.ofNullable(songs).orElse(new HashSet<>());
        this.name = Optional.ofNullable(name).orElse("");
        this.description = Optional.ofNullable(description).orElse("");
        this.websiteUrl = Optional.ofNullable(websiteUrl).orElse("");
        this.youtubeUrl = Optional.ofNullable(youtubeUrl).orElse("");
        this.tumblrUrl = Optional.ofNullable(tumblrUrl).orElse("");
        this.twitterUrl = Optional.ofNullable(twitterUrl).orElse("");
        this.soundcloudUrl = Optional.ofNullable(soundcloudUrl).orElse("");
        this.imageUrl = Optional.ofNullable(imageUrl).orElse("");
        setActive(Optional.ofNullable(active).orElse(false));
        setTags(Optional.ofNullable(tags).orElse(new HashSet<>()));

        // Not null safe
        this.facebookId = facebookId;
    }

    @Override
    public String getUrl() {
        return String.format(
                "/Artists/%s/%s",
                this.getIdBase64(),
                this.getName().replace(" ", "_")
        );
    }

    @Override
    public String getDefiningName() {
        return name;
    }
}
