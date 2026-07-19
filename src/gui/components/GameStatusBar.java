package gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class GameStatusBar extends JPanel {
    private String message = "";
    private Color baseColor = Color.WHITE;
    private int alpha = 0; 
    private final Timer fadeTimer;
    private final Timer rollTimer; 
    private final double scale;

    private int animType = 0; 
    private int offsetY = 0;  

    public GameStatusBar(double scale) {
        this.scale = scale;
        setOpaque(false);
        
        // =========================================================================
        // ALTURA AMPLIADA: De 32 para 46 para permitir duas linhas confortavelmente
        // =========================================================================
        int width = (int) (220 * scale);
        int height = (int) (46 * scale);
        setPreferredSize(new Dimension(width, height));

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

        rollTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                offsetY -= 5; 
                if (offsetY <= 0) {
                    offsetY = 0;
                    rollTimer.stop();
                }
                repaint();
            }
        });
    }

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

    public void updateSlotMachineStatus(String newMessage, Color color) {
        if (fadeTimer.isRunning()) fadeTimer.stop();
        if (rollTimer.isRunning()) rollTimer.stop();
        
        this.animType = 1; 
        this.message = newMessage.toUpperCase();
        this.baseColor = color;
        this.alpha = 255; 
        this.offsetY = getHeight(); 
        
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

        // 1. ANIMAÇÃO DE COR & BRILHO REATIVO
        Color animatedColor = (animType == 0) ? new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha) : baseColor;

        // Fundo estilo "Gamer Glass" (Fundo ultra escuro e bem visível)
        g2.setColor(new Color(15, 15, 20, 230));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

        // Borda com Neon sutil que brilha na cor do evento (Ex: Vermelho se errou, Verde se acertou)
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(animatedColor.getRed(), animatedColor.getGreen(), animatedColor.getBlue(), Math.min(alpha, 130)));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());

        // 2. CONFIGURAÇÃO DO TEXTO MODERNO
        g2.setColor(animatedColor);
        g2.setFont(new Font("SansSerif", Font.BOLD, (int) (11 * scale)));
        FontMetrics metrics = g2.getFontMetrics();

        // 3. ALGORITMO DE QUEBRA AUTOMÁTICA DE TEXTO (WORD WRAP)
        List<String> lines = new ArrayList<>();
        String[] words = message.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int maxLineWidth = getWidth() - (int) (30 * scale); // Margem de segurança interna da pílula

        for (String word : words) {
            String testLine = (currentLine.length() == 0) ? word : currentLine + " " + word;
            if (metrics.stringWidth(testLine) > maxLineWidth) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    lines.add(word); 
                }
            } else {
                currentLine.append((currentLine.length() == 0) ? word : " " + word);
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        // 4. RENDERIZAÇÃO CENTRALIZADA MULTILINHA COM MÁSCARA ANIMADA
        g2.setClip(0, 0, getWidth(), getHeight()); // Previne o texto de vazar para fora ao rolar

        int lineHeight = metrics.getHeight();
        int totalTextHeight = lines.size() * lineHeight;
        // Calcula o ponto Y inicial para que o bloco completo de linhas fique perfeitamente centralizado verticalmente
        int startY = ((getHeight() - totalTextHeight) / 2) + metrics.getAscent();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int textX = (getWidth() - metrics.stringWidth(line)) / 2; // Centraliza linha por linha horizontalmente
            int textY = startY + (i * lineHeight) + offsetY;
            g2.drawString(line, textX, textY);
        }

        g2.dispose();
    }
}