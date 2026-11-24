package com.franquias.Persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.UncheckedIOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.franquias.Model.Produto;
import com.franquias.Model.entities.Cliente;
import com.franquias.Model.entities.Franquia;
import com.franquias.Model.entities.Pedido;
import com.franquias.Model.entities.Usuários.Dono;
import com.franquias.Model.entities.Usuários.Gerente;
import com.franquias.Model.entities.Usuários.Vendedor;

public class ArquivoTest {

    @Test
    void deveRetornarStringVaziaQuandoArquivoNaoExiste(@TempDir Path tempDir) {
        Path inexistente = tempDir.resolve("nao_existe.json");
        assertEquals("", Arquivo.le(inexistente.toString()));
    }

    @Test
    void deveSalvarELerConteudoCorretamente(@TempDir Path tempDir) {
        Path arquivo = tempDir.resolve("subpasta").resolve("dados.json");
        Arquivo.salva(arquivo.toString(), "{\"ok\":true}");
        assertEquals("{\"ok\":true}", Arquivo.le(arquivo.toString()));
    }

    @Test
    void devePropagarErroQuandoLeituraFalha(@TempDir Path tempDir) {
        assertThrows(UncheckedIOException.class, () -> Arquivo.le(tempDir.toString()));
    }

    @Test
    void findAllDasPersistenciasDeveRetornarListasImutaveis() {
        assertThrows(UnsupportedOperationException.class, () -> new ClientePersistence().findAll().add(new Cliente()));
        assertThrows(UnsupportedOperationException.class,
                () -> new DonoPersistence().findAll().add(new Dono("A", "98435909026", "a@a.com", "1")));
        assertThrows(UnsupportedOperationException.class, () -> new FranquiaPersistence().findAll().add(new Franquia()));
        assertThrows(UnsupportedOperationException.class, () -> new GerentePersistence().findAll().add(new Gerente()));
        assertThrows(UnsupportedOperationException.class, () -> new PedidoPersistence().findAll().add(new Pedido()));
        assertThrows(UnsupportedOperationException.class, () -> new ProdutoPersistence().findAll().add(new Produto()));
        assertThrows(UnsupportedOperationException.class, () -> new VendedorPersistence().findAll().add(new Vendedor()));
    }
}
