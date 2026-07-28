package gui.components;

import control.GameStatusManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GameStatusBar extends JPanel {
    private String message = "";
    private Color baseColor = Color.WHITE;
    private String currentIconPath = null;
    private ImageIcon currentIcon = null; // Guardará a imagem/GIF carregado
    private int alpha = 0; 
    private final Timer fadeTimer;
    private final Timer rollTimer; 
    private final double scale;

    private int animType = 0; 
    private int offsetY = 0;  

    public GameStatusBar(double scale) {
        this.scale = scale;
        setOpaque(false);
        
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

    public void updateStatus(GameStatusManager status, Object... args) {
        updateStatus(status.format(args), status.getColor(), status.getIconPath());
    }

    public void updateStatus(String newMessage, Color color) {
        updateStatus(newMessage, color, null);
    }

    public void updateStatus(String newMessage, Color color, String iconPath) {
        if (fadeTimer.isRunning()) fadeTimer.stop();
        if (rollTimer.isRunning()) rollTimer.stop();
        
        this.animType = 0; 
        this.offsetY = 0;
        this.message = newMessage.toUpperCase();
        this.baseColor = color;
        this.currentIconPath = iconPath;

        // Carrega o ícone/GIF a partir dos recursos
        if (iconPath != null && !iconPath.isEmpty()) {
            URL url = getClass().getResource(iconPath);
            if (url != null) {
                this.currentIcon = new ImageIcon(url);
                // Garante que GIFs continuem animando no Swing
                this.currentIcon.setImageObserver(this); 
            } else {
                this.currentIcon = null;
            }
        } else {
            this.currentIcon = null;
        }

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
        this.currentIconPath = null;
        this.currentIcon = null;
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

        Color animatedColor = (animType == 0) ? new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha) : baseColor;

        // Fundo
        g2.setColor(new Color(15, 15, 20, 230));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

        // Borda
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(animatedColor.getRed(), animatedColor.getGreen(), animatedColor.getBlue(), Math.min(alpha, 130)));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());

        g2.setColor(animatedColor);

        // Define a fonte para o texto
        Font font = new Font("SansSerif", Font.BOLD, (int) (11 * scale));
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics();

        // Configurações da Imagem
        int iconSize = (int) (24 * scale);
        int iconSpacing = (currentIcon != null) ? (int) (8 * scale) : 0;
        int maxLineWidth = getWidth() - (int) (30 * scale) - (currentIcon != null ? (iconSize + iconSpacing) : 0);

        // Quebra de texto por linhas
        List<String> lines = new ArrayList<>();
        String[] words = message.split(" ");
        StringBuilder currentLine = new StringBuilder();

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

        g2.setClip(0, 0, getWidth(), getHeight());

        int lineHeight = metrics.getHeight();
        int totalTextHeight = lines.size() * lineHeight;
        int startY = ((getHeight() - totalTextHeight) / 2) + metrics.getAscent();

        // Aplica transparência da animação para a imagem se necessário
        if (animType == 0) {
            float floatAlpha = alpha / 255.0f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, floatAlpha));
        }

        // Desenhar Ícone (se existir)
        int textXOffset = 0;
        if (currentIcon != null) {
            int iconX = (int) (12 * scale);
            int iconY = (getHeight() - iconSize) / 2 + offsetY;
            g2.drawImage(currentIcon.getImage(), iconX, iconY, iconSize, iconSize, this);
            textXOffset = iconX + iconSize + iconSpacing;
        }

        // Desenhar Texto
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int textX;
            if (currentIcon != null) {
                textX = textXOffset; // Alinhado à direita da imagem
            } else {
                textX = (getWidth() - metrics.stringWidth(line)) / 2; // Centralizado
            }
            int textY = startY + (i * lineHeight) + offsetY;
            g2.drawString(line, textX, textY);
        }

        g2.dispose();
    }
}