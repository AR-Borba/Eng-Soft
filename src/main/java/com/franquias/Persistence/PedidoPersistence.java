package com.franquias.Persistence;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.franquias.Model.entities.Pedido;
import com.franquias.Repository.PedidoRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class PedidoPersistence implements PedidoRepository {

    private static final String PATH = Arquivo.DATA_DIRECTORY + File.separator + "pedido.json";
    private final Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setPrettyPrinting()
            .create();

    private long proximoId;
    private final List<Pedido> pedidosEmMemoria;

    public PedidoPersistence() {
        this.pedidosEmMemoria = new ArrayList<>();
        carregarDoArquivo();
        determinarProximoId();
    }

    private void carregarDoArquivo() {
        String json = Arquivo.le(PATH);
        if (json != null && !json.trim().isEmpty()) {
            Type tipoLista = new TypeToken<List<Pedido>>() {
            }.getType();
            List<Pedido> pedidosDoArquivo = gson.fromJson(json, tipoLista);
            if (pedidosDoArquivo != null) {
                this.pedidosEmMemoria.addAll(pedidosDoArquivo);
            }
        }
    }

    private void determinarProximoId() {
        if (!this.pedidosEmMemoria.isEmpty()) {
            long maiorId = this.pedidosEmMemoria.stream().mapToLong(Pedido::getId).max().getAsLong();
            this.proximoId = maiorId + 1;
        } else {
            this.proximoId = 1;
        }
    }

    private void save(List<Pedido> itens) {
        String json = gson.toJson(itens);
        Arquivo.salva(PATH, json);
    }

    @Override
    public List<Pedido> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(this.pedidosEmMemoria));
    }

    @Override
    public void adicionarPedido(Pedido novoPedido) {
        novoPedido.setId(this.proximoId);
        this.pedidosEmMemoria.add(novoPedido);
        this.proximoId++;
        save(this.pedidosEmMemoria);
    }

    @Override
    public Pedido buscarPorId(long idPedido) {
        return pedidosEmMemoria.stream().filter(p -> p.getId() == idPedido).findFirst().orElse(null);
    }

    public long getProximoId() {
        return this.proximoId;
    }

    @Override
    public void update(Pedido pedido) {
        int index = -1;
        for (int i = 0; i < pedidosEmMemoria.size(); i++) {
            if (pedidosEmMemoria.get(i).equals(pedido)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            pedidosEmMemoria.set(index, pedido);
            save(this.pedidosEmMemoria);
        }
    }

    @Override
    public List<Pedido> findByFranquia(long franquiaId) {
        return Collections.unmodifiableList(pedidosEmMemoria.stream()
                .filter(pedido -> pedido.getFranquiaId() == franquiaId)
                .collect(Collectors.toList()));
    }
}
