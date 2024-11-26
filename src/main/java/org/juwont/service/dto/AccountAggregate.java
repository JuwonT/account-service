package org.juwont.service.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.SneakyThrows;
import org.juwont.domain.Event;
import org.juwont.domain.EventData;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class AccountAggregate {
    private UUID id;
    private BigDecimal amountDue;
    private Instant createdAt;
    private Instant closedAt;
    private Instant lastFunded;
    private boolean isClosed;
    private long version;
    private final List<Transaction> transactions;

    public AccountAggregate() {
        this.id = null;
        this.amountDue = BigDecimal.ZERO;
        this.transactions = new ArrayList<>();
        this.isClosed = false;
        this.version = 0;
    }

    public void apply(Event event) {
        if (this.isClosed) return;
        getEventData(event).enrich(this, event);
    }

    @SneakyThrows
    private EventData getEventData(final Event event) {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readValue(cleanseData(event.data()), event.type().getEventClazz());
    }

    private String cleanseData(final String data) {
        if (data.startsWith("\"") && data.endsWith("\"")) {
            return data.substring(1, data.length() - 1).replaceAll("\\\\", "");
        }

        return data;
    }
}
