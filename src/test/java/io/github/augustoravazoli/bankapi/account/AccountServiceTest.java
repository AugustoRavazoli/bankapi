package io.github.augustoravazoli.bankapi.account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
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

}
