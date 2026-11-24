package com.franquias.application.pagamento;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.franquias.Model.enums.FormaDePagamento;

public class FormaDePagamentoStrategyTest {

    @Test
    void deveCriarEstrategiaParaTodasAsFormasDePagamento() {
        BigDecimal produtos = new BigDecimal("100.00");
        BigDecimal taxas = new BigDecimal("15.00");

        for (FormaDePagamento forma : FormaDePagamento.values()) {
            FormaDePagamentoStrategy estrategia =
                    FormaDePagamentoStrategyFactory.criar(forma);

            assertEquals(new BigDecimal("115.00"),
                    estrategia.calcularValorTotal(produtos, taxas));
        }
    }

    @Test
    void deveUsarDinheiroQuandoFormaNaoFoiInformada() {
        FormaDePagamentoStrategy estrategia =
                FormaDePagamentoStrategyFactory.criar(null);

        assertEquals(new BigDecimal("125.00"),
                estrategia.calcularValorTotal(
                        new BigDecimal("120.00"),
                        new BigDecimal("5.00")));
    }
}
