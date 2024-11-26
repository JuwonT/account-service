package org.juwont.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.juwont.service.dto.AccountAggregate;
import org.juwont.service.dto.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Value
@Builder
public class TransactionEventData extends EventData {
    String recipient;
    BigDecimal amount;
    Instant timestamp;

    @Override
    protected void enrich(final AccountAggregate account) {
        account.getTransactions().add(new Transaction(recipient, amount, timestamp));

        final BigDecimal amountDue = account.getAmountDue().subtract(amount).setScale(2, RoundingMode.HALF_EVEN);
        account.setAmountDue(amountDue);
    }
}
