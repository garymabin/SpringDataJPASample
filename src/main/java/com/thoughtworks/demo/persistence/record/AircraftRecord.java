package com.thoughtworks.demo.persistence.record;


import com.thoughtworks.demo.annotations.Required;
import com.thoughtworks.demo.domain.aggregates.IAircraftAggregate;
import com.thoughtworks.demo.domain.events.WorkPackageCreatedEvent;
import com.thoughtworks.demo.persistence.dao.AircraftDAO;
import com.thoughtworks.demo.persistence.dao.WorkPackageDAO;
import com.thoughtworks.demo.util.EventPublisher;
import com.thoughtworks.demo.util.RequiredFieldsValidator;
import com.thoughtworks.demo.util.SpringEntityListener;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.JoinFormula;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Table(name = "aircraft")
@Entity
@Data
@Builder
@EntityListeners(value = {AuditingEntityListener.class, RequiredFieldsValidator.class, SpringEntityListener.class})
public class AircraftRecord implements IAircraftAggregate {

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Transient
    @Autowired
    private AircraftDAO aircraftDAO;

    @SuppressWarnings("SpringJavaAutowiredMembersInspection")
    @Transient
    @Autowired
    private WorkPackageDAO workPackageDAO;

    @Tolerate
    AircraftRecord() {
    }

    @GeneratedValue
    @Id
    private Long id;

    @Column(name = "tail")
    @Required
    private String tailNumber;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(
            mappedBy = "aircraft",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @ToString.Exclude
    @Builder.Default
    private List<WorkPackageRecord> workPackages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinFormula(value = "(SELECT wp.id FROM work_package wp WHERE wp.aircraft_id = id ORDER BY wp.name LIMIT 1)")
    private WorkPackageRecord firstWorkPackage;

    @Override
    @Transactional
    public void addNewWorkPackage(String workPackageName, TaskRecord... tasks) {
        workPackages.add(WorkPackageRecord.builder().name(workPackageName).aircraft(this).tasks(Set.of(tasks)).build());
        aircraftDAO.saveAndFlush(this);
        EventPublisher.publish(new WorkPackageCreatedEvent("source", workPackageDAO.findOneByName(workPackageName).get()));
    }
}
