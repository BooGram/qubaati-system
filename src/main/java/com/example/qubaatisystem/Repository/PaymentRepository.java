package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByLocalReference(String localReference);

    // Native query used instead of @Lock(PESSIMISTIC_WRITE) — Hibernate's JPQL lock generates
    // "FOR UPDATE OF alias" which MariaDB does not support; plain "FOR UPDATE" is compatible.
    @Query(value = "SELECT * FROM payment WHERE local_reference = :localReference FOR UPDATE",
           nativeQuery = true)
    Optional<Payment> findByLocalReferenceForUpdate(@Param("localReference") String localReference);

    Optional<Payment> findByMoyasarPaymentId(String moyasarPaymentId);
}
