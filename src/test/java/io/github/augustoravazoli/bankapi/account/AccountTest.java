package io.github.augustoravazoli.bankapi.account;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

  private Account account;

  @BeforeEach
  void setUp() {
    account = new Account(1L, "", null);
  }

  @Test
  void whenDeposit_thenBalanceIncrease() {
    // given
    var amount = new BigDecimal(2000);
    // when
    account.deposit(amount);
    // then
    assertThat(account.getBalance()).isEqualTo(new BigDecimal(2000));
  }

  @Test
  void givenSufficientBalance_whenWithdraw_thenBalanceDecrease() {
    // given
    account.deposit(new BigDecimal(2000));
    // when
    account.withdraw(new BigDecimal(1000));
    // then
    assertThat(account.getBalance()).isEqualTo(new BigDecimal(1000));
  }

  @Test
  void givenInsufficientBalance_whenWithdraw_thenThrowsInsufficienteBalanceException() {
    assertThatThrownBy(() -> account.withdraw(new BigDecimal(1000)))
      .isInstanceOf(InsufficientBalanceException.class);
    assertThat(account.getBalance()).isEqualTo(BigDecimal.ZERO);
  }

  @Test
  void whenTransfer_thenBothAccountsBalanceChange() {
    // given
    var origin = account;
    var destination = new Account(2L, "", null);
    origin.deposit(new BigDecimal(2000));
    // when
    origin.transfer(new BigDecimal(1000), destination);
    // then
    assertThat(origin.getBalance()).isEqualTo(new BigDecimal(1000));
    assertThat(destination.getBalance()).isEqualTo(new BigDecimal(1000));
  }

  @Test
  void givenSameAccount_whenTransfer_thenThrowsSelfTransferException() {
    // given
    var origin = account;
    var destination = origin;
    // then
    assertThatThrownBy(() -> origin.transfer(new BigDecimal(1000), destination))
      .isInstanceOf(SelfTransferException.class);
    assertThat(origin.getBalance()).isEqualTo(BigDecimal.ZERO);
  }

}
