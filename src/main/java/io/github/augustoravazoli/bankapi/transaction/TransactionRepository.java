package io.github.augustoravazoli.bankapi.transaction;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TransactionRepository extends JpaRepository<Transaction, Long> {

  List<Transaction> findAllByOriginAccountId(long accountId, Pageable pageable);

}
