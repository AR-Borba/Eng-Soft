package com.franquias.Controller;

import javax.swing.*;
import java.awt.*;

import com.franquias.Persistence.ClientePersistence;
import com.franquias.Persistence.DonoPersistence;
import com.franquias.Persistence.FranquiaPersistence;
import com.franquias.Persistence.GerentePersistence;
import com.franquias.Persistence.PedidoPersistence;
import com.franquias.Persistence.ProdutoPersistence;
import com.franquias.Persistence.VendedorPersistence;
import com.franquias.Repository.ClienteRepository;
import com.franquias.Repository.DonoRepository;
import com.franquias.Repository.FranquiaRepository;
import com.franquias.Repository.GerenteRepository;
import com.franquias.Repository.PedidoRepository;
import com.franquias.Repository.ProdutoRepository;
import com.franquias.Repository.VendedorRepository;
import com.franquias.Service.AutenticacaoService;
import com.franquias.Service.ClienteService;
import com.franquias.Service.EquipeService;
import com.franquias.Service.EstoqueService;
import com.franquias.Service.FranquiaService;
import com.franquias.Service.PedidoService;
import com.franquias.Service.RelatorioService;
import com.franquias.View.*;

public class AplicacaoPrincipal {

    private JFrame telaPricipal;
    private JMenuBar menuBarPrincipal;
    private JMenu menuAtualDoPainel;

    private JPanel painelDeConteudo;
    private CardLayout cardLayout;

    private final DonoRepository donoRepository = new DonoPersistence();
    private final FranquiaRepository franquiaRepository = new FranquiaPersistence();
    private final GerenteRepository gerenteRepository = new GerentePersistence();
    private final VendedorRepository vendedorRepository = new VendedorPersistence();
    private final ProdutoRepository produtoRepository = new ProdutoPersistence();
    private final PedidoRepository pedidoRepository = new PedidoPersistence();
    private final ClienteRepository clienteRepository = new ClientePersistence();

    private LoginController loginController;
    private VendedorController vendedorController;
    private GerenteController gerenteController;
    private DonoController donoController;
    private PedidoController pedidoController;

    public final int WIDTH = 600;
    public final int HEIGHT = 400;

    public void iniciar() {
        telaPricipal = new JFrame("Franquia");
        telaPricipal.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        telaPricipal.setSize(WIDTH, HEIGHT);

        menuBarPrincipal = new JMenuBar();
        JMenu menuSistema = new JMenu("Sistema");
        JMenuItem itemDeslogar = new JMenuItem("Deslogar");
        itemDeslogar.addActionListener(e -> mostrarTela("LOGIN"));

        menuSistema.add(itemDeslogar);
        menuBarPrincipal.add(menuSistema);

        telaPricipal.setJMenuBar(menuBarPrincipal);

        cardLayout = new CardLayout();
        painelDeConteudo = new JPanel(cardLayout);

        AutenticacaoService autenticacaoService = new AutenticacaoService(
                donoRepository, gerenteRepository, vendedorRepository);
        EstoqueService estoqueService = new EstoqueService(produtoRepository);
        RelatorioService relatorioService = new RelatorioService(pedidoRepository, vendedorRepository);
        PedidoService pedidoService = new PedidoService(pedidoRepository, estoqueService, vendedorRepository);
        ClienteService clienteService = new ClienteService(clienteRepository);
        FranquiaService franquiaService = new FranquiaService(
                donoRepository, franquiaRepository, gerenteRepository, autenticacaoService);
        EquipeService equipeService = new EquipeService(
                gerenteRepository, vendedorRepository, autenticacaoService, relatorioService);

        loginController = new LoginController(autenticacaoService);
        vendedorController = new VendedorController(pedidoService, clienteService);
        gerenteController = new GerenteController(equipeService, estoqueService, pedidoService, relatorioService);
        donoController = new DonoController(franquiaService, relatorioService);
        pedidoController = new PedidoController(pedidoService);

        PainelCadastroDono painelCadastroDono = new PainelCadastroDono(this, donoController);
        PainelLogin painelLogin = new PainelLogin(this, loginController);
        PainelVendedor painelVendedor = new PainelVendedor(this, vendedorController, pedidoController);
        PainelGerente painelGerente = new PainelGerente(this, gerenteController, pedidoController);
        PainelDono painelDono = new PainelDono(this, donoController);

        painelDeConteudo.add(painelCadastroDono, "CADASTRO_DONO");
        painelDeConteudo.add(painelLogin, "LOGIN");
        painelDeConteudo.add(painelVendedor, "VENDEDOR");
        painelDeConteudo.add(painelGerente, "GERENTE");
        painelDeConteudo.add(painelDono, "DONO");

        telaPricipal.add(painelDeConteudo);

        if (!donoRepository.hasDono()) {
            mostrarTela("CADASTRO_DONO");
        } else {
            mostrarTela("LOGIN");
        }

        telaPricipal.setLocationRelativeTo(null);
        telaPricipal.setVisible(true);
    }

    public void mostrarTela(String nomePainel) {
        if (menuAtualDoPainel != null) {
            menuBarPrincipal.remove(menuAtualDoPainel);
            menuAtualDoPainel = null;
        }

        cardLayout.show(painelDeConteudo, nomePainel);

        JPanel painelAtual = null;
        for (Component comp : painelDeConteudo.getComponents()) {
            if (comp.isVisible()) {
                painelAtual = (JPanel) comp;
                break;
            }
        }

        if (painelAtual instanceof PainelBase) {
            menuAtualDoPainel = ((PainelBase) painelAtual).getMenu();
            if (menuAtualDoPainel != null) {
                menuBarPrincipal.add(menuAtualDoPainel);
            }
        }

        menuBarPrincipal.revalidate();
        menuBarPrincipal.repaint();
    }

    public JFrame getFramePrincipal() {
        return telaPricipal;
    }

    public VendedorController getVendedorController() {
        return vendedorController;
    }

    public GerenteController getGerenteController() {
        return gerenteController;
    }

    public DonoController getDonoController() {
        return donoController;
    }

    public PedidoController getPedidoController() {
        return pedidoController;
    }
}
