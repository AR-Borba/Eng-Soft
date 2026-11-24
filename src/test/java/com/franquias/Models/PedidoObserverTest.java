package com.franquias.Models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.franquias.Model.entities.Pedido;
import com.franquias.Model.enums.StatusPedido;
import com.franquias.application.factory.PedidoFactory;

public class PedidoObserverTest {

    @Test
    void deveNotificarObserverQuandoStatusDoPedidoForAlterado() {
        Pedido pedido = PedidoFactory.criarVazio();
        AtomicReference<StatusPedido> statusAnterior = new AtomicReference<>();
        AtomicReference<StatusPedido> novoStatus = new AtomicReference<>();

        pedido.adicionarObserver((pedidoAlterado, anterior, novo) -> {
            statusAnterior.set(anterior);
            novoStatus.set(novo);
        });

        pedido.setStatusPedido(StatusPedido.ATIVO);
        pedido.setStatusPedido(StatusPedido.PENDENTE_ALTERACAO);

        assertEquals(StatusPedido.ATIVO, statusAnterior.get());
        assertEquals(StatusPedido.PENDENTE_ALTERACAO, novoStatus.get());
    }

    @Test
    void naoDeveNotificarQuandoStatusNaoMudar() {
        Pedido pedido = PedidoFactory.criarVazio();
        AtomicReference<StatusPedido> statusNotificado = new AtomicReference<>();
        pedido.adicionarObserver((pedidoAlterado, anterior, novo) -> statusNotificado.set(novo));

        pedido.setStatusPedido(null);

        assertEquals(null, statusNotificado.get());
    }

    @Test
    void deveRejeitarObserverNulo() {
        Pedido pedido = PedidoFactory.criarVazio();

        assertThrows(IllegalArgumentException.class, () -> pedido.adicionarObserver(null));
    }
}
