package com.franquias.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.franquias.Model.Produto;
import com.franquias.Model.entities.Cliente;
import com.franquias.Model.entities.Pedido;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Model.enums.FormaDePagamento;
import com.franquias.Model.enums.ModalidadeEntrega;
import com.franquias.Model.enums.StatusPedido;
import com.franquias.Repository.PedidoRepository;
import com.franquias.Repository.VendedorRepository;
import com.franquias.exceptions.EstoqueInsuficienteException;
import com.franquias.exceptions.ProdutoNaoEncontradoException;

public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final EstoqueService estoqueService;
    private final VendedorRepository vendedorRepository;

    public PedidoService(PedidoRepository pedidoRepository, EstoqueService estoqueService,
            VendedorRepository vendedorRepository) {
        this.pedidoRepository = pedidoRepository;
        this.estoqueService = estoqueService;
        this.vendedorRepository = vendedorRepository;
    }

    public Pedido buscarPorId(long idPedido) {
        return pedidoRepository.buscarPorId(idPedido);
    }

    public void atualizar(Pedido pedido) {
        pedidoRepository.update(pedido);
    }

    public void salvarPedido(Pedido pedido) {
        pedidoRepository.adicionarPedido(pedido);
    }

    public List<Pedido> listarPorFranquia(long idFranquia) {
        return pedidoRepository.findByFranquia(idFranquia);
    }

    public List<Pedido> listarPendentesPorFranquia(long idFranquia) {
        List<Pedido> pendentes = new ArrayList<>();
        for (Pedido pedido : pedidoRepository.findByFranquia(idFranquia)) {
            if (pedido.getStatusPedido() == StatusPedido.PENDENTE_ALTERACAO
                    || pedido.getStatusPedido() == StatusPedido.PENDENTE_EXCLUSAO) {
                pendentes.add(pedido);
            }
        }
        return Collections.unmodifiableList(pendentes);
    }

    public List<Pedido> listarPorIds(List<Long> idsPedidos) {
        if (idsPedidos == null) {
            return Collections.emptyList();
        }
        List<Pedido> pedidos = new ArrayList<>();
        for (Long idPedido : idsPedidos) {
            Pedido pedido = pedidoRepository.buscarPorId(idPedido);
            if (pedido != null) {
                pedidos.add(pedido);
            }
        }
        return Collections.unmodifiableList(pedidos);
    }

    public List<Pedido> listarEmAlteracaoPorIds(List<Long> idsPedidos) {
        if (idsPedidos == null) {
            return Collections.emptyList();
        }
        List<Pedido> pedidos = new ArrayList<>();
        for (Long idPedido : idsPedidos) {
            Pedido pedido = pedidoRepository.buscarPorId(idPedido);
            if (pedido != null && pedido.getStatusPedido() == StatusPedido.EM_ALTERACAO) {
                pedidos.add(pedido);
            }
        }
        return Collections.unmodifiableList(pedidos);
    }

    public void adicionarItem(Pedido pedido, long idProduto, int quantidade)
            throws EstoqueInsuficienteException, ProdutoNaoEncontradoException {
        Produto produto = estoqueService.buscarProduto(idProduto);
        estoqueService.validarDisponibilidade(produto, quantidade);
        pedido.adicionarItem(produto, quantidade);
    }

    public void removerItem(Pedido pedido, long idProduto) {
        Produto produto = estoqueService.buscarProdutoSilenciosamente(idProduto);
        if (produto != null) {
            pedido.removerItem(produto);
        }
    }

    public void solicitarAlteracao(Pedido pedido) {
        pedido.setStatusPedido(StatusPedido.PENDENTE_ALTERACAO);
    }

    public void solicitarCancelamento(Pedido pedido) {
        pedido.setStatusPedido(StatusPedido.PENDENTE_EXCLUSAO);
    }

    public void salvarAlteracoes(Pedido pedido) {
        pedido.calcularEAtualizaValorTotal();
        pedido.setStatusPedido(StatusPedido.CONCLUIDO);
        pedidoRepository.update(pedido);
    }

    public void finalizar(Pedido pedido, Vendedor vendedor, Cliente cliente, BigDecimal taxa,
            ModalidadeEntrega modalidadeEntrega, FormaDePagamento formaDePagamento)
            throws EstoqueInsuficienteException {
        pedido.setCliente(cliente);
        pedido.setTaxa(taxa);
        pedido.setModalidadeDeEntrega(modalidadeEntrega);
        pedido.setFormaDePagamento(formaDePagamento);
        pedido.setDataHora(LocalDateTime.now());
        pedido.setStatusPedido(StatusPedido.CONCLUIDO);
        pedido.calcularEAtualizaValorTotal();

        for (Map.Entry<Produto, Integer> item : pedido.getItens().entrySet()) {
            estoqueService.baixarEstoque(item.getKey(), item.getValue());
        }

        pedidoRepository.adicionarPedido(pedido);
        vendedor.adicionarPedidoPorId(pedido.getId());
        vendedorRepository.update(vendedor);
    }

    public void autorizarAlteracao(Pedido pedido) {
        if (pedido != null && pedido.getStatusPedido() == StatusPedido.PENDENTE_ALTERACAO) {
            pedido.setStatusPedido(StatusPedido.EM_ALTERACAO);
            pedidoRepository.update(pedido);
        }
    }

    public void negarAlteracao(Pedido pedido) {
        if (pedido != null) {
            pedido.setStatusPedido(StatusPedido.CONCLUIDO);
            pedidoRepository.update(pedido);
        }
    }

    public void autorizarExclusao(Pedido pedido) {
        if (pedido != null && pedido.getStatusPedido() == StatusPedido.PENDENTE_EXCLUSAO) {
            pedido.setStatusPedido(StatusPedido.CANCELADO);
            pedidoRepository.update(pedido);
        }
    }
}
