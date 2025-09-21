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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceUnitTests {
    @Mock
    private PaymentRepository repository;

    @InjectMocks
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
        pagamentoPix.setStatus(PaymentStatus.PENDENTE_PROCESSAMENTO);
    }

    @BeforeEach
    void setupBoleto() {
        pagamentoBoleto = new Payment();
        pagamentoBoleto.setCodigoDebito(BigInteger.ONE);
        pagamentoBoleto.setCpfCnpj("12345678900");
        pagamentoBoleto.setMetodo(PaymentMethod.PIX);
        pagamentoBoleto.setValor(BigDecimal.valueOf(100.0));
        pagamentoBoleto.setStatus(PaymentStatus.PENDENTE_PROCESSAMENTO);
    }

    @BeforeEach
    void setupDebito() {
        pagamentoDebito = new Payment();
        pagamentoDebito.setCodigoDebito(BigInteger.ONE);
        pagamentoDebito.setCpfCnpj("12345678900");
        pagamentoDebito.setMetodo(PaymentMethod.PIX);
        pagamentoDebito.setValor(BigDecimal.valueOf(100.0));
        pagamentoDebito.setNumeroCartao("44444444444444");
        pagamentoDebito.setStatus(PaymentStatus.PENDENTE_PROCESSAMENTO);
    }

    @BeforeEach
    void setupCredito() {
        pagamentoCredito = new Payment();
        pagamentoCredito.setCodigoDebito(BigInteger.ONE);
        pagamentoCredito.setCpfCnpj("12345678900");
        pagamentoCredito.setMetodo(PaymentMethod.PIX);
        pagamentoCredito.setValor(BigDecimal.valueOf(100.0));
        pagamentoDebito.setNumeroCartao("44444444444444");
        pagamentoCredito.setStatus(PaymentStatus.PENDENTE_PROCESSAMENTO);
    }

    @Test
    @DisplayName("Deve criar um pagamento com o PIX")
    void deveCriarPagamentoComPix() {
        when(repository.save(any(Payment.class))).thenReturn(pagamentoPix);

        Payment result = service.create(pagamentoPix);

        assertNotNull(result);
        assertEquals(PaymentStatus.PENDENTE_PROCESSAMENTO, result.getStatus());
        verify(repository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Deve criar um pagamento com o Boleto")
    void deveCriarPagamentoComBoleto() {
        when(repository.save(any(Payment.class))).thenReturn(pagamentoBoleto);

        Payment result = service.create(pagamentoBoleto);

        assertNotNull(result);
        assertEquals(PaymentStatus.PENDENTE_PROCESSAMENTO, result.getStatus());
        verify(repository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Deve criar um pagamento com o Debito")
    void deveCriarPagamentoComDebito() {
        when(repository.save(any(Payment.class))).thenReturn(pagamentoDebito);

        Payment result = service.create(pagamentoDebito);

        assertNotNull(result);
        assertEquals(PaymentStatus.PENDENTE_PROCESSAMENTO, result.getStatus());
        verify(repository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Deve criar um pagamento com o Crédito")
    void deveCriarPagamentoComCredito() {
        when(repository.save(any(Payment.class))).thenReturn(pagamentoCredito);

        Payment result = service.create(pagamentoCredito);

        assertNotNull(result);
        assertEquals(PaymentStatus.PENDENTE_PROCESSAMENTO, result.getStatus());
        verify(repository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando o numero do cartão não existir caso o método for debito ou crédito")
    void deveLancarExcecaoQuandoCartaoSemNumero() {
        pagamentoPix.setMetodo(PaymentMethod.CREDITO);
        pagamentoPix.setNumeroCartao(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.create(pagamentoPix));

        assertEquals("Número do cartão obrigatório para pagamento com cartão", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar o status de PENDENTE_PROCESSAMENTO para PROCESSADO_COM_SUCESSO")
    void deveAtualizarStatusDePendenteParaProcessadoComSucesso() {
        pagamentoBoleto.setStatus(PaymentStatus.PENDENTE_PROCESSAMENTO);
        when(repository.findById(1L)).thenReturn(Optional.of(pagamentoBoleto));
        when(repository.save(any(Payment.class))).thenReturn(pagamentoBoleto);

        Payment result = service.updateStatus(1L, PaymentStatus.PROCESSADO_COM_SUCESSO);

        assertEquals(PaymentStatus.PROCESSADO_COM_SUCESSO, result.getStatus());
        verify(repository).save(pagamentoBoleto);
    }

    @Test
    @DisplayName("Deve lançar uma exceção se tentar alterar um pagamento que esta com status de PROCESSADO_COM_SUCESSO")
    void deveLancarExcecaoSeStatusJaProcessadoComSucesso() {
        pagamentoCredito.setStatus(PaymentStatus.PROCESSADO_COM_SUCESSO);
        when(repository.findById(1L)).thenReturn(Optional.of(pagamentoCredito));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateStatus(1L, PaymentStatus.PENDENTE_PROCESSAMENTO));

        assertEquals("Pagamento já processado com sucesso. Status não pode ser alterado", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar o status de um pagamento que esta em PROCESSADO_COM_FALHA para PENDENTE_PROCESSAMENTO")
    void devePermitirProcessadoComFalhaVoltarParaPendente() {
        pagamentoPix.setStatus(PaymentStatus.PROCESSADO_COM_FALHA);
        when(repository.findById(1L)).thenReturn(Optional.of(pagamentoPix));
        when(repository.save(any(Payment.class))).thenReturn(pagamentoPix);

        Payment result = service.updateStatus(1L, PaymentStatus.PENDENTE_PROCESSAMENTO);

        assertEquals(PaymentStatus.PENDENTE_PROCESSAMENTO, result.getStatus());
        verify(repository).save(pagamentoPix);
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando o pagamento não for encontrado")
    void deveLancarExcecaoQuandoPagamentoNaoEncontradoNoUpdate() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.updateStatus(99L, PaymentStatus.PENDENTE_PROCESSAMENTO));

        assertEquals("Pagamento não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve desativar um pagamento que esta com o status de PENDENTE_PROCESSAMENTO")
    void deveInativarPagamentoComStatusPendente() {
        pagamentoBoleto.setStatus(PaymentStatus.PENDENTE_PROCESSAMENTO);
        when(repository.findById(1L)).thenReturn(Optional.of(pagamentoBoleto));

        service.deletePay(1L);

        assertEquals(PaymentStatus.INATIVO, pagamentoBoleto.getStatus());
        verify(repository).save(pagamentoBoleto);
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando tenta excluir um pagamento que não esta com o status de PENDENTE_PROCESSAMENTO")
    void deveLancarExcecaoAoExcluirPagamentoNaoPendente() {
        pagamentoDebito.setStatus(PaymentStatus.PROCESSADO_COM_SUCESSO);
        when(repository.findById(1L)).thenReturn(Optional.of(pagamentoDebito));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.deletePay(1L));

        assertEquals("Só é possível excluir pagamentos com status PENDENTE_PROCESSAMENTO", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando não encontrar o ID do pagamento")
    void deveLancarExcecaoQuandoPagamentoNaoEncontradoNoDelete() {
        when(repository.findById(123L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.deletePay(123L));

        assertEquals("Pagamento não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lista todos os pagamentos")
    void deveListarTodosOsPagamentos() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(pagamentoPix)));

        Page<Payment> result = service.listAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(pagamentoPix, result.getContent().get(0));
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Deve filtrar os pagamentos por um CPF ou CNPJ")
    void deveFiltrarPagamentosPorCpfCnpj() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(repository.findByFilters(null, "12345678900", null, pageable))
                .thenReturn(new PageImpl<>(List.of(pagamentoCredito)));

        Page<Payment> result = service.filterListPayments(null, "12345678900", null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("12345678900", result.getContent().get(0).getCpfCnpj());
        verify(repository, times(1)).findByFilters(null, "12345678900", null, pageable);
    }

    @Test
    @DisplayName("Deve retorna uma lista vazia se não encontrar nenhum pagamento no filtro")
    void deveRetornarListaVaziaSeNenhumPagamentoEncontradoNoFiltro() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(repository.findByFilters(BigInteger.TEN, null, PaymentStatus.INATIVO, pageable))
                .thenReturn(Page.empty());

        Page<Payment> result = service.filterListPayments(BigInteger.TEN, null, PaymentStatus.INATIVO, pageable);

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findByFilters(BigInteger.TEN, null, PaymentStatus.INATIVO, pageable);
    }
}
