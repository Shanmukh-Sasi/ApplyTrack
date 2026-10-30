package com.project1.applytrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "status_history")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class StatusHistory {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ApplicationStatus previousStatus;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ApplicationStatus newStatus;

        @CreationTimestamp
        @Column(nullable = false, updatable = false)
        private LocalDateTime changedAt;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "application_id", nullable = false)
        private JobApplication jobApplication;
    }

