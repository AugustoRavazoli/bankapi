package io.github.augustoravazoli.bankapi.transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import io.github.augustoravazoli.bankapi.account.Account;
import io.github.augustoravazoli.bankapi.account.AccountRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private TransactionService transactionService;

  @Test
  void whenCreateDepositTransaction_thenReturnsCreatedTransaction() {
    // given
    var account = new Account(1L, "", null);
    var newTransaction = new Transaction(1L, BigDecimal.TEN, null, 1L, 1L);
    // and
    when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
    when(transactionRepository.save(any(Transaction.class))).then(returnsFirstArg());
    // when
    var savedTransaction = transactionService.createDepositTransaction(newTransaction);
    // then
    assertThat(savedTransaction)
      .usingRecursiveComparison()
      .ignoringFields("type")
      .isEqualTo(newTransaction);
    assertThat(account.getBalance()).isEqualTo(BigDecimal.TEN);
    assertThat(savedTransaction.getType()).isEqualTo(TransactionType.DEPOSIT);
  }

  @Test
  void givenNonexistentAccount_whenCreateDepositTransaction_thenThrowsInvalidAccountException() {
    // given
    var nonexistentAccount = Optional.<Account>empty();
    var newTransaction = new Transaction(1L, BigDecimal.TEN, null, 1L, 1L);
    // and
    when(accountRepository.findById(anyLong())).thenReturn(nonexistentAccount);
    // then
    assertThatThrownBy(() -> transactionService.createDepositTransaction(newTransaction))
      .isInstanceOf(InvalidAccountException.class);
    verify(transactionRepository, never()).save(any(Transaction.class));
  }

  @Test
  void whenCreateWithdrawalTransaction_thenReturnsCreatedTransaction() {
    // given
    var account = new Account(1L, "", null);
    var newTransaction = new Transaction(1L, BigDecimal.TEN, null, 1L, 1L);
    // and
    account.deposit(BigDecimal.TEN);
    when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
    when(transactionRepository.save(any(Transaction.class))).then(returnsFirstArg());
    // when
    var savedTransaction = transactionService.createWithdrawalTransaction(newTransaction);
    // then
    assertThat(savedTransaction)
      .usingRecursiveComparison()
      .ignoringFields("type")
      .isEqualTo(newTransaction);
    assertThat(account.getBalance()).isEqualTo(BigDecimal.ZERO);
    assertThat(savedTransaction.getType()).isEqualTo(TransactionType.WITHDRAWAL);
  }

  @Test
  void givenNonexistentAccount_whenCreateWithdrawalTransaction_thenThrowsInvalidAccountException() {
    // given
    var nonexistentAccount = Optional.<Account>empty();
    var newTransaction = new Transaction(1L, BigDecimal.TEN, null, 1L, 1L);
    // and
    when(accountRepository.findById(anyLong())).thenReturn(nonexistentAccount);
    // then
    assertThatThrownBy(() -> transactionService.createDepositTransaction(newTransaction))
      .isInstanceOf(InvalidAccountException.class);
    verify(transactionRepository, never()).save(any(Transaction.class));
  }

  @Test @SuppressWarnings("unchecked")
  void whenCreateTransferationTransaction_thenReturnsCreatedTransaction() {
    // given
    var origin = new Account(1L, "", null);
    var destination = new Account(2L, "", null);
    var newTransaction = new Transaction(1L, BigDecimal.TEN, null, 1L, 2L);
    // and
    origin.deposit(BigDecimal.TEN);
    when(accountRepository.findById(anyLong())).thenReturn(Optional.of(origin), Optional.of(destination));
    when(transactionRepository.save(any(Transaction.class))).then(returnsFirstArg());
    // when
    var savedTransaction = transactionService.createTransferationTransaction(newTransaction);
    // then
    assertThat(savedTransaction)
      .usingRecursiveComparison()
      .ignoringFields("type")
      .isEqualTo(newTransaction);
    assertThat(origin.getBalance()).isEqualTo(BigDecimal.ZERO);
    assertThat(destination.getBalance()).isEqualTo(BigDecimal.TEN);
    assertThat(savedTransaction.getType()).isEqualTo(TransactionType.TRANSFERATION);
  }

  @Test
  void givenNonexistentOriginAccount_whenCreateTransferationTransaction_thenThrowsInvalidAccountException() {
    // given
    var nonexistentOriginAccount = Optional.<Account>empty();
    var newTransaction = new Transaction(1L, BigDecimal.TEN, null, 1L, 1L);
    // and
    when(accountRepository.findById(anyLong())).thenReturn(nonexistentOriginAccount);
    // then
    assertThatThrownBy(() -> transactionService.createTransferationTransaction(newTransaction))
      .isInstanceOf(InvalidAccountException.class);
    verify(transactionRepository, never()).save(any(Transaction.class));
  }

  @Test @SuppressWarnings("unchecked")
  void givenNonexistentDestinationAccount_whenCreateTransferationTransaction_thenThrowsInvalidAccountException() {
    // given
    var originAccount = new Account(1L, "", null);
    var nonexistentDestinationAccount = Optional.<Account>empty();
    var newTransaction = new Transaction(1L, BigDecimal.TEN, null, 1L, 1L);
    // and
    when(accountRepository.findById(anyLong())).thenReturn(
      Optional.of(originAccount), nonexistentDestinationAccount
    );
    // then
    assertThatThrownBy(() -> transactionService.createTransferationTransaction(newTransaction))
      .isInstanceOf(InvalidAccountException.class);
    verify(transactionRepository, never()).save(any(Transaction.class));
  }

  @Test
  void whenFindAllTransactions_thenReturnsFindedTransactions() {
    // given
    var transactions = List.of(
      new Transaction(1L, BigDecimal.TEN, TransactionType.DEPOSIT, 1L, 1L),
      new Transaction(2L, BigDecimal.TEN, TransactionType.WITHDRAWAL, 1L, 1L),
      new Transaction(3L, BigDecimal.TEN, TransactionType.DEPOSIT, 1L, 1L),
      new Transaction(4L, BigDecimal.TEN, TransactionType.WITHDRAWAL, 1L, 1L)
    );
    // and
    when(transactionRepository.findAllByOriginAccountId(anyLong(), any(Pageable.class)))
      .thenReturn(transactions);
    // when
    var findedTransactions = transactionService.findAllTransactions(1L, 0, 4);
    // then
    assertThat(findedTransactions).isEqualTo(transactions);
  }

}
