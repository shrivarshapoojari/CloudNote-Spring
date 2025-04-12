package com.cloudnote.spring.demo.repository;



import com.cloudnote.spring.demo.Repository.NoteRepository;
import com.cloudnote.spring.demo.model.Note;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class NoteRepositoryTest {

    @Autowired
    private NoteRepository noteRepository;

    @Test
    @DisplayName("Save note and verify it was saved")
    public void testSaveNote() {
        Note note = new Note();
        note.setContent("My first note");
        note.setOwnerUsername("shrivarsha");

        Note savedNote = noteRepository.save(note);

        assertThat(savedNote.getId()).isNotNull();
        assertThat(savedNote.getContent()).isEqualTo("My first note");
    }

    @Test
    @DisplayName("Find note by ID")
    public void testFindById() {
        Note note = new Note();
        note.setContent("Find me");
        note.setOwnerUsername("shrivarsha");
        Note savedNote = noteRepository.save(note);

        Optional<Note> foundNote = noteRepository.findById(savedNote.getId());

        assertThat(foundNote).isPresent();
        assertThat(foundNote.get().getContent()).isEqualTo("Find me");
    }

    @Test
    @DisplayName("Find all notes")
    public void testFindAll() {
        Note note1 = new Note();
        note1.setContent("Note 1");
        note1.setOwnerUsername("user1");

        Note note2 = new Note();
        note2.setContent("Note 2");
        note2.setOwnerUsername("user2");

        noteRepository.save(note1);
        noteRepository.save(note2);

        List<Note> notes = noteRepository.findAll();

        assertThat(notes.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Delete note by ID")
    public void testDeleteById() {
        Note note = new Note();
        note.setContent("To be deleted");
        note.setOwnerUsername("shrivarsha");
        Note savedNote = noteRepository.save(note);

        noteRepository.deleteById(savedNote.getId());

        Optional<Note> deletedNote = noteRepository.findById(savedNote.getId());
        assertThat(deletedNote).isNotPresent();
    }

    @Test
    @DisplayName("Find notes by ownerUsername")
    public void testFindByOwnerUsername() {
        Note note = new Note();
        note.setContent("User-specific note");
        note.setOwnerUsername("shrivarsha");
        noteRepository.save(note);

        List<Note> notes = noteRepository.findByOwnerUsername("shrivarsha");

        assertThat(notes).isNotEmpty();
        assertThat(notes.get(0).getOwnerUsername()).isEqualTo("shrivarsha");
    }
}
