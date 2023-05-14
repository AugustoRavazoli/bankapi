package io.github.augustoravazoli.bankapi.transaction;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.augustoravazoli.bankapi.account.Account;
import io.github.augustoravazoli.bankapi.account.AccountRepository;

@Service
class TransactionService {

  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;

  @Autowired
  public TransactionService(
    TransactionRepository transactionRepository,
    AccountRepository accountRepository
  ) {
    this.transactionRepository = transactionRepository;
    this.accountRepository = accountRepository;
  }

  @Transactional
  public Transaction createDepositTransaction(Transaction newTransaction) {
    var account = getOriginAccountById(newTransaction.getOriginAccountId());
    account.deposit(newTransaction.getAmount());
    newTransaction.setType(TransactionType.DEPOSIT);
    return transactionRepository.save(newTransaction);
  }

  @Transactional
  public Transaction createWithdrawalTransaction(Transaction newTransaction) {
    var account = getOriginAccountById(newTransaction.getOriginAccountId());
    account.withdraw(newTransaction.getAmount());
    newTransaction.setType(TransactionType.WITHDRAWAL);
    return transactionRepository.save(newTransaction);
  }

  @Transactional
  public Transaction createTransferTransaction(Transaction newTransaction) {
    var origin = getOriginAccountById(newTransaction.getOriginAccountId());
    var destination = getDestinationAccountById(newTransaction.getDestinationAccountId());
    origin.transfer(newTransaction.getAmount(), destination);
    newTransaction.setType(TransactionType.TRANSFER);
    return transactionRepository.save(newTransaction);
  }

  public List<Transaction> findAllTransactions(long accountId, int page, int size) {
    var currentPage = PageRequest.of(page, size, Sort.by("date").ascending());
    return transactionRepository.findAllByOriginAccountId(accountId, currentPage);
  }

  private Account getOriginAccountById(long id) {
    return accountRepository
      .findById(id)
      .orElseThrow(() -> new InvalidAccountException("origin account doesn't exists"));
  }

  private Account getDestinationAccountById(long id) {
    return accountRepository
      .findById(id)
      .orElseThrow(() -> new InvalidAccountException("destination account doesn't exists"));
  }

}
