package com.franquias.Service;

import com.franquias.Model.Produto;
import com.franquias.Persistence.ProdutoPersistence;
import com.franquias.exceptions.EstoqueInsuficienteException;
import com.franquias.exceptions.ProdutoNaoEncontradoException;

public class EstoqueService {

    private final ProdutoPersistence produtoPersistence;

    public EstoqueService(ProdutoPersistence produtoPersistence) {
        this.produtoPersistence = produtoPersistence;
    }

    public Produto buscarProduto(long idProduto) throws ProdutoNaoEncontradoException {
        Produto produto = produtoPersistence.buscarPorId(idProduto);
        if (produto == null) {
            throw new ProdutoNaoEncontradoException("Produto não encontrado!");
        }
        return produto;
    }

    public Produto buscarProdutoSilenciosamente(long idProduto) {
        return produtoPersistence.buscarPorId(idProduto);
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
        produtoPersistence.update(produto);
    }

    public void adicionarAoEstoque(Produto produto, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        produtoPersistence.adicionarAoEstoque(produto, quantidade);
    }
}
