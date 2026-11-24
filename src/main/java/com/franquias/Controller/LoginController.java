package com.franquias.Controller;

import com.franquias.Model.entities.Usuários.Usuario;
import com.franquias.Service.AutenticacaoService;
import com.franquias.exceptions.AuthenticationException;

public class LoginController {

    private final AutenticacaoService autenticacaoService;

    public LoginController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    public Usuario realizarLogin(String email, String senha) throws AuthenticationException {
        return autenticacaoService.autenticar(email, senha);
    }

    public boolean emailJaExiste(String email) {
        return autenticacaoService.emailJaExiste(email);
    }
}
