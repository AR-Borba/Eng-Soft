package com.franquias.ControllerTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.franquias.Controller.LoginController;
import com.franquias.Model.entities.Usuários.Dono;
import com.franquias.Model.entities.Usuários.Gerente;
import com.franquias.Model.entities.Usuários.Usuario;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Repository.DonoRepository;
import com.franquias.Repository.GerenteRepository;
import com.franquias.Repository.VendedorRepository;
import com.franquias.Service.AutenticacaoService;
import com.franquias.exceptions.AuthenticationException;

public class LoginControllerTest {

    private LoginController loginController;
    private InMemoryDonoRepository donoRepository;
    private InMemoryGerenteRepository gerenteRepository;
    private InMemoryVendedorRepository vendedorRepository;

    @BeforeEach
    void setUp() {
        donoRepository = new InMemoryDonoRepository();
        gerenteRepository = new InMemoryGerenteRepository();
        vendedorRepository = new InMemoryVendedorRepository();
        AutenticacaoService autenticacaoService = new AutenticacaoService(
                donoRepository, gerenteRepository, vendedorRepository);
        loginController = new LoginController(autenticacaoService);
    }

    @Test
    void testaEmailJaExistente() {
        String emailTeste = "teste@gmail.com";
        gerenteRepository.adicionarGerente(new Gerente("Gerente Teste", emailTeste, "1234", "98435909026"));

        assertTrue(loginController.emailJaExiste(emailTeste));
    }

    @Test
    void testaEmailNaoExistente() {
        gerenteRepository.adicionarGerente(new Gerente("Gerente Teste", "outro@gmail.com", "1234", "98435909026"));

        assertFalse(loginController.emailJaExiste("naoexiste@gmail.com"));
    }

    @Test
    void deveAutenticarGerenteSemInterfaceGrafica() throws AuthenticationException {
        gerenteRepository.adicionarGerente(new Gerente("Maria", "maria@franquia.com", "segredo", "98435909026"));

        Usuario autenticado = loginController.realizarLogin("maria@franquia.com", "segredo");

        assertInstanceOf(Gerente.class, autenticado);
        assertEquals("Maria", autenticado.getNome());
    }

    @Test
    void deveLancarAuthenticationExceptionParaCredenciaisInvalidas() {
        donoRepository.adicionarDono(new Dono("Arthur", "98435909026", "dono@franquia.com", "1234"));

        assertThrows(AuthenticationException.class,
                () -> loginController.realizarLogin("dono@franquia.com", "errada"));
        assertThrows(AuthenticationException.class,
                () -> loginController.realizarLogin(" ", "1234"));
    }

    private static class InMemoryDonoRepository implements DonoRepository {
        private final List<Dono> donos = new ArrayList<>();

        @Override
        public List<Dono> findAll() {
            return Collections.unmodifiableList(new ArrayList<>(donos));
        }

        @Override
        public void adicionarDono(Dono novoDono) {
            donos.clear();
            donos.add(novoDono);
        }

        @Override
        public boolean hasDono() {
            return !donos.isEmpty();
        }

        @Override
        public Dono findByEmailAndPassword(String email, String senha) {
            return donos.stream()
                    .filter(d -> d.getEmail().equalsIgnoreCase(email) && d.getSenha().equals(senha))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public Dono findByEmail(String email) {
            return donos.stream()
                    .filter(d -> d.getEmail().equalsIgnoreCase(email))
                    .findFirst()
                    .orElse(null);
        }
    }

    private static class InMemoryGerenteRepository implements GerenteRepository {
        private final List<Gerente> gerentes = new ArrayList<>();
        private long nextId = 1;

        @Override
        public List<Gerente> findAll() {
            return Collections.unmodifiableList(new ArrayList<>(gerentes));
        }

        @Override
        public Gerente buscarPorId(long idGerente) {
            return gerentes.stream().filter(g -> g.getId() == idGerente).findFirst().orElse(null);
        }

        @Override
        public void adicionarGerente(Gerente novoGerente) {
            novoGerente.setId(nextId++);
            gerentes.add(novoGerente);
        }

        @Override
        public void removerGerente(long idGerente) {
            gerentes.removeIf(g -> g.getId() == idGerente);
        }

        @Override
        public void update(Gerente gerente) {
            removerGerente(gerente.getId());
            gerentes.add(gerente);
        }

        @Override
        public Gerente findByEmailAndPassword(String email, String senha) {
            return gerentes.stream()
                    .filter(g -> g.getEmail().equalsIgnoreCase(email) && g.getSenha().equals(senha))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public Gerente findByEmail(String email) {
            return gerentes.stream()
                    .filter(g -> g.getEmail().equalsIgnoreCase(email))
                    .findFirst()
                    .orElse(null);
        }
    }

    private static class InMemoryVendedorRepository implements VendedorRepository {
        private final List<Vendedor> vendedores = new ArrayList<>();
        private long nextId = 1;

        @Override
        public List<Vendedor> findAll() {
            return Collections.unmodifiableList(new ArrayList<>(vendedores));
        }

        @Override
        public Vendedor buscarPorId(long idVendedor) {
            return vendedores.stream().filter(v -> v.getId() == idVendedor).findFirst().orElse(null);
        }

        @Override
        public void adicionarVendedor(Vendedor novoVendedor) {
            novoVendedor.setId(nextId++);
            vendedores.add(novoVendedor);
        }

        @Override
        public void removerVendedor(long idVendedor) {
            vendedores.removeIf(v -> v.getId() == idVendedor);
        }

        @Override
        public void update(Vendedor vendedor) {
            removerVendedor(vendedor.getId());
            vendedores.add(vendedor);
        }

        @Override
        public List<Vendedor> findByFranquia(long franquiaId) {
            return Collections.unmodifiableList(vendedores.stream()
                    .filter(v -> v.getFranquiaId() == franquiaId)
                    .collect(Collectors.toList()));
        }

        @Override
        public Vendedor findByEmailAndPassword(String email, String senha) {
            return vendedores.stream()
                    .filter(v -> v.getEmail().equalsIgnoreCase(email) && v.getSenha().equals(senha))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public Vendedor findByEmail(String email) {
            return vendedores.stream()
                    .filter(v -> v.getEmail().equalsIgnoreCase(email))
                    .findFirst()
                    .orElse(null);
        }
    }
}
