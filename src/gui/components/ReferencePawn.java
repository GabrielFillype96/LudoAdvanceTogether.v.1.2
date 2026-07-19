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
    
    // CORREÇÃO: Variável própria para controlar o ícone atual sem ativar o desenho padrão do JLabel
    private ImageIcon currentIcon; 
    
    private double actualAngle = 0;
    private boolean isInclinedToRight = true;
    private Timer wobbleTimer;
    private String currentVisualState = "NORMAL";
    private int pawnNumber;
    private double scale;
    
    public ReferencePawn(String stdReferencePawnImgPath, String disabledReferencePawnImgPath, String goldenReferencePawnImgPath, int pawnNumber, double scale) {
        this.pawnNumber = pawnNumber;
        this.scale = scale;

        int referencePawnWidth = (int) (40 * scale);
        int referencePawnHeight = (int) (40 * scale);

        this.stdReferencePawnIcon = ImageLoaderManager.loadIcon(
            stdReferencePawnImgPath, 
            referencePawnWidth, 
            referencePawnHeight
        );
        this.disabledReferencePawnIcon = ImageLoaderManager.loadIcon( 
            disabledReferencePawnImgPath, 
            referencePawnWidth, 
            referencePawnHeight
        );
        this.goldenReferencePawnIcon = ImageLoaderManager.loadIcon( 
            goldenReferencePawnImgPath,
            referencePawnWidth, 
            referencePawnHeight
        );

        // CORREÇÃO: Define a imagem na nossa variável de controle em vez de usar setIcon()
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

    public void setVisualState(String pawnState) {
        this.currentVisualState = pawnState; 
        
        // CORREÇÃO: Atualiza a variável interna em vez do setIcon() do JLabel
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
            if ("DESABILITADO".equals(pawnState)) {
                setCursor(Cursor.getDefaultCursor());
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        } else if ("DESABILITADO".equals(pawnState)) {
            setCursor(Cursor.getDefaultCursor());
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
        
        // Chama o super no início. Como não há ícone definido no JLabel, ele limpa/prepara o componente sem sobrescrever nada
        super.paintComponent(g2);
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = this.getWidth();
        int h = this.getHeight();
        int centerX = w / 2;
        int centerY = h / 2;

        if (this.currentIcon != null) {
            // 1. Cria a imagem temporária transparente
            java.awt.image.BufferedImage canvas = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            Graphics2D gCanvas = canvas.createGraphics();
            gCanvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gCanvas.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // 2. Desenha o peão rotacionado no canvas temporário
            gCanvas.rotate(Math.toRadians(actualAngle), centerX, centerY);
            gCanvas.drawImage(this.currentIcon.getImage(), 0, 0, this);

            // 3. Aplica a propriedade para "vazar/recortar" os pixels do peão
            gCanvas.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT, 1.0f));

            // 4. Configura e escreve o número (ele atuará apagando a área onde for desenhado)
            String numStr = String.valueOf(this.pawnNumber);
            gCanvas.setFont(new Font("SansSerif", Font.BOLD, (int) (22 * scale))); 
            FontMetrics fm = gCanvas.getFontMetrics();
            
            int textX = (w - fm.stringWidth(numStr)) / 2;
            int textY = (h + fm.getAscent() - fm.getDescent()) / 2 + (int)(2 * scale);

            gCanvas.drawString(numStr, textX, textY);
            gCanvas.dispose();

            // 5. Renderiza a imagem final recortada diretamente na tela do jogo
            g2.drawImage(canvas, 0, 0, null);
        }
        
        g2.dispose();
    }

    public double getActualAngle() {
        return this.actualAngle;
    }

    public void setActualAngle(double actualAngle) {
        this.actualAngle = actualAngle;
    }

    public boolean isInclinedToRight() {
        return this.isInclinedToRight;
    }

    public void setInclinedToRight(boolean isInclinedToRight) {
        this.isInclinedToRight = isInclinedToRight; 
    }
}