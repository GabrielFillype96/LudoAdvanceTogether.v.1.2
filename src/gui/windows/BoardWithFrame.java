package gui.windows;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Point;
import java.awt.Dimension;

public class BoardWithFrame extends JPanel {
    
    private final BoardScreen boardScreen;
    private final double scale;
    private final int thickness; // Espessura da moldura (não escalada)

    // Caminho opcional para a imagem de textura futuramente (ex: "/assets/wood_texture.png")
    private static final String TEXTURE_PATH = "/assets/img/woodTextured.jpg"; 

    public BoardWithFrame(BoardScreen boardScreen, double scale) {
        this.boardScreen = boardScreen;
        this.scale = scale;
        this.thickness = 40; // 40px de largura definidos por você

        setLayout(null);
        setOpaque(false);

        // Calcula o tamanho total: 600 do tabuleiro + 40 da esquerda + 40 da direita
        int totalUnscaledSize = 600 + (thickness * 2);
        int totalScaledSize = (int) (totalUnscaledSize * scale);
        
        Dimension size = new Dimension(totalScaledSize, totalScaledSize);
        setPreferredSize(size);
        setSize(size);

        // Posiciona o tabuleiro original exatamente após a moldura esquerda e superior
        int offset = (int) (thickness * scale);
        int boardSize = (int) (600 * scale);
        this.boardScreen.setBounds(offset, offset, boardSize, boardSize);

        // Adiciona o tabuleiro como filho deste painel
        add(this.boardScreen);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int t = (int) (thickness * scale);

        // CORES PROCEDURAIS DE MADEIRA (Caso não haja imagem de textura)
        Color woodTopBottom = new Color(133, 78, 37);   // Tom base horizontal
        Color woodSides = new Color(115, 64, 30);       // Tom vertical (levemente mais escuro)

        // 1. DESENHO DOS TRAPÉZIOS (Corte de Meia-Esquadria de 45°)
        // Ripas Horizontais (Topo e Base)
        Polygon topPlank = new Polygon(new int[]{0, w, w - t, t}, new int[]{0, 0, t, t}, 4);
        Polygon bottomPlank = new Polygon(new int[]{0, w, w - t, t}, new int[]{h, h, h - t, h - t}, 4);
        
        // Ripas Verticais (Esquerda e Direita)
        Polygon leftPlank = new Polygon(new int[]{0, t, t, 0}, new int[]{0, t, h - t, h}, 4);
        Polygon rightPlank = new Polygon(new int[]{w, w - t, w - t, w}, new int[]{0, t, h - t, h}, 4);

        // Pintura das ripas com tons complementares para dar volume
        g2.setColor(woodTopBottom);
        g2.fillPolygon(topPlank);
        g2.fillPolygon(bottomPlank);

        g2.setColor(woodSides);
        g2.fillPolygon(leftPlank);
        g2.fillPolygon(rightPlank);

        // Linhas divisórias finas de 45 graus nos cantos
        g2.setColor(new Color(60, 30, 10, 80));
        g2.drawPolygon(topPlank);
        g2.drawPolygon(bottomPlank);
        g2.drawPolygon(leftPlank);
        g2.drawPolygon(rightPlank);

        // 2. CHANFROS RELEVOS 3D (Simulação de Luz vinda do canto superior esquerdo)
        // Luzes (Bordas externas superiores/esquerdas)
        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new java.awt.BasicStroke(2f));
        g2.drawLine(0, 0, w, 0); // Externa Topo
        g2.drawLine(0, 0, 0, h); // Externa Esquerda

        // Sombras (Bordas externas inferiores/direitas)
        g2.setColor(new Color(0, 0, 0, 120));
        g2.drawLine(0, h - 1, w, h - 1); // Externa Base
        g2.drawLine(w - 1, 0, w - 1, h); // Externa Direita

        // 3. SOMBRA INTERNA PROJETADA (Efeito de tabuleiro rebaixado/fundo)
        // Criamos um degradê suave em direção ao centro útil do tabuleiro
        for (int i = 0; i < 8; i++) {
            int alpha = 90 - (i * 12); // Vai suavizando a cor preta
            if (alpha < 0) alpha = 0;
            g2.setColor(new Color(0, 0, 0, alpha));
            g2.drawRect(t - i, t - i, (w - (t * 2)) + (i * 2), (h - (t * 2)) + (i * 2));
        }

        // Borda preta nítida de encaixe do tabuleiro
        g2.setColor(new Color(0, 0, 0, 200));
        g2.setStroke(new java.awt.BasicStroke(1.5f));
        g2.drawRect(t, t, w - (t * 2), h - (t * 2));

        g2.dispose();
    }
}