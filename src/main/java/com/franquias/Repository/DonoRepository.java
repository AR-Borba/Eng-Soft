package com.franquias.Repository;

import java.util.List;

import com.franquias.Model.entities.Usuários.Dono;

public interface DonoRepository {

    List<Dono> findAll();

    void adicionarDono(Dono novoDono);

    boolean hasDono();

    Dono findByEmailAndPassword(String email, String senha);

    Dono findByEmail(String email);
}
