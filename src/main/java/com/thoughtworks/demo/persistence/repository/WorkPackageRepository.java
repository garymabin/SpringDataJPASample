package com.thoughtworks.demo.persistence.repository;

import com.thoughtworks.demo.persistence.record.QWorkPackageRecord;
import com.thoughtworks.demo.persistence.record.WorkPackageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkPackageRepository extends JpaRepository<WorkPackageRecord, String>, JpaSpecificationExecutor<WorkPackageRecord>, QuerydslPredicateExecutor<WorkPackageRecord>, WorkPackageRepositoryCustom {

    Optional<WorkPackageRecord> findOneByName(String name);

    default Iterable<WorkPackageRecord> findAllNameIncludes(String text) {
        return findAll(QWorkPackageRecord.workPackageRecord.name.contains(text));
    }
}
