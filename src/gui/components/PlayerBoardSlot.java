package gui.components;

import javax.swing.*;
import java.awt.*;

public class PlayerBoardSlot extends JPanel {
    private final String playerName;
    private final Color playerColor;

    public PlayerBoardSlot(String name, Color color, double scale) {
        // Garante que o nome não venha vazio
        this.playerName = (name != null && !name.trim().isEmpty()) ? name.toUpperCase() : "JOGADOR";
        this.playerColor = color;

        setOpaque(false);
        
        // Define o tamanho do slot baseado na escala do tabuleiro
        int width = (int) (140 * scale);
        int height = (int) (28 * scale);
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Ativa suavização de serrilhado nas bordas e textos
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. Desenha o fundo escuro translúcido para dar contraste
        g2.setColor(new Color(25, 25, 25, 220));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

        // 2. Desenha a borda fina com a cor do respectivo jogador
        g2.setColor(playerColor);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);

        // 3. Configura e desenha o texto centralizado do nome
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, (int) (12 * (getWidth() / 210.0 + 0.8))));
        
        FontMetrics metrics = g2.getFontMetrics();
        int textX = (getWidth() - metrics.stringWidth(playerName)) / 2;
        int textY = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
        
        g2.drawString(playerName, textX, textY);
        g2.dispose();
    }
}