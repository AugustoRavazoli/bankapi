package io.github.augustoravazoli.bankapi.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
record TransactionResponse(
  Long id,
  BigDecimal amount,
  TransactionType type,
  LocalDate date,
  Long originAccountId,
  Long destinationAccountId
) {}
