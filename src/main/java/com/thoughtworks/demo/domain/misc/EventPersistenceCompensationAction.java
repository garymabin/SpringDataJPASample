package com.thoughtworks.demo.domain.misc;

import com.thoughtworks.demo.persistence.dao.EventDAO;
import com.thoughtworks.demo.persistence.dao.WorkPackageDAO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


//TODO: Move this to a separate application.
@Service
public class EventPersistenceCompensationAction {

    private final EventDAO eventDAO;

    private final WorkPackageDAO workPackageDAO;

    public EventPersistenceCompensationAction(EventDAO eventDAO, WorkPackageDAO workPackageDAO) {
        this.eventDAO = eventDAO;
        this.workPackageDAO = workPackageDAO;
    }

    @Scheduled(fixedRate = 360000)
    public void fixWorkPackageCreationEvents() {
        //fix records.
    }
}
