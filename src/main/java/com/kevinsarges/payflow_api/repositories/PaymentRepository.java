package com.kevinsarges.payflow_api.repositories;

import com.kevinsarges.payflow_api.entities.Payment;
import com.kevinsarges.payflow_api.entities.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;

public interface PaymentRepository  extends JpaRepository<Payment, Long> {

    @Query("""
        SELECT p FROM Payment p
        WHERE (:codigoDebito IS NULL OR p.codigoDebito = :codigoDebito)
        AND (:cpfCnpj IS NULL OR p.cpfCnpj = :cpfCnpj)
        AND (:status IS NULL OR p.status = :status)
    """)
    Page<Payment> findByFilters(
            @Param("codigoDebito") BigInteger codigoDebito,
            @Param("cpfCnpj") String cpfCnpj,
            @Param("status") PaymentStatus status,
            Pageable pageable
    );
}
