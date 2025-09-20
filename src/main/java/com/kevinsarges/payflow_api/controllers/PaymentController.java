package com.kevinsarges.payflow_api.controllers;

import com.kevinsarges.payflow_api.DTOs.DeletePaymentResponseDTO;
import com.kevinsarges.payflow_api.DTOs.FilterPaymentRequestDTO;
import com.kevinsarges.payflow_api.DTOs.PaymentRequestDTO;
import com.kevinsarges.payflow_api.DTOs.StatusUpdateDTO;
import com.kevinsarges.payflow_api.entities.Payment;
import com.kevinsarges.payflow_api.entities.PaymentMethod;
import com.kevinsarges.payflow_api.entities.PaymentStatus;
import com.kevinsarges.payflow_api.sevices.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService service;

    @PostMapping
    @Operation(summary = "Criando um pagamento")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Pagamento Criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Payment.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Erro na requisição"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Payment> create(@Valid @RequestBody PaymentRequestDTO dto) {
        Payment pay = Payment.builder()
                .codigoDebito(dto.getCodigoDebito())
                .cpfCnpj(dto.getCpfCnpj())
                .metodo(PaymentMethod.valueOf(dto.getMetodo().toUpperCase()))
                .numeroCartao(dto.getNumeroCartao())
                .valor(dto.getValor())
                .build();
        Payment saved = service.create(pay);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    @Operation(summary = "Lista todos os pagamentos")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Pagamentos encontrados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Payment.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Erro na requisição"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Page<Payment>> list(Pageable pageable) {
        return ResponseEntity.ok(service.listAll(pageable));
    }

    @PostMapping("/filter")
    @Operation(summary = "Listando os pagamentos através de filtros")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Pagamentos encontrados",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Payment.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Erro na requisição"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Page<Payment>> filterListPayments(Pageable pageable, @Valid @RequestBody FilterPaymentRequestDTO dto) {
        PaymentStatus st = null;
        if (dto.getStatus() != null) {
            st = PaymentStatus.valueOf(dto.getStatus().toUpperCase());
        }

        Page<Payment> result = service.filterListPayments(dto.getCodigoDebito(), dto.getCpfCnpj(), st, pageable);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/status")
    @PostMapping
    @Operation(summary = "Atualizando status de um pagamento (PENDENTE_PROCESSAMENTO, PROCESSADO_COM_SUCESSO, PROCESSADO_COM_FALHA, INATIVO)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200", description = "Status atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Payment.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Erro na requisição"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<Payment> updateStatus(@PathVariable Long id, @Valid StatusUpdateDTO status) {
        PaymentStatus novoStatus = PaymentStatus.valueOf(
                status.getNovoStatus().toUpperCase().replace(" ", "_"));
        Payment updated = service.updateStatus(id, novoStatus);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletando um pagamento")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro na requisição"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.deletePay(id);
        return ResponseEntity.ok(new DeletePaymentResponseDTO(
                200,
                "Pagamento desativado !!",
                LocalDateTime.now())
        );
    }
}
