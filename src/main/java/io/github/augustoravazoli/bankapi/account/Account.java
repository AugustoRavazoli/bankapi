package io.github.augustoravazoli.bankapi.account;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import static jakarta.persistence.FetchType.LAZY;
import jakarta.persistence.GeneratedValue;
import static jakarta.persistence.GenerationType.IDENTITY;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import io.github.augustoravazoli.bankapi.Application.Default;
import io.github.augustoravazoli.bankapi.customer.Customer;

@Entity
public class Account {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String bank;

  @PositiveOrZero
  @Column(nullable = false)
  private BigDecimal balance;

  @Column(nullable = false)
  private LocalDate createdAt;

  @ManyToOne(fetch = LAZY, optional = false)
  @JoinColumn(name = "account_id", nullable = false)
  private Customer owner;

  public Account() {}

  public Account(String bankName, Customer owner) {
    bank = bankName;
    balance = BigDecimal.ZERO;
    createdAt = LocalDate.now();
    setOwner(owner);
  }

  @Default
  public Account(Long id, String bank, Customer owner) {
    this(bank, owner);
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public String getBank() {
    return bank;
  }

  protected void setBank(String bank) {
    this.bank = bank;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public LocalDate getCreatedAt() {
    return createdAt;
  }

  public Customer getOwner() {
    return owner;
  }

  protected void setOwner(Customer owner) {
    if (owner != null) {
      owner.addAccount(this);
    } else if (this.owner != null) {
      this.owner.removeAccount(this);
    }
    this.owner = owner;
  }

  public void deposit(@Positive BigDecimal amount) {
    balance = balance.add(amount);
  }

  public void withdraw(@Positive BigDecimal amount) {
    if (amount.compareTo(balance) <= 0) {
      balance = balance.subtract(amount);
    } else {
      throw new InsufficientBalanceException();
    }
  }

  public void transfer(@Positive BigDecimal amount, Account destination) {
    if (this.id != destination.id) {
      this.withdraw(amount);
      destination.deposit(amount);
    } else {
      throw new SelfTransferException();
    }
  }

}
