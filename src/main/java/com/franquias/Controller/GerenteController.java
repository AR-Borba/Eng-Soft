package com.franquias.Controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.franquias.Model.Produto;
import com.franquias.Model.entities.Cliente;
import com.franquias.Model.entities.Pedido;
import com.franquias.Model.entities.Usuários.Gerente;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Model.enums.StatusPedido;
import com.franquias.Persistence.GerentePersistence;
import com.franquias.Persistence.PedidoPersistence;
import com.franquias.Persistence.ProdutoPersistence;
import com.franquias.Persistence.VendedorPersistence;
import com.franquias.exceptions.ValidationException;
import com.franquias.Service.EstoqueService;
import com.franquias.Service.PedidoService;
import com.franquias.Service.RelatorioService;


public class GerenteController {

    private AplicacaoPrincipal app;
    private LoginController loginController;
    private GerentePersistence gerentePersistence;
    private VendedorPersistence vendedorPersistence;
    private ProdutoPersistence produtoPersistence;
    private PedidoPersistence pedidoPersistence;
    private Gerente gerenteLogado;
    private final EstoqueService estoqueService;
    private final PedidoService pedidoService;
    private final RelatorioService relatorioService;

    public GerenteController(AplicacaoPrincipal app,
            LoginController loginController,
            GerentePersistence gerentePersistence,
            VendedorPersistence vendedorPersistence,
            ProdutoPersistence produtoPersistence,
            PedidoPersistence pedidoPersistence) {
        this.app = app;
        this.loginController = loginController;
        this.gerentePersistence = gerentePersistence;
        this.vendedorPersistence = vendedorPersistence;
        this.produtoPersistence = produtoPersistence;
        this.pedidoPersistence = pedidoPersistence;
        this.estoqueService = new EstoqueService(produtoPersistence);
        this.pedidoService = new PedidoService(pedidoPersistence, estoqueService, vendedorPersistence);
        this.relatorioService = new RelatorioService(pedidoPersistence);
    }

    public void iniciarSessao(Gerente gerente) {
        this.gerenteLogado = gerente;
    }

    public List<Vendedor> getEquipeDeVendasOrdenadaPorVendas() {
        if (gerenteLogado == null)
            return new ArrayList<>();

        long idFranquia = gerenteLogado.getFranquiaId();

        List<Vendedor> vendedores = vendedorPersistence.findByFranquia(idFranquia);

        Comparator<Vendedor> comparadorPorVendas = Comparator
                .comparing(vendedor -> calcularTotalVendasPorVendedor(vendedor));

        vendedores.sort(comparadorPorVendas.reversed());

        return Collections.unmodifiableList(vendedores);
    }

    public void editarVendedor(Vendedor vendedorEditado) {
        vendedorPersistence.update(vendedorEditado);
    }

    public Vendedor buscarVendedorPorId(long idVendedor) {
        return vendedorPersistence.buscarPorId(idVendedor);
    }

    public void removerVendedor(long idVendedor) {
        vendedorPersistence.removerVendedor(idVendedor);
    }

    public void adicionarVendedor(Vendedor vendedor) throws ValidationException {
        if (gerenteLogado.getFranquiaId() == -1) {
            throw new ValidationException("O Gerente não está resonsável por nenhuma franquia no momento.");
        }

        if (loginController.emailJaExiste(vendedor.getEmail())) {
            throw new ValidationException("O email " + vendedor.getEmail() + " já está em uso no sistema");
        }

        vendedorPersistence.adicionarVendedor(vendedor);
        gerenteLogado.adicionarVendedorPorId(vendedor.getId());
        gerentePersistence.update(gerenteLogado);
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

        if (gerenteLogado == null)
            return new ArrayList<>();

        long idFranquia = gerenteLogado.getFranquiaId();

        return Collections.unmodifiableList(pedidoPersistence.findByFranquia(idFranquia));
    }

    public List<Pedido> getPedidosPendentes() {
        if (gerenteLogado == null)
            return new ArrayList<>();

        long idFranquia = gerenteLogado.getFranquiaId();

        List<Pedido> pedidosFranquia = pedidoPersistence.findByFranquia(idFranquia);

        List<Pedido> pedidosFranquiaPendentes = new ArrayList<>();

        for (Pedido pedido : pedidosFranquia)
            if (pedido.getStatusPedido() == StatusPedido.PENDENTE_ALTERACAO
                    || pedido.getStatusPedido() == StatusPedido.PENDENTE_EXCLUSAO)
                pedidosFranquiaPendentes.add(pedido);

        return Collections.unmodifiableList(pedidosFranquiaPendentes);
    }

    public Pedido buscarPedidoPorId(long idPedido) {
        return pedidoPersistence.buscarPorId(idPedido);
    }

    public void atualizarPedido(Pedido pedido) {
        pedidoPersistence.update(pedido);
    }

    public List<Produto> getProdutos() {
        List<Produto> produtos;

        produtos = produtoPersistence.findAll();

        return Collections.unmodifiableList(produtos);
    }

    public List<Produto> getProdutosComEstoqueBaixo(int limite) {
        List<Produto> todosOsProdutos = produtoPersistence.findAll();

        return Collections.unmodifiableList(todosOsProdutos.stream()
                .filter(produto -> produto.getQuantidadeEstoque() < limite)
                .collect(Collectors.toList()));
    }

    public void editarProduto(Produto produtoEditado) {
        produtoPersistence.update(produtoEditado);
    }

    public void removerProduto(long idProduto) {
        produtoPersistence.removerProduto(idProduto);
    }

    public void adicionarProduto(Produto produto) {
        produtoPersistence.adicionarProduto(produto);
    }

    public void adicionarAoEstoque(Produto produto, int qtd) {
        estoqueService.adicionarAoEstoque(produto, qtd);
    }

    public Produto buscarProdutoPorId(long idProduto) {
        return produtoPersistence.buscarPorId(idProduto);
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
