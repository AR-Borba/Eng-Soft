package com.franquias.application.entrega;

import com.franquias.Model.enums.ModalidadeEntrega;

public final class ModalidadeEntregaStrategyFactory {

    private ModalidadeEntregaStrategyFactory() {
    }

    public static ModalidadeEntregaStrategy criar(ModalidadeEntrega modalidadeEntrega) {
        ModalidadeEntrega modalidade = modalidadeEntrega == null
                ? ModalidadeEntrega.RETIRADA_NA_LOJA
                : modalidadeEntrega;

        return switch (modalidade) {
            case RETIRADA_NA_LOJA -> new RetiradaNaLojaStrategy();
            case ENTREGA_CLIENTE -> new EntregaClienteStrategy();
        };
    }
}
