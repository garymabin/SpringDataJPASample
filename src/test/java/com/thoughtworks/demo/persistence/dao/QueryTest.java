package com.thoughtworks.demo.persistence.dao;

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

import java.util.stream.StreamSupport;

import static com.thoughtworks.demo.persistence.dao.specifications.WorkPackageSpecifications.workPackageIncludesText;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureEmbeddedDatabase(beanName = "dataSource")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class QueryTest {

    @Autowired
    private WorkPackageDAO workPackageDAO;

    @BeforeEach
    public void setUp() {
        WorkPackageRecord wp1 = WorkPackageRecord.builder()
                .name("WP1")
                .build();

        WorkPackageRecord wp2 = WorkPackageRecord.builder()
                .name("WP2")
                .build();

        workPackageDAO.save(wp1);
        workPackageDAO.save(wp2);
        workPackageDAO.flush();
    }

    @Test
    public void testDerrivedQuery() {
        assertNotNull(workPackageDAO.findOneByName("WP1").get());
    }

    @Test
    public void testQueryByExample() {
        assertEquals(2, workPackageDAO.findAll(Example.of(WorkPackageRecord.builder().name("WP").build(),
                ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING))).size());
    }

    @Test
    public void testQueryBySpecification() {
        assertEquals(2, workPackageDAO.findAll(workPackageIncludesText("WP")).size());
    }

    @Test
    public void testQueryDSLJPAExtension() {
        assertEquals(2, StreamSupport.stream(workPackageDAO.findAllNameIncludes("WP").spliterator(), false).count());
    }
}
