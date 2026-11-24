package com.franquias.application.observer;

import com.franquias.Model.entities.Pedido;
import com.franquias.Model.enums.StatusPedido;

@FunctionalInterface
public interface PedidoObserver {
    void pedidoAlterado(Pedido pedido, StatusPedido statusAnterior, StatusPedido novoStatus);
}
