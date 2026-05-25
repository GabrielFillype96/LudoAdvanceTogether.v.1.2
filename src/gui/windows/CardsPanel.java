package gui.windows;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Component;
import java.awt.Image;
import javax.swing.JPanel;
import Cards.CustomCards;

/**
 * Painel base responsável por receber, renderizar e gerenciar 
 * a exibição visual das cartas durante o teste de jogabilidade.
 */
public class CardsPanel extends JPanel {

    private final Color MOLDURA_PRETA = new Color(0, 0, 0);

    public CardsPanel() {
        // Dimensões definidas por si (220x340)
        Dimension tamanhoCard = new Dimension(220, 340);
        setPreferredSize(tamanhoCard);
        setSize(tamanhoCard);
        setOpaque(false); // Transparência total para ver o fundo amadeirado
        setLayout(null);  
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Ativa o anti-aliasing para suavizar as bordas arredondadas
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int raioCurva = 18;

        // =========================================================================
        // DESTAQUE: CAPTURA A CARTA ADICIONADA E DESENHA A SUA IMAGEM DIRECTAMENTE
        // =========================================================================
        if (getComponentCount() > 0) {
            Component comp = getComponent(0);
            if (comp instanceof CustomCards) {
                CustomCards carta = (CustomCards) comp;
                Image imgCarta = carta.getCardImage(); // Pega a imagem carregada
                
                if (imgCarta != null) {
                    // Desenha a imagem preenchendo os 220x340 do painel
                    g2.drawImage(imgCarta, 0, 0, w, h, this);
                }
            }
        }

        // Desenha a moldura preta de acabamento por cima da imagem da carta
        g2.setColor(MOLDURA_PRETA);
        g2.setStroke(new BasicStroke(6)); // Espessura nítida para a borda
        g2.drawRoundRect(3, 3, w - 7, h - 7, raioCurva, raioCurva);
        
        g2.dispose();
    }
}