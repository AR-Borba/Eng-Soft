package com.franquias.Model.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import com.franquias.Model.*;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Model.enums.*;
import com.franquias.application.observer.PedidoObserver;
import com.franquias.application.pagamento.FormaDePagamentoStrategy;
import com.franquias.application.pagamento.FormaDePagamentoStrategyFactory;
import com.franquias.application.entrega.ModalidadeEntregaStrategy;
import com.franquias.application.entrega.ModalidadeEntregaStrategyFactory;

public class Pedido {
    private long id;
    private Map<Produto, Integer> produtos;
    private transient LocalDateTime dataHora;
    private String dataHoraFormatada;
    private Cliente cliente;
    private FormaDePagamento formaDePagamento;
    private BigDecimal taxas;
    private ModalidadeEntrega modalidadeDeEntrega;
    private StatusPedido statusPedido;
    private BigDecimal valorTotal;
    private Vendedor vendedorResponsavel;
    private long franquiaId;
    private transient List<PedidoObserver> observers;

    public Pedido() {
        this.produtos = new HashMap<Produto, Integer>();
        this.observers = new ArrayList<>();
    }

    public Pedido(Vendedor vendedor) {
        this.vendedorResponsavel = vendedor;
        this.franquiaId = vendedor.getFranquiaId();
        this.produtos = new HashMap<>();
        this.observers = new ArrayList<>();
        this.cliente = new Cliente();
        this.dataHora = LocalDateTime.now();
        atualizarDataHoraFormatada();
        this.formaDePagamento = FormaDePagamento.DINHEIRO;
        this.taxas = BigDecimal.ZERO;
        this.modalidadeDeEntrega = ModalidadeEntrega.RETIRADA_NA_LOJA;
        this.statusPedido = StatusPedido.ATIVO;
    }

    public Pedido(Map<Produto, Integer> produtos, Cliente cliente, LocalDateTime datahora,
            FormaDePagamento formaDePagamento, BigDecimal taxas, ModalidadeEntrega modalidadeDeEntrega,
            StatusPedido statusPedido) {
        this.produtos = produtos;
        this.observers = new ArrayList<>();
        this.cliente = cliente;
        this.dataHora = LocalDateTime.now();
        atualizarDataHoraFormatada();
        this.formaDePagamento = formaDePagamento;
        this.taxas = taxas;
        this.modalidadeDeEntrega = modalidadeDeEntrega;
        this.statusPedido = statusPedido;

        calcularEAtualizaValorTotal();
    }

    public void atualizarDataHoraFormatada() {
        if (this.dataHora != null) {
            this.dataHoraFormatada = this.dataHora.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    public String getDataHoraFormatada() {
        return this.dataHoraFormatada;
    }

    private void atualizarDataHoraPelaString() {
        if (this.dataHoraFormatada != null && !this.dataHoraFormatada.isEmpty()) {
            this.dataHora = LocalDateTime.parse(this.dataHoraFormatada, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    public Map<Produto, Integer> getItens() {
        return Collections.unmodifiableMap(this.produtos);
    }

    public void adicionarItem(Produto produto, int quantidade) {
        this.produtos.put(produto, quantidade);
    }

    public void removerItem(Produto produto) {
        if (produtos.get(produto) != null)
            this.produtos.remove(produto);
    }

    public Vendedor getVendedorResponsavel() {
        return vendedorResponsavel;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long idPedido) {
        this.id = idPedido;
    }

    public long getFranquiaId() {
        return this.franquiaId;
    }

    public BigDecimal getTaxa() {
        return this.taxas;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxas = taxa;
    }

    public LocalDateTime getDatahora() {
        if (this.dataHora == null && this.dataHoraFormatada != null) {
            atualizarDataHoraPelaString();
        }
        return this.dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
        atualizarDataHoraFormatada();
    }

    public FormaDePagamento getFormaDePagamento() {
        return formaDePagamento;
    }

    public void setFormaDePagamento(FormaDePagamento formaDePagamento) {
        this.formaDePagamento = formaDePagamento;
    }

    public BigDecimal getTaxas() {
        return taxas;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public ModalidadeEntrega getModalidadeDeEntrega() {
        return modalidadeDeEntrega;
    }

    public void setModalidadeDeEntrega(ModalidadeEntrega modalidadeEntrega) {
        this.modalidadeDeEntrega = modalidadeEntrega;
    }

    public StatusPedido getStatusPedido() {
        return statusPedido;
    }

    public void adicionarObserver(PedidoObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("O observer do pedido não pode ser nulo.");
        }

        observers().add(observer);
    }

    public void removerObserver(PedidoObserver observer) {
        observers().remove(observer);
    }

    public void setStatusPedido(StatusPedido statusPedido) {
        StatusPedido statusAnterior = this.statusPedido;
        this.statusPedido = statusPedido;

        if (statusAnterior != statusPedido) {
            for (PedidoObserver observer : new ArrayList<>(observers())) {
                observer.pedidoAlterado(this, statusAnterior, statusPedido);
            }
        }
    }

    private List<PedidoObserver> observers() {
        if (observers == null) {
            observers = new ArrayList<>();
        }
        return observers;
    }

    public void calcularEAtualizaValorTotal() {
        BigDecimal subtotalItens = BigDecimal.ZERO;

        for (Map.Entry<Produto, Integer> entrada : produtos.entrySet()) {
            Produto produto = entrada.getKey();
            Integer quantidade = entrada.getValue();

            BigDecimal precoProduto = produto.getPreco();

            BigDecimal subtotal = precoProduto.multiply(new BigDecimal(quantidade));

            subtotalItens = subtotalItens.add(subtotal);
        }

        BigDecimal taxasAtuais = (this.taxas == null) ? BigDecimal.ZERO : taxas;
        FormaDePagamentoStrategy estrategia =
                FormaDePagamentoStrategyFactory.criar(this.formaDePagamento);
        BigDecimal valorComPagamento =
                estrategia.calcularValorTotal(subtotalItens, taxasAtuais);

        ModalidadeEntregaStrategy estrategiaEntrega =
                ModalidadeEntregaStrategyFactory.criar(this.modalidadeDeEntrega);
        this.valorTotal =
                estrategiaEntrega.calcularValorTotal(valorComPagamento, BigDecimal.ZERO);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;

        Pedido outro = (Pedido) obj;

        return this.id == outro.getId();
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
