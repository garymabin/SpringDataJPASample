package com.thoughtworks.demo.persistence.repository.specifications;


import com.thoughtworks.demo.persistence.record.WorkPackageRecord;
import org.springframework.data.jpa.domain.Specification;

public class WorkPackageSpecifications {
    public static Specification<WorkPackageRecord> workPackageIncludesText(String text) {
        return (Specification<WorkPackageRecord>) (root, query, cb) -> cb.like(root.get("name"), "%" + text + "%");
    }
}
