package com.franquias.Repository;

import java.util.List;

import com.franquias.Model.Produto;

public interface ProdutoRepository {

    List<Produto> findAll();

    Produto buscarPorId(long idProduto);

    void adicionarProduto(Produto novoProduto);

    void removerProduto(long idProduto);

    void update(Produto produto);

    void adicionarAoEstoque(Produto produto, int quantidade);
}
