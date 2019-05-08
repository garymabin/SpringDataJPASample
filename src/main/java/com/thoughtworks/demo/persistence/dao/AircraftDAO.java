package com.thoughtworks.demo.persistence.dao;

import com.thoughtworks.demo.persistence.record.AircraftRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AircraftDAO extends JpaRepository<AircraftRecord, Long>, JpaSpecificationExecutor<AircraftRecord> {

    @Query("SELECT a FROM AircraftRecord a join fetch a.workPackages WHERE a.tailNumber = :tailNumber")
    Optional<AircraftRecord> findByTailNumberWithWorkPackages(@Param("tailNumber") String tailNumber);
}
