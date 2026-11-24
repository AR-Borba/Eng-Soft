package com.franquias.Controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.ValidationException;

import com.franquias.Model.Produto;
import com.franquias.Model.entities.Cliente;
import com.franquias.Model.entities.Pedido;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Model.enums.FormaDePagamento;
import com.franquias.Model.enums.ModalidadeEntrega;
import com.franquias.Model.enums.StatusPedido;
import com.franquias.Persistence.ClientePersistence;
import com.franquias.Persistence.PedidoPersistence;
import com.franquias.Persistence.ProdutoPersistence;
import com.franquias.Persistence.VendedorPersistence;
import com.franquias.application.factory.PedidoFactory;
import com.franquias.exceptions.EstoqueInsuficienteException;
import com.franquias.exceptions.ProdutoNaoEncontradoException;
import com.franquias.Service.ClienteService;
import com.franquias.Service.EstoqueService;
import com.franquias.Service.PedidoService;

public class VendedorController {

    private AplicacaoPrincipal app;
    private Vendedor vendedorLogado;
    private Pedido pedidoAtual;
    private VendedorPersistence vendedorPersistence;
    private PedidoPersistence pedidoPersistence;
    private ProdutoPersistence produtoPersistence;
    private ClientePersistence clientePersistence;
    private final PedidoService pedidoService;
    private final ClienteService clienteService;

    public VendedorController(AplicacaoPrincipal app, PedidoPersistence pedidoPersistence,
            ProdutoPersistence produtoPersistence, VendedorPersistence vendedorPersistence,
            ClientePersistence clientePersistence) {
        this.app = app;
        this.pedidoPersistence = pedidoPersistence;
        this.produtoPersistence = produtoPersistence;
        this.vendedorPersistence = vendedorPersistence;
        this.clientePersistence = clientePersistence;
        EstoqueService estoqueService = new EstoqueService(produtoPersistence);
        this.pedidoService = new PedidoService(pedidoPersistence, estoqueService, vendedorPersistence);
        this.clienteService = new ClienteService(clientePersistence);

        this.pedidoAtual = PedidoFactory.criarVazio();
    }

    public void iniciarSessao(Vendedor vendedor) {
        this.vendedorLogado = vendedor;
        this.iniciarNovoPedido();
    }

    public void iniciarNovoPedido() {
        this.pedidoAtual = PedidoFactory.criarNovo(vendedorLogado);
    }

    public void adicionarItemAoPedido(long idProduto, int quantidade)
            throws EstoqueInsuficienteException, ProdutoNaoEncontradoException {
        pedidoService.adicionarItem(pedidoAtual, idProduto, quantidade);
    }

    public void removerItemDoPedido(long idProduto) {
        pedidoService.removerItem(pedidoAtual, idProduto);
    }

    public void finalizaPedido() {
        pedidoPersistence.adicionarPedido(pedidoAtual);
        this.iniciarNovoPedido();
    }

    public Pedido getPedidoAtual() {
        return pedidoAtual;
    }

    public Pedido buscarPedidoPorId(long idPedido) {
        return pedidoPersistence.buscarPorId(idPedido);
    }

    public List<Pedido> getPedidosVendedor() {
        List<Long> idPedidos = vendedorLogado.getListaIdPedidos();

        if (idPedidos == null)
            return new ArrayList<>();

        List<Pedido> pedidosVendedor = new ArrayList<>();

        Pedido pedidoId;
        for (Long idPedido : idPedidos) {
            pedidoId = pedidoPersistence.buscarPorId(idPedido);

            if (pedidoId != null)
                pedidosVendedor.add(pedidoId);
        }

        return Collections.unmodifiableList(pedidosVendedor);
    }

    public List<Pedido> getPedidosVendedorPendentesAlteracao() {
        List<Long> idPedidos = vendedorLogado.getListaIdPedidos();

        if (idPedidos == null)
            return new ArrayList<>();

        List<Pedido> pedidosVendedor = new ArrayList<>();

        Pedido pedidoId;
        for (Long idPedido : idPedidos) {
            pedidoId = pedidoPersistence.buscarPorId(idPedido);

            if (pedidoId != null && pedidoId.getStatusPedido() == StatusPedido.EM_ALTERACAO)
                pedidosVendedor.add(pedidoId);
        }

        return Collections.unmodifiableList(pedidosVendedor);
    }

    public void atualizarPedido(Pedido pedido) {
        pedidoPersistence.update(pedido);
    }

    public void finalizarPedido(Cliente cliente, BigDecimal taxa, ModalidadeEntrega modalidadeEntrega,
            FormaDePagamento formaDePagamento) {
        try {
            pedidoService.finalizar(pedidoAtual, vendedorLogado, cliente, taxa, modalidadeEntrega,
                    formaDePagamento);
        } catch (EstoqueInsuficienteException e) {
            throw new IllegalStateException("Não foi possível finalizar o pedido: " + e.getMessage(), e);
        }
        iniciarNovoPedido();
    }

    public List<Cliente> getClientesDisponiveis() {
        return clienteService.listar();
    }

    public void adicionarCliente(Cliente cliente) throws ValidationException {
        clienteService.cadastrar(cliente);
    }
}
