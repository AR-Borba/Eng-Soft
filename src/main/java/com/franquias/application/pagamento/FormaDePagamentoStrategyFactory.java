package com.franquias.application.pagamento;

import com.franquias.Model.enums.FormaDePagamento;

public final class FormaDePagamentoStrategyFactory {

    private FormaDePagamentoStrategyFactory() {
    }

    public static FormaDePagamentoStrategy criar(FormaDePagamento formaDePagamento) {
        FormaDePagamento forma = formaDePagamento == null
                ? FormaDePagamento.DINHEIRO
                : formaDePagamento;

        return switch (forma) {
            case PIX -> new PagamentoPixStrategy();
            case DINHEIRO -> new PagamentoDinheiroStrategy();
            case CARTAO_DEBITO -> new PagamentoCartaoDebitoStrategy();
            case CARTAO_CREDITO -> new PagamentoCartaoCreditoStrategy();
        };
    }
}
