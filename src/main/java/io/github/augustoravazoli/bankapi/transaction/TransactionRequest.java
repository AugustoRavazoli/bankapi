package io.github.augustoravazoli.bankapi.transaction;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonInclude;

interface OnDepositOrWithdraw {}
interface OnTransfer {}

@JsonInclude(JsonInclude.Include.NON_NULL)
record TransactionRequest(

  @Positive
  @NotNull
  BigDecimal amount,

  @NotNull
  Long originAccountId,

  @NotNull(groups = OnTransfer.class)
  @Null(groups = OnDepositOrWithdraw.class)
  Long destinationAccountId

) {}
