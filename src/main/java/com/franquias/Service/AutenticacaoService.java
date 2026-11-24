package com.franquias.Service;

import com.franquias.Model.entities.Usuários.Dono;
import com.franquias.Model.entities.Usuários.Gerente;
import com.franquias.Model.entities.Usuários.Usuario;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Repository.DonoRepository;
import com.franquias.Repository.GerenteRepository;
import com.franquias.Repository.VendedorRepository;
import com.franquias.exceptions.AuthenticationException;

public class AutenticacaoService {

    private final DonoRepository donoRepository;
    private final GerenteRepository gerenteRepository;
    private final VendedorRepository vendedorRepository;

    public AutenticacaoService(DonoRepository donoRepository,
            GerenteRepository gerenteRepository,
            VendedorRepository vendedorRepository) {
        this.donoRepository = donoRepository;
        this.gerenteRepository = gerenteRepository;
        this.vendedorRepository = vendedorRepository;
    }

    public Usuario autenticar(String email, String senha) throws AuthenticationException {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new AuthenticationException("Email e senha são obrigatórios.");
        }

        Dono dono = donoRepository.findByEmailAndPassword(email, senha);
        if (dono != null) {
            return dono;
        }

        Gerente gerente = gerenteRepository.findByEmailAndPassword(email, senha);
        if (gerente != null) {
            return gerente;
        }

        Vendedor vendedor = vendedorRepository.findByEmailAndPassword(email, senha);
        if (vendedor != null) {
            return vendedor;
        }

        throw new AuthenticationException("Email ou senha inválidos.");
    }

    public boolean emailJaExiste(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return donoRepository.findByEmail(email) != null
                || gerenteRepository.findByEmail(email) != null
                || vendedorRepository.findByEmail(email) != null;
    }
}
