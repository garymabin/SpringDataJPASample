package com.thoughtworks.demo.domain.aggregates;

import com.thoughtworks.demo.domain.events.IBaseDomainEvent;

import java.time.Instant;

public interface IEventAggregate {

    String getType();

    Instant getCreatedAt();

}
