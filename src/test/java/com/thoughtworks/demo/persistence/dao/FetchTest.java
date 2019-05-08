package com.thoughtworks.demo.persistence.dao;

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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureEmbeddedDatabase(beanName = "dataSource")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class FetchTest {

    @Autowired
    private AircraftDAO aircraftDAO;

    @Autowired
    private WorkPackageDAO workPackageDAO;

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
    public void testEagerFetchingJPQL() {
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

        aircraftDAO.saveAndFlush(aircraftRecord);

        //clear query cache
        entityManager.clear();
        appender.clear();

        assertTrue(aircraftDAO.findByTailNumberWithWorkPackages("NS701").get().getWorkPackages().size() > 0);

        assertEquals(1, appender.getEventList().size());
    }

    @Test
    public void testEagerFetchingQBS() {
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

        aircraftDAO.saveAndFlush(aircraftRecord);

        //clear query cache
        entityManager.clear();
        appender.clear();

        assertTrue(aircraftDAO.findOne(
                (Specification<AircraftRecord>) (root, query, cb) -> {
                    root.fetch("workPackages");
                    return cb.equal(root.get("tailNumber"), "NS701");
                }).get().getWorkPackages().size() > 0);

        assertEquals(1, appender.getEventList().size());
    }

    @Test
    public void testEagerFetchingQueryDSL() {
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

        workPackageDAO.save(workPackage1);
        workPackageDAO.save(workPackage2);

        workPackageDAO.flush();


        //clear query cache
        entityManager.clear();
        appender.clear();

        assertTrue(workPackageDAO.findAllNameIncludesQueryDSL("WP1").iterator().next().getTasks().size() > 0);

        assertEquals(2, appender.getEventList().size());
    }
}
