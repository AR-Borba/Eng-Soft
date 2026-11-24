package com.franquias.Repository;

import java.util.List;

import com.franquias.Model.entities.Usuários.Vendedor;

public interface VendedorRepository {

    List<Vendedor> findAll();

    Vendedor buscarPorId(long idVendedor);

    void adicionarVendedor(Vendedor novoVendedor);

    void removerVendedor(long idVendedor);

    void update(Vendedor vendedor);

    List<Vendedor> findByFranquia(long franquiaId);

    Vendedor findByEmailAndPassword(String email, String senha);

    Vendedor findByEmail(String email);
}
