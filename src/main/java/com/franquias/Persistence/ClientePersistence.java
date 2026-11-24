package com.franquias.Persistence;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.franquias.Model.entities.Cliente;
import com.franquias.Repository.ClienteRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ClientePersistence implements ClienteRepository {

    private static final String PATH = Arquivo.DATA_DIRECTORY + File.separator + "cliente.json";
    private final Gson gson = new Gson();
    private long proximoId;

    private final List<Cliente> clientesEmMemoria;

    public ClientePersistence() {
        this.clientesEmMemoria = new ArrayList<>();
        carregarDoArquivo();
        determinarProximoId();
    }

    private void carregarDoArquivo() {
        String json = Arquivo.le(PATH);
        if (json != null && !json.trim().isEmpty()) {
            Type tipoLista = new TypeToken<List<Cliente>>() {
            }.getType();
            List<Cliente> clientesDoArquivo = gson.fromJson(json, tipoLista);
            if (clientesDoArquivo != null) {
                this.clientesEmMemoria.addAll(clientesDoArquivo);
            }
        }
    }

    private void determinarProximoId() {
        if (!this.clientesEmMemoria.isEmpty()) {
            long maiorId = this.clientesEmMemoria.stream().mapToLong(Cliente::getId).max().getAsLong();
            this.proximoId = maiorId + 1;
        } else {
            this.proximoId = 1;
        }
    }

    private void save(List<Cliente> itens) {
        String json = gson.toJson(itens);
        Arquivo.salva(PATH, json);
    }

    @Override
    public List<Cliente> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(this.clientesEmMemoria));
    }

    @Override
    public void add(Cliente novoCliente) {
        novoCliente.setId(proximoId);
        clientesEmMemoria.add(novoCliente);
        proximoId++;
        save(clientesEmMemoria);
    }

    @Override
    public Cliente findByCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return null;
        }

        return clientesEmMemoria.stream()
                .filter(cliente -> cpf.equals(cliente.getCpf()))
                .findFirst()
                .orElse(null);
    }
}
