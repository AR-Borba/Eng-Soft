package com.franquias.Persistence;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.franquias.Model.Produto;
import com.franquias.Repository.ProdutoRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ProdutoPersistence implements ProdutoRepository {

    private static final String PATH = Arquivo.DATA_DIRECTORY + File.separator + "produto.json";
    private final Gson gson = new Gson();

    private final List<Produto> produtosEmMemoria;
    private long proximoId;

    public ProdutoPersistence() {
        this.produtosEmMemoria = new ArrayList<>();
        carregarDoArquivo();
        determinarProximoId();
    }

    private void carregarDoArquivo() {
        String json = Arquivo.le(PATH);
        if (json != null && !json.trim().isEmpty()) {
            Type tipoLista = new TypeToken<List<Produto>>() {
            }.getType();
            List<Produto> produtosDoArquivo = gson.fromJson(json, tipoLista);
            if (produtosDoArquivo != null) {
                this.produtosEmMemoria.addAll(produtosDoArquivo);
            }
        }
    }

    private void determinarProximoId() {
        if (!this.produtosEmMemoria.isEmpty()) {
            long maiorId = this.produtosEmMemoria.stream().mapToLong(Produto::getId).max().getAsLong();
            this.proximoId = maiorId + 1;
        } else {
            this.proximoId = 1;
        }
    }

    private void save(List<Produto> itens) {
        String json = gson.toJson(itens);
        Arquivo.salva(PATH, json);
    }

    @Override
    public List<Produto> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(this.produtosEmMemoria));
    }

    @Override
    public void adicionarProduto(Produto novoProduto) {
        novoProduto.setId(this.proximoId);
        this.produtosEmMemoria.add(novoProduto);
        this.proximoId++;
        save(this.produtosEmMemoria);
    }

    @Override
    public void adicionarAoEstoque(Produto produto, int qtd) {
        int novoEstoque = produto.getQuantidadeEstoque() + qtd;
        produto.setQuantidadeEstoque(novoEstoque);
        update(produto);
    }

    @Override
    public void removerProduto(long idProduto) {
        boolean foiRemovido = this.produtosEmMemoria.removeIf(produto -> produto.getId() == idProduto);
        if (foiRemovido) {
            save(produtosEmMemoria);
        }
    }

    @Override
    public void update(Produto produto) {
        Produto produtoAntigo = buscarPorId(produto.getId());
        if (produtoAntigo != null) {
            produtosEmMemoria.remove(produtoAntigo);
            produtosEmMemoria.add(produto);
            save(produtosEmMemoria);
        }
    }

    @Override
    public Produto buscarPorId(long idProduto) {
        return produtosEmMemoria.stream().filter(p -> p.getId() == idProduto).findFirst().orElse(null);
    }
}
