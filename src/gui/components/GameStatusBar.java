package gui.components;

import control.GameStatusManager;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GameStatusBar extends JPanel {

    public enum TextAnimType {
        FADE,           // Suave e padrão
        SLOT_MACHINE,   // Giro vertical de caça-níquel
        CASCADE_DROP,   // Letras caindo em cascata
        SPLIT_MERGE     // Frase vindo dos dois lados e se fundindo
    }

    private String message = "";
    private Color baseColor = Color.WHITE;
    private String currentIconPath = null;
    private ImageIcon currentIcon = null;
    private final double scale;

    // Animação
    private TextAnimType currentAnim = TextAnimType.FADE;
    private float animProgress = 1.0f; // 0.0 (início) a 1.0 (fim)
    private final Timer animTimer;

    public GameStatusBar(double scale) {
        this.scale = scale;
        setOpaque(false);
        
        int width = (int) (220 * scale);
        int height = (int) (46 * scale);
        setPreferredSize(new Dimension(width, height));

        // Timer de animação rodando a ~60 FPS (16ms)
        animTimer = new Timer(16, e -> {
            animProgress += 0.035f; // ~450ms de duração para a animação ser bem perceptível
            if (animProgress >= 1.0f) {
                animProgress = 1.0f;
                ((Timer) e.getSource()).stop();
            }
            repaint();
        });
    }

    public void updateStatus(GameStatusManager status, Object... args) {
        TextAnimType anim = TextAnimType.FADE;

        if (status == GameStatusManager.SORTEIO_GIRO) {
            anim = TextAnimType.SLOT_MACHINE;
        } else if (status.name().contains("AZAR") || status.name().contains("PENALIDADE")) {
            anim = TextAnimType.CASCADE_DROP;
        } else if (status.name().contains("CARTA") || status.name().contains("ATAQUE") || status.name().contains("SUA_VEZ")) {
            anim = TextAnimType.SPLIT_MERGE;
        }

        updateStatus(status.format(args), status.getColor(), status.getIconPath(), anim);
    }

    public void updateStatus(String newMessage, Color color) {
        updateStatus(newMessage, color, currentIconPath, TextAnimType.FADE);
    }

    public void updateStatus(String newMessage, Color color, String iconPath, TextAnimType animType) {
        if (animTimer.isRunning()) animTimer.stop();

        this.message = newMessage.toUpperCase();
        this.baseColor = color;
        this.currentAnim = animType;
        this.animProgress = 0.0f;

        // Mantém a instância do ícone se for o mesmo (evita resetar GIFs de animação)
        boolean sameIcon = (iconPath != null && iconPath.equals(this.currentIconPath));
        if (!sameIcon) {
            this.currentIconPath = iconPath;
            if (iconPath != null && !iconPath.isEmpty()) {
                URL url = getClass().getResource(iconPath);
                this.currentIcon = (url != null) ? new ImageIcon(url) : null;
                if (this.currentIcon != null) this.currentIcon.setImageObserver(this);
            } else {
                this.currentIcon = null;
            }
        }

        repaint();
        animTimer.start();
    }

    public void updateSlotMachineStatus(String newMessage, Color color) {
        updateStatus(newMessage, color, currentIconPath, TextAnimType.SLOT_MACHINE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (message.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int h = getHeight();
        int w = getWidth();

        int alpha = (currentAnim == TextAnimType.FADE) ? (int) (animProgress * 255) : 255;
        Color animatedColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);
        Color darkBg = new Color(15, 15, 20, 230);
        Color borderColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), Math.min(alpha, 130));

        // --- 1. DESENHAR CÁPSULA DA BARRA ---
        g2.setColor(darkBg);
        g2.fillRoundRect(0, 0, w, h, h, h);

        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);

        // --- 2. ESPAÇAMENTO E ÍCONE/GIF ---
        int margin = (int) (10 * scale);
        int iconSize = (int) (24 * scale);
        int iconSpacing = (currentIcon != null) ? (int) (8 * scale) : 0;

        int textX = margin;
        int textW = w - (margin * 2);

        if (currentIcon != null) {
            int iconX = margin;
            int iconY = (h - iconSize) / 2;
            g2.drawImage(currentIcon.getImage(), iconX, iconY, iconSize, iconSize, this);

            textX = iconX + iconSize + iconSpacing;
            textW = w - textX - margin;
        }

        // --- 3. QUEBRA DE TEXTO EM MÚLTIPLAS LINHAS (WORD WRAP) ---
        Font font = new Font("SansSerif", Font.BOLD, (int) (11 * scale));
        g2.setFont(font);
        FontMetrics metrics = g2.getFontMetrics();

        List<String> lines = new ArrayList<>();
        String[] words = message.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = (currentLine.length() == 0) ? word : currentLine + " " + word;
            if (metrics.stringWidth(testLine) > textW) {
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

        g2.setClip(textX, 0, textW, h);

        int lineHeight = metrics.getHeight();
        int totalTextHeight = lines.size() * lineHeight;
        int startY = ((h - totalTextHeight) / 2) + metrics.getAscent();

        // --- 4. RENDERIZAÇÃO DAS ANIMAÇÕES MULTI-LINHAS ---
        switch (currentAnim) {

            case SLOT_MACHINE: {
                // Desliza verticalmente de baixo para o centro
                int offsetY = (int) ((1.0f - animProgress) * (h * 0.7f));
                g2.setColor(baseColor);

                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    int lineX = textX + (textW - metrics.stringWidth(line)) / 2;
                    int lineY = startY + (i * lineHeight) + offsetY;
                    g2.drawString(line, lineX, lineY);
                }
                break;
            }

            case CASCADE_DROP: {
                // Letras caindo uma a uma respeitando as linhas
                int globalCharIndex = 0;
                int totalChars = message.length();

                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    int lineX = textX + (textW - metrics.stringWidth(line)) / 2;
                    int currentX = lineX;
                    int lineY = startY + (i * lineHeight);

                    for (int j = 0; j < line.length(); j++) {
                        char ch = line.charAt(j);
                        String charStr = String.valueOf(ch);
                        int charW = metrics.charWidth(ch);

                        float charDelay = (float) globalCharIndex / Math.max(1, totalChars) * 0.5f;
                        float charProgress = Math.max(0.0f, Math.min(1.0f, (animProgress - charDelay) / 0.5f));

                        int dropY = (int) ((1.0f - charProgress) * -16 * scale);
                        int charAlpha = (int) (charProgress * 255);

                        g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), charAlpha));
                        g2.drawString(charStr, currentX, lineY + dropY);

                        currentX += charW;
                        globalCharIndex++;
                    }
                    globalCharIndex++; // Espaço entre linhas
                }
                break;
            }

            case SPLIT_MERGE: {
                // Metade da frase vem da esquerda, a outra vem da direita
                int offsetX = (int) ((1.0f - animProgress) * (25 * scale));
                g2.setColor(animatedColor);

                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    int midIndex = line.length() / 2;

                    String leftPart = line.substring(0, midIndex);
                    String rightPart = line.substring(midIndex);

                    int leftWidth = metrics.stringWidth(leftPart);
                    int lineX = textX + (textW - metrics.stringWidth(line)) / 2;
                    int lineY = startY + (i * lineHeight);

                    g2.drawString(leftPart, lineX - offsetX, lineY);
                    g2.drawString(rightPart, lineX + leftWidth + offsetX, lineY);
                }
                break;
            }

            case FADE:
            default: {
                // Fade In padrão mantendo o texto totalmente centralizado
                g2.setColor(animatedColor);
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    int lineX = textX + (textW - metrics.stringWidth(line)) / 2;
                    int lineY = startY + (i * lineHeight);
                    g2.drawString(line, lineX, lineY);
                }
                break;
            }
        }

        g2.dispose();
    }
}