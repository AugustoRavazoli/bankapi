package io.github.augustoravazoli.bankapi.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class CustomerService {

  private final CustomerRepository customerRepository;

  @Autowired
  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public Customer createCustomer(Customer newCustomer) {
    if (customerRepository.existsByEmail(newCustomer.getEmail())) {
      throw new EmailTakenException();
    }
    if (customerRepository.existsByCpf(newCustomer.getCpf())) {
      throw new CpfTakenException();
    }
    return customerRepository.save(newCustomer);
  }

  public Customer findCustomer(String cpf) {
    return customerRepository
      .findByCpf(cpf)
      .orElseThrow(CustomerNotFoundException::new);
  }

}
