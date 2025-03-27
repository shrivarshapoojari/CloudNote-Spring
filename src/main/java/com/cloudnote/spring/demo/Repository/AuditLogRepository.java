package com.cloudnote.spring.demo.Repository;

import com.cloudnote.spring.demo.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {
    List<AuditLog> findByNoteId(Long noteId);
}
