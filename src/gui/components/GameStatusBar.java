package gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameStatusBar extends JPanel {
    private String message = "";
    private Color baseColor = Color.WHITE;
    private int alpha = 0; // Controla a opacidade (0 invisível, 255 totalmente visível)
    private final Timer fadeTimer;
    private final double scale;

    public GameStatusBar(double scale) {
        this.scale = scale;
        setOpaque(false);
        
        // Define o tamanho padrão alinhado com a largura dos outros containers
        int width = (int) (220 * scale);
        int height = (int) (32 * scale);
        setPreferredSize(new Dimension(width, height));

        // Timer que roda a cada 15ms aumentando a opacidade do texto
        fadeTimer = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 17; // Velocidade do surgimento (quanto maior, mais rápido)
                if (alpha >= 255) {
                    alpha = 255;
                    fadeTimer.stop();
                }
                repaint();
            }
        });
    }

    /**
     * Altera o texto e dispara a animação de surgimento suave
     * @param newMessage O texto descritivo da situação
     * @param color A cor temática do texto para o momento
     */
    public void updateStatus(String newMessage, Color color) {
        if (fadeTimer.isRunning()) {
            fadeTimer.stop();
        }
        this.message = newMessage.toUpperCase();
        this.baseColor = color;
        this.alpha = 0; // Reseta para totalmente transparente
        repaint();
        fadeTimer.start(); // Inicia o efeito de fade-in
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (message.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. Desenha o fundo sutil em formato de pílula fina
        g2.setColor(new Color(20, 20, 20, 140));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), (int)(10 * scale), (int)(10 * scale));

        // 2. Aplica a cor do texto combinada com a opacidade atual da animação
        Color animatedColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);
        g2.setColor(animatedColor);
        g2.setFont(new Font("SansSerif", Font.BOLD, (int) (11 * scale)));

        // Centraliza o texto milimetricamente dentro da pílula
        FontMetrics metrics = g2.getFontMetrics();
        int textX = (getWidth() - metrics.stringWidth(message)) / 2;
        int textY = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

        g2.drawString(message, textX, textY);
        g2.dispose();
    }
}