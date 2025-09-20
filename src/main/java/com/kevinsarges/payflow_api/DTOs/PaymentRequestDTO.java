package com.kevinsarges.payflow_api.DTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
public class PaymentRequestDTO {
    @NotNull
    private BigInteger codigoDebito;

    @NotNull
    private String cpfCnpj;

    @NotNull
    private String metodo;

    private String numeroCartao;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal valor;
}
