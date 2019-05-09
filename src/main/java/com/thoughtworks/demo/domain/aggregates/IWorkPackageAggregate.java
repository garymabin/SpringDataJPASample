package com.thoughtworks.demo.domain.aggregates;

import com.thoughtworks.demo.persistence.record.TaskRecord;

import java.time.Instant;
import java.util.Set;

public interface IWorkPackageAggregate {
    String getId();
    String getName();
    Instant getCreatedAt();

    Long getAircraftId();

    Set<TaskRecord> getTasks();
}
