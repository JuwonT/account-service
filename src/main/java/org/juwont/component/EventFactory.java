package org.juwont.component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.juwont.domain.CloseAccountEventData;
import org.juwont.domain.CreateAccountEventData;
import org.juwont.domain.Event;
import org.juwont.domain.FundBalanceEventData;
import org.juwont.domain.TransactionEventData;
import org.juwont.web.dto.FundBalanceDTO;
import org.juwont.web.dto.TransactionDTO;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

import static org.juwont.domain.EventType.CLOSE_ACCOUNT;
import static org.juwont.domain.EventType.CREATE_ACCOUNT;
import static org.juwont.domain.EventType.FUND_ACCOUNT;
import static org.juwont.domain.EventType.TRANSACTION;

@Component
@RequiredArgsConstructor
public class EventFactory {

    private final ObjectMapper mapper;

    public Event createAccountEvent(final UUID id) {
        final Instant now = Instant.now();
        final CreateAccountEventData eventData = CreateAccountEventData.builder()
                .id(id)
                .timestamp(now)
                .build();
        return Event.builder()
                .aggregateId(id)
                .type(CREATE_ACCOUNT)
                .version(1L)
                .data(formatData(eventData))
                .timestamp(now)
                .build();
    }

    public Event createTransactionEvent(final long version, final TransactionDTO transaction) {
        final TransactionEventData eventData = TransactionEventData.builder()
                .recipient(transaction.recipient())
                .amount(transaction.amount().setScale(2, RoundingMode.HALF_EVEN))
                .timestamp(Instant.now())
                .build();

        return Event.builder()
                .aggregateId(transaction.accountId())
                .type(TRANSACTION)
                .version(version)
                .data(formatData(eventData))
                .timestamp(Instant.now())
                .build();
    }

    public Event createfundBalanceEvent(final long version, final FundBalanceDTO fundBalance) {
        final FundBalanceEventData eventData = FundBalanceEventData.builder()
                .amount(fundBalance.amount())
                .timestamp(Instant.now())
                .build();

        return Event.builder()
                .aggregateId(fundBalance.accountId())
                .type(FUND_ACCOUNT)
                .version(version)
                .data(formatData(eventData))
                .timestamp(Instant.now())
                .build();
    }

    public Event closeAccountEvent(final long version, final UUID id) {
        final Instant now = Instant.now();
        final CloseAccountEventData eventData = new CloseAccountEventData(now);
        return Event.builder()
                .aggregateId(id)
                .type(CLOSE_ACCOUNT)
                .version(version)
                .data(formatData(eventData))
                .timestamp(now)
                .build();
    }

    @SneakyThrows
    private <T> String formatData(final T eventData) {
        return mapper.writeValueAsString(eventData);
    }
}
