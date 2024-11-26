package org.juwont.service.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InvalidFundAmountException extends RuntimeException {
    public InvalidFundAmountException() {
        super("Fund amount can not be more than what is due");
    }
}
