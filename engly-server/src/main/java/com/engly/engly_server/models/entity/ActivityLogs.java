package com.engly.engly_server.models.entity;

import com.engly.engly_server.models.enums.Action;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "activity_logs")
public class ActivityLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "user_id")
    private Users user;

    @Enumerated(EnumType.STRING)
    private Action action;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    private Instant createdAt;
}
