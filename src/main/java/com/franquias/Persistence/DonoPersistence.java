package com.franquias.Persistence;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.franquias.Model.entities.Usuários.Dono;
import com.franquias.Repository.DonoRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DonoPersistence implements DonoRepository {

    private static final String PATH = Arquivo.DATA_DIRECTORY + File.separator + "dono.json";
    private final Gson gson = new Gson();

    private void save(List<Dono> itens) {
        String json = gson.toJson(itens);
        Arquivo.salva(PATH, json);
    }

    @Override
    public List<Dono> findAll() {
        String json = Arquivo.le(PATH);
        List<Dono> itens = new ArrayList<>();

        if (!json.trim().isEmpty()) {
            Type tipoLista = new TypeToken<List<Dono>>() {
            }.getType();
            List<Dono> carregados = gson.fromJson(json, tipoLista);
            if (carregados != null) {
                itens = carregados;
            }
        }
        return Collections.unmodifiableList(new ArrayList<>(itens));
    }

    @Override
    public void adicionarDono(Dono novoDono) {
        List<Dono> donos = new ArrayList<>();
        donos.add(novoDono);
        save(donos);
    }

    @Override
    public boolean hasDono() {
        return !findAll().isEmpty();
    }

    @Override
    public Dono findByEmailAndPassword(String email, String senha) {
        return findAll().stream()
                .filter(d -> d.getEmail() != null && d.getEmail().equalsIgnoreCase(email)
                        && d.getSenha() != null && d.getSenha().equals(senha))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Dono findByEmail(String email) {
        return findAll().stream()
                .filter(d -> d.getEmail() != null && d.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
}
