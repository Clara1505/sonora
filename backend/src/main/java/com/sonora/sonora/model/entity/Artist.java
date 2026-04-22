package com.sonora.sonora.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Artist entity containing artist profile data. */
@Entity
@Table(name = "artists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artist {

    /** Unique artist identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Display name of the artist. */
    @Column(nullable = false)
    private String name;

    /** Free-text artist biography. */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /** Profile image URL for the artist. */
    private String imageUrl;

    /** Albums created by the artist. */
        @OneToMany(
            mappedBy = "artist",
            cascade = CascadeType.ALL,
            orphanRemoval = true
        )
    @Builder.Default
    private List<Album> albums = new ArrayList<>();

    /** Tracks created by the artist. */
        @OneToMany(
            mappedBy = "artist",
            cascade = CascadeType.ALL,
            orphanRemoval = true
        )
    @Builder.Default
    private List<Track> tracks = new ArrayList<>();
}
