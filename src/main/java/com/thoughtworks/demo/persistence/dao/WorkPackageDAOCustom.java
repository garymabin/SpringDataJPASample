package com.thoughtworks.demo.persistence.dao;

import com.thoughtworks.demo.persistence.record.WorkPackageRecord;

public interface WorkPackageDAOCustom {
    Iterable<WorkPackageRecord> findAllNameIncludesQueryDSL(String text);
}
