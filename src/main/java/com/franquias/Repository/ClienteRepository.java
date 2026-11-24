package com.franquias.Repository;

import java.util.List;

import com.franquias.Model.entities.Cliente;

public interface ClienteRepository {

    List<Cliente> findAll();

    Cliente findByCpf(String cpf);

    void add(Cliente cliente);
}
