package com.franquias.application.pagamento;

import java.math.BigDecimal;

public class PagamentoCartaoCreditoStrategy implements FormaDePagamentoStrategy {

    @Override
    public BigDecimal calcularValorTotal(BigDecimal valorProdutos, BigDecimal taxas) {
        return valorProdutos.add(taxas);
    }
}
