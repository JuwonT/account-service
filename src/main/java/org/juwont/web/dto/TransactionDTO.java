package org.juwont.web.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;
import org.juwont.web.validation.DecimalPlaces;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record TransactionDTO(UUID accountId,
                             String recipient,
                             @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than to 0.0")
                             @DecimalPlaces
                             BigDecimal amount) {}
