package com.franquias.Controller;

import com.franquias.Model.entities.Pedido;
import com.franquias.Persistence.PedidoPersistence;
import com.franquias.Persistence.ProdutoPersistence;
import com.franquias.exceptions.EstoqueInsuficienteException;
import com.franquias.Service.EstoqueService;
import com.franquias.Service.PedidoService;

public class PedidoController {

    private PedidoPersistence pedidoPersistence;
    private ProdutoPersistence produtoPersistence;
    private final PedidoService pedidoService;

    public PedidoController(PedidoPersistence pedidoPersistence, ProdutoPersistence produtoPersistence) {
        this.pedidoPersistence = pedidoPersistence;
        this.produtoPersistence = produtoPersistence;
        this.pedidoService = new PedidoService(pedidoPersistence, new EstoqueService(produtoPersistence));
    }

    public void solicitarAlteracao(Pedido pedido) {
        pedidoService.solicitarAlteracao(pedido);
    }

    public void solicitarCancelamento(Pedido pedido) {
        pedidoService.solicitarCancelamento(pedido);
    }

    public void adicionarItem(Pedido pedido, long idProduto, int qtd) throws EstoqueInsuficienteException {
        pedidoService.adicionarItem(pedido, idProduto, qtd);
    }

    public void removerItem(Pedido pedido, long idProduto) {
        pedidoService.removerItem(pedido, idProduto);
    }

    public void salvarAlteracoes(Pedido pedido) {
        pedidoService.salvarAlteracoes(pedido);
    }

    public void removerItemDoPedido(Pedido pedido, long idProduto) {
        if (pedido != null) {
            pedidoService.removerItem(pedido, idProduto);
            pedido.calcularEAtualizaValorTotal();
        }
    }
}
