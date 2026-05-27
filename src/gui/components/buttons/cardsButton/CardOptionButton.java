package gui.components.buttons.cardsButton;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

public class CardOptionButton extends JButton {
    
    // --- ATRIBUTOS DE ESTADO ---
    private final String tipoPergunta;
    private String letraAlternativa = "";  
    private String textoAlternativa = "";  
    private boolean isHovered = false;
    
    // --- DESIGN & CORES DINÂMICAS ---
    private final Color corFundoCarta; // Copia a cor de fundo da carta (Ex: Verde claro da pergunta fácil)
    private final Color COR_BORDA = new Color(35, 45, 35, 140); // Linha fina sutil ao redor da alternativa
    private final Color COR_TEXTO_ESCURO = new Color(35, 35, 35); // Fonte combinando com o enunciado
    
    // Configurações do Box da Letra (Baseado na referência)
    private final Color COR_BOX_LETRA = new Color(240, 240, 240, 200); // Fundo claro para a letra
    private final Color COR_BOX_HOVER = new Color(255, 255, 255); // Brilha um pouco mais no hover
    private final Color COR_TEXTO_LETRA = new Color(35, 35, 35); 

    /**
     * Construtor atualizado para receber a cor de fundo da carta pai
     */
    public CardOptionButton(String textoCompleto, String tipoPergunta, Color corFundoCarta) {
        this.tipoPergunta = tipoPergunta;
        this.corFundoCarta = corFundoCarta;
        
        // Trata o texto para separar o índice ("A", "B", etc.) do enunciado
        separarLetraETexto(textoCompleto);
        
        // Configurações do JButton
        setText(textoAlternativa);
        setFont(new Font("Tahoma", Font.BOLD, 12));
        setForeground(COR_TEXTO_ESCURO); // Texto escuro para contrastar no fundo claro
        
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(152, 24));
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                isHovered = true;
                repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                isHovered = false;
                repaint();
            }
        });
    }

    private void separarLetraETexto(String textoCompleto) {
        if (textoCompleto == null) return;
        
        if (textoCompleto.length() > 2 && (textoCompleto.charAt(1) == ')' || textoCompleto.charAt(1) == '-')) {
            this.letraAlternativa = String.valueOf(textoCompleto.charAt(0)).toUpperCase();
            this.textoAlternativa = textoCompleto.substring(2).trim();
        } else {
            this.letraAlternativa = String.valueOf(textoCompleto.charAt(0)).toUpperCase();
            this.textoAlternativa = textoCompleto;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        int raioArredondamento = 8;
        
        // 1. DESENHA O FUNDO DO BOTÃO (Com a mesma cor interna da carta)
        // Se o mouse estiver em cima, damos um feedback visual clareando bem de leve
        if (isHovered) {
            g2.setColor(corFundoCarta.brighter());
        } else {
            g2.setColor(corFundoCarta);
        }
        g2.fillRoundRect(0, 0, w, h, raioArredondamento, raioArredondamento);
        
        // 2. DESENHA A BORDA DO BOTÃO
        g2.setColor(COR_BORDA);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawRoundRect(0, 0, w - 1, h - 1, raioArredondamento, raioArredondamento);
        
        // 3. DESENHA O MINI BOX DA LETRA DO LADO ESQUERDO
        int tamanhoBox = h - 6; 
        int xBox = 3;
        int yBox = 3;
        
        g2.setColor(isHovered ? COR_BOX_HOVER : COR_BOX_LETRA);
        g2.fillRoundRect(xBox, yBox, tamanhoBox, tamanhoBox, 6, 6);
        
        // Desenha uma linha divisória fina no box da letra
        g2.setColor(new Color(35, 35, 35, 60));
        g2.drawRoundRect(xBox, yBox, tamanhoBox, tamanhoBox, 6, 6);
        
        // 4. DESENHA A LETRA DENTRO DO BOX
        g2.setFont(new Font("Tahoma", Font.BOLD, 12));
        g2.setColor(COR_TEXTO_LETRA);
        int largLetra = g2.getFontMetrics().stringWidth(letraAlternativa);
        int xLetra = xBox + (tamanhoBox - largLetra) / 2;
        int yLetra = yBox + (tamanhoBox + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2;
        g2.drawString(letraAlternativa, xLetra, yLetra);
        
        // 5. DESENHA O TEXTO DA ALTERNATIVA
        g2.setFont(getFont());
        g2.setColor(getForeground());
        
        int xTextoAlternativa = xBox + tamanhoBox + 10; 
        int yTextoAlternativa = (h + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2;
        
        g2.drawString(textoAlternativa, xTextoAlternativa, yTextoAlternativa);
        
        g2.dispose();
    }

    public String getTextoCompleto() {
        return letraAlternativa + ") " + textoAlternativa;
    }
    
    @Override
    public String getTextoAlternativa() {
        return textoAlternativa;
    }
    
    public String getTipoPergunta() {
        return tipoPergunta;
    }
}