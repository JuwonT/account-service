package org.juwont.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.juwont.service.dto.AccountAggregate;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Value
@Builder
@AllArgsConstructor
public class CloseAccountEventData extends EventData {
    Instant timestamp;

    @Override
    protected void enrich(final AccountAggregate account) {
        account.setClosed(true);
        account.setClosedAt(timestamp);
    }
}
