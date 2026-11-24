package com.franquias.Controller;

import com.franquias.Model.entities.Pedido;
import com.franquias.Persistence.PedidoPersistence;
import com.franquias.Persistence.ProdutoPersistence;
import com.franquias.exceptions.EstoqueInsuficienteException;
import com.franquias.exceptions.ProdutoNaoEncontradoException;
import com.franquias.Service.EstoqueService;
import com.franquias.Service.PedidoService;
import com.franquias.Persistence.VendedorPersistence;

public class PedidoController {

    private PedidoPersistence pedidoPersistence;
    private ProdutoPersistence produtoPersistence;
    private final PedidoService pedidoService;

    public PedidoController(PedidoPersistence pedidoPersistence, ProdutoPersistence produtoPersistence,
            VendedorPersistence vendedorPersistence) {
        this.pedidoPersistence = pedidoPersistence;
        this.produtoPersistence = produtoPersistence;
        this.pedidoService = new PedidoService(pedidoPersistence, new EstoqueService(produtoPersistence),
                vendedorPersistence);
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
