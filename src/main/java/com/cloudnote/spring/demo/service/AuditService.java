package com.cloudnote.spring.demo.service;

import com.cloudnote.spring.demo.model.Note;

public interface AuditService {
    void logNoteCreation(String username, Note note);

    void logNoteUpdate(String username, Note note);

    void logNoteDeletion(String username, Long noteId);
}
