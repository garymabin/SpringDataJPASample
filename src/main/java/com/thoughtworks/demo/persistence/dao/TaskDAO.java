package com.thoughtworks.demo.persistence.dao;

import com.thoughtworks.demo.persistence.record.TaskRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDAO extends JpaRepository<TaskRecord, Long> {
    List<TaskRecord> findAllByNameIn(List<String> names);
}
