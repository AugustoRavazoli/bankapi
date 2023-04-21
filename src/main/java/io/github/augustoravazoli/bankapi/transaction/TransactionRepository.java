package io.github.augustoravazoli.bankapi.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
