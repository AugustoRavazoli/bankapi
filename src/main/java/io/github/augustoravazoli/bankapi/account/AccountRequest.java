package io.github.augustoravazoli.bankapi.account;

import jakarta.validation.constraints.NotNull;

record AccountRequest(

  @NotNull
  Integer bankCode

) {}
