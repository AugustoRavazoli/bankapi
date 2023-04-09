package io.github.augustoravazoli.bankapi.customer;

import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.stereotype.Controller;
import jakarta.validation.Valid;

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

}
