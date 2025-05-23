package com.engly.engly_server.models.entity;

import com.engly.engly_server.models.enums.EnglishLevels;
import com.engly.engly_server.models.enums.Goals;
import com.engly.engly_server.models.enums.NativeLanguage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "additional_info")
public class AdditionalInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    private EnglishLevels englishLevel;

    @Enumerated(EnumType.STRING)
    private NativeLanguage nativeLanguage;

    @Enumerated(EnumType.STRING)
    private Goals goal;

    @OneToOne(mappedBy = "additionalInfo")
    private Users user;
}
