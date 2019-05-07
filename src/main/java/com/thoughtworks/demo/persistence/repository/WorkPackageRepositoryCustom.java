package com.thoughtworks.demo.persistence.repository;

import com.thoughtworks.demo.persistence.record.WorkPackageRecord;

public interface WorkPackageRepositoryCustom {
    Iterable<WorkPackageRecord> findAllNameIncludesQueryDSL(String text);
}
