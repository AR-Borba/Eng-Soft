package com.franquias.Controller;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.franquias.Model.Produto;
import com.franquias.Model.entities.Cliente;
import com.franquias.Model.entities.Pedido;
import com.franquias.Model.entities.Usuários.Gerente;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Service.EquipeService;
import com.franquias.Service.EstoqueService;
import com.franquias.Service.PedidoService;
import com.franquias.Service.RelatorioService;
import com.franquias.exceptions.ValidationException;

public class GerenteController {

    private final EquipeService equipeService;
    private final EstoqueService estoqueService;
    private final PedidoService pedidoService;
    private final RelatorioService relatorioService;
    private Gerente gerenteLogado;

    public GerenteController(EquipeService equipeService,
            EstoqueService estoqueService,
            PedidoService pedidoService,
            RelatorioService relatorioService) {
        this.equipeService = equipeService;
        this.estoqueService = estoqueService;
        this.pedidoService = pedidoService;
        this.relatorioService = relatorioService;
    }

    public void iniciarSessao(Gerente gerente) {
        this.gerenteLogado = gerente;
    }

    public List<Vendedor> getEquipeDeVendasOrdenadaPorVendas() {
        if (gerenteLogado == null) {
            return Collections.emptyList();
        }
        return equipeService.listarEquipeOrdenadaPorVendas(gerenteLogado.getFranquiaId());
    }

    public void editarVendedor(Vendedor vendedorEditado) {
        equipeService.editarVendedor(vendedorEditado);
    }

    public Vendedor buscarVendedorPorId(long idVendedor) {
        return equipeService.buscarVendedorPorId(idVendedor);
    }

    public void removerVendedor(long idVendedor) {
        equipeService.removerVendedor(idVendedor);
    }

    public void adicionarVendedor(Vendedor vendedor) throws ValidationException {
        equipeService.adicionarVendedor(gerenteLogado, vendedor);
    }

    public void negarAlteracaoPedido(Pedido pedido) {
        pedidoService.negarAlteracao(pedido);
    }

    public void autorizarAlteracaoPedido(Pedido pedido) {
        pedidoService.autorizarAlteracao(pedido);
    }

    public void autorizarExclusaoPedido(Pedido pedido) {
        pedidoService.autorizarExclusao(pedido);
    }

    public List<Pedido> getPedidos() {
        if (gerenteLogado == null) {
            return Collections.emptyList();
        }
        return pedidoService.listarPorFranquia(gerenteLogado.getFranquiaId());
    }

    public List<Pedido> getPedidosPendentes() {
        if (gerenteLogado == null) {
            return Collections.emptyList();
        }
        return pedidoService.listarPendentesPorFranquia(gerenteLogado.getFranquiaId());
    }

    public Pedido buscarPedidoPorId(long idPedido) {
        return pedidoService.buscarPorId(idPedido);
    }

    public void atualizarPedido(Pedido pedido) {
        pedidoService.atualizar(pedido);
    }

    public List<Produto> getProdutos() {
        return estoqueService.listarTodos();
    }

    public List<Produto> getProdutosComEstoqueBaixo(int limite) {
        return estoqueService.listarComEstoqueBaixo(limite);
    }

    public void editarProduto(Produto produtoEditado) {
        estoqueService.editarProduto(produtoEditado);
    }

    public void removerProduto(long idProduto) {
        estoqueService.removerProduto(idProduto);
    }

    public void adicionarProduto(Produto produto) {
        estoqueService.adicionarProduto(produto);
    }

    public void adicionarAoEstoque(Produto produto, int qtd) {
        estoqueService.adicionarAoEstoque(produto, qtd);
    }

    public Produto buscarProdutoPorId(long idProduto) {
        return estoqueService.buscarProdutoSilenciosamente(idProduto);
    }

    public BigDecimal calcularTotalVendasPorVendedor(Vendedor vendedor) {
        return relatorioService.calcularTotalVendas(vendedor);
    }

    public Map<Cliente, Long> getContagemDePedidosPorCliente() {
        return relatorioService.contarPedidosPorCliente();
    }

    public void atribiuFranquiaId(Vendedor vendedor) {
        vendedor.setFranquiaId(gerenteLogado.getFranquiaId());
    }
}
