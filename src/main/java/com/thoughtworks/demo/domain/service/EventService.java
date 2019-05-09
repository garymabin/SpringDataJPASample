package com.thoughtworks.demo.domain.service;

import com.thoughtworks.demo.domain.events.IBaseDomainEvent;
import com.thoughtworks.demo.persistence.dao.EventDAO;
import com.thoughtworks.demo.persistence.record.EventAuditRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class EventService {


    @Autowired
    private EventDAO eventDAO;

    @Transactional
    public void logEvent(IBaseDomainEvent event) {
        eventDAO.saveAndFlush(EventAuditRecord.builder().createdAt(event.getCreatedAt()).type(event.getType()).build());
    }

}
