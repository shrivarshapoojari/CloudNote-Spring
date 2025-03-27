package com.cloudnote.spring.demo.controller;

import com.cloudnote.spring.demo.model.AuditLog;
import com.cloudnote.spring.demo.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AuditLogController {

    @Autowired
    AuditService auditService;


    @GetMapping()
    public List<AuditLog> getAuditLogs()
    {
        return  auditService.getAllAuditLogs();
    }

    @GetMapping("/note/{id}")
    public List<AuditLog> getNoteAuditLogs(@PathVariable  Long id)
    {
          return  auditService.getAuditLogsforNotes(id);
    }
}
