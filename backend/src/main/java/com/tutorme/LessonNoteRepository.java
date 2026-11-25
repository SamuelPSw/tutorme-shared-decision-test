package com.tutorme;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface LessonNoteRepository extends JpaRepository<LessonNote, Long> {
    List<LessonNote> findAllByOrderByIdAsc();

    List<LessonNote> findByFamilyIdOrderByIdAsc(int familyId);
}
