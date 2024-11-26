package org.juwont.service.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountInArrearsException extends RuntimeException {
    public AccountInArrearsException(final UUID id, final BigDecimal amountDue) {
        super("Account %s is still due to pay %s".formatted(id, amountDue));
    }
}
