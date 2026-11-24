package com.franquias.application.entrega;

import java.math.BigDecimal;

public interface ModalidadeEntregaStrategy {
    BigDecimal calcularValorTotal(BigDecimal valorProdutos, BigDecimal taxas);
}
