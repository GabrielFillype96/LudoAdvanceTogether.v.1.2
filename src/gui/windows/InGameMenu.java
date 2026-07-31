package gui.windows;

import gui.components.buttons.CustomButton;
import gui.theme.GameColors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class InGameMenu extends JDialog {

    private WindowManager windowManager;

    public InGameMenu(Component parentComponent, WindowManager windowManager) {
        super((Frame) SwingUtilities.getWindowAncestor(parentComponent), "Menu do Jogo", true);
        this.windowManager = windowManager;

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        Window owner = SwingUtilities.getWindowAncestor(parentComponent);
        if (owner != null) {
            setBounds(owner.getBounds());
        } else {
            setSize(800, 600);
            setLocationRelativeTo(parentComponent);
        }

        // --- ATALHO TECLA ESC (FECHAR MENU / VOLTAR AO JOGO) ---
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "fecharMenu");
        
        getRootPane().getActionMap().put("fecharMenu", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Painel Overlay Principal
        JPanel overlayPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Camada escura semitransparente
                g2.setColor(new Color(0, 0, 0, 140)); 
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                g2.dispose();
            }
        };
        overlayPanel.setOpaque(false);

        // Card Central do Menu
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fundo roxo escuro
                g2.setColor(GameColors.PURPLE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

                // Borda dourada
                g2.setColor(GameColors.GOLD_ACCENT);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 24, 24);

                g2.dispose();
            }
        };

        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;

        // Título
        JLabel lblTitle = new JLabel("MENU DO JOGO", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Serif", Font.BOLD, 20));
        lblTitle.setForeground(GameColors.GOLD_ACCENT);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 16, 0);
        contentPanel.add(lblTitle, gbc);

        gbc.insets = new Insets(6, 0, 6, 0);

        // 1. VOLTAR AO JOGO
        CustomButton btnResume = new CustomButton("VOLTAR AO JOGO");
        btnResume.addActionListener(e -> dispose());
        gbc.gridy = 1;
        contentPanel.add(btnResume, gbc);

        // 2. REINICIAR PARTIDA
        CustomButton btnRestart = new CustomButton("REINICIAR PARTIDA");
        btnRestart.addActionListener(e -> {
            if (confirmarAcao("Tem certeza que deseja reiniciar a partida?\nTodo o progresso atual será perdido.")) {
                dispose();
                if (this.windowManager != null) {
                    this.windowManager.iniciarNovoJogoOffline();
                }
            }
        });
        gbc.gridy = 2;
        contentPanel.add(btnRestart, gbc);

        // 3. MENU PRINCIPAL
        CustomButton btnMainMenu = new CustomButton("MENU PRINCIPAL");
        btnMainMenu.addActionListener(e -> {
            if (confirmarAcao("Tem certeza que deseja voltar ao Menu Principal?\nA partida atual será encerrada.")) {
                dispose();
                if (this.windowManager != null) {
                    this.windowManager.exibirMenuPrincipal();
                }
            }
        });
        gbc.gridy = 3;
        contentPanel.add(btnMainMenu, gbc);

        // 4. SAIR DO JOGO
        CustomButton btnExit = new CustomButton("SAIR DO JOGO");
        btnExit.addActionListener(e -> {
            if (confirmarAcao("Deseja realmente fechar o jogo?")) {
                System.exit(0);
            }
        });
        gbc.gridy = 4;
        contentPanel.add(btnExit, gbc);

        overlayPanel.add(contentPanel);
        setContentPane(overlayPanel);
    }

    /**
     * Exibe um modal estilizado de confirmação (Sim / Não)
     */
    private boolean confirmarAcao(String mensagem) {
        final boolean[] confirmado = {false};

        JDialog confirmDialog = new JDialog(this, "Confirmação", true);
        confirmDialog.setUndecorated(true);
        confirmDialog.setBackground(new Color(0, 0, 0, 0));
        confirmDialog.setBounds(this.getBounds());

        // --- ATALHO TECLA ESC NO MODAL DE CONFIRMAÇÃO (CANCELAR / NÃO) ---
        confirmDialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelarConfirmacao");
        
        confirmDialog.getRootPane().getActionMap().put("cancelarConfirmacao", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmado[0] = false;
                confirmDialog.dispose();
            }
        });

        // Overlay escuro interno
        JPanel overlay = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        overlay.setOpaque(false);

        // Card da mensagem
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(GameColors.PURPLE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

                g2.setColor(GameColors.GOLD_ACCENT);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 24, 24);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();

        // Texto da mensagem
        String htmlText = "<html><center>" + mensagem.replace("\n", "<br>") + "</center></html>";
        JLabel lblMsg = new JLabel(htmlText, SwingConstants.CENTER);
        lblMsg.setFont(new Font("Serif", Font.BOLD, 20));
        lblMsg.setForeground(Color.WHITE);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 25, 0);
        card.add(lblMsg, gbc);

        // Botão SIM
        CustomButton btnSim = new CustomButton("SIM");
        btnSim.addActionListener(e -> {
            confirmado[0] = true;
            confirmDialog.dispose();
        });

        // Botão NÃO
        CustomButton btnNao = new CustomButton("NÃO");
        btnNao.addActionListener(e -> {
            confirmado[0] = false;
            confirmDialog.dispose();
        });

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        card.add(btnSim, gbc);

        gbc.gridx = 1;
        card.add(btnNao, gbc);

        overlay.add(card);
        confirmDialog.setContentPane(overlay);
        confirmDialog.setVisible(true);

        return confirmado[0];
    }
}