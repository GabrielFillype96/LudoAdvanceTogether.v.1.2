package gui.windows;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Painel base responsável por receber, posicionar e gerenciar 
 * a exibição visual das novas cartas dinâmicas.
 */
public class CardsPanel extends JPanel {

    private final Color MOLDURA_PRETA = new Color(0, 0, 0);

    public CardsPanel() {
        // Mantém as dimensões de 220x340 definidas no layout
        Dimension tamanhoCard = new Dimension(220, 340);
        setPreferredSize(tamanhoCard);
        setSize(tamanhoCard);
        setOpaque(false);   // Mantém transparente para o fundo amadeirado do GamePanel aparecer atrás
        setLayout(null);    // Permite que a CustomCards ocupe o espaço absoluto interno
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Ativa o anti-aliasing para suavizar os contornos da moldura externa
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int raioCurva = 18;

        // =========================================================================
        // CORRIGIDO: Removeu-se a busca por 'carta.getCardImage()'.
        // O Swing agora vai renderizar o paintComponent interno da CustomCards automaticamente.
        // =========================================================================

        // Desenha apenas uma moldura preta fina de contenção ao redor do bloco da carta para dar acabamento
        g2.setColor(MOLDURA_PRETA);
        g2.setStroke(new BasicStroke(2.0f)); 
        g2.drawRoundRect(0, 0, w - 1, h - 1, raioCurva, raioCurva);

        g2.dispose();
    }
}