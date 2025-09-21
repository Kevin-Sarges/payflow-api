package com.kevinsarges.payflow_api.services;

import com.kevinsarges.payflow_api.entities.Payment;
import com.kevinsarges.payflow_api.entities.PaymentMethod;
import com.kevinsarges.payflow_api.entities.PaymentStatus;
import com.kevinsarges.payflow_api.repositories.PaymentRepository;
import com.kevinsarges.payflow_api.sevices.PaymentService;
import com.kevinsarges.payflow_api.utils.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class PaymentServiceIntegrationTests {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private PaymentService service;

    private Payment pagamentoPix;
    private Payment pagamentoBoleto;
    private Payment pagamentoDebito;
    private Payment pagamentoCredito;

    @BeforeEach
    void setupPix() {
        pagamentoPix = new Payment();
        pagamentoPix.setCodigoDebito(BigInteger.ONE);
        pagamentoPix.setCpfCnpj("12345678900");
        pagamentoPix.setMetodo(PaymentMethod.PIX);
        pagamentoPix.setValor(BigDecimal.valueOf(100.0));
    }

    @BeforeEach
    void setupBoleto() {
        pagamentoBoleto = new Payment();
        pagamentoBoleto.setCodigoDebito(BigInteger.ONE);
        pagamentoBoleto.setCpfCnpj("12345678900");
        pagamentoBoleto.setMetodo(PaymentMethod.BOLETO);
        pagamentoBoleto.setValor(BigDecimal.valueOf(100.0));
    }

    @BeforeEach
    void setupDebito() {
        pagamentoDebito = new Payment();
        pagamentoDebito.setCodigoDebito(BigInteger.ONE);
        pagamentoDebito.setCpfCnpj("12345678900");
        pagamentoDebito.setMetodo(PaymentMethod.DEBITO);
        pagamentoDebito.setValor(BigDecimal.valueOf(100.0));
        pagamentoDebito.setNumeroCartao("999999999999");
    }

    @BeforeEach
    void setupCredito() {
        pagamentoCredito = new Payment();
        pagamentoCredito.setCodigoDebito(BigInteger.ONE);
        pagamentoCredito.setCpfCnpj("12345678900");
        pagamentoCredito.setMetodo(PaymentMethod.CREDITO);
        pagamentoCredito.setValor(BigDecimal.valueOf(100.0));
        pagamentoCredito.setNumeroCartao("999999999999");
    }

    @Test
    @DisplayName("Deve criar um pagamento com o PIX")
    void deveCriarPagamentoComPix() {
        Payment result = service.create(pagamentoPix);

        assertNotNull(result.getId());
        assertEquals(PaymentStatus.PENDENTE_PROCESSAMENTO, result.getStatus());

        Payment persisted = repository.findById(result.getId()).orElse(null);
        assertNotNull(persisted);
    }

    @Test
    @DisplayName("Deve criar um pagamento com Boleto")
    void deveCriarPagamentoBolero() {
        Payment result = service.create(pagamentoBoleto);

        assertNotNull(result.getId());
        assertEquals(PaymentStatus.PENDENTE_PROCESSAMENTO, result.getStatus());

        Payment persisted = repository.findById(result.getId()).orElse(null);
        assertNotNull(persisted);
    }

    @Test
    @DisplayName("Deve criar um pagamento com Debito")
    void deveCriarPagamentoDebito() {
        Payment result = service.create(pagamentoDebito);

        assertNotNull(result.getId());
        assertEquals(PaymentStatus.PENDENTE_PROCESSAMENTO, result.getStatus());

        Payment persisted = repository.findById(result.getId()).orElse(null);
        assertNotNull(persisted);
    }

    @Test
    @DisplayName("Deve criar um pagamento com Crédito")
    void deveCriarPagamentoCredito() {
        Payment result = service.create(pagamentoCredito);

        assertNotNull(result.getId());
        assertEquals(PaymentStatus.PENDENTE_PROCESSAMENTO, result.getStatus());

        Payment persisted = repository.findById(result.getId()).orElse(null);
        assertNotNull(persisted);
    }

    @Test
    @DisplayName("Deve lançar exceção quando número do cartão for obrigatório mas não informado")
    void deveLancarExcecaoQuandoCartaoSemNumero() {
        pagamentoPix.setMetodo(PaymentMethod.CREDITO);
        pagamentoPix.setNumeroCartao(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.create(pagamentoPix));

        assertEquals("Número do cartão obrigatório para pagamento com cartão", ex.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar o status de PENDENTE_PROCESSAMENTO para PROCESSADO_COM_SUCESSO")
    void deveAtualizarStatusDePendenteParaProcessadoComSucesso() {
        Payment saved = service.create(pagamentoBoleto);

        Payment result = service.updateStatus(saved.getId(), PaymentStatus.PROCESSADO_COM_SUCESSO);

        assertEquals(PaymentStatus.PROCESSADO_COM_SUCESSO, result.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção se tentar alterar um pagamento que esta com status de PROCESSADO_COM_SUCESSO")
    void deveLancarExcecaoSeStatusJaProcessadoComSucesso() {
        Payment saved = service.create(pagamentoDebito);
        service.updateStatus(saved.getId(), PaymentStatus.PROCESSADO_COM_SUCESSO);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateStatus(saved.getId(), PaymentStatus.PENDENTE_PROCESSAMENTO));

        assertEquals("Pagamento já processado com sucesso. Status não pode ser alterado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve permitir PROCESSADO_COM_FALHA voltar para PENDENTE_PROCESSAMENTO")
    void devePermitirProcessadoComFalhaVoltarParaPendente() {
        Payment saved = service.create(pagamentoPix);
        service.updateStatus(saved.getId(), PaymentStatus.PROCESSADO_COM_FALHA);

        Payment result = service.updateStatus(saved.getId(), PaymentStatus.PENDENTE_PROCESSAMENTO);

        assertEquals(PaymentStatus.PENDENTE_PROCESSAMENTO, result.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagamento não encontrado no update")
    void deveLancarExcecaoQuandoPagamentoNaoEncontradoNoUpdate() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateStatus(999L, PaymentStatus.PROCESSADO_COM_SUCESSO));

        assertEquals("Pagamento não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve inativar pagamento com status PENDENTE_PROCESSAMENTO")
    void deveInativarPagamentoComStatusPendente() {
        Payment saved = service.create(pagamentoBoleto);

        service.deletePay(saved.getId());

        Payment inativo = repository.findById(saved.getId()).orElseThrow();
        assertEquals(PaymentStatus.INATIVO, inativo.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao excluir pagamento que não está com o status de PENDENTE_PROCESSAMENTO")
    void deveLancarExcecaoAoExcluirPagamentoNaoPendente() {
        Payment saved = service.create(pagamentoCredito);
        service.updateStatus(saved.getId(), PaymentStatus.PROCESSADO_COM_SUCESSO);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.deletePay(saved.getId()));

        assertEquals("Só é possível excluir pagamentos com status PENDENTE_PROCESSAMENTO", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando pagamento não encontrado no delete")
    void deveLancarExcecaoQuandoPagamentoNaoEncontradoNoDelete() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.deletePay(123L));

        assertEquals("Pagamento não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve listar todos os pagamentos")
    void deveListarTodosOsPagamentos() {
        service.create(pagamentoPix);

        Page<Payment> result = service.listAll(PageRequest.of(0, 10));

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve filtrar pagamentos por CPF/CNPJ")
    void deveFiltrarPagamentosPorCpfCnpj() {
        service.create(pagamentoPix);

        Page<Payment> result = service.filterListPayments(null, "12345678900", null, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("12345678900", result.getContent().get(0).getCpfCnpj());
    }

    @Test
    @DisplayName("Deve retornar lista vazia se filtro não encontrar resultados")
    void deveRetornarListaVaziaSeNenhumPagamentoEncontradoNoFiltro() {
        Page<Payment> result = service.filterListPayments(BigInteger.TEN, null, PaymentStatus.INATIVO, PageRequest.of(0, 10));

        assertTrue(result.isEmpty());
    }
}
