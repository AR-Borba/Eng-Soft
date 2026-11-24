package com.franquias.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.franquias.Model.entities.Franquia;
import com.franquias.Model.entities.Usuários.Dono;
import com.franquias.Model.entities.Usuários.Gerente;
import com.franquias.Model.enums.TipoDeConta;
import com.franquias.Repository.DonoRepository;
import com.franquias.Repository.FranquiaRepository;
import com.franquias.Repository.GerenteRepository;
import com.franquias.Utils.Endereco;
import com.franquias.application.factory.UsuarioFactory;
import com.franquias.exceptions.ValidationException;

public class FranquiaService {

    private final DonoRepository donoRepository;
    private final FranquiaRepository franquiaRepository;
    private final GerenteRepository gerenteRepository;
    private final AutenticacaoService autenticacaoService;

    public FranquiaService(DonoRepository donoRepository,
            FranquiaRepository franquiaRepository,
            GerenteRepository gerenteRepository,
            AutenticacaoService autenticacaoService) {
        this.donoRepository = donoRepository;
        this.franquiaRepository = franquiaRepository;
        this.gerenteRepository = gerenteRepository;
        this.autenticacaoService = autenticacaoService;
    }

    public void cadastrarDono(String nome, String cpf, String email, String senha) throws ValidationException {
        if (autenticacaoService.emailJaExiste(email)) {
            throw new ValidationException("O email " + email + " já está em uso no sistema");
        }
        Dono novoDono = (Dono) UsuarioFactory.criar(
                TipoDeConta.DONO, nome, email, senha, cpf, -1L);
        donoRepository.adicionarDono(novoDono);
    }

    public void cadastrarGerente(Gerente gerente) throws ValidationException {
        if (autenticacaoService.emailJaExiste(gerente.getEmail())) {
            throw new ValidationException("O email " + gerente.getEmail() + " já está em uso no sistema");
        }
        gerenteRepository.adicionarGerente(gerente);
    }

    public void removerGerente(long idGerente) {
        gerenteRepository.removerGerente(idGerente);
    }

    public List<Gerente> listarGerentes() {
        return gerenteRepository.findAll();
    }

    public List<String> listarNomesGerentes() {
        List<String> nomes = new ArrayList<>();
        for (Gerente gerente : gerenteRepository.findAll()) {
            nomes.add(gerente.getNome());
        }
        return Collections.unmodifiableList(nomes);
    }

    public long buscarIdGerentePorNome(String nome) {
        if (nome == null) {
            return -1;
        }
        for (Gerente gerente : gerenteRepository.findAll()) {
            if (nome.equals(gerente.getNome())) {
                return gerente.getId();
            }
        }
        return -1;
    }

    public Gerente buscarGerentePorId(long id) {
        return gerenteRepository.buscarPorId(id);
    }

    public void editarGerente(Gerente gerenteParaEditar) {
        gerenteRepository.update(gerenteParaEditar);
    }

    public void cadastrarFranquia(Endereco endereco, long idGerenteResponsavel) {
        Franquia novaFranquia = new Franquia(endereco, idGerenteResponsavel);
        franquiaRepository.adicionarFranquia(novaFranquia);
        associarGerenteAFranquia(novaFranquia, idGerenteResponsavel);
    }

    public void cadastrarFranquia(Franquia novaFranquia) throws ValidationException {
        franquiaRepository.adicionarFranquia(novaFranquia);
        if (novaFranquia.getGerenteId() != -1) {
            associarGerenteAFranquia(novaFranquia, novaFranquia.getGerenteId());
        }
    }

    public List<Franquia> verificarFranquiasSemGerente() {
        List<Franquia> franquiasSemGerente = new ArrayList<>();
        for (Franquia franquia : franquiaRepository.findAll()) {
            if (franquia.getGerenteId() == -1) {
                franquiasSemGerente.add(franquia);
            }
        }
        return Collections.unmodifiableList(franquiasSemGerente);
    }

    public List<Franquia> listarFranquias() {
        return franquiaRepository.findAll();
    }

    public void removerFranquia(long idFranquia) {
        franquiaRepository.removerFranquia(idFranquia);
    }

    public Franquia buscarFranquiaPorId(long idFranquia) {
        return franquiaRepository.buscarPorId(idFranquia);
    }

    public void editarFranquia(Franquia franquiaParaEditar) {
        franquiaRepository.update(franquiaParaEditar);
    }

    public String obterNomeGerente(Franquia franquia) {
        Franquia unidade = franquiaRepository.buscarPorId(franquia.getId());
        if (unidade != null) {
            Gerente gerente = gerenteRepository.buscarPorId(unidade.getGerenteId());
            if (gerente != null) {
                return gerente.getNome();
            }
        }
        return "-";
    }

    public void associarGerenteAFranquia(Franquia franquia, long gerenteId) {
        Gerente gerente = gerenteRepository.buscarPorId(gerenteId);
        if (gerente != null) {
            gerente.setFranquiaId(franquia.getId());
            gerenteRepository.update(gerente);
        }
    }
}
