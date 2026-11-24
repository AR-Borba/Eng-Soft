package com.franquias.Controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.franquias.Model.entities.Usuários.Dono;
import com.franquias.Model.entities.Usuários.Gerente;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Model.entities.Franquia;
import com.franquias.Model.entities.Pedido;
import com.franquias.Persistence.GerentePersistence;
import com.franquias.Persistence.PedidoPersistence;
import com.franquias.Persistence.VendedorPersistence;
import com.franquias.Utils.Endereco;
import com.franquias.exceptions.ValidationException;
import com.franquias.Persistence.DonoPersistence;
import com.franquias.Persistence.FranquiaPersistence;
import com.franquias.Service.RelatorioService;

public class DonoController {
    private AplicacaoPrincipal app;
    private LoginController loginController;
    private DonoPersistence donoPersistence;
    private FranquiaPersistence franquiaPersistence;
    private GerentePersistence gerentePersistence;
    private VendedorPersistence vendedorPersistence;
    private PedidoPersistence pedidoPersistence;
    private Dono donoLogado;
    private final RelatorioService relatorioService;

    public DonoController(AplicacaoPrincipal app, LoginController loginController, DonoPersistence donoPersistence,
            FranquiaPersistence franquiaPersistence, GerentePersistence gerentePersistence, PedidoPersistence pedidoPersistence,
            VendedorPersistence vendedorPersistence) {
        this.app = app;
        this.loginController = loginController;
        this.donoPersistence = donoPersistence;
        this.franquiaPersistence = franquiaPersistence;
        this.gerentePersistence = gerentePersistence;
        this.pedidoPersistence = pedidoPersistence;
        this.vendedorPersistence = vendedorPersistence;
        this.relatorioService = new RelatorioService(pedidoPersistence);
    }

    public void iniciarSessao(Dono dono) {
        this.donoLogado = dono;
        System.out.println("Sessão iniciada para o Dono: " + donoLogado.getNome());

        // verificarFranquiasSemGerenteEnotificar();
    }

    public void cadastrarDono(String nome, String cpf, String email, String senha) throws ValidationException {
        if(loginController.emailJaExiste(email)) {
            throw new ValidationException("O email " + email + " já está em uso no sistema");
        }
        Dono novoDono = new Dono(nome, cpf, email, senha);
        donoPersistence.adicionarDono(novoDono);
    }

    public void cadastrarGerente(Gerente gerente) throws ValidationException {
        if(loginController.emailJaExiste(gerente.getEmail())) {
            throw new ValidationException("O email " + gerente.getEmail() + " já está em uso no sistema");
        }

        gerentePersistence.adicionarGerente(gerente);
    }

    public void removerGerente(long idGerente) {
        gerentePersistence.removerGerente(idGerente);
    }

    public List<Gerente> getGerentes() {
        return gerentePersistence.findAll();
    }

    public List<String> getNomesGerentes() {
        List<String> nomes = new ArrayList<>();
        for(Gerente gerente : gerentePersistence.findAll())
            nomes.add(gerente.getNome());
        return nomes;
    }

    public long getIdGerente(String nome) {
        for(Gerente gerente : gerentePersistence.findAll()){
            if(nome == gerente.getNome())
                return gerente.getId();
        }
        return -1;
    }

    public void cadastrarFranquia(Endereco endereco, long iDgerenteResponsavel) {
        List<Franquia> todasFranquias = franquiaPersistence.findAll();
        
        Franquia novaFranquia = new Franquia(endereco, iDgerenteResponsavel);
        
        Gerente gerenteDaFranquia = gerentePersistence.buscarPorId(iDgerenteResponsavel);
        gerenteDaFranquia.setFranquiaId(novaFranquia.getId());

        todasFranquias.add(novaFranquia);
        franquiaPersistence.save(todasFranquias);
    }

    public List<Franquia> verificarFranquiasSemGerente() {
        List<Franquia> todasFranquias = franquiaPersistence.findAll();
        List<Franquia> franquiasSemGerente = new ArrayList<>();

        for (Franquia franquia : todasFranquias) {
            if (franquia.getGerenteId() == -1) {
                franquiasSemGerente.add(franquia);
            }
        }
        return franquiasSemGerente;
    }

    public List<Franquia> getUnidades() {
        return franquiaPersistence.findAll();
    }

    // public void cadastrarFranquia(Franquia novaFranquia) throws ValidationException{
    //     franquiaPersistence.adicionarFranquia(novaFranquia);
    // }

    public Gerente buscarGerentePorId(long id) {
        return gerentePersistence.buscarPorId(id);
    }

    public void editarGerente(Gerente gerenteParaEditar) {
        gerentePersistence.update(gerenteParaEditar);
    }

    public void removerFranquia(long idFranquia) {
        franquiaPersistence.removerFranquia(idFranquia);
    }

    public Franquia buscarFranquiaPorId(long idFranquia) {
        return franquiaPersistence.buscarPorId(idFranquia);
    }

    public void editarFranquia(Franquia franquiaParaEditar) {
        franquiaPersistence.update(franquiaParaEditar);
    }

    public List<Vendedor> getRankingVendedores() {
        List<Vendedor> vendedores = vendedorPersistence.findAll();

        Comparator<Vendedor> comparadorPorVendas = new Comparator<Vendedor>() {
            @Override
            public int compare(Vendedor v1, Vendedor v2) {
                BigDecimal receitaV1 = calcularTotalVendasPorVendedor(v1);
                BigDecimal receitaV2 = calcularTotalVendasPorVendedor(v2);
                return receitaV2.compareTo(receitaV1);
            }
        };
        vendedores.sort(comparadorPorVendas);
        return vendedores;
    }

    public BigDecimal calcularTotalVendasPorVendedor(Vendedor vendedor) {
        return relatorioService.calcularTotalVendas(vendedor);
    }

    public BigDecimal getReceita(Franquia franquia) {
        return relatorioService.calcularReceita(franquia);
    }

    public Map<Long, BigDecimal> calcularFaturamentoPorFranquia() {
        return relatorioService.calcularFaturamentoPorFranquia();
    }

    public Map<Long, Long> calcularTotalPedidosPorFranquia() {
        return relatorioService.calcularTotalPedidosPorFranquia();
    }

    public Map<Long, BigDecimal> calcularTicketMedioPorFranquia() {
        return relatorioService.calcularTicketMedioPorFranquia();
    }

    public String getNomeGerente(Franquia franquia) {
        for(Franquia unidade : franquiaPersistence.findAll()){
            if(unidade.getId() == franquia.getId()){
                Gerente gerente = gerentePersistence.buscarPorId(unidade.getGerenteId());
                return gerente.getNome();
            }
        }
        return "-";
    }

    public void setGerenteFranquiaId(Franquia franquia, long gerenteId) {
        List<Gerente> gerentes = gerentePersistence.findAll();
        for(Gerente gerente : gerentes){
            if(gerente.getId() == gerenteId)
                gerente.setFranquiaId(franquia.getId());
        }
        gerentePersistence.save(gerentes);
    }

    public void cadastrarFranquia(Franquia novaFranquia) throws ValidationException {
        franquiaPersistence.adicionarFranquia(novaFranquia);
        List<Gerente> gerentes = gerentePersistence.findAll();
        for(Gerente gerente : gerentes){
            if (gerente != null) {
                gerente.setFranquiaId(novaFranquia.getId());
                
                gerentePersistence.update(gerente);
            }
        }

        }
}
