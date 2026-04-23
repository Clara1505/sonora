package com.sonora.sonora.model.entity;

import com.sonora.sonora.model.enums.Genre;
import com.sonora.sonora.model.enums.Mood;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Track entity representing a playable audio resource. */
@Entity
@Table(name = "tracks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Track {

    /** Unique track identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Track title. */
    @Column(nullable = false)
    private String title;

    /** Track duration in seconds. */
    private Integer duration;

    /** Assigned genre for recommendations and filtering. */
    @Enumerated(EnumType.STRING)
    private Genre genre;

    /** Assigned mood for recommendations and filtering. */
    @Enumerated(EnumType.STRING)
    private Mood mood;

    /** Storage path to the audio file. */
    private String filePath;

    /** Album containing this track, if present. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    /** Artist that published the track. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    /** Playlists that contain this track. */
    @ManyToMany(mappedBy = "tracks")
    @Builder.Default
    private List<Playlist> playlists = new ArrayList<>();
}
