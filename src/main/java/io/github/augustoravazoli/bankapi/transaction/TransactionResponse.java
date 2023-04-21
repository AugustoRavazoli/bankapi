package io.github.augustoravazoli.bankapi.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

record TransactionResponse(
  Long id,
  BigDecimal amount,
  TransactionType type,
  LocalDate date,
  Long originAccountId,
  Long destinationAccountId
) {}
