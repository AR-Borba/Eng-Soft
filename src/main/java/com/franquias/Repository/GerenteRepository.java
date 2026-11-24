package com.franquias.Repository;

import java.util.List;

import com.franquias.Model.entities.Usuários.Gerente;

public interface GerenteRepository {

    List<Gerente> findAll();

    Gerente buscarPorId(long idGerente);

    void adicionarGerente(Gerente novoGerente);

    void removerGerente(long idGerente);

    void update(Gerente gerente);

    Gerente findByEmailAndPassword(String email, String senha);

    Gerente findByEmail(String email);
}
