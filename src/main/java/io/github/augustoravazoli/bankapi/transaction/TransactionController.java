package io.github.augustoravazoli.bankapi.transaction;

import java.util.List;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.validation.Valid;

@Validated
@RequestMapping("/api/v1/transactions")
@Controller
class TransactionController {

  private final TransactionService transactionService;
  private final TransactionMapper transactionMapper;

  @Autowired
  public TransactionController(
    TransactionService transactionService,
    TransactionMapper transactionMapper
  ) {
    this.transactionService = transactionService;
    this.transactionMapper = transactionMapper;
  }

  @Validated(OnDepositOrWithdraw.class)
  @PostMapping("/deposits")
  public ResponseEntity<TransactionResponse> createDepositTransaction(
    @Valid @RequestBody TransactionRequest newTransaction
  ) {
    var savedTransaction = Stream.of(newTransaction)
      .map(transactionMapper::toEntity)
      .map(transactionService::createDepositTransaction)
      .map(transactionMapper::toResponse)
      .findFirst()
      .get();
    var location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(savedTransaction.id())
      .toUri();
    return ResponseEntity.created(location).body(savedTransaction);
  }

  @Validated(OnDepositOrWithdraw.class)
  @PostMapping("/withdrawals")
  public ResponseEntity<TransactionResponse> createWithdrawalTransaction(
    @Valid @RequestBody TransactionRequest newTransaction
  ) {
    var savedTransaction = Stream.of(newTransaction)
      .map(transactionMapper::toEntity)
      .map(transactionService::createWithdrawalTransaction)
      .map(transactionMapper::toResponse)
      .findFirst()
      .get();
    var location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(savedTransaction.id())
      .toUri();
    return ResponseEntity.created(location).body(savedTransaction);
  }

  @Validated(OnTransfer.class)
  @PostMapping("/transferations")
  public ResponseEntity<TransactionResponse> createTransferationTransaction(
    @Valid @RequestBody TransactionRequest newTransaction
  ) {
    var savedTransaction = Stream.of(newTransaction)
      .map(transactionMapper::toEntity)
      .map(transactionService::createTransferationTransaction)
      .map(transactionMapper::toResponse)
      .findFirst()
      .get();
    var location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(savedTransaction.id())
      .toUri();
    return ResponseEntity.created(location).body(savedTransaction);
  }

  @GetMapping
  public ResponseEntity<List<TransactionResponse>> findAllTransactions(
    @RequestParam(name = "account-id") long accountId,
    @RequestParam(name = "page") int page,
    @RequestParam(name = "size") int size
  ) {
    var findedTransactions = transactionService
      .findAllTransactions(accountId, page, size)
      .stream()
      .map(transactionMapper::toResponse)
      .toList();
    return ResponseEntity.ok().body(findedTransactions);
  }

}
