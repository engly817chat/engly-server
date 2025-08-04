package com.engly.engly_server.models.entity;

import com.engly.engly_server.models.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "users")
@ToString(exclude = {"refreshTokens", "rooms", "activityLogs", "moderations", "notifications", "messages"})
public class Users implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(name = "USER_NAME", unique = true, nullable = false)
    private String username;

    @Column(nullable = false, name = "EMAIL_ID", unique = true)
    private String email;

    @Column(nullable = false, name = "PASSWORD")
    private String password;

    @Column(nullable = false, name = "ROLES")
    private String roles;

    @Column(nullable = false)
    private Boolean emailVerified;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    private Instant lastLogin;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "provider_id")
    private String providerId;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private AdditionalInfo additionalInfo;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserSettings userSettings;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rooms> rooms;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ActivityLogs> activityLogs;

    @OneToMany(mappedBy = "moder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Moderation> moderations;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notifications> notifications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;
}
