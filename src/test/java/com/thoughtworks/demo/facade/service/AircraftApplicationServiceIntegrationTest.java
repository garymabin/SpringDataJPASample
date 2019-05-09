package com.thoughtworks.demo.facade.service;

import com.google.common.collect.Lists;
import com.thoughtworks.demo.persistence.dao.AircraftDAO;
import com.thoughtworks.demo.persistence.dao.EventDAO;
import com.thoughtworks.demo.persistence.dao.TaskDAO;
import com.thoughtworks.demo.persistence.record.AircraftRecord;
import com.thoughtworks.demo.persistence.record.TaskRecord;
import com.thoughtworks.demo.persistence.record.WorkPackageRecord;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

@AutoConfigureEmbeddedDatabase(beanName = "dataSource")
@SpringBootTest
@ExtendWith(SpringExtension.class)
class AircraftApplicationServiceIntegrationTest {

    @Autowired
    private AircraftDAO aircraftDAO;

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private AircraftApplicationService aircraftApplicationService;

    @Test
    void createNewWorkPackageForAircraft() {
        AircraftRecord aircraftRecord = AircraftRecord.builder()
                .tailNumber("NS701")
                .workPackages(Lists.newArrayList())
                .build();

        TaskRecord task1 = TaskRecord.builder()
                .name("task1")
                .build();
        TaskRecord task2 = TaskRecord.builder()
                .name("task2")
                .build();

        aircraftRecord.getWorkPackages().addAll(Lists.newArrayList(
                WorkPackageRecord.builder()
                        .name("WP1")
                        .aircraft(aircraftRecord)
                        .tasks(Set.of(task1, task2))
                        .build(),
                WorkPackageRecord.builder()
                        .aircraft(aircraftRecord)
                        .name("WP2")
                        .build(),
                WorkPackageRecord.builder()
                        .aircraft(aircraftRecord)
                        .name("WP3")
                        .tasks(Set.of(task1))
                        .build()));

        aircraftDAO.saveAndFlush(aircraftRecord);

        aircraftApplicationService.createNewWorkPackageForAircraft("NS701", "WP4", "task2");

        assertEquals(4, aircraftDAO.findByTailNumberWithWorkPackages("NS701").get().getWorkPackages().size());

        await().untilAsserted(() -> assertEquals(1, eventDAO.findAll().size()));
    }
}