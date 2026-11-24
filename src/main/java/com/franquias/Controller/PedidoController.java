package com.franquias.Controller;

import com.franquias.Model.entities.Pedido;
import com.franquias.Service.PedidoService;
import com.franquias.exceptions.EstoqueInsuficienteException;
import com.franquias.exceptions.ProdutoNaoEncontradoException;

public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    public void solicitarAlteracao(Pedido pedido) {
        pedidoService.solicitarAlteracao(pedido);
    }

    public void solicitarCancelamento(Pedido pedido) {
        pedidoService.solicitarCancelamento(pedido);
    }

    public void adicionarItem(Pedido pedido, long idProduto, int qtd)
            throws EstoqueInsuficienteException, ProdutoNaoEncontradoException {
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
