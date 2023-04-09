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

  public Customer editCustomer(String cpf, Customer newCustomer) {
    var customer = customerRepository
      .findByCpf(cpf)
      .orElseThrow(CustomerNotFoundException::new);
    var emailExists = customerRepository.existsByEmail(newCustomer.getEmail());
    var emailChanged = !customer.getEmail().equals(newCustomer.getEmail());
    if (emailChanged && emailExists) {
      throw new EmailTakenException();
    }
    customer.setName(newCustomer.getName());
    customer.setEmail(newCustomer.getEmail());
    customer.setBirthDate(newCustomer.getBirthDate());
    return customerRepository.save(customer);
  }

}
