package com.thoughtworks.demo.persistence.record;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "work_package", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
@Data
@Builder
public class WorkPackageRecord {

    @Tolerate
    protected WorkPackageRecord() {
    }

    @GenericGenerator(name = "work_package_id_generator", strategy = "com.thoughtworks.demo.util.IDGenerator")
    @GeneratedValue(generator = "work_package_id_generator")
    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "aircraft_id")
    private AircraftRecord aircraft;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "workpackage_task",
            joinColumns = @JoinColumn(name = "workpackage_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id")
    )
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private Set<TaskRecord> tasks = new HashSet<>();
}
