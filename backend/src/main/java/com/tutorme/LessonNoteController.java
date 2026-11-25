package com.tutorme;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
class LessonNoteController {
    private final LessonNoteRepository notes;

    LessonNoteController(LessonNoteRepository notes) {
        this.notes = notes;
    }

    @PostMapping("/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public LessonNote create(@Valid @RequestBody CreateNoteRequest request) {
        return notes.save(new LessonNote(request.familyId(), request.title(), request.content()));
    }

    @GetMapping("/notes")
    public List<LessonNote> list() {
        return notes.findAllByOrderByIdAsc();
    }

    @Transactional
    @PutMapping("/notes/{id}/approve")
    public LessonNote approve(@PathVariable long id) {
        LessonNote note = notes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        note.approve();
        return note;
    }

    @GetMapping("/families/{familyId}/notes")
    public List<LessonNote> listForFamily(@PathVariable int familyId) {
        return notes.findByFamilyIdOrderByIdAsc(familyId);
    }

    record CreateNoteRequest(
            @NotNull @Min(1) @Max(2) Integer familyId,
            @NotBlank String title,
            @NotBlank String content) {
    }
}
