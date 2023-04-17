package io.github.augustoravazoli.bankapi.account;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import io.github.augustoravazoli.bankapi.customer.Customer;
import io.github.augustoravazoli.bankapi.customer.CustomerService;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private CustomerService customerService;

  @Mock
  private BankClient bankClient;

  @InjectMocks
  private AccountService accountService;

  @Test
  void whenCreateAccount_thenReturnsCreatedAccount() {
    // given
    var customer = new Customer();
    var newAccount = new Account("bankname", customer);
    // and
    when(customerService.findCustomer(anyString())).thenReturn(customer);
    when(bankClient.findBankNameByCode(anyInt())).thenReturn("bankname");
    when(accountRepository.save(any(Account.class))).then(returnsFirstArg());
    // when
    var savedAccount = accountService.createAccount("xxx.xxx.xxx-xx", new AccountRequest(1));
    // then
    assertThat(savedAccount).usingRecursiveComparison().isEqualTo(newAccount);
  }

  @Test
  void whenFindAccount_thenReturnsFindedAccount() {
    // given
    var customer = new Customer();
    var account = new Account("bankname", customer);
    // and
    when(customerService.findCustomer(anyString())).thenReturn(customer);
    when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
    // when
    var findedAccount = accountService.findAccount("xxx.xxx.xxx-xx", 1L);
    // then
    assertThat(findedAccount).isEqualTo(account);
  }

  @Test
  void givenNonexistentAccount_whenFindAccount_thenThrowsAccountNotFoundException() {
    // given
    var nonexistentAccount = Optional.<Account>empty();
    // and
    when(customerService.findCustomer(anyString())).thenReturn(new Customer());
    when(accountRepository.findById(anyLong())).thenReturn(nonexistentAccount);
    // then
    assertThatThrownBy(() -> accountService.findAccount("xxx.xxx.xxx-xx", 1L))
      .isInstanceOf(AccountNotFoundException.class);
  }

  @Test
  void givenAccountNotBelongsToCustomer_whenFindAccount_thenThrowsAccountMismatchException() {
    // given
    var customer = new Customer(1L, "", "", "", null);
    var account = new Account(1L, "bankname", new Customer(2L, "", "", "", null));
    // and
    when(customerService.findCustomer(anyString())).thenReturn(customer);
    when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
    // then
    assertThatThrownBy(() -> accountService.findAccount("xxx.xxx.xxx-xx", 1L))
      .isInstanceOf(AccountMismatchException.class);
  }

  @Test
  void whenEditAccount_thenReturnsEditedAccount() {
    // given
    var customer = new Customer();
    var oldAccount = new Account(1L, "bankname", customer);
    var newAccount = new Account(1L, "edited", customer);
    // and
    when(customerService.findCustomer(anyString())).thenReturn(customer);
    when(accountRepository.findById(anyLong())).thenReturn(Optional.of(oldAccount));
    when(bankClient.findBankNameByCode(anyInt())).thenReturn("edited");
    when(accountRepository.save(any(Account.class))).then(returnsFirstArg());
    // when
    var editedAccount = accountService.editAccount("xxx.xxx.xxx-xx", 1L, new AccountRequest(2));
    // then
    assertThat(editedAccount).usingRecursiveComparison().isEqualTo(newAccount);
    verify(accountRepository, times(1)).save(any(Account.class));
  }

  @Test
  void givenNonexistentAccount_whenEditAccount_thenThrowsAccountNotFoundException() {
    // given
    var customer = new Customer();
    var nonexistentAccount = Optional.<Account>empty();
    // and
    when(customerService.findCustomer(anyString())).thenReturn(customer);
    when(accountRepository.findById(anyLong())).thenReturn(nonexistentAccount);
    // then
    assertThatThrownBy(() -> accountService.editAccount("xxx.xxx.xxx-xx", 1L, new AccountRequest(2)))
      .isInstanceOf(AccountNotFoundException.class);
    verify(accountRepository, never()).save(any(Account.class));
  }

  @Test
  void givenAccountNotBelongsToCustomer_whenEditAccount_thenThrowsAccountMismatchException() {
    // given
    var customer = new Customer(1L, "", "", "", null);
    var oldAccount = new Account(1L, "bankname", new Customer(2L, "", "", "", null));
    // and
    when(customerService.findCustomer(anyString())).thenReturn(customer);
    when(accountRepository.findById(anyLong())).thenReturn(Optional.of(oldAccount));
    // then
    assertThatThrownBy(() -> accountService.editAccount("xxx.xxx.xxx-xx", 1L, new AccountRequest(2)))
      .isInstanceOf(AccountMismatchException.class);
    verify(accountRepository, never()).save(any(Account.class));
  }

}
