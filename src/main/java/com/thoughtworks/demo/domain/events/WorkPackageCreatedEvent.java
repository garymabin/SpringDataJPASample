package com.thoughtworks.demo.domain.events;

import com.thoughtworks.demo.domain.aggregates.IWorkPackageAggregate;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;

public class WorkPackageCreatedEvent extends ApplicationEvent implements IBaseDomainEvent {

    private IWorkPackageAggregate workPackageAggregate;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public WorkPackageCreatedEvent(Object source, IWorkPackageAggregate aggregate) {
        super(source);
        this.workPackageAggregate = aggregate;
    }

    @Override
    public Instant getCreatedAt() {
        return workPackageAggregate.getCreatedAt();
    }

    @Override
    public String getType() {
        return "WORK_PACKAGE_CREATED";
    }
}
