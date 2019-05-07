package com.thoughtworks.demo.persistence.repository;

import com.google.common.collect.Lists;
import com.thoughtworks.demo.persistence.record.AircraftRecord;
import com.thoughtworks.demo.persistence.record.QWorkPackageRecord;
import com.thoughtworks.demo.persistence.record.WorkPackageRecord;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.Collections;
import java.util.stream.StreamSupport;

import static com.thoughtworks.demo.persistence.repository.specifications.WorkPackageSpecifications.workPackageIncludesText;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureEmbeddedDatabase(beanName = "dataSource")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class QueryTest {

    @Autowired
    private WorkPackageRepository workPackageRepository;

    @BeforeEach
    public void setUp() {
        WorkPackageRecord wp1 = WorkPackageRecord.builder()
                .name("WP1")
                .build();

        WorkPackageRecord wp2 = WorkPackageRecord.builder()
                .name("WP2")
                .build();

        workPackageRepository.save(wp1);
        workPackageRepository.save(wp2);
        workPackageRepository.flush();
    }

    @Test
    public void testDerrivedQuery() {
        assertNotNull(workPackageRepository.findOneByName("WP1").get());
    }

    @Test
    public void testQueryByExample() {
        assertEquals(2, workPackageRepository.findAll(Example.of(WorkPackageRecord.builder().name("WP").build(),
                ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING))).size());
    }

    @Test
    public void testQueryBySpecification() {
        assertEquals(2, workPackageRepository.findAll(workPackageIncludesText("WP")).size());
    }

    @Test
    public void testQueryDSLJPAExtension() {
        assertEquals(2, StreamSupport.stream(workPackageRepository.findAllNameIncludes("WP").spliterator(), false).count());
    }
}
