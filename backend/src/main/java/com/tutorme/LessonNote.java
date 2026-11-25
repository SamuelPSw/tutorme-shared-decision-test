package com.tutorme;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class LessonNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int familyId;

    @Lob
    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean approved;

    @Column(nullable = false)
    private Instant createdAt;

    protected LessonNote() {
    }

    LessonNote(int familyId, String title, String content) {
        this.familyId = familyId;
        this.title = title;
        this.content = content;
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    void approve() {
        approved = true;
    }

    public Long getId() {
        return id;
    }

    public int getFamilyId() {
        return familyId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public boolean isApproved() {
        return approved;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
