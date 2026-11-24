package com.franquias.application.factory;

import com.franquias.Model.entities.Usuários.Dono;
import com.franquias.Model.entities.Usuários.Gerente;
import com.franquias.Model.entities.Usuários.Usuario;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Model.enums.TipoDeConta;

public final class UsuarioFactory {

    private UsuarioFactory() {
    }

    public static Usuario criar(TipoDeConta tipo, String nome, String email,
            String senha, String cpf, long franquiaId) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de conta é obrigatório.");
        }

        validarDadosBasicos(nome, email, senha, cpf);

        switch (tipo) {
            case DONO:
                return new Dono(nome, cpf, email, senha);
            case GERENTE:
                Gerente gerente = new Gerente(nome, email, senha, cpf);
                gerente.setFranquiaId(franquiaId);
                return gerente;
            case VENDEDOR:
                return new Vendedor(nome, email, senha, cpf, franquiaId);
            default:
                throw new IllegalArgumentException("Tipo de conta não suportado.");
        }
    }

    public static Usuario criarVazio(TipoDeConta tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo de conta é obrigatório.");
        }

        switch (tipo) {
            case DONO:
                return new Dono(null, null, null, null);
            case GERENTE:
                return new Gerente();
            case VENDEDOR:
                return new Vendedor();
            default:
                throw new IllegalArgumentException("Tipo de conta não suportado.");
        }
    }

    private static void validarDadosBasicos(String nome, String email,
            String senha, String cpf) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email é obrigatório.");
        }
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória.");
        }
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }
    }
}
