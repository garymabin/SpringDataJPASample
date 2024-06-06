package com.thoughtworks.demo.persistence.repository;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.thoughtworks.demo.persistence.record.AircraftRecord;
import com.thoughtworks.demo.persistence.record.TaskRecord;
import com.thoughtworks.demo.persistence.record.WorkPackageRecord;
import com.thoughtworks.demo.util.TestAppender;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityManager;
import junit.framework.TestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;


import java.util.Objects;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureEmbeddedDatabase(beanName = "dataSource", provider = ZONKY, type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
@SpringBootTest
@Transactional
public class FetchTest {

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

        aircraftRepository.saveAndFlush(aircraftRecord);

        //clear query cache
        entityManager.clear();
        appender.clear();

        assertTrue(aircraftRepository.findByTailNumberWithWorkPackages("NS701").get().getWorkPackages().size() > 0);

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

        aircraftRepository.saveAndFlush(aircraftRecord);

        //clear query cache
        entityManager.clear();
        appender.clear();

        assertTrue(aircraftRepository.findOne(
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

        workPackageRepository.save(workPackage1);
        workPackageRepository.save(workPackage2);

        workPackageRepository.flush();


        //clear query cache
        entityManager.clear();
        appender.clear();

        assertTrue(workPackageRepository.findAllNameIncludesQueryDSL("WP1").iterator().next().getTasks().size() > 0);

        assertEquals(2, appender.getEventList().size());
    }
}
