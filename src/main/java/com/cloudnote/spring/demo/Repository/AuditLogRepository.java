package com.cloudnote.spring.demo.Repository;

import com.cloudnote.spring.demo.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long> {
}
