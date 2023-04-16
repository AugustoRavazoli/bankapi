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
import io.github.augustoravazoli.bankapi.customer.Customer;

@Entity
public class Account {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String bank;

  @Column(nullable = false)
  private BigDecimal balance;

  @Column(nullable = false)
  private LocalDate createdAt;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  private Customer owner;

  protected Account() {}

  protected Account(String bankName, Customer owner) {
    bank = bankName;
    balance = BigDecimal.ZERO;
    createdAt = LocalDate.now();
    setOwner(owner);
  }

  protected Account(long id, String bank, Customer owner) {
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
    }
    else if (this.owner != null) {
      this.owner.removeAccount(this);
    }
    this.owner = owner;
  }

}
