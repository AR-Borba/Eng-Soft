package com.franquias.Repository;

import java.util.List;

import com.franquias.Model.entities.Franquia;

public interface FranquiaRepository {

    List<Franquia> findAll();

    Franquia buscarPorId(long idFranquia);

    void adicionarFranquia(Franquia novaFranquia);

    void removerFranquia(long idFranquia);

    void update(Franquia franquia);
}
