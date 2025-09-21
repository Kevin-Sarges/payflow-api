package com.kevinsarges.payflow_api.repositories;

import com.kevinsarges.payflow_api.entities.Payment;
import com.kevinsarges.payflow_api.entities.PaymentMethod;
import com.kevinsarges.payflow_api.entities.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository repository;

    private Payment criarPagamento(BigInteger codigoDebito, String cpfCnpj, PaymentStatus status, PaymentMethod metodo) {
        Payment pay = new Payment();
        pay.setCodigoDebito(codigoDebito);
        pay.setCpfCnpj(cpfCnpj);
        pay.setMetodo(metodo);
        pay.setNumeroCartao(metodo == PaymentMethod.PIX ? null : "4111111111111111");
        pay.setValor(BigDecimal.valueOf(100.0));
        pay.setStatus(status);
        return pay;
    }

    @Test
    @DisplayName("Deve salvar e buscar um pagamento")
    void deveSalvarEBuscarPagamento() {
        Payment pagamento = criarPagamento(BigInteger.valueOf(10), "12345678900",
                PaymentStatus.PENDENTE_PROCESSAMENTO, PaymentMethod.PIX);

        Payment salvo = repository.save(pagamento);

        assertNotNull(salvo.getId());

        Payment encontrado = repository.findById(salvo.getId()).orElse(null);
        assertNotNull(encontrado);
        assertEquals("12345678900", encontrado.getCpfCnpj());
    }

    @Test
    @DisplayName("Deve filtrar um pagamento por CPF/CNPJ")
    void deveFiltrarPorCpfCnpj() {
        Payment p1 = criarPagamento(BigInteger.valueOf(1), "11111111111",
                PaymentStatus.PENDENTE_PROCESSAMENTO, PaymentMethod.PIX);
        Payment p2 = criarPagamento(BigInteger.valueOf(2), "22222222222",
                PaymentStatus.PROCESSADO_COM_SUCESSO, PaymentMethod.CREDITO);

        repository.save(p1);
        repository.save(p2);

        Page<Payment> result = repository.findByFilters(null, "11111111111", null, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("11111111111", result.getContent().get(0).getCpfCnpj());
    }

    @Test
    @DisplayName("Deve filtrar um pagamento pelo código do débito e o status")
    void deveFiltrarPorCodigoDebitoEStatus() {
        Payment p1 = criarPagamento(BigInteger.valueOf(123), "99999999999",
                PaymentStatus.PENDENTE_PROCESSAMENTO, PaymentMethod.PIX);
        Payment p2 = criarPagamento(BigInteger.valueOf(456), "88888888888",
                PaymentStatus.PROCESSADO_COM_SUCESSO, PaymentMethod.CREDITO);

        repository.save(p1);
        repository.save(p2);

        Page<Payment> result = repository.findByFilters(BigInteger.valueOf(123), null,
                PaymentStatus.PENDENTE_PROCESSAMENTO, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(BigInteger.valueOf(123), result.getContent().get(0).getCodigoDebito());
        assertEquals(PaymentStatus.PENDENTE_PROCESSAMENTO, result.getContent().get(0).getStatus());
    }

    @Test
    @DisplayName("Deve retornar todos do pagamentos quando o filtro for null")
    void deveRetornarTodosQuandoFiltrosSaoNull() {
        repository.save(criarPagamento(BigInteger.valueOf(1), "11111111111",
                PaymentStatus.PENDENTE_PROCESSAMENTO, PaymentMethod.PIX));
        repository.save(criarPagamento(BigInteger.valueOf(2), "22222222222",
                PaymentStatus.PROCESSADO_COM_SUCESSO, PaymentMethod.CREDITO));

        Page<Payment> result = repository.findByFilters(null, null, null, PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
    }

    @Test
    @DisplayName("Deve retorna um array vazio quando nenhum pagamento for encontrado com os filtros")
    void deveRetornarVazioQuandoNenhumPagamentoBateComFiltro() {
        repository.save(criarPagamento(BigInteger.valueOf(1), "11111111111",
                PaymentStatus.PENDENTE_PROCESSAMENTO, PaymentMethod.PIX));

        Page<Payment> result = repository.findByFilters(BigInteger.valueOf(99), "99999999999",
                PaymentStatus.PROCESSADO_COM_FALHA, PageRequest.of(0, 10));

        assertTrue(result.isEmpty());
    }
}
