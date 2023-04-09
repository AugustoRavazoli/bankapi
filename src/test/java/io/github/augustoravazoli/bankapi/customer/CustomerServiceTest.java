package io.github.augustoravazoli.bankapi.customer;

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

}
