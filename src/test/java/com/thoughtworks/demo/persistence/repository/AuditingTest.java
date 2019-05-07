package com.thoughtworks.demo.persistence.repository;

import com.google.common.collect.Lists;
import com.thoughtworks.demo.persistence.record.AircraftRecord;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AutoConfigureEmbeddedDatabase(beanName = "dataSource")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class AuditingTest {

    @Autowired
    private AircraftRepository aircraftRepository;

    @Test
    public void testCreatedDateAuditingRules() {
        AircraftRecord aircraftRecord = AircraftRecord.builder()
                .tailNumber("NS701")
                .workPackages(Lists.newArrayList())
                .build();

        aircraftRepository.saveAndFlush(aircraftRecord);

        assertNotNull(aircraftRepository.findAll().get(0).getCreatedAt());
    }

    @Test
    public void testRequiredCustomizedAuditingRules() {
        AircraftRecord aircraftRecord = AircraftRecord.builder()
                .workPackages(Lists.newArrayList())
                .build();


        assertThrows(RuntimeException.class, () -> aircraftRepository.saveAndFlush(aircraftRecord));
    }
}
