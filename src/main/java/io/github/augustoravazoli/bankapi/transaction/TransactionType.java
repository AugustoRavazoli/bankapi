package io.github.augustoravazoli.bankapi.transaction;

import com.fasterxml.jackson.annotation.JsonValue;

enum TransactionType {

  TRANSFER,
  DEPOSIT,
  WITHDRAWAL;

  @JsonValue
  @Override
  public String toString() {
    return super.toString().toLowerCase();
  }

}
