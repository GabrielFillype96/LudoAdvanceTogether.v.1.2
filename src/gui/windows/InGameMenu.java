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
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class InGameMenu extends JDialog {

    private WindowManager windowManager;

    public InGameMenu(Component parentComponent, WindowManager windowManager) {
        super((Frame) SwingUtilities.getWindowAncestor(parentComponent), "Menu do Jogo", true);
        this.windowManager = windowManager;

        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fundo roxo escuro
                g2.setColor(GameColors.PURPLE_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

                // Borda dourada externa
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

        // Botões do padrão CustomButton
        CustomButton btnResume = new CustomButton("VOLTAR AO JOGO");
        btnResume.addActionListener(e -> dispose());
        gbc.gridy = 1;
        contentPanel.add(btnResume, gbc);

        CustomButton btnRestart = new CustomButton("REINICIAR PARTIDA");
        btnRestart.addActionListener(e -> {
            dispose();
            if (this.windowManager != null) {
                this.windowManager.iniciarNovoJogoOffline();
            }
        });
        gbc.gridy = 2;
        contentPanel.add(btnRestart, gbc);

        CustomButton btnMainMenu = new CustomButton("MENU PRINCIPAL");
        btnMainMenu.addActionListener(e -> {
            dispose();
            if (this.windowManager != null) {
                this.windowManager.exibirMenuPrincipal();
            }
        });
        gbc.gridy = 3;
        contentPanel.add(btnMainMenu, gbc);

        CustomButton btnExit = new CustomButton("SAIR DO JOGO");
        btnExit.addActionListener(e -> System.exit(0));
        gbc.gridy = 4;
        contentPanel.add(btnExit, gbc);

        add(contentPanel);
        pack();
        setLocationRelativeTo(parentComponent);
    }
}