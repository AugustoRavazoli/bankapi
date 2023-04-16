package io.github.augustoravazoli.bankapi.account;

import java.util.stream.Stream;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.validation.Valid;

@Validated
@RequestMapping("/api/v1/customers/{cpf}/accounts")
@Controller
class AccountController {

  private final AccountService accountService;
  private final AccountMapper accountMapper;

  @Autowired
  public AccountController(AccountService accountService, AccountMapper accountMapper) {
    this.accountService = accountService;
    this.accountMapper = accountMapper;
  }

  @PostMapping
  public ResponseEntity<AccountResponse> createAccount(
    @CPF @PathVariable("cpf") String cpf,
    @Valid @RequestBody AccountRequest newAccount
  ) {
    var createdAccount = Stream.of(newAccount)
      .map(account -> accountService.createAccount(cpf, account))
      .map(accountMapper::toResponse)
      .findFirst()
      .get();
    var location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(createdAccount.id())
      .toUri();
    return ResponseEntity.created(location).body(createdAccount);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AccountResponse> findAccount(
    @CPF @PathVariable("cpf") String cpf,
    @PathVariable("id") long id
  ) {
    var findedAccount = Stream.of(accountService.findAccount(cpf, id))
      .map(accountMapper::toResponse)
      .findFirst()
      .get();
    return ResponseEntity.ok().body(findedAccount);
  }

}
