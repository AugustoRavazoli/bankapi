package io.github.augustoravazoli.bankapi.customer;

import java.time.LocalDate;
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
  void whenCreateCustomer_thenReturnsCreatedCustomer() {
    // given
    var newCustomer = new Customer(
      1L, "customer", "customer@example.com", "xxx.xxx.xxx-xx", LocalDate.of(1990, 9, 9)
    );
    // and
    when(customerRepository.existsByEmail(anyString())).thenReturn(false);
    when(customerRepository.existsByCpf(anyString())).thenReturn(false);
    when(customerRepository.save(any(Customer.class))).then(returnsFirstArg());
    // when
    var savedCustomer = customerService.createCustomer(newCustomer);
    // then
    assertThat(savedCustomer).isEqualTo(newCustomer);
    verify(customerRepository, times(1)).save(any(Customer.class));
  }

  @Test
  void givenEmailTaken_whenCreateCustomer_thenThrowsEmailTakenException() {
    // given
    var newCustomer = new Customer(
      1L, "customer", "customer@example.com", "xxx.xxx.xxx-xx", LocalDate.of(1990, 9, 9)
    );
    // and
    when(customerRepository.existsByEmail(anyString())).thenReturn(true);
    // then
    assertThatThrownBy(() -> customerService.createCustomer(newCustomer))
      .isInstanceOf(EmailTakenException.class);
    verify(customerRepository, never()).save(any(Customer.class));
  }

  @Test
  void givenCpfTaken_whenCreateCustomer_thenThrowsCpfTakenException() {
    // given
    var newCustomer = new Customer(
      1L, "customer", "customer@example.com", "xxx.xxx.xxx-xx", LocalDate.of(1990, 9, 9)
    );
    // and
    when(customerRepository.existsByEmail(anyString())).thenReturn(false);
    when(customerRepository.existsByCpf(anyString())).thenReturn(true);
    // then
    assertThatThrownBy(() -> customerService.createCustomer(newCustomer))
      .isInstanceOf(CpfTakenException.class);
    verify(customerRepository, never()).save(any(Customer.class));
  }

  @Test
  void whenFindCustomer_thenReturnsFindedCustomer() {
    // given
    var customer = new Customer(
      1L, "customer", "customer@example.com", "xxx.xxx.xxx-xx", LocalDate.of(1990, 9, 9)
    );
    // and
    when(customerRepository.findByCpf(anyString())).thenReturn(Optional.of(customer));
    // when
    var findedCustomer = customerService.findCustomer("xxx.xxx.xxx-xx");
    // then
    assertThat(findedCustomer).isEqualTo(customer);
  }

  @Test
  void givenNonexistentCustomer_whenFindCustomer_threnThrowsCustomerNotFoundException() {
    // given
    var nonexistentCustomer = Optional.<Customer>empty();
    // and
    when(customerRepository.findByCpf(anyString())).thenReturn(nonexistentCustomer);
    // then
    assertThatThrownBy(() -> customerService.findCustomer("xxx.xxx.xxx-xx"))
      .isInstanceOf(CustomerNotFoundException.class);
  }

  @Test
  void whenEditCustomer_thenReturnsEditedCustomer() {
    // given
    var oldCustomer = new Customer(
      1L, "customer", "customer@example.com", "xxx.xxx.xxx-xx", LocalDate.of(1990, 9, 9)
    );
    var newCustomer = new Customer(
      1L, "edited", "edited@example.com", "xxx.xxx.xxx-xx", LocalDate.of(2000, 2, 2)
    );
    // and
    when(customerRepository.findByCpf(anyString())).thenReturn(Optional.of(oldCustomer));
    when(customerRepository.existsByEmail(anyString())).thenReturn(false);
    when(customerRepository.save(any(Customer.class))).then(returnsFirstArg());
    // when
    var editedCustomer = customerService.editCustomer("xxx.xxx.xxx-xx", newCustomer);
    // then
    assertThat(editedCustomer).usingRecursiveComparison().isEqualTo(newCustomer);
    verify(customerRepository, times(1)).save(any(Customer.class));
  }

  @Test
  void givenNonexistentCustomer_whenEditCustomer_thenThrowsCustomerNotFoundException() {
    // given
    var newCustomer = new Customer(
      1L, "customer", "customer@example.com", "xxx.xxx.xxx-xx", LocalDate.of(2000, 2, 2)
    );
    // and
    when(customerRepository.findByCpf(anyString())).thenReturn(Optional.empty());
    // then
    assertThatThrownBy(() -> customerService.editCustomer("xxx.xxx.xxx-xx", newCustomer))
      .isInstanceOf(CustomerNotFoundException.class);
    verify(customerRepository, never()).save(any(Customer.class));
  }

  @Test
  void givenEmailTaken_whenEditCustomer_thenThrowsEmailTakenException() {
    // given
    var oldCustomer = new Customer(
      1L, "customer", "customer@example.com", "xxx.xxx.xxx-xx", LocalDate.of(1990, 9, 9)
    );
    var newCustomer = new Customer(
      1L, "edited", "edited@example.com", "xxx.xxx.xxx-xx", LocalDate.of(2000, 2, 2)
    );
    // and
    when(customerRepository.findByCpf(anyString())).thenReturn(Optional.of(oldCustomer));
    when(customerRepository.existsByEmail(anyString())).thenReturn(true);
    // then
    assertThatThrownBy(() -> customerService.editCustomer("xxx.xxx.xxx-xx", newCustomer))
      .isInstanceOf(EmailTakenException.class);
    verify(customerRepository, never()).save(any(Customer.class));
  }

  @Test
  void whenRemoveCustomer_thenReturnsNothing() {
    // given
    when(customerRepository.existsByCpf(anyString())).thenReturn(true);
    // when
    customerService.removeCustomer("xxx.xxx.xxx-xx");
    // then
    verify(customerRepository, times(1)).deleteByCpf(anyString());
  }

  @Test
  void givenNonexistentCustomer_whenRemoveCustomer_thenThrowsCustomerNotFoundException() {
    // given
    when(customerRepository.existsByCpf(anyString())).thenReturn(false);
    // then
    assertThatThrownBy(() -> customerService.removeCustomer("xxx.xxx.xxx-xx"))
      .isInstanceOf(CustomerNotFoundException.class);
    verify(customerRepository, never()).deleteByCpf(anyString());
  }

}
