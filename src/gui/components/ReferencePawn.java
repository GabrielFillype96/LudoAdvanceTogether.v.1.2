package gui.components;

import gui.events.WobbleListener;

import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Cursor;
import java.awt.BasicStroke;
import java.awt.geom.GeneralPath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ReferencePawn extends JLabel {
    private Color baseColor;
    private Color disabledColor = new Color(130, 130, 130);
    private Color goldenColor = new Color(245, 185, 30);
    
    private double actualAngle = 0;
    private boolean isInclinedToRight = true;
    private Timer wobbleTimer;
    private String currentVisualState = "NORMAL";
    private int pawnNumber;
    private double scale;
    
    private boolean isCenterPawn = false;
    private float alpha = 1.0f; // Controle dinâmico de transparência para a animação
    private boolean isCrowned = false; // Estado de coroa (peão que atingiu o centro)

    public ReferencePawn(Color baseColor, int pawnNumber, double scale) {
        this.baseColor = baseColor != null ? baseColor : new Color(50, 140, 220);
        this.pawnNumber = pawnNumber;
        this.scale = scale;

        int referencePawnWidth = (int) (50 * scale);
        int referencePawnHeight = (int) (60 * scale);

        this.setSize(referencePawnWidth, referencePawnHeight);

        WobbleListener wobbleListener = new WobbleListener(this); 
        this.wobbleTimer = new Timer(50, wobbleListener);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!"DESABILITADO".equals(currentVisualState)) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
        });
    }

    // Construtor auxiliar para aceitar String de cor (mantém compatibilidade)
    public ReferencePawn(String colorName, int pawnNumber, double scale) {
        this(parseColor(colorName), pawnNumber, scale);
    }

    // Construtor legado de compatibilidade (ignora caminhos de imagens)
    public ReferencePawn(String stdImgPath, String disabledImgPath, String goldenImgPath, int pawnNumber, double scale) {
        this(parseColorFromPath(stdImgPath), pawnNumber, scale);
    }

    private static Color parseColor(String colorName) {
        if (colorName == null) return new Color(50, 140, 220);
        switch (colorName.toLowerCase()) {
            case "roxo": return new Color(127, 90, 190);
            case "rosa": return new Color(225, 85, 125);
            case "amarelo": return new Color(245, 175, 20);
            case "azul": 
            default: return new Color(50, 140, 220);
        }
    }

    private static Color parseColorFromPath(String path) {
        if (path == null) return new Color(50, 140, 220);
        String lower = path.toLowerCase();
        if (lower.contains("purple") || lower.contains("roxo")) return new Color(127, 90, 190);
        if (lower.contains("pink") || lower.contains("rosa")) return new Color(225, 85, 125);
        if (lower.contains("yellow") || lower.contains("amarelo")) return new Color(245, 175, 20);
        return new Color(50, 140, 220);
    }

    public void setCenterPawn(boolean isCenter) {
        this.isCenterPawn = isCenter;
        repaint();
    }

    public void setAlpha(float alpha) {
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
        repaint();
    }

    public boolean isCrowned() {
        return this.isCrowned;
    }

    public void setCrowned(boolean crowned) {
        this.isCrowned = crowned;
        if (crowned) {
            this.currentVisualState = "DOURADO";
        }
        repaint();
    }

    public void setVisualState(String pawnState) {
        this.currentVisualState = pawnState != null ? pawnState.toUpperCase() : "NORMAL"; 
        if ("DOURADO".equals(this.currentVisualState)) {
            this.isCrowned = true;
        }

        if (getMousePosition() != null) {
            setCursor("DESABILITADO".equals(this.currentVisualState) ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        repaint(); 
    }

    public void startReferencePawnWobble() {
        if (wobbleTimer != null && wobbleTimer.isRunning()) return;
        wobbleTimer.start();
    }

    public void stopReferencePawnWobble() {
        if (wobbleTimer != null) {
            wobbleTimer.stop();
        }
        actualAngle = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        super.paintComponent(g2);
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = this.getWidth();
        int h = this.getHeight();
        int centerX = w / 2;
        int centerY = (h / 2) - (int)(4 * scale);

        // Aplica transparência dinamicamente para animação do carrossel
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Determina a cor base conforme o estado visual
        Color activePawnColor;
        switch (currentVisualState) {
            case "DESABILITADO":
                activePawnColor = disabledColor;
                break;
            case "DOURADO":
                activePawnColor = goldenColor;
                break;
            case "NORMAL":
            default:
                activePawnColor = baseColor;
                break;
        }

        // 1. DESENHO DO PEÃO VETORIAL 3D (com suporte a rotação Wobble)
        Graphics2D gPawn = (Graphics2D) g2.create();
        gPawn.rotate(Math.toRadians(actualAngle), centerX, centerY);
        
        // Desenha usando o motor gráfico 3D vetorial unificado
        PlayerPawn.draw3DPawn(gPawn, 0, 0, w, h - (int)(4 * scale), activePawnColor, alpha);

        // Desenho da Coroa no Peão de Referência quando atinge o centro
        if (isCrowned || "DOURADO".equals(currentVisualState)) {
            drawCrown(gPawn, centerX, (int)(8 * scale), (int)(18 * scale));
        }

        gPawn.dispose();

        // 2. BADGE/SELO NUMÉRICO NA BASE
        int badgeRadius = (int) (Math.min(w, h) * 0.16);
        int badgeX = centerX - badgeRadius;
        int badgeY = h - (badgeRadius * 2) - (int)(2 * scale);

        g2.setColor(isCenterPawn ? new Color(212, 160, 23) : new Color(50, 50, 50, 220));
        g2.fillOval(badgeX, badgeY, badgeRadius * 2, badgeRadius * 2);

        g2.setColor(Color.WHITE);
        g2.drawOval(badgeX, badgeY, badgeRadius * 2, badgeRadius * 2);

        g2.setFont(new Font("SansSerif", Font.BOLD, (int) (badgeRadius * 1.1)));
        FontMetrics fm = g2.getFontMetrics();
        String numStr = String.valueOf(this.pawnNumber);
        int textX = centerX - (fm.stringWidth(numStr) / 2);
        int textY = badgeY + badgeRadius + (fm.getAscent() / 2) - (int)(1.5 * scale);
        g2.drawString(numStr, textX, textY);

        g2.dispose();
    }

    private void drawCrown(Graphics2D g2, int centerX, int topY, int size) {
        int half = size / 2;
        int crownHeight = (int)(size * 0.65);
        
        GeneralPath crown = new GeneralPath();
        crown.moveTo(centerX - half, topY + crownHeight);
        crown.lineTo(centerX - half, topY);
        crown.lineTo(centerX - half / 2, topY + crownHeight / 2);
        crown.lineTo(centerX, topY);
        crown.lineTo(centerX + half / 2, topY + crownHeight / 2);
        crown.lineTo(centerX + half, topY);
        crown.lineTo(centerX + half, topY + crownHeight);
        crown.closePath();

        g2.setColor(new Color(255, 215, 0));
        g2.fill(crown);
        
        g2.setColor(new Color(180, 130, 10));
        g2.setStroke(new BasicStroke((float)(1.0 * scale)));
        g2.draw(crown);

        // Joias no topo das 3 pontas da coroa
        g2.setColor(Color.WHITE);
        int gemSize = Math.max(2, (int)(2.5 * scale));
        g2.fillOval(centerX - half - gemSize/2, topY - gemSize/2, gemSize, gemSize);
        g2.fillOval(centerX - gemSize/2, topY - gemSize/2, gemSize, gemSize);
        g2.fillOval(centerX + half - gemSize/2, topY - gemSize/2, gemSize, gemSize);
    }

    public Color getBaseColor() { return this.baseColor; }
    public void setBaseColor(Color baseColor) { this.baseColor = baseColor; repaint(); }
    public int getPawnNumber() { return this.pawnNumber; }
    public double getActualAngle() { return this.actualAngle; }
    public void setActualAngle(double actualAngle) { this.actualAngle = actualAngle; }
    public boolean isInclinedToRight() { return this.isInclinedToRight; }
    public void setInclinedToRight(boolean isInclinedToRight) { this.isInclinedToRight = isInclinedToRight; }
}