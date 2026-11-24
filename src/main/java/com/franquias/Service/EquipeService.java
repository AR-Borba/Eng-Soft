package com.franquias.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.franquias.Model.entities.Usuários.Gerente;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Repository.GerenteRepository;
import com.franquias.Repository.VendedorRepository;
import com.franquias.exceptions.ValidationException;

public class EquipeService {

    private final GerenteRepository gerenteRepository;
    private final VendedorRepository vendedorRepository;
    private final AutenticacaoService autenticacaoService;
    private final RelatorioService relatorioService;

    public EquipeService(GerenteRepository gerenteRepository,
            VendedorRepository vendedorRepository,
            AutenticacaoService autenticacaoService,
            RelatorioService relatorioService) {
        this.gerenteRepository = gerenteRepository;
        this.vendedorRepository = vendedorRepository;
        this.autenticacaoService = autenticacaoService;
        this.relatorioService = relatorioService;
    }

    public List<Vendedor> listarEquipeOrdenadaPorVendas(long idFranquia) {
        List<Vendedor> vendedores = new ArrayList<>(vendedorRepository.findByFranquia(idFranquia));
        Comparator<Vendedor> comparadorPorVendas = Comparator
                .comparing(relatorioService::calcularTotalVendas);
        vendedores.sort(comparadorPorVendas.reversed());
        return Collections.unmodifiableList(vendedores);
    }

    public void adicionarVendedor(Gerente gerente, Vendedor vendedor) throws ValidationException {
        if (gerente == null || gerente.getFranquiaId() == -1) {
            throw new ValidationException("O Gerente não está responsável por nenhuma franquia no momento.");
        }
        if (autenticacaoService.emailJaExiste(vendedor.getEmail())) {
            throw new ValidationException("O email " + vendedor.getEmail() + " já está em uso no sistema");
        }

        vendedorRepository.adicionarVendedor(vendedor);
        gerente.adicionarVendedorPorId(vendedor.getId());
        gerenteRepository.update(gerente);
    }

    public void editarVendedor(Vendedor vendedorEditado) {
        vendedorRepository.update(vendedorEditado);
    }

    public Vendedor buscarVendedorPorId(long idVendedor) {
        return vendedorRepository.buscarPorId(idVendedor);
    }

    public void removerVendedor(long idVendedor) {
        vendedorRepository.removerVendedor(idVendedor);
    }
}
