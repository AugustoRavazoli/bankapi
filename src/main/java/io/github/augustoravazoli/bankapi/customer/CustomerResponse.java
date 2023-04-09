package io.github.augustoravazoli.bankapi.customer;

import java.time.LocalDate;

record CustomerResponse (
  Long id,
  String name,
  String email,
  String cpf,
  LocalDate birthDate
) {}
