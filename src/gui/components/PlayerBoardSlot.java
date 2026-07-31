package gui.components;

import javax.swing.*;
import java.awt.*;

public class PlayerBoardSlot extends JPanel {
    private final String playerName;
    private final Color playerColor;

    private boolean activeTurn = false;
    private float dashPhase = 0f;
    private final Timer marqueeTimer;

    // Feixe (30px) + Espaço (50px) = 80px no total
    private static final float[] LASER_PATTERN = { 30.0f, 50.0f };
    private static final float PATTERN_LENGTH = 80.0f;

    public PlayerBoardSlot(String name, Color color, double scale) {
        this.playerName = (name != null && !name.trim().isEmpty()) ? name.toUpperCase() : "JOGADOR";
        this.playerColor = color;

        setOpaque(false);
        
        int width = (int) (140 * scale);
        int height = (int) (28 * scale);
        setPreferredSize(new Dimension(width, height));

        // Timer de animação suave
        this.marqueeTimer = new Timer(16, e -> {
            // Avança o phase mantendo sempre entre 0 e 80 (nunca negativo)
            dashPhase = (dashPhase + 2.0f) % PATTERN_LENGTH;
            
            // Redesenha o painel pai (BoardScreen) para atualizar o slot rotacionado no tabuleiro
            if (getParent() != null) {
                getParent().repaint();
            } else {
                repaint();
            }
        });
    }

    /**
     * Liga ou desliga a animação do slot
     */
    public void setActiveTurn(boolean active) {
        this.activeTurn = active;
        if (active) {
            if (!marqueeTimer.isRunning()) marqueeTimer.start();
        } else {
            if (marqueeTimer.isRunning()) marqueeTimer.stop();
            dashPhase = 0f;
        }
        
        if (getParent() != null) {
            getParent().repaint();
        } else {
            repaint();
        }
    }

    public boolean isActiveTurn() {
        return activeTurn;
    }

    /**
     * Renderização direta do slot na tela
     */
    public void drawSlot(Graphics2D g2) {
        int w = getWidth();
        int h = getHeight();

        if (w <= 0 || h <= 0) return;

        // 1. Fundo escuro translúcido
        if (activeTurn) {
            g2.setColor(new Color(35, 35, 35, 240));
        } else {
            g2.setColor(new Color(25, 25, 25, 220));
        }
        g2.fillRoundRect(0, 0, w, h, 12, 12);

        // 2. Moldura e Efeito Visual
        if (activeTurn) {
            // A) Linha base estática e sutil no fundo
            g2.setColor(new Color(playerColor.getRed(), playerColor.getGreen(), playerColor.getBlue(), 60));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, w - 2, h - 2, 12, 12);

            // Para inverter a direção sem usar valor negativo:
            float currentPhase = (PATTERN_LENGTH - dashPhase) % PATTERN_LENGTH;

            // --- CAMADA 1: GLOW (Brilho focado e macio) ---
            BasicStroke glowStroke = new BasicStroke(
                4.5f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                10.0f,
                LASER_PATTERN,
                currentPhase // Sempre positivo!
            );
            g2.setColor(new Color(playerColor.getRed(), playerColor.getGreen(), playerColor.getBlue(), 140));
            g2.setStroke(glowStroke);
            g2.drawRoundRect(1, 1, w - 2, h - 2, 12, 12);

            // --- CAMADA 2: NÚCLEO LASER (Linha viva e brilhante) ---
            BasicStroke laserStroke = new BasicStroke(
                2.0f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND,
                10.0f,
                LASER_PATTERN,
                currentPhase // Sempre positivo!
            );
            g2.setColor(playerColor.brighter());
            g2.setStroke(laserStroke);
            g2.drawRoundRect(1, 1, w - 2, h - 2, 12, 12);

        } else {
            // Moldura Padrão Estática (Jogadores fora do turno)
            g2.setColor(playerColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, w - 2, h - 2, 12, 12);
        }

        // 3. Nome do jogador centralizado
        g2.setColor(activeTurn ? Color.WHITE : new Color(190, 190, 190));
        g2.setFont(new Font("SansSerif", Font.BOLD, (int) (8 * (w / 150.0 + 0.8))));
        
        FontMetrics metrics = g2.getFontMetrics();
        int textX = (w - metrics.stringWidth(playerName)) / 2;
        int textY = ((h - metrics.getHeight()) / 2) + metrics.getAscent();
        
        g2.drawString(playerName, textX, textY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawSlot(g2);
        g2.dispose();
    }
}