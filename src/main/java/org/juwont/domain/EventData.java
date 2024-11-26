package org.juwont.domain;

import org.juwont.service.dto.AccountAggregate;

public abstract class EventData {
    public void enrich(final AccountAggregate account, final Event event) {
        account.setVersion(event.version());
        enrich(account);
    }

    protected abstract void enrich(final AccountAggregate accountAggregate);
}
