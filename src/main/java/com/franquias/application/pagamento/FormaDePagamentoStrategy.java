package com.franquias.application.pagamento;

import java.math.BigDecimal;

public interface FormaDePagamentoStrategy {
    BigDecimal calcularValorTotal(BigDecimal valorProdutos, BigDecimal taxas);
}
