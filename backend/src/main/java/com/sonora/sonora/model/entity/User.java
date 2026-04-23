package com.sonora.sonora.model.entity;

import com.sonora.sonora.model.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** User entity storing authentication and ownership data. */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** Unique user identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique user email address. */
    @Column(nullable = false, unique = true)
    private String email;

    /** Password hash used for authentication. */
    @Column(nullable = false)
    private String passwordHash;

    /** User authorization role. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** Account creation timestamp. */
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /** Playlists owned by this user. */
        @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
        )
    @Builder.Default
    private List<Playlist> playlists = new ArrayList<>();

    /** Favorites saved by this user. */
        @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
        )
    @Builder.Default
    private List<Favorite> favorites = new ArrayList<>();
}
