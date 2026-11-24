package com.franquias.application.factory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.franquias.Model.Produto;
import com.franquias.Model.entities.Cliente;
import com.franquias.Model.entities.Pedido;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Model.enums.FormaDePagamento;
import com.franquias.Model.enums.ModalidadeEntrega;
import com.franquias.Model.enums.StatusPedido;

public final class PedidoFactory {

    private PedidoFactory() {
    }

    public static Pedido criarVazio() {
        return new Pedido();
    }

    public static Pedido criarNovo(Vendedor vendedor) {
        if (vendedor == null) {
            throw new IllegalArgumentException("Vendedor responsável é obrigatório.");
        }
        return new Pedido(vendedor);
    }

    public static Pedido criarCompleto(Map<Produto, Integer> produtos, Cliente cliente,
            LocalDateTime dataHora, FormaDePagamento formaDePagamento, BigDecimal taxas,
            ModalidadeEntrega modalidadeEntrega, StatusPedido statusPedido) {
        if (produtos == null) {
            throw new IllegalArgumentException("Lista de produtos é obrigatória.");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente é obrigatório.");
        }
        if (formaDePagamento == null) {
            throw new IllegalArgumentException("Forma de pagamento é obrigatória.");
        }
        if (taxas == null) {
            throw new IllegalArgumentException("Taxas são obrigatórias.");
        }
        if (modalidadeEntrega == null) {
            throw new IllegalArgumentException("Modalidade de entrega é obrigatória.");
        }
        if (statusPedido == null) {
            throw new IllegalArgumentException("Status do pedido é obrigatório.");
        }

        return new Pedido(produtos, cliente, dataHora, formaDePagamento, taxas,
                modalidadeEntrega, statusPedido);
    }
}
