package io.github.augustoravazoli.bankapi.customer;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CustomerRepository extends JpaRepository<Customer, Long> {

  Optional<Customer> findByCpf(String cpf);

  boolean existsByEmail(String email);

  boolean existsByCpf(String cpf);

  void deleteByCpf(String cpf);

}
