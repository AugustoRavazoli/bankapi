package io.github.augustoravazoli.bankapi.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.augustoravazoli.bankapi.customer.CustomerNotFoundException;
import io.github.augustoravazoli.bankapi.customer.CustomerRepository;

@Service
class AccountService {

  private final AccountRepository accountRepository;
  private final CustomerRepository customerRepository;
  private final BankClient bankClient;

  @Autowired
  public AccountService(
    AccountRepository accountRepository,
    CustomerRepository customerRepository,
    BankClient bankClient
  ) {
    this.accountRepository = accountRepository;
    this.customerRepository = customerRepository;
    this.bankClient = bankClient;
  }

  public Account createAccount(String ownerCpf, AccountRequest newAccount) {
    var customer = customerRepository
      .findByCpf(ownerCpf)
      .orElseThrow(CustomerNotFoundException::new);
    var bankName = bankClient.findBankNameByCode(newAccount.bankCode());
    var account = new Account(bankName, customer);
    return accountRepository.save(account);
  }

  public Account findAccount(String ownerCpf, long accountId) {
    var customer = customerRepository
      .findByCpf(ownerCpf)
      .orElseThrow(CustomerNotFoundException::new);
    var account = accountRepository
      .findById(accountId)
      .orElseThrow(AccountNotFoundException::new);
    if (customer.getId() != account.getOwner().getId()) {
      throw new AccountMismatchException();
    }
    return account;
  }

  public Account editAccount(String ownerCpf, long accountId, AccountRequest newAccount) {
    var customer = customerRepository
      .findByCpf(ownerCpf)
      .orElseThrow(CustomerNotFoundException::new);
    var account = accountRepository
      .findById(accountId)
      .orElseThrow(AccountNotFoundException::new);
    if (customer.getId() != account.getOwner().getId()) {
      throw new AccountMismatchException();
    }
    var bankName = bankClient.findBankNameByCode(newAccount.bankCode());
    account.setBank(bankName);
    return accountRepository.save(account);
  }

  public void removeAccount(String ownerCpf, long accountId) {
    var customer = customerRepository
      .findByCpf(ownerCpf)
      .orElseThrow(CustomerNotFoundException::new);
    var account = accountRepository
      .findById(accountId)
      .orElseThrow(AccountNotFoundException::new);
    if (customer.getId() != account.getOwner().getId()) {
      throw new AccountMismatchException();
    }
    account.setOwner(null);
    accountRepository.delete(account);
  }

}
