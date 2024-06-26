package com.thoughtworks.demo.persistence.repository;

import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.thoughtworks.demo.persistence.record.*;
import com.thoughtworks.demo.util.TestAppender;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityManager;
import org.jetbrains.annotations.NotNull;
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
    private AircraftRecord aircraftRecord;


    @BeforeEach
    public void setUp() {
        Logger root = (Logger) LoggerFactory.getLogger("root");
        appender = Objects.requireNonNull((TestAppender) root.getAppender("TEST"));
        appender.clear();
        this.aircraftRecord = createAircraftRecord();
    }

    @Test
    public void testAircraftWorkPackageCascadingUpdateAttached() {
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
        var toBeSavedAircraft = aircraftRepository.findAll().get(0);

        //clear query cache
        resetHibernateCacheAndLogAppender();

        toBeSavedAircraft.setTailNumber("NS801");
        toBeSavedAircraft.getWorkPackages().get(0).setName("WP4");
        aircraftRepository.saveAndFlush(toBeSavedAircraft);

        assertEquals(2, appender.getEventList().stream().filter(event -> event.getFormattedMessage().contains("update")).toList().size());
    }

    @Test
    public void testAircraftWorkPackageCascadingUpdateDetached() {
        AircraftRecord aircraftRecord2 = AircraftRecord.builder()
                .tailNumber("NS701")
                .version(aircraftRecord.getVersion())
                .id(aircraftRecord.getId())
                .build();

        aircraftRecord2.getWorkPackages().addAll(Lists.newArrayList(
                WorkPackageRecord.builder()
                        .id(aircraftRecord.getWorkPackages().get(0).getId())
                        .name("WP1")
                        .aircraft(aircraftRecord2)
                        .workPackageSchedule(WorkPackageScheduleNoEquals.builder()
                                .scheduleDate("2024-12-29")
                                .repeatTimes(1)
                                .repeatInterval(30)
                                .location(Address.builder().countryCode("US").build()).build())
                        .version(aircraftRecord.getWorkPackages().get(0).getVersion())
                        .build(),
                WorkPackageRecord.builder()
                        .id(aircraftRecord.getWorkPackages().get(1).getId())
                        .aircraft(aircraftRecord2)
                        .workPackageSchedule(WorkPackageScheduleNoEquals.builder()
                                .scheduleDate("2024-12-30")
                                .repeatTimes(1)
                                .repeatInterval(30)
                                .location(Address.builder().countryCode("US").build()).build())
                        .name("WP2")
                        .version(aircraftRecord.getWorkPackages().get(1).getVersion())
                        .build(),
                WorkPackageRecord.builder()
                        .id(aircraftRecord.getWorkPackages().get(2).getId())
                        .aircraft(aircraftRecord2)
                        .workPackageSchedule(WorkPackageScheduleNoEquals.builder()
                                .scheduleDate("2024-12-31")
                                .repeatTimes(1)
                                .repeatInterval(30)
                                .location(Address.builder().countryCode("US").build()).build())
                        .version(aircraftRecord.getWorkPackages().get(2).getVersion())
                        .name("WP3")
                        .build()));

        //clear query cache
        resetHibernateCacheAndLogAppender();

        aircraftRepository.saveAndFlush(aircraftRecord2);

        assertEquals(2, appender.getEventList().stream().filter(event -> event.getFormattedMessage().contains("update")).toList().size());
    }

    private @NotNull AircraftRecord createAircraftRecord() {
        Address address = Address.builder().countryCode("US").build();
        AircraftRecord aircraftRecord = AircraftRecord.builder()
                .tailNumber("NS701")
                .workPackages(Lists.newArrayList())
                .build();

        aircraftRecord.getWorkPackages().addAll(Lists.newArrayList(
                WorkPackageRecord.builder()
                        .name("WP1")
                        .workPackageSchedule(WorkPackageScheduleNoEquals.builder().scheduleDate("2024-12-30").repeatTimes(1).repeatInterval(30).location(address).build())
                        .aircraft(aircraftRecord)
                        .build(),
                WorkPackageRecord.builder()
                        .aircraft(aircraftRecord)
                        .workPackageSchedule(WorkPackageScheduleNoEquals.builder().scheduleDate("2024-12-30").repeatTimes(1).repeatInterval(30).location(address).build())
                        .name("WP2")
                        .build(),
                WorkPackageRecord.builder()
                        .aircraft(aircraftRecord)
                        .workPackageSchedule(WorkPackageScheduleNoEquals.builder().scheduleDate("2024-12-30").repeatTimes(1).repeatInterval(30).location(address).build())
                        .name("WP3")
                        .build()));

        aircraftRepository.saveAndFlush(aircraftRecord);
        return aircraftRecord;
    }

    private void resetHibernateCacheAndLogAppender() {
        //clear query cache
        appender.clear();
    }

}