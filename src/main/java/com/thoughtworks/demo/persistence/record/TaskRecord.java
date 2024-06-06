package com.thoughtworks.demo.persistence.record;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.NaturalId;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "task")
@Data
@Builder
public class TaskRecord {

    @Tolerate
    TaskRecord() {
    }

    @GeneratedValue
    @Id
    @EqualsAndHashCode.Exclude
    private Long id;

    @Column
    @NaturalId
    private String name;

    @ManyToMany(mappedBy = "tasks")
    @EqualsAndHashCode.Exclude
    private Set<WorkPackageRecord> workPackages = new HashSet<>();
}
