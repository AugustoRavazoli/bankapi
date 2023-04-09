package io.github.augustoravazoli.bankapi.customer;

import java.util.stream.Stream;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

@Validated
@RequestMapping("/api/v1/customers")
@Controller
class CustomerController {

  private final CustomerService customerService;
  private final CustomerMapper customerMapper;

  @Autowired
  public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
    this.customerService = customerService;
    this.customerMapper = customerMapper;
  }

  @Validated(OnCreate.class)
  @PostMapping
  public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest newCustomer) {
    var savedCustomer = Stream.of(newCustomer)
      .map(customerMapper::toEntity)
      .map(customerService::createCustomer)
      .map(customerMapper::toResponse)
      .findFirst()
      .get();
    var location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(savedCustomer.id())
      .toUri();
    return ResponseEntity.created(location).body(savedCustomer);
  }

  @GetMapping("/{cpf}")
  public ResponseEntity<CustomerResponse> findCustomer(@CPF @PathVariable String cpf) {
    var findedCustomer = Stream.of(cpf)
      .map(customerService::findCustomer)
      .map(customerMapper::toResponse)
      .findFirst()
      .get();
    return ResponseEntity.ok().body(findedCustomer);
  }

  @Validated(OnEdit.class)
  @PutMapping("/{cpf}")
  public ResponseEntity<CustomerResponse> editCustomer(
    @CPF @PathVariable String cpf,
    @Valid @RequestBody CustomerRequest newCustomer
  ) {
    var editedCustomer = Stream.of(newCustomer)
      .map(customerMapper::toEntity)
      .map(customer -> customerService.editCustomer(cpf, customer))
      .map(customerMapper::toResponse)
      .findFirst()
      .get();
    return ResponseEntity.ok().body(editedCustomer);
  }

}
