package com.kevinsarges.payflow_api.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class FilterPaymentRequestDTO {
    private BigInteger codigoDebito;
    private String cpfCnpj;

    @Schema(description = "Status do pagamento", example = "PENDENTE_PROCESSAMENTO")
    private String status;
}
