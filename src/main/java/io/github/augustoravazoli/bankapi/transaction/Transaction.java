package io.github.augustoravazoli.bankapi.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import static jakarta.persistence.EnumType.STRING;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import static jakarta.persistence.GenerationType.IDENTITY;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Positive;
import io.github.augustoravazoli.bankapi.Application.Default;

@Entity
class Transaction {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Positive
  @Column(nullable = false)
  private BigDecimal amount;

  @Enumerated(STRING)
  @Column(nullable = false)
  private TransactionType type;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false)
  private Long originAccountId;

  private Long destinationAccountId;

  public Transaction() {}

  @Default
  public Transaction(
    Long id,
    BigDecimal amount,
    TransactionType type,
    Long originAccountId,
    Long destinationAccountId
  ) {
    this.id = id;
    this.amount = amount;
    this.type = type;
    this.date = LocalDate.now();
    this.originAccountId = originAccountId;
    this.destinationAccountId = destinationAccountId;
  }

  public Long getId() {
    return id;
  }

  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public LocalDate getDate() {
    return date;
  }

  public Long getOriginAccountId() {
    return originAccountId;
  }

  public Long getDestinationAccountId() {
    return destinationAccountId;
  }

}
