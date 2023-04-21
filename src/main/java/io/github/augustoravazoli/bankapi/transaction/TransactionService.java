package io.github.augustoravazoli.bankapi.transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    var account = accountRepository
      .findById(newTransaction.getOriginAccountId())
      .orElseThrow(() -> new InvalidAccountException("origin account doesn't exists"));
    account.deposit(newTransaction.getAmount());
    newTransaction.setType(TransactionType.DEPOSIT);
    return transactionRepository.save(newTransaction);
  }

  @Transactional
  public Transaction createWithdrawalTransaction(Transaction newTransaction) {
    var account = accountRepository
      .findById(newTransaction.getOriginAccountId())
      .orElseThrow(() -> new InvalidAccountException("origin account doesn't exists"));
    account.withdraw(newTransaction.getAmount());
    newTransaction.setType(TransactionType.WITHDRAWAL);
    return transactionRepository.save(newTransaction);
  }

  @Transactional
  public Transaction createTransferationTransaction(Transaction newTransaction) {
    var origin = accountRepository
      .findById(newTransaction.getOriginAccountId())
      .orElseThrow(() -> new InvalidAccountException("origin account doesn't exists"));
    var destination = accountRepository
      .findById(newTransaction.getDestinationAccountId())
      .orElseThrow(() -> new InvalidAccountException("destination account doesn't exists"));
    origin.transfer(newTransaction.getAmount(), destination);
    newTransaction.setType(TransactionType.TRANSFERATION);
    return transactionRepository.save(newTransaction);
  }


}
