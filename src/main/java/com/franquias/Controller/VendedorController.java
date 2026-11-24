package com.franquias.Controller;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.ValidationException;

import com.franquias.Model.entities.Cliente;
import com.franquias.Model.entities.Pedido;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Model.enums.FormaDePagamento;
import com.franquias.Model.enums.ModalidadeEntrega;
import com.franquias.Service.ClienteService;
import com.franquias.Service.PedidoService;
import com.franquias.application.factory.PedidoFactory;
import com.franquias.exceptions.EstoqueInsuficienteException;
import com.franquias.exceptions.ProdutoNaoEncontradoException;

public class VendedorController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private Vendedor vendedorLogado;
    private Pedido pedidoAtual;

    public VendedorController(PedidoService pedidoService, ClienteService clienteService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
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
        pedidoService.salvarPedido(pedidoAtual);
        this.iniciarNovoPedido();
    }

    public Pedido getPedidoAtual() {
        return pedidoAtual;
    }

    public Pedido buscarPedidoPorId(long idPedido) {
        return pedidoService.buscarPorId(idPedido);
    }

    public List<Pedido> getPedidosVendedor() {
        return pedidoService.listarPorIds(vendedorLogado.getListaIdPedidos());
    }

    public List<Pedido> getPedidosVendedorPendentesAlteracao() {
        return pedidoService.listarEmAlteracaoPorIds(vendedorLogado.getListaIdPedidos());
    }

    public void atualizarPedido(Pedido pedido) {
        pedidoService.atualizar(pedido);
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
