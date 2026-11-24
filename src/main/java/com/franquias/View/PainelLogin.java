package com.franquias.View;

import javax.swing.*;
import java.awt.*;

import com.franquias.Controller.AplicacaoPrincipal;
import com.franquias.Controller.LoginController;
import com.franquias.Model.entities.Usuários.Dono;
import com.franquias.Model.entities.Usuários.Gerente;
import com.franquias.Model.entities.Usuários.Usuario;
import com.franquias.Model.entities.Usuários.Vendedor;
import com.franquias.exceptions.AuthenticationException;

public class PainelLogin extends JPanel {

    JPasswordField pfSenha;
    JTextField tfEmail;

    public PainelLogin(AplicacaoPrincipal app, LoginController controller) {
        this.setLayout(new GridBagLayout());
        JPanel painelLogin = new JPanel();
        painelLogin.setLayout(new GridBagLayout());
        painelLogin.setBorder(BorderFactory.createTitledBorder("Login"));
        painelLogin.setBackground(Color.lightGray);

        painelLogin.setPreferredSize(new Dimension(250, 200));

        GridBagConstraints gbc;

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelLogin.add(new JLabel("Email:"), gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        tfEmail = new JTextField(15);
        painelLogin.add(tfEmail, gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 2;
        painelLogin.add(new JLabel("Senha:"), gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        pfSenha = new JPasswordField(15);
        painelLogin.add(pfSenha, gbc);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton btEntrar = new JButton("Entrar");

        btEntrar.addActionListener(e -> {
            String email = tfEmail.getText();
            String senha = new String(pfSenha.getPassword());

            if (email.isBlank() || senha.isBlank()) {
                JOptionPane.showMessageDialog(this, "Email e senha não podem estar em branco.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Usuario usuarioAutenticado = controller.realizarLogin(email, senha);
                JOptionPane.showMessageDialog(this, "Bem vindo " + usuarioAutenticado.getNome() + "!",
                        "Autenticação Bem Sucedida!", JOptionPane.INFORMATION_MESSAGE);

                if (usuarioAutenticado instanceof Dono dono) {
                    app.getDonoController().iniciarSessao(dono);
                    app.mostrarTela("DONO");
                } else if (usuarioAutenticado instanceof Gerente gerente) {
                    app.getGerenteController().iniciarSessao(gerente);
                    app.mostrarTela("GERENTE");
                } else if (usuarioAutenticado instanceof Vendedor vendedor) {
                    app.getVendedorController().iniciarSessao(vendedor);
                    app.mostrarTela("VENDEDOR");
                }
            } catch (AuthenticationException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de autenticação!",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        painelLogin.add(btEntrar, gbc);

        this.add(painelLogin);
    }
}
