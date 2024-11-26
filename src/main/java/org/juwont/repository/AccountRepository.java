package org.juwont.repository;

import lombok.RequiredArgsConstructor;
import org.juwont.domain.Event;
import org.juwont.repository.exception.AccountNotFoundException;
import org.juwont.service.dto.AccountAggregate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AccountRepository {
    private final EventStore store;

    public AccountAggregate findAccount(final UUID id) {
        final List<Event> events = getEvents(id);
        final AccountAggregate accountAggregate = new AccountAggregate();
        events.forEach(accountAggregate::apply);

        if (accountAggregate.getId() == null) throw new AccountNotFoundException(id);

        return accountAggregate;
    }

    @Transactional
    public void insertEvent(final Event event) {
        store.insertEvent(event);
    }

    private List<Event> getEvents(final UUID id) {
        return store.getEvents(id);
    }
}
