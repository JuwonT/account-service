package org.juwont.domain;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record Event(UUID aggregateId,
                    EventType type,
                    String data,
                    Instant timestamp,
                    Long version) {}
