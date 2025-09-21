package com.kevinsarges.payflow_api.sevices;

import com.kevinsarges.payflow_api.DTOs.DeletePaymentResponseDTO;
import com.kevinsarges.payflow_api.DTOs.PaymentRequestDTO;
import com.kevinsarges.payflow_api.entities.Payment;
import com.kevinsarges.payflow_api.entities.PaymentMethod;
import com.kevinsarges.payflow_api.entities.PaymentStatus;
import com.kevinsarges.payflow_api.repositories.PaymentRepository;
import com.kevinsarges.payflow_api.utils.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository repository;

    @Transactional
    public Payment create(PaymentRequestDTO dto) {
        PaymentMethod method;

        try {
            method = PaymentMethod.valueOf(dto.getMetodo().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Método de pagamento inválido: " + dto.getMetodo());
        }

        Payment pay = new Payment();
        pay.setCodigoDebito(dto.getCodigoDebito());
        pay.setCpfCnpj(dto.getCpfCnpj());
        pay.setMetodo(method);
        pay.setNumeroCartao(dto.getNumeroCartao());
        pay.setValor(dto.getValor());
        pay.setStatus(PaymentStatus.PENDENTE_PROCESSAMENTO);

        if((pay.getMetodo() == PaymentMethod.CREDITO || pay.getMetodo() == PaymentMethod.DEBITO)
            && (pay.getNumeroCartao() == null || pay.getNumeroCartao().isBlank())) {
            throw new RuntimeException("Número do cartão obrigatório para pagamento com cartão");
        }

        return repository.save(pay);
    }

    public Page<Payment> listAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Payment> filterListPayments(
            BigInteger codigoDebito,
            String cpfCnpj,
            String status,
            Pageable pageable
    ) {
        PaymentStatus st = null;

        if (status != null && !status.isBlank()) {
            try {
                st = PaymentStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Status de pagamento inválido: " + status);
            }
        }

        return repository.findByFilters(codigoDebito, cpfCnpj, st, pageable);
    }

    @Transactional
    public Payment updateStatus(Long id, PaymentStatus novoStatus) {
        Payment existing = repository.findById(id).orElseThrow(() -> new BusinessException("Pagamento não encontrado"));
        PaymentStatus statusAtual = existing.getStatus();

        if(statusAtual == PaymentStatus.PENDENTE_PROCESSAMENTO) {
            if(novoStatus == PaymentStatus.PROCESSADO_COM_SUCESSO || novoStatus == PaymentStatus.PROCESSADO_COM_FALHA) {
                existing.setStatus(novoStatus);
                return repository.save(existing);
            }
            throw new BusinessException("Transição inválida a partir de PENDENTE_PROCESSAMENTO");
        }

        if(statusAtual == PaymentStatus.PROCESSADO_COM_SUCESSO)
            throw new BusinessException("Pagamento já processado com sucesso. Status não pode ser alterado");

        if(statusAtual == PaymentStatus.PROCESSADO_COM_FALHA) {
            if(novoStatus == PaymentStatus.PENDENTE_PROCESSAMENTO) {
                existing.setStatus(novoStatus);
                return repository.save(existing);
            }
            throw new BusinessException("Para o status de PROCESSADO_COM_FALHA só é permitida volta para PENDENTE_PROCESSAMENTO");
        }

        if(statusAtual == PaymentStatus.INATIVO) throw new BusinessException("Pagamento inativo!! Operação não permitida");

        throw new BusinessException("Transição de status inválida");
    }

    @Transactional
    public DeletePaymentResponseDTO deletePay(Long id) {
        Payment existing = repository.findById(id).orElseThrow(() -> new  BusinessException("Pagamento não encontrado"));

        if(existing.getStatus() != PaymentStatus.PENDENTE_PROCESSAMENTO) {
            throw new BusinessException("Só é possível excluir pagamentos com status PENDENTE_PROCESSAMENTO");
        }

        existing.setStatus(PaymentStatus.INATIVO);
        repository.save(existing);

        return new DeletePaymentResponseDTO(200, "Pagamento desativado !!", LocalDateTime.now());
    }
}
