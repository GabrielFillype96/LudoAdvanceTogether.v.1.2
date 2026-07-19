package gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameStatusBar extends JPanel {
    private String message = "";
    private Color baseColor = Color.WHITE;
    private int alpha = 0; 
    private final Timer fadeTimer;
    private final Timer rollTimer; // NOVO: Timer específico para o caça-níquel
    private final double scale;

    private int animType = 0; // NOVO: 0 = Fade comum, 1 = Caça-níquel
    private int offsetY = 0;  // NOVO: Controla a posição vertical do texto

    public GameStatusBar(double scale) {
        this.scale = scale;
        setOpaque(false);
        
        int width = (int) (220 * scale);
        int height = (int) (32 * scale);
        setPreferredSize(new Dimension(width, height));

        // Timer do Fade comum
        fadeTimer = new Timer(15, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 17; 
                if (alpha >= 255) {
                    alpha = 255;
                    fadeTimer.stop();
                }
                repaint();
            }
        });

        // NOVO: Timer que faz o texto subir de baixo para o centro
        rollTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                offsetY -= 5; // Velocidade da subida
                if (offsetY <= 0) {
                    offsetY = 0;
                    rollTimer.stop();
                }
                repaint();
            }
        });
    }

    // Mantém o comportamento original para mensagens padrão do jogo
    public void updateStatus(String newMessage, Color color) {
        if (fadeTimer.isRunning()) fadeTimer.stop();
        if (rollTimer.isRunning()) rollTimer.stop();
        
        this.animType = 0; 
        this.offsetY = 0;
        this.message = newMessage.toUpperCase();
        this.baseColor = color;
        this.alpha = 0; 
        repaint();
        fadeTimer.start(); 
    }

    // NOVO MÉTODO: Ativa exclusivamente o efeito caça-níquel vertical
    public void updateSlotMachineStatus(String newMessage, Color color) {
        if (fadeTimer.isRunning()) fadeTimer.stop();
        if (rollTimer.isRunning()) rollTimer.stop();
        
        this.animType = 1; 
        this.message = newMessage.toUpperCase();
        this.baseColor = color;
        this.alpha = 255; // Totalmente visível durante o giro
        this.offsetY = getHeight(); // Começa abaixo da barra (escondido)
        
        repaint();
        rollTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (message.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. Desenha o fundo em formato de pílula
        g2.setColor(new Color(20, 20, 20, 140));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), (int)(10 * scale), (int)(10 * scale));

        // 2. Aplica a cor baseada na animação ativa
        Color animatedColor;
        if (animType == 0) {
            animatedColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);
        } else {
            animatedColor = baseColor; 
        }
        g2.setColor(animatedColor);
        g2.setFont(new Font("SansSerif", Font.BOLD, (int) (11 * scale)));

        // Centraliza o texto no plano horizontal e vertical
        FontMetrics metrics = g2.getFontMetrics();
        int textX = (getWidth() - metrics.stringWidth(message)) / 2;
        int textY = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

        // IMPORTANTE: Cria uma máscara para o texto não aparecer fora da caixinha enquanto sobe
        g2.setClip(0, 0, getWidth(), getHeight());

        // Desenha aplicando o deslocamento vertical
        g2.drawString(message, textX, textY + offsetY);
        g2.dispose();
    }
}