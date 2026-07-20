package gui.components;

import control.ImageLoaderManager;
import gui.events.WobbleListener;

import javax.swing.ImageIcon;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ReferencePawn extends JLabel {
    private ImageIcon stdReferencePawnIcon; 
    private ImageIcon disabledReferencePawnIcon; 
    private ImageIcon goldenReferencePawnIcon; 
    
    private ImageIcon currentIcon; 
    private double actualAngle = 0;
    private boolean isInclinedToRight = true;
    private Timer wobbleTimer;
    private String currentVisualState = "NORMAL";
    private int pawnNumber;
    private double scale;
    
    private boolean isCenterPawn = false;
    private float alpha = 1.0f; // Controle dinâmico de transparência para a animação

    public ReferencePawn(String stdReferencePawnImgPath, String disabledReferencePawnImgPath, String goldenReferencePawnImgPath, int pawnNumber, double scale) {
        this.pawnNumber = pawnNumber;
        this.scale = scale;

        int referencePawnWidth = (int) (50 * scale);
        int referencePawnHeight = (int) (60 * scale);

        this.stdReferencePawnIcon = ImageLoaderManager.loadIcon(stdReferencePawnImgPath, referencePawnWidth, referencePawnHeight);
        this.disabledReferencePawnIcon = ImageLoaderManager.loadIcon(disabledReferencePawnImgPath, referencePawnWidth, referencePawnHeight);
        this.goldenReferencePawnIcon = ImageLoaderManager.loadIcon(goldenReferencePawnImgPath, referencePawnWidth, referencePawnHeight);

        if (this.stdReferencePawnIcon != null) {
            this.currentIcon = this.stdReferencePawnIcon;
        }

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

    public void setCenterPawn(boolean isCenter) {
        this.isCenterPawn = isCenter;
        repaint();
    }

    public void setAlpha(float alpha) {
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
        repaint();
    }

    public void setVisualState(String pawnState) {
        this.currentVisualState = pawnState; 
        
        switch (pawnState) {
            case "NORMAL":
                this.currentIcon = this.stdReferencePawnIcon;
                break;
            case "DESABILITADO":
                this.currentIcon = this.disabledReferencePawnIcon;
                break;
            case "DOURADO":
                this.currentIcon = this.goldenReferencePawnIcon;
                break;
        }

        if (getMousePosition() != null) {
            setCursor("DESABILITADO".equals(pawnState) ? Cursor.getDefaultCursor() : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
        int centerY = (h / 2) - (int)(6 * scale);

        // Aplica o alpha dinâmico interpolado
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // 1. DESENHO DO PEÃO
        if (this.currentIcon != null) {
            Graphics2D gPawn = (Graphics2D) g2.create();
            gPawn.rotate(Math.toRadians(actualAngle), centerX, centerY);
            
            int imgW = (int) (w * 0.72);
            int imgH = (int) (h * 0.73);
            int imgX = (w - imgW) / 2;
            int imgY = 2;
            
            gPawn.drawImage(this.currentIcon.getImage(), imgX, imgY, imgW, imgH, this);
            gPawn.dispose();
        }

        // 2. BADGE/SELO NUMÉRICO NA BASE
        int badgeRadius = (int) (Math.min(w, h) * 0.18);
        int badgeX = centerX - badgeRadius;
        int badgeY = h - (badgeRadius * 2) - 2;

        g2.setColor(isCenterPawn ? new Color(212, 160, 23) : new Color(80, 80, 80));
        g2.fillOval(badgeX, badgeY, badgeRadius * 2, badgeRadius * 2);

        g2.setColor(Color.WHITE);
        g2.drawOval(badgeX, badgeY, badgeRadius * 2, badgeRadius * 2);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, (int) (badgeRadius * 1.1)));
        FontMetrics fm = g2.getFontMetrics();
        String numStr = String.valueOf(this.pawnNumber);
        int textX = centerX - (fm.stringWidth(numStr) / 2);
        int textY = badgeY + badgeRadius + (fm.getAscent() / 2) - 2;
        g2.drawString(numStr, textX, textY);

        g2.dispose();
    }

    public int getPawnNumber() { return this.pawnNumber; }
    public double getActualAngle() { return this.actualAngle; }
    public void setActualAngle(double actualAngle) { this.actualAngle = actualAngle; }
    public boolean isInclinedToRight() { return this.isInclinedToRight; }
    public void setInclinedToRight(boolean isInclinedToRight) { this.isInclinedToRight = isInclinedToRight; }
}