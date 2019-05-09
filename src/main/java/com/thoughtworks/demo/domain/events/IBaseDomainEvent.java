package com.thoughtworks.demo.domain.events;

import java.time.Instant;

public interface IBaseDomainEvent {

    Instant getCreatedAt();

    String getType();
}
