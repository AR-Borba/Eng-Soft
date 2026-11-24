package com.franquias.application.entrega;

import java.math.BigDecimal;

public class EntregaClienteStrategy implements ModalidadeEntregaStrategy {

    @Override
    public BigDecimal calcularValorTotal(BigDecimal valorProdutos, BigDecimal taxas) {
        return valorProdutos.add(taxas);
    }
}
