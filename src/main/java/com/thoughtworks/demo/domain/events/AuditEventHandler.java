package com.thoughtworks.demo.domain.events;

import com.thoughtworks.demo.domain.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class AuditEventHandler {

    @Autowired
    private EventService eventService;


    @Async
    @EventListener
    @Transactional
    public void handleWorkPackageCreationEvent(WorkPackageCreatedEvent event) {
        eventService.logEvent(event);
    }
}
