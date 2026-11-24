package com.franquias.Models;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.franquias.Model.entities.Usuários.Gerente;
import com.franquias.exceptions.CPFException;
import com.franquias.exceptions.EmailException;

public class UsuarioTest {

    @Test
    void validarEmailDeveAceitarEmailValido() {
        Gerente usuario = new Gerente();
        assertDoesNotThrow(() -> usuario.validarEmail("gerente@franquia.com"));
    }

    @Test
    void validarEmailDeveLancarEmailExceptionParaEmailInvalido() {
        Gerente usuario = new Gerente();
        assertThrows(EmailException.class, () -> usuario.validarEmail("email-invalido"));
        assertThrows(EmailException.class, () -> usuario.validarEmail("   "));
        assertThrows(EmailException.class, () -> usuario.validarEmail(null));
    }

    @Test
    void validarCpfDeveAceitarCpfValido() {
        Gerente usuario = new Gerente();
        assertDoesNotThrow(() -> usuario.validarCPF("98435909026"));
        assertDoesNotThrow(() -> usuario.validarCPF("984.359.090-26"));
    }

    @Test
    void validarCpfDeveLancarCpfExceptionParaCpfInvalido() {
        Gerente usuario = new Gerente();
        assertThrows(CPFException.class, () -> usuario.validarCPF("11111111111"));
        assertThrows(CPFException.class, () -> usuario.validarCPF(" "));
        assertThrows(CPFException.class, () -> usuario.validarCPF(null));
    }
}
