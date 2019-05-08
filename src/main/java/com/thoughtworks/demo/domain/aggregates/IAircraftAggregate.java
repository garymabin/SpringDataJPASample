package com.thoughtworks.demo.domain.aggregates;

import com.thoughtworks.demo.persistence.record.TaskRecord;

import java.util.List;

public interface IAircraftAggregate {
    Long getId();
    String getTailNumber();

    List<? extends IWorkPackageAggregate> getWorkPackages();

    void addNewWorkPackage(String workPackageName, TaskRecord... tasks);
}
