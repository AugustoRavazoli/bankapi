package io.github.augustoravazoli.bankapi.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CustomerRepository extends JpaRepository<Customer, Long> {

  boolean existsByEmail(String email);

  boolean existsByCpf(String cpf);

}
