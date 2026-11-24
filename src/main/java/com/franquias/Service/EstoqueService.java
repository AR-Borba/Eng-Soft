package com.franquias.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.franquias.Model.Produto;
import com.franquias.Repository.ProdutoRepository;
import com.franquias.exceptions.EstoqueInsuficienteException;
import com.franquias.exceptions.ProdutoNaoEncontradoException;

public class EstoqueService {

    private final ProdutoRepository produtoRepository;

    public EstoqueService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public List<Produto> listarComEstoqueBaixo(int limite) {
        return Collections.unmodifiableList(produtoRepository.findAll().stream()
                .filter(produto -> produto.getQuantidadeEstoque() < limite)
                .collect(Collectors.toList()));
    }

    public void adicionarProduto(Produto produto) {
        produtoRepository.adicionarProduto(produto);
    }

    public void editarProduto(Produto produtoEditado) {
        produtoRepository.update(produtoEditado);
    }

    public void removerProduto(long idProduto) {
        produtoRepository.removerProduto(idProduto);
    }

    public Produto buscarProduto(long idProduto) throws ProdutoNaoEncontradoException {
        Produto produto = produtoRepository.buscarPorId(idProduto);
        if (produto == null) {
            throw new ProdutoNaoEncontradoException("Produto não encontrado!");
        }
        return produto;
    }

    public Produto buscarProdutoSilenciosamente(long idProduto) {
        return produtoRepository.buscarPorId(idProduto);
    }

    public void validarDisponibilidade(Produto produto, int quantidade)
            throws EstoqueInsuficienteException {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new EstoqueInsuficienteException("Estoque insuficiente para o produto: " + produto.getProduto()
                    + ". Disponível: " + produto.getQuantidadeEstoque());
        }
    }

    public void baixarEstoque(Produto produto, int quantidade) throws EstoqueInsuficienteException {
        validarDisponibilidade(produto, quantidade);
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtoRepository.update(produto);
    }

    public void adicionarAoEstoque(Produto produto, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        produtoRepository.adicionarAoEstoque(produto, quantidade);
    }
}
