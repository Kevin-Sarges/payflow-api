package com.kevinsarges.payflow_api.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigInteger codigoDebito;
    private String cpfCnpj;

    @Enumerated(EnumType.STRING)
    private PaymentMethod metodo;

    private String numeroCartao;
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public Payment(
        Long id,
        BigInteger codigoDebito,
        String cpfCnpj,
        PaymentMethod metodo,
        String numeroCartao,
        BigDecimal valor,
        PaymentStatus status
    ) {
        this.id = id;
        this.codigoDebito = codigoDebito;
        this.cpfCnpj = cpfCnpj;
        this.metodo = metodo;
        this.numeroCartao = numeroCartao;
        this.valor = valor;
        this.status = status;
    }
}
