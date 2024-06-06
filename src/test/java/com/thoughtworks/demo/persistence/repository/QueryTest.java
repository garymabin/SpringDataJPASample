package com.thoughtworks.demo.persistence.repository;

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

import java.util.stream.StreamSupport;

import static com.thoughtworks.demo.persistence.repository.specifications.WorkPackageSpecifications.workPackageIncludesText;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureEmbeddedDatabase(beanName = "dataSource", provider = ZONKY, type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
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
