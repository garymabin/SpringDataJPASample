package com.thoughtworks.demo.persistence.repository;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.thoughtworks.demo.persistence.record.AircraftRecord;
import com.thoughtworks.demo.persistence.record.TaskRecord;
import com.thoughtworks.demo.persistence.record.WorkPackageRecord;
import com.thoughtworks.demo.util.TestAppender;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Objects;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureEmbeddedDatabase(beanName = "dataSource")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class AssociationTest {

    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private WorkPackageRepository workPackageRepository;

    @Autowired
    private EntityManager entityManager;

    private TestAppender appender;


    @BeforeEach
    public void setUp() {
        Logger root = (Logger) LoggerFactory.getLogger("root");
        appender = Objects.requireNonNull((TestAppender) root.getAppender("TEST"));
        appender.clear();
    }

    @Test
    public void testAircraftWorkPackageOneToManyBidirectional() {
        AircraftRecord aircraftRecord = AircraftRecord.builder()
                .tailNumber("NS701")
                .workPackages(Lists.newArrayList())
                .build();

        aircraftRecord.getWorkPackages().addAll(Lists.newArrayList(
                WorkPackageRecord.builder()
                        .name("WP1")
                        .aircraft(aircraftRecord)
                        .build(),
                WorkPackageRecord.builder()
                        .aircraft(aircraftRecord)
                        .name("WP2")
                        .build(),
                WorkPackageRecord.builder()
                        .aircraft(aircraftRecord)
                        .name("WP3")
                        .build()));

        aircraftRepository.saveAndFlush(aircraftRecord);

        assertEquals(5, appender.getEventList().size());

        //clear query cache
        resetHibernateCacheAndLogAppender();

        assertNotNull(aircraftRepository.findAll().get(0).getWorkPackages().iterator().next().getId());
        assertEquals(2, appender.getEventList().size());

        resetHibernateCacheAndLogAppender();

        assertNotNull(workPackageRepository.findAll().get(0).getAircraft().getId());
        assertEquals(1, appender.getEventList().size());

        //clear query cache
        resetHibernateCacheAndLogAppender();

        assertEquals("NS701", workPackageRepository.findAll().get(0).getAircraft().getTailNumber());
        assertEquals(2, appender.getEventList().size());

    }


    @Test
    public void testAircraftJoinFormulaOnLatestWorkPackage() {
        AircraftRecord aircraftRecord = AircraftRecord.builder()
                .tailNumber("NS701")
                .workPackages(Lists.newArrayList())
                .build();

        aircraftRecord.getWorkPackages().addAll(Lists.newArrayList(
                WorkPackageRecord.builder()
                        .name("WP1")
                        .aircraft(aircraftRecord)
                        .build(),
                WorkPackageRecord.builder()
                        .aircraft(aircraftRecord)
                        .name("WP2")
                        .build(),
                WorkPackageRecord.builder()
                        .aircraft(aircraftRecord)
                        .name("WP3")
                        .build()));

        aircraftRepository.saveAndFlush(aircraftRecord);

        //clear query cache
        resetHibernateCacheAndLogAppender();

        assertEquals("WP1", aircraftRepository.findAll().get(0).getFirstWorkPackage().getName());
        assertEquals(2, appender.getEventList().size());

    }

    @Test
    public void testWorkPackageTasksManyToMany() {
        TaskRecord task1 = TaskRecord.builder()
                .name("task1")
                .build();
        TaskRecord task2 = TaskRecord.builder()
                .name("task2")
                .build();

        WorkPackageRecord workPackage1 = WorkPackageRecord.builder()
                .name("WP1")
                .tasks(Sets.newHashSet(task1, task2))
                .build();
        WorkPackageRecord workPackage2 = WorkPackageRecord.builder()
                .name("WP2")
                .tasks(Sets.newHashSet(task2))
                .build();

        workPackageRepository.save(workPackage1);
        workPackageRepository.save(workPackage2);

        workPackageRepository.flush();

        assertEquals(9, appender.getEventList().size());

        WorkPackageRecord workPackageRecord = workPackageRepository.findAll().get(0);
        workPackageRecord.getTasks().remove(task1);

        appender.clear();

        workPackageRepository.saveAndFlush(workPackage1);

        assertEquals(1, appender.getEventList().size());
    }

    private void resetHibernateCacheAndLogAppender() {
        //clear query cache
        entityManager.clear();
        appender.clear();
    }
}