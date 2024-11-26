package org.juwont.service.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(String recipient, BigDecimal amount, Instant timestamp) {
}
