package com.franquias.Persistence;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.franquias.Model.entities.Franquia;
import com.franquias.Repository.FranquiaRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FranquiaPersistence implements FranquiaRepository {

    private static final String PATH = Arquivo.DATA_DIRECTORY + File.separator + "franquias.json";
    private final Gson gson = new Gson();

    private final List<Franquia> franquiasEmMemoria;
    private long proximoId;

    public FranquiaPersistence() {
        this.franquiasEmMemoria = new ArrayList<>();
        carregarDoArquivo();
        determinarProximoId();
    }

    private void carregarDoArquivo() {
        String json = Arquivo.le(PATH);
        if (json != null && !json.trim().isEmpty()) {
            Type tipoLista = new TypeToken<List<Franquia>>() {
            }.getType();
            List<Franquia> franquiasDoArquivo = gson.fromJson(json, tipoLista);
            if (franquiasDoArquivo != null) {
                this.franquiasEmMemoria.addAll(franquiasDoArquivo);
            }
        }
    }

    private void determinarProximoId() {
        if (!this.franquiasEmMemoria.isEmpty()) {
            long maiorId = this.franquiasEmMemoria.stream().mapToLong(Franquia::getId).max().getAsLong();
            this.proximoId = maiorId + 1;
        } else {
            this.proximoId = 1;
        }
    }

    private void save(List<Franquia> itens) {
        String json = gson.toJson(itens);
        Arquivo.salva(PATH, json);
    }

    @Override
    public List<Franquia> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(this.franquiasEmMemoria));
    }

    @Override
    public void adicionarFranquia(Franquia novaFranquia) {
        novaFranquia.setId(this.proximoId);
        this.franquiasEmMemoria.add(novaFranquia);
        this.proximoId++;
        save(this.franquiasEmMemoria);
    }

    @Override
    public void removerFranquia(long idFranquia) {
        boolean foiRemovido = this.franquiasEmMemoria.removeIf(franquia -> franquia.getId() == idFranquia);
        if (foiRemovido) {
            save(franquiasEmMemoria);
        }
    }

    @Override
    public void update(Franquia franquia) {
        Franquia franquiaAntiga = buscarPorId(franquia.getId());
        if (franquiaAntiga != null) {
            franquiasEmMemoria.remove(franquiaAntiga);
            franquiasEmMemoria.add(franquia);
            save(franquiasEmMemoria);
        }
    }

    @Override
    public Franquia buscarPorId(long idFranquia) {
        return franquiasEmMemoria.stream().filter(f -> f.getId() == idFranquia).findFirst().orElse(null);
    }
}
