package com.franquias.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.franquias.Model.entities.Franquia;
import com.franquias.Model.entities.Usuários.Dono;
import com.franquias.Model.entities.Usuários.Gerente;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.Service.FranquiaService;
import com.franquias.Service.RelatorioService;
import com.franquias.Utils.Endereco;
import com.franquias.exceptions.ValidationException;

public class DonoController {

    private final FranquiaService franquiaService;
    private final RelatorioService relatorioService;
    private Dono donoLogado;

    public DonoController(FranquiaService franquiaService, RelatorioService relatorioService) {
        this.franquiaService = franquiaService;
        this.relatorioService = relatorioService;
    }

    public void iniciarSessao(Dono dono) {
        this.donoLogado = dono;
    }

    public Dono getDonoLogado() {
        return donoLogado;
    }

    public void cadastrarDono(String nome, String cpf, String email, String senha) throws ValidationException {
        franquiaService.cadastrarDono(nome, cpf, email, senha);
    }

    public void cadastrarGerente(Gerente gerente) throws ValidationException {
        franquiaService.cadastrarGerente(gerente);
    }

    public void removerGerente(long idGerente) {
        franquiaService.removerGerente(idGerente);
    }

    public List<Gerente> getGerentes() {
        return franquiaService.listarGerentes();
    }

    public List<String> getNomesGerentes() {
        return franquiaService.listarNomesGerentes();
    }

    public long getIdGerente(String nome) {
        return franquiaService.buscarIdGerentePorNome(nome);
    }

    public void cadastrarFranquia(Endereco endereco, long idGerenteResponsavel) {
        franquiaService.cadastrarFranquia(endereco, idGerenteResponsavel);
    }

    public void cadastrarFranquia(Franquia novaFranquia) throws ValidationException {
        franquiaService.cadastrarFranquia(novaFranquia);
    }

    public List<Franquia> verificarFranquiasSemGerente() {
        return franquiaService.verificarFranquiasSemGerente();
    }

    public List<Franquia> getUnidades() {
        return franquiaService.listarFranquias();
    }

    public Gerente buscarGerentePorId(long id) {
        return franquiaService.buscarGerentePorId(id);
    }

    public void editarGerente(Gerente gerenteParaEditar) {
        franquiaService.editarGerente(gerenteParaEditar);
    }

    public void removerFranquia(long idFranquia) {
        franquiaService.removerFranquia(idFranquia);
    }

    public Franquia buscarFranquiaPorId(long idFranquia) {
        return franquiaService.buscarFranquiaPorId(idFranquia);
    }

    public void editarFranquia(Franquia franquiaParaEditar) {
        franquiaService.editarFranquia(franquiaParaEditar);
    }

    public List<Vendedor> getRankingVendedores() {
        return relatorioService.getRankingVendedores();
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
        return franquiaService.obterNomeGerente(franquia);
    }

    public void setGerenteFranquiaId(Franquia franquia, long gerenteId) {
        franquiaService.associarGerenteAFranquia(franquia, gerenteId);
    }
}
