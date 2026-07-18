// Packages
package gui.components; 

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;

import control.ImageLoaderManager;
import gui.animations.TurnHighlightAnimation; // <-- NOVO IMPORT

public class CardDeckBackground extends JPanel {
    // VARIÁVEIS DE INSTÂNCIA
    private Image backgroundDeckImg;
    private TurnHighlightAnimation highlightAnimation; // <-- NOVA VARIÁVEL
    private static final double SCALE = 1.5;

    /**
     ** Construtor da classe CardDeckBackground
     * @param backgroundDeckPath Path da imagem do deck das cartas
    */
    public CardDeckBackground(String backgroundDeckPath) {
        setOpaque(false); // Retira o fundo cinza do java

        // Carrega a imagem do deck das cartas
        ImageIcon backgroundDeckIcon = ImageLoaderManager.loadIcon(backgroundDeckPath);

        /*
         * A "CardDeckBackground" é um "JPanel" e como não existe um método automático para botar fundo nele, nós usamos o "ImageLoaderManager" que devolve um "ImageIcon"
         * O "Graphics2D" só aceita o formato "Image" e por isso precisamos de uma trava de segurança caso o "backgroundDeckIcon.getImage()" retorne "null"
         * Trava de segurança para impedir que se caso a variável "backgroundDeckIcon" não dá crash no jogo
        */
        if (backgroundDeckIcon != null) {
            this.backgroundDeckImg = backgroundDeckIcon.getImage();
        }
        
        // <-- NOVO: INICIALIZA A ANIMAÇÃO PASSANDO ESTE PAINEL COMO ALVO -->
        this.highlightAnimation = new TurnHighlightAnimation(this);
    }

    // <-- NOVOS MÉTODOS PARA LIGAR E DESLIGAR A ANIMAÇÃO -->
    public void startTurnHighlight() {
        if (this.highlightAnimation != null) {
            this.highlightAnimation.start();
        }
    }

    public void stopTurnHighlight() {
        if (this.highlightAnimation != null) {
            this.highlightAnimation.stop();
        }
    }

   @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g.create(); 
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int yOffset = 0;
        int glowOpacity = 0;
        
        if (this.highlightAnimation != null) {
            yOffset = this.highlightAnimation.getYOffset();
            glowOpacity = this.highlightAnimation.getGlowOpacity();
        }

        // =========================================================
        // CAIXA DA IMAGEM (Onde o PNG é desenhado com a transparência)
        // =========================================================
        int width = (int) (250 * SCALE);
        int height = (int) (375 * SCALE);
        int arcSize = (int) (15 * SCALE); 
        
        int glowMargin = 25; 
        int cardX = glowMargin;
        int cardY = glowMargin + yOffset;

        // =========================================================
        // NOVO: CAIXA VISUAL (Onde os efeitos visuais vão abraçar)
        // =========================================================
        // Ajuste estes valores (em pixels) para encolher a borda até ela colar na carta!
        // Como o seu problema principal está na esquerda, coloquei '4' para testar.
        int gapEsquerda = 4;  
        int gapDireita  = 2;  
        int gapTopo     = 2;
        int gapBase     = 2;

        int visualX = cardX + gapEsquerda;
        int visualY = cardY + gapTopo;
        int visualWidth = width - gapEsquerda - gapDireita;
        int visualHeight = height - gapTopo - gapBase;
        // =========================================================

        // 1. BRILHO SUAVE E DIFUSO (NEON BLUR) - Agora usando a Caixa Visual
        if (glowOpacity > 0) {
            int glowSpread = 20; 
            Color glowColor = new Color(0, 191, 255); 
            
            for (int i = glowSpread; i >= 1; i--) {
                double ratio = 1.0 - ((double) i / glowSpread);
                int alpha = (int) (glowOpacity * (ratio * ratio) * 0.6);
                alpha = Math.max(0, Math.min(255, alpha)); 
                
                g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), alpha)); 
                
                // USANDO AS COORDENADAS VISUAIS
                g2.fillRoundRect(
                    visualX - i, 
                    visualY - i, 
                    visualWidth + (i * 2), 
                    visualHeight + (i * 2), 
                    arcSize + (i * 2), 
                    arcSize + (i * 2)
                );
            }
        }

        // 2. DESENHA A SOMBRA (Comentada temporariamente)
        /*
        if (yOffset < 0) {
            int shadowX = visualX + 6; 
            int shadowY = visualY + 8; 
            g2.setColor(new Color(0, 0, 0, 90)); 
            g2.fillRoundRect(shadowX, shadowY, visualWidth, visualHeight, arcSize, arcSize);
        }
        */

        // 3. DESENHA A IMAGEM DA CARTA - Continua usando a Caixa Original!
        if (backgroundDeckImg != null) {
            g2.drawImage(backgroundDeckImg, cardX, cardY, width, height, this);
        } else {
            g2.setColor(new Color(128, 0, 128)); 
            g2.fillRoundRect(cardX, cardY, width, height, arcSize, arcSize);
        }

        // 4. DESENHA A BORDA NÍTIDA DA CARTA - Agora usando a Caixa Visual
        g2.setColor(new Color(255, 255, 255, 200)); 
        g2.setStroke(new BasicStroke(4.0f)); 
        
        // USANDO AS COORDENADAS VISUAIS
        g2.drawRoundRect(visualX, visualY, visualWidth, visualHeight, arcSize, arcSize);
        
        g2.dispose(); 
    }
}