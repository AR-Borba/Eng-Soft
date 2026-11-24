package com.franquias.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.franquias.Model.entities.Cliente;
import com.franquias.Model.entities.Franquia;
import com.franquias.Model.entities.Pedido;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Model.enums.StatusPedido;
import com.franquias.Persistence.PedidoPersistence;

public class RelatorioService {

    private final PedidoPersistence pedidoPersistence;

    public RelatorioService(PedidoPersistence pedidoPersistence) {
        this.pedidoPersistence = pedidoPersistence;
    }

    public BigDecimal calcularTotalVendas(Vendedor vendedor) {
        if (vendedor.getListaIdPedidos() == null) {
            return BigDecimal.ZERO;
        }
        return vendedor.getListaIdPedidos().stream()
                .map(pedidoPersistence::buscarPorId)
                .filter(pedido -> pedido != null && pedido.getStatusPedido() == StatusPedido.CONCLUIDO)
                .map(Pedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calcularReceita(Franquia franquia) {
        return pedidosConcluidos().stream()
                .filter(pedido -> pedido.getFranquiaId() == franquia.getId())
                .map(Pedido::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<Long, BigDecimal> calcularFaturamentoPorFranquia() {
        Map<Long, BigDecimal> resultado = new HashMap<>();
        for (Pedido pedido : pedidosConcluidos()) {
            resultado.merge(pedido.getFranquiaId(), pedido.getValorTotal(), BigDecimal::add);
        }
        return resultado;
    }

    public Map<Long, Long> calcularTotalPedidosPorFranquia() {
        Map<Long, Long> resultado = new HashMap<>();
        for (Pedido pedido : pedidosConcluidos()) {
            resultado.merge(pedido.getFranquiaId(), 1L, Long::sum);
        }
        return resultado;
    }

    public Map<Long, BigDecimal> calcularTicketMedioPorFranquia() {
        Map<Long, BigDecimal> faturamento = calcularFaturamentoPorFranquia();
        Map<Long, Long> pedidos = calcularTotalPedidosPorFranquia();
        Map<Long, BigDecimal> resultado = new HashMap<>();

        faturamento.forEach((franquiaId, valor) -> {
            long quantidade = pedidos.getOrDefault(franquiaId, 0L);
            if (quantidade > 0) {
                resultado.put(franquiaId, valor.divide(BigDecimal.valueOf(quantidade), 2, RoundingMode.HALF_UP));
            }
        });
        return resultado;
    }

    public Map<Cliente, Long> contarPedidosPorCliente() {
        return Collections.unmodifiableMap(pedidoPersistence.findAll().stream()
                .filter(pedido -> pedido.getCliente() != null)
                .collect(Collectors.groupingBy(Pedido::getCliente, Collectors.counting())));
    }

    private List<Pedido> pedidosConcluidos() {
        return pedidoPersistence.findAll().stream()
                .filter(pedido -> pedido.getStatusPedido() == StatusPedido.CONCLUIDO)
                .collect(Collectors.toList());
    }
}
