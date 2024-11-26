package org.juwont.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.juwont.service.dto.AccountAggregate;

import java.time.Instant;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Value
@Builder
public class CreateAccountEventData extends EventData {
    UUID id;
    Instant timestamp;

    @Override
    protected void enrich(final AccountAggregate account) {
        account.setId(id);
        account.setCreatedAt(timestamp);
    }
}
