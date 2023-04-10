package io.github.augustoravazoli.bankapi.customer;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

  @Mock
  private CustomerRepository customerRepository;

  @InjectMocks
  private CustomerService customerService;

  @Test
  void whenCreateCustomer_thenReturnsCustomer() {
    var newCustomer = CustomerFactory.createEntity();
    when(customerRepository.existsByEmail(anyString())).thenReturn(false);
    when(customerRepository.existsByCpf(anyString())).thenReturn(false);
    when(customerRepository.save(any(Customer.class))).then(returnsFirstArg());
    var savedCustomer = customerService.createCustomer(newCustomer);
    assertThat(savedCustomer).isEqualTo(newCustomer);
    verify(customerRepository, times(1)).save(any(Customer.class));
  }

  @Test
  void givenEmailTaken_whenCreateCustomer_thenThrowsEmailTakenException() {
    var newCustomer = CustomerFactory.createEntity();
    when(customerRepository.existsByEmail(anyString())).thenReturn(true);
    assertThatThrownBy(() -> customerService.createCustomer(newCustomer))
      .isInstanceOf(EmailTakenException.class);
    verify(customerRepository, never()).save(any(Customer.class));
  }

  @Test
  void givenCpfTaken_whenCreateCustomer_thenThrowsCpfTakenException() {
    var newCustomer = CustomerFactory.createEntity();
    when(customerRepository.existsByEmail(anyString())).thenReturn(false);
    when(customerRepository.existsByCpf(anyString())).thenReturn(true);
    assertThatThrownBy(() -> customerService.createCustomer(newCustomer))
      .isInstanceOf(CpfTakenException.class);
    verify(customerRepository, never()).save(any(Customer.class));
  }

  @Test
  void whenFindCustomer_thenReturnsCustomer() {
    var customer = CustomerFactory.createEntity();
    when(customerRepository.findByCpf(anyString())).thenReturn(Optional.of(customer));
    var findedCustomer = customerService.findCustomer("xxx.xxx.xxx-xx");
    assertThat(findedCustomer).isEqualTo(customer);
  }

  @Test
  void givenCustomerDoesNotExists_whenFindCustomer_threnThrowsCustomerNotFoundException() {
    when(customerRepository.findByCpf(anyString())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> customerService.findCustomer("xxx.xxx.xxx-xx"))
      .isInstanceOf(CustomerNotFoundException.class);
  }

  @Test
  void whenEditCustomer_thenReturnsEditedCustomer() {
    var oldCustomer = CustomerFactory.createEntity();
    var newCustomer = CustomerFactory.createDistinctEntity();
    when(customerRepository.findByCpf(anyString())).thenReturn(Optional.of(oldCustomer));
    when(customerRepository.existsByEmail(anyString())).thenReturn(false);
    when(customerRepository.save(any(Customer.class))).then(returnsFirstArg());
    var editedCustomer = customerService.editCustomer("xxx.xxx.xxx-xx", newCustomer);
    assertThat(editedCustomer).usingRecursiveComparison().isEqualTo(newCustomer);
    verify(customerRepository, times(1)).save(any(Customer.class));
  }

  @Test
  void givenCustomerDoesNotExists_whenEditCustomer_thenThrowsCustomerNotFoundException() {
    var oldCustomer = CustomerFactory.createEntity();
    when(customerRepository.findByCpf(anyString())).thenReturn(Optional.empty());
    assertThatThrownBy(() -> customerService.editCustomer("xxx.xxx.xxx-xx", oldCustomer))
      .isInstanceOf(CustomerNotFoundException.class);
    verify(customerRepository, never()).save(any(Customer.class));
  }

  @Test
  void givenEmailTaken_whenEditCustomer_thenThrowsEmailTakenException() {
    var oldCustomer = CustomerFactory.createEntity();
    var newCustomer = CustomerFactory.createDistinctEntity();
    when(customerRepository.findByCpf(anyString())).thenReturn(Optional.of(oldCustomer));
    when(customerRepository.existsByEmail(anyString())).thenReturn(true);
    assertThatThrownBy(() -> customerService.editCustomer("xxx.xxx.xxx-xx", newCustomer))
      .isInstanceOf(EmailTakenException.class);
    verify(customerRepository, never()).save(any(Customer.class));
  }

  @Test
  void whenRemoveCustomer_thenReturnsNothing() {
    when(customerRepository.existsByCpf(anyString())).thenReturn(true);
    customerService.removeCustomer("xxx.xxx.xxx-xx");
    verify(customerRepository, times(1)).deleteByCpf(anyString());
  }

  @Test
  void givenCustomerDoesNotExists_whenRemoveCustomer_thenThrowsCustomerNotFoundException() {
    when(customerRepository.existsByCpf(anyString())).thenReturn(false);
    assertThatThrownBy(() -> customerService.removeCustomer("xxx.xxx.xxx-xx"))
      .isInstanceOf(CustomerNotFoundException.class);
    verify(customerRepository, never()).deleteByCpf(anyString());
  }

}
