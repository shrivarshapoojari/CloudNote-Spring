package com.cloudnote.spring.demo.service.impl;




import com.cloudnote.spring.demo.Repository.NoteRepository;

import com.cloudnote.spring.demo.model.Note;
import com.cloudnote.spring.demo.service.AuditService;
import com.cloudnote.spring.demo.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteRepository noteRepository;


    @Autowired
    AuditService auditService;

    @Override
    public Note createNoteForUser(String username, String content) {
        Note note = new Note();
        note.setContent(content);
        note.setOwnerUsername(username);
        Note savedNote = noteRepository.save(note);
        auditService.logNoteCreation(username,note);
        return savedNote;
    }

    @Override
    public Note updateNoteForUser(Long noteId, String content, String username) {
        Note note = noteRepository.findById(noteId).orElseThrow(()
                -> new RuntimeException("Note not found"));
        note.setContent(content);
          auditService.logNoteUpdate(username,note);
        return noteRepository.save(note);
    }

    @Override
    public void deleteNoteForUser(Long noteId, String username) {
        Note note = noteRepository.findById(noteId).orElseThrow(
                () -> new RuntimeException("Note not found")
        );

        noteRepository.delete(note);
        auditService.logNoteDeletion(username,noteId);
    }

    @Override
    public List<Note> getNotesForUser(String username) {

        return noteRepository
                .findByOwnerUsername(username);
    }
}

