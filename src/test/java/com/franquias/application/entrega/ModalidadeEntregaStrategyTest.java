package com.franquias.application.entrega;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.franquias.Model.enums.ModalidadeEntrega;

public class ModalidadeEntregaStrategyTest {

    @Test
    void deveCriarEstrategiaParaTodasAsModalidades() {
        BigDecimal produtos = new BigDecimal("100.00");
        BigDecimal taxas = new BigDecimal("15.00");

        for (ModalidadeEntrega modalidade : ModalidadeEntrega.values()) {
            ModalidadeEntregaStrategy estrategia =
                    ModalidadeEntregaStrategyFactory.criar(modalidade);

            assertEquals(new BigDecimal("115.00"),
                    estrategia.calcularValorTotal(produtos, taxas));
        }
    }

    @Test
    void deveUsarRetiradaNaLojaQuandoModalidadeNaoFoiInformada() {
        ModalidadeEntregaStrategy estrategia =
                ModalidadeEntregaStrategyFactory.criar(null);

        assertEquals(new BigDecimal("125.00"),
                estrategia.calcularValorTotal(
                        new BigDecimal("120.00"),
                        new BigDecimal("5.00")));
    }
}
