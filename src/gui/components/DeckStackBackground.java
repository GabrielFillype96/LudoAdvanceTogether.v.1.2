package gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import control.ImageLoaderManager;

public class DeckStackBackground extends JPanel {

    private static final double SCALE = 1.5;
    
    // Dimensões da carta
    private final int widthCard = (int) (250 * SCALE);
    private final int heightCard = (int) (375 * SCALE);
    private final int arcSize = (int) (15 * SCALE);

    // Imagem para o topo do deck
    private Image cardBackImg;

    // =========================================================================
    // DESLOCAMENTOS FIXOS ORGÂNICOS: Simulam imperfeição manual sem oscilar no repaint
    // Mapeados para os índices de 1 a 6 da renderização
    // =========================================================================
    private static final int[] OFFSETS_X = {0, 2, 5, 3, 8, 6, 10};
    private static final int[] OFFSETS_Y = {0, 2, 4, 9, 7, 12, 14};

    // Recebe o caminho da imagem no construtor
    public DeckStackBackground(String cardBackPath) {
        setOpaque(false); 
        
        // Carrega a imagem da parte de trás
        ImageIcon icon = ImageLoaderManager.loadIcon(cardBackPath);
        if (icon != null) {
            this.cardBackImg = icon.getImage();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int camadas = 6;         // Quantidade de cartas na pilha

        // Desenhamos de trás para frente
        for (int i = camadas; i >= 1; i--) {
            // Substituição do cálculo linear pelos desvios manuais assustados
            int x = OFFSETS_X[i];
            int y = OFFSETS_Y[i];

            // Se for a carta do TOPO da pilha (i == 1) e a imagem estiver carregada
            if (i == 1 && cardBackImg != null) {
                // Desenha a imagem real da capa roxa ligeiramente deslocada do centro
                g2.drawImage(cardBackImg, x, y, widthCard, heightCard, this);
                
                // =========================================================
                // DESENHA A MESMA BORDA DA CAMADA 2 PARA ALINHAR PERFEITAMENTE
                // =========================================================
                int gapEsquerda = 4;  
                int gapDireita  = 2;  
                int gapTopo     = 2;
                int gapBase     = 2;

                int visualX = x + gapEsquerda;
                int visualY = y + gapTopo;
                int visualWidth = widthCard - gapEsquerda - gapDireita;
                int visualHeight = heightCard - gapTopo - gapBase;

                g2.setColor(new Color(255, 255, 255, 200)); 
                g2.setStroke(new BasicStroke(4.0f)); 
                g2.drawRoundRect(visualX, visualY, visualWidth, visualHeight, arcSize, arcSize);
                
            } else {
                // Para as cartas de baixo (i > 1), desenha apenas as bordas simulando as folhas de papel
                
                // 1. Corpo/borda da carta (cinza claro/papel)
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRoundRect(x, y, widthCard, heightCard, arcSize, arcSize);

                // 2. Contorno fino para separar as cartas empilhadas
                g2.setColor(new Color(150, 150, 150, 180));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(x, y, widthCard, heightCard, arcSize, arcSize);
                
                // 3. Sombra interna para profundidade
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(x + 2, y + heightCard - 4, widthCard - 4, 4, arcSize, arcSize);
                g2.fillRoundRect(x + widthCard - 4, y + 2, 4, heightCard - 4, arcSize, arcSize);
            }
        }

        g2.dispose();
    }
}