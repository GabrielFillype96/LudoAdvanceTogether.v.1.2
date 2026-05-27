package gui.components.buttons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import javax.swing.JButton;

public class CustomButton extends JButton {
    
    private String labelTexto;

    // Gerenciamento de estados visuais
    protected boolean selecionadoFixado = false;
    protected boolean mousePorCima = false;

    // Dimensões do projeto (200x45)
    private static final int btnWidth = 200;
    private static final int btnHeight = 45;

    // --- CORES EXTRAÍDAS DO SEU DESIGN SYSTEM ---
    private final Color ROXO_TEMA = new Color(52, 34, 64);       // Roxo profundo extraído do menu
    private final Color BRANCO_PURO = new Color(255, 255, 255);  // Fundo do estado normal solicitado
    
    private final Color DOURADO_BORDAS = new Color(222, 179, 102); // Dourado fosco selecionado
    private final Color DOURADO_INTERNA = new Color(245, 198, 114); // Dourado brilhante interno

    public CustomButton(String labelTexto) {
        this.labelTexto = labelTexto;

        // Limpeza dos estilos nativos do Swing para evitar blocos quadrados amarelos
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);

        Dimension fixedSize = new Dimension(btnWidth, btnHeight);
        setPreferredSize(fixedSize);
        setMinimumSize(fixedSize);
        setMaximumSize(fixedSize);
        //setSize(fixedSize);

        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ouvintes para redesenhar dinamicamente ao interagir com o mouse
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mousePorCima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mousePorCima = false;
                repaint();
            }
        });
    }

    /**
     * Renderiza a geometria assimétrica com cortes diagonais idêntica à imagem de referência 2.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Ativa suavização máxima de serrilhados nas bordas e fontes
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int corte = 18; // Tamanho do chanfro diagonal em pixels

        // --- CONSTRUÇÃO HISTÓRICA DO POLÍGONO ASSIMÉTRICO (IMAGEM 2) ---
        GeneralPath formatoBotao = new GeneralPath();
        
        formatoBotao.moveTo(corte, 0);          // 1. Início do corte superior esquerdo
        formatoBotao.lineTo(w, 0);              // 2. Linha reta até o topo direito (Canto Reto!)
        formatoBotao.lineTo(w, h - corte);      // 3. Desce reto até o começo do corte inferior direito
        formatoBotao.lineTo(w - corte, h);      // 4. Faz a diagonal no canto inferior direito
        formatoBotao.lineTo(0, h);              // 5. Linha reta até a base inferior esquerda (Canto Reto!)
        formatoBotao.lineTo(0, corte);          // 6. Sobe reto até o encontro do corte superior esquerdo
        formatoBotao.closePath();               // 7. Fecha o polígono aplicando a diagonal na subida esquerda

        // Seleção dinâmica das cores baseada nos estados
        Color corFundo;
        Color corBorda;
        Color corTexto;

        if (selecionadoFixado || mousePorCima) {
            // ESTADO SELECIONADO/HOVER: Placa dourada com texto escuro
            corFundo = DOURADO_INTERNA;
            corBorda = DOURADO_BORDAS;
            corTexto = ROXO_TEMA;
        } else {
            // NOVO ESTADO NORMAL SOLICITADO: Fundo branco, moldura roxa, texto roxo
            corFundo = BRANCO_PURO;
            corBorda = ROXO_TEMA;
            corTexto = ROXO_TEMA;
        }

        // 1. Preenche a região interna do polígono assimétrico
        g2.setColor(corFundo);
        g2.fill(formatoBotao);

        // 2. Contorna a moldura com espessura visível e limpa
        g2.setColor(corBorda);
        g2.setStroke(new BasicStroke(3f)); 
        g2.draw(formatoBotao);

        // 3. Insere o texto perfeitamente centralizado
        g2.setColor(corTexto);
        g2.setFont(new Font("Arial", Font.BOLD, 14));

        FontMetrics fm = g2.getFontMetrics();
        int xTexto = (w - fm.stringWidth(labelTexto)) / 2;
        int yTexto = ((h - fm.getHeight()) / 2) + fm.getAscent();

        g2.drawString(labelTexto, xTexto, yTexto);

        g2.dispose();
    }

    public void setSelecionado(boolean estado) {
        this.selecionadoFixado = estado;
        repaint();
    }
}