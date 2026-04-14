package com.sonora.sonora.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "play_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", nullable = false)
    private Track track;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime playedAt = LocalDateTime.now();

    private Integer durationListened; // en secondes
}