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
    protected boolean selecionadoFixado = false; // Estado de Aba Ativa (Menu Principal)
    protected boolean mousePorCima = false;       // Hover
    protected boolean pressionado = false;         // Clique do mouse
    protected boolean eAbaLateral = false;         // Habilita deslocamento + seta

    private static final double SCALE = 1.5;    

    // Dimensões da base do botão (200x45 escalado)
    private static final int btnWidth = (int) (200 * SCALE);
    private static final int btnHeight = (int) (45 * SCALE);

    // PALETA DE CORES
    private final Color ROXO_TEMA = new Color(52, 34, 64);
    private final Color ROXO_ESCURO_BORDA = new Color(30, 18, 40);
    private final Color BRANCO_PURO = new Color(255, 255, 255);
    
    private final Color DOURADO_INTERNA = new Color(245, 198, 114);  // Botão Selecionado / Aba Ativa
    private final Color DOURADO_HOVER_BG = new Color(254, 224, 130); // Hover amarelado original

    /**
     * Construtor Padrão: Para botões normais de submenus / in-game (sem deslocamento)
     */
    public CustomButton(String labelTexto) {
        this(labelTexto, false);
    }

    /**
     * Construtor Flexível: Permite definir se é uma aba do menu principal
     * @param labelTexto Texto do botão
     * @param eAbaLateral Se 'true', ativa deslocamento + seta ao selecionar
     */
    public CustomButton(String labelTexto, boolean eAbaLateral) {
        this.labelTexto = labelTexto;
        this.eAbaLateral = eAbaLateral;

        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);

        Dimension fixedSize = new Dimension(btnWidth, btnHeight);
        setPreferredSize(fixedSize);
        setMinimumSize(fixedSize);
        setMaximumSize(fixedSize);

        setCursor(new Cursor(Cursor.HAND_CURSOR));

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

            @Override
            public void mousePressed(MouseEvent e) {
                pressionado = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressionado = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 1. Definição de Cores e Espessura da Borda
        Color corFundo;
        Color corBorda;
        Color corTexto = ROXO_TEMA;
        float espessuraBorda;

        if (selecionadoFixado || pressionado) {
            // Aba Ativa ou Botão Pressionado
            corFundo = DOURADO_INTERNA;
            corBorda = ROXO_ESCURO_BORDA;
            espessuraBorda = 3.5f;
        } else if (mousePorCima) {
            // Hover (Mouse por cima)
            corFundo = DOURADO_HOVER_BG;
            corBorda = ROXO_ESCURO_BORDA;
            espessuraBorda = 3.2f;
        } else {
            // Estado Normal
            corFundo = BRANCO_PURO;
            corBorda = ROXO_TEMA;
            espessuraBorda = 2.0f;
        }

        // 2. Margem de segurança para evitar corte da borda
        float margin = espessuraBorda / 2.0f + 1.0f;

        // 3. Deslocamento Lateral (Apenas se for Aba Lateral e estiver Selecionada)
        int offsetX = (eAbaLateral && selecionadoFixado) ? (int) (18 * SCALE) : 0;

        g2.translate(margin + offsetX, margin);

        float w = getWidth() - (margin * 2) - offsetX;
        float h = getHeight() - (margin * 2);
        float corte = 16.0f; // Chanfro diagonal

        GeneralPath formatoBotao = new GeneralPath();
        formatoBotao.moveTo(corte, 0);          
        formatoBotao.lineTo(w, 0);              
        formatoBotao.lineTo(w, h - corte);      
        formatoBotao.lineTo(w - corte, h);      
        formatoBotao.lineTo(0, h);              
        formatoBotao.lineTo(0, corte);          
        formatoBotao.closePath();               

        // Preenchimento do Fundo
        g2.setColor(corFundo);
        g2.fill(formatoBotao);

        // Desenho da Borda
        g2.setColor(corBorda);
        g2.setStroke(new BasicStroke(espessuraBorda, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); 
        g2.draw(formatoBotao);

        // 4. Desenho do Texto e da Seta Indicadora
        g2.setFont(new Font("Arial", Font.BOLD, (int) (14 * SCALE)));
        FontMetrics fm = g2.getFontMetrics();

        int textoWidth = fm.stringWidth(labelTexto);
        boolean desenharSeta = eAbaLateral && selecionadoFixado;
        int espacoIndicador = desenharSeta ? (int) (14 * SCALE) : 0;

        int xTexto = (int) ((w - (textoWidth + espacoIndicador)) / 2);
        int yTexto = (int) (((h - fm.getHeight()) / 2) + fm.getAscent());

        g2.setColor(corTexto);
        g2.drawString(labelTexto, xTexto, yTexto);

        // Seta (Triângulo) exibida apenas nas abas laterais ativas
        if (desenharSeta) {
            int triX = xTexto + textoWidth + (int) (8 * SCALE);
            int triYCenter = (int) (h / 2);
            int raioTriangulo = (int) (5 * SCALE);

            int[] px = {triX, triX + (int) (7 * SCALE), triX};
            int[] py = {triYCenter - raioTriangulo, triYCenter, triYCenter + raioTriangulo};

            g2.fillPolygon(px, py, 3);
        }

        g2.dispose();
    }

    public void setSelecionado(boolean estado) {
        this.selecionadoFixado = estado;
        repaint();
    }

    public void setEAbaLateral(boolean eAbaLateral) {
        this.eAbaLateral = eAbaLateral;
        repaint();
    }

    public boolean isSelecionado() {
        return selecionadoFixado;
    }
}