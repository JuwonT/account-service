package org.juwont.domain;

import lombok.Getter;

@Getter
public enum EventType {
    CREATE_ACCOUNT(CreateAccountEventData.class),
    TRANSACTION(TransactionEventData.class),
    FUND_ACCOUNT(FundBalanceEventData.class),
    CLOSE_ACCOUNT(CloseAccountEventData.class);

    private final Class<? extends EventData> eventClazz;

    EventType(final Class<? extends EventData> eventClazz) {
        this.eventClazz = eventClazz;
    }
}
