package com.engly.engly_server.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "statistics")
public class Statistics implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "room_id")
    private Rooms room;

    @Column(nullable = false, name = "message_count")
    private Long messageCount;

    @Column(nullable = false)
    private LocalDateTime lastMessageTime;
}
