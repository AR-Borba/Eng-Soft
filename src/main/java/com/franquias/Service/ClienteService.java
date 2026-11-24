package com.franquias.Service;

import java.util.Collections;
import java.util.List;

import javax.validation.ValidationException;

import com.franquias.Model.entities.Cliente;
import com.franquias.Persistence.ClientePersistence;

public class ClienteService {

    private final ClientePersistence clientePersistence;

    public ClienteService(ClientePersistence clientePersistence) {
        this.clientePersistence = clientePersistence;
    }

    public List<Cliente> listar() {
        return Collections.unmodifiableList(clientePersistence.findAll());
    }

    public void cadastrar(Cliente cliente) throws ValidationException {
        if (clientePersistence.findByCpf(cliente.getCpf()) != null) {
            throw new ValidationException("O CPF '" + cliente.getCpf() + "' já está cadastrado no sistema.");
        }
        clientePersistence.add(cliente);
    }
}
