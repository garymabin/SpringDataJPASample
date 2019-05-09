package com.thoughtworks.demo.persistence.dao;

import com.thoughtworks.demo.persistence.record.EventAuditRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDAO extends JpaRepository<EventAuditRecord, Long> {
}
