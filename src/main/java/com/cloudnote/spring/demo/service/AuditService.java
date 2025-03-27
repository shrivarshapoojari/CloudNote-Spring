package com.cloudnote.spring.demo.service;

import com.cloudnote.spring.demo.model.AuditLog;
import com.cloudnote.spring.demo.model.Note;

import java.util.List;

public interface AuditService {
    void logNoteCreation(String username, Note note);

    void logNoteUpdate(String username, Note note);

    void logNoteDeletion(String username, Long noteId);

    List<AuditLog> getAllAuditLogs();

    List<AuditLog> getAuditLogsforNotes(Long noteId);
}
