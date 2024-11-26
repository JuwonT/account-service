package org.juwont.repository.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(final UUID id) {
        super("Account %s was not found".formatted(id));
    }
}
