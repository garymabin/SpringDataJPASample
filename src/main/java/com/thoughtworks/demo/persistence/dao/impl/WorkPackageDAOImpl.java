package com.thoughtworks.demo.persistence.dao.impl;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.thoughtworks.demo.persistence.record.QWorkPackageRecord;
import com.thoughtworks.demo.persistence.record.WorkPackageRecord;
import com.thoughtworks.demo.persistence.dao.WorkPackageDAOCustom;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class WorkPackageDAOImpl implements WorkPackageDAOCustom {

    @PersistenceContext
    private EntityManager entityMananger;

    public Iterable<WorkPackageRecord> findAllNameIncludesQueryDSL(String text) {
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityMananger);
        QWorkPackageRecord workPackageRecord = QWorkPackageRecord.workPackageRecord;

        return jpaQueryFactory
                .selectFrom(workPackageRecord)
                .where(workPackageRecord.name.like(text))
                .leftJoin(workPackageRecord.tasks)
                .fetch();
    }}
