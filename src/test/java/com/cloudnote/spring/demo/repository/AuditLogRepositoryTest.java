package com.cloudnote.spring.demo.repository;

import com.cloudnote.spring.demo.Repository.AuditLogRepository;
import com.cloudnote.spring.demo.model.AuditLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class AuditLogRepositoryTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    private AuditLog createAuditLog(String action, String username, Long noteId, String noteContent) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setUsername(username);
        log.setNoteId(noteId);
        log.setNoteContent(noteContent);
        log.setTimestamp(LocalDateTime.now());
        return auditLogRepository.save(log);
    }

    @Test
    @DisplayName("Save and retrieve AuditLog by ID")
    void saveAndFindById() {
        AuditLog log = createAuditLog("CREATE", "user1", 1L, "Note A");
        Optional<AuditLog> found = auditLogRepository.findById(log.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("Find AuditLogs by noteId")
    void findByNoteId() {
        createAuditLog("CREATE", "user1", 100L, "Note 1");
        createAuditLog("UPDATE", "user2", 100L, "Note 2");
        createAuditLog("DELETE", "user3", 200L, "Note 3");

        List<AuditLog> logs = auditLogRepository.findByNoteId(100L);

        assertThat(logs).hasSize(2);
        assertThat(logs)
                .extracting("noteContent")
                .containsExactlyInAnyOrder("Note 1", "Note 2");
    }

    @Test
    @DisplayName("Find all audit logs")
    void findAllLogs() {
        createAuditLog("CREATE", "user1", 101L, "A");
        createAuditLog("DELETE", "user2", 102L, "B");

        List<AuditLog> logs = auditLogRepository.findAll();

        assertThat(logs).hasSize(2);
    }

    @Test
    @DisplayName("Delete AuditLog by ID")
    void deleteById() {
        AuditLog log = createAuditLog("UPDATE", "userX", 999L, "To be deleted");

        auditLogRepository.deleteById(log.getId());

        Optional<AuditLog> deleted = auditLogRepository.findById(log.getId());
        assertThat(deleted).isNotPresent();
    }

    @Test
    @DisplayName("Update AuditLog")
    void updateAuditLog() {
        AuditLog log = createAuditLog("CREATE", "user1", 1L, "Original Content");

        log.setAction("UPDATED");
        log.setNoteContent("New Content");
        auditLogRepository.save(log);

        Optional<AuditLog> updated = auditLogRepository.findById(log.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getAction()).isEqualTo("UPDATED");
        assertThat(updated.get().getNoteContent()).isEqualTo("New Content");
    }
}
