package com.thoughtworks.demo.persistence.record;


import com.thoughtworks.demo.annotations.Required;
import com.thoughtworks.demo.util.RequiredFieldsValidator;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.JoinFormula;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Table(name = "aircraft")
@Entity
@Data
@Builder
@EntityListeners(value = {AuditingEntityListener.class, RequiredFieldsValidator.class})
public class AircraftRecord {

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
    private List<WorkPackageRecord> workPackages = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinFormula(value = "(SELECT wp.id FROM work_package wp WHERE wp.aircraft_id = id ORDER BY wp.name LIMIT 1)")
    private WorkPackageRecord firstWorkPackage;
}
