package com.franquias.Persistence;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Repository.VendedorRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class VendedorPersistence implements VendedorRepository {

    private static final String PATH = Arquivo.DATA_DIRECTORY + File.separator + "vendedor.json";
    private final Gson gson = new Gson();
    private final List<Vendedor> vendedoresEmMemoria;
    private long proximoId;

    public VendedorPersistence() {
        this.vendedoresEmMemoria = new ArrayList<>();
        carregarDoArquivo();
        determinarProximoId();
    }

    private void carregarDoArquivo() {
        String json = Arquivo.le(PATH);
        if (json != null && !json.trim().isEmpty()) {
            Type tipoLista = new TypeToken<List<Vendedor>>() {
            }.getType();
            List<Vendedor> vendedoresDoArquivo = gson.fromJson(json, tipoLista);
            if (vendedoresDoArquivo != null) {
                this.vendedoresEmMemoria.addAll(vendedoresDoArquivo);
            }
        }
    }

    private void determinarProximoId() {
        if (!this.vendedoresEmMemoria.isEmpty()) {
            long maiorId = this.vendedoresEmMemoria.stream().mapToLong(Vendedor::getId).max().getAsLong();
            this.proximoId = maiorId + 1;
        } else {
            this.proximoId = 1;
        }
    }

    private void save(List<Vendedor> itens) {
        String json = gson.toJson(itens);
        Arquivo.salva(PATH, json);
    }

    @Override
    public List<Vendedor> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(this.vendedoresEmMemoria));
    }

    @Override
    public void adicionarVendedor(Vendedor novoVendedor) {
        novoVendedor.setId(this.proximoId);
        this.vendedoresEmMemoria.add(novoVendedor);
        this.proximoId++;
        save(this.vendedoresEmMemoria);
    }

    @Override
    public void removerVendedor(long idVendedor) {
        boolean foiRemovido = this.vendedoresEmMemoria.removeIf(vendedor -> vendedor.getId() == idVendedor);
        if (foiRemovido) {
            save(vendedoresEmMemoria);
        }
    }

    @Override
    public void update(Vendedor vendedorAtualizando) {
        int index = -1;
        for (int i = 0; i < vendedoresEmMemoria.size(); i++) {
            if (vendedoresEmMemoria.get(i).getId() == vendedorAtualizando.getId()) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            vendedoresEmMemoria.set(index, vendedorAtualizando);
            save(vendedoresEmMemoria);
        }
    }

    @Override
    public Vendedor buscarPorId(long idVendedor) {
        return vendedoresEmMemoria.stream().filter(v -> v.getId() == idVendedor).findFirst().orElse(null);
    }

    @Override
    public List<Vendedor> findByFranquia(long franquiaId) {
        return Collections.unmodifiableList(vendedoresEmMemoria.stream()
                .filter(vendedor -> vendedor.getFranquiaId() == franquiaId)
                .collect(Collectors.toList()));
    }

    @Override
    public Vendedor findByEmailAndPassword(String email, String senha) {
        return vendedoresEmMemoria.stream()
                .filter(v -> v.getEmail() != null && v.getEmail().equalsIgnoreCase(email)
                        && v.getSenha() != null && v.getSenha().equals(senha))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Vendedor findByEmail(String email) {
        return vendedoresEmMemoria.stream()
                .filter(v -> v.getEmail() != null && v.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
}
