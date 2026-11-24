package com.franquias.Repository;

import java.util.List;

import com.franquias.Model.entities.Pedido;

public interface PedidoRepository {

    List<Pedido> findAll();

    Pedido buscarPorId(long idPedido);

    void adicionarPedido(Pedido pedido);

    void update(Pedido pedido);

    List<Pedido> findByFranquia(long franquiaId);
}
