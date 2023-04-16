package io.github.augustoravazoli.bankapi.account;

import java.math.BigDecimal;
import java.time.LocalDate;

record AccountResponse(
  Long id,
  String bank,
  BigDecimal balance,
  LocalDate createdAt
) {}
