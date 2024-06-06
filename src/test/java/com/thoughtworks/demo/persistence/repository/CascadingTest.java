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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureEmbeddedDatabase(beanName = "dataSource", provider = ZONKY, type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
@SpringBootTest
@Transactional
public class CascadingTest {

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
    public void testAircraftWorkPackageCascadingUpdate() {
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

}