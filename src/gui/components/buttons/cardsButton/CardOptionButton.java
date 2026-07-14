package gui.components.buttons.cardsButton;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

// --- NOVOS IMPORTS PARA A QUEBRA DE LINHA AUTOMÁTICA ---
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
// -------------------------------------------------------

import javax.swing.ImageIcon;
import javax.swing.JButton;

import actions.CardAnswerValidation;
import cards.CustomCards;

public class CardOptionButton extends JButton {
    // VARIÁVEIS DE INSTÂNCIA

    private static final double SCALE = 1.5;
    
    private final String CARD_QUESTION_TYPE;
    private String cardAnswerLetter = "";  
    private String cardAnswerTxt = "";  
    private boolean isHovered = false;
    
    private final Color CARD_COLOR; // Copia a cor de fundo da carta (Ex: Verde claro da pergunta fácil)
    private final Color COR_BORDA = new Color(35, 45, 35, 140); // Linha fina sutil ao redor da alternativa
    private final Color COR_TEXTO_ESCURO = new Color(35, 35, 35); // Fonte combinando com o enunciado
    
    private final Color COR_BOX_LETRA = new Color(240, 240, 240, 200); // Fundo claro para a letra
    private final Color COR_BOX_HOVER = new Color(255, 255, 255); // Brilha um pouco mais no hover
    private final Color COR_TEXTO_LETRA = new Color(35, 35, 35);

    private Image cardBtnBGImg; // Variável para armazenar a imagem de fundo dos botões das alternativas
    private final static String CARD_BTN_BG_IMG_PATH = "/assets/cardBtnImg_160x40.png"; // Caminho da imagem de fundo dos botões das alternativas

    // Construtor para instanciar os botões de resposta
    public CardOptionButton(String cardAnswerCompleteTxt, String cardQuestionType, Color cardColor, String tipoPergunta) {
        super(cardAnswerCompleteTxt);
        this.CARD_QUESTION_TYPE = cardQuestionType;
        this.CARD_COLOR = cardColor;
        
        // Trata o texto para separar o índice ("A", "B", etc.) do enunciado
        cardAnswerTxtConfig(cardAnswerCompleteTxt);
        
        // Configurações do JButton
        setText(cardAnswerTxt);
        setFont(new Font("Tahoma", Font.BOLD, (int) (10 * SCALE)));
        setForeground(COR_TEXTO_ESCURO); // Texto escuro para contrastar no fundo claro
        
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(152, 24));

        // Procura o path da imagem de fundo dos botões de resposta
        java.net.URL cardBtnBGImgPath = getClass().getResource(CARD_BTN_BG_IMG_PATH);
        if (cardBtnBGImgPath != null) {
            // Se encontrou a imagem, carrega e armazena na variável cardBtnBGImg
            System.out.println(
                "[CardOptionButton] Imagem de fundo das alternativas encontrada em: " + CARD_BTN_BG_IMG_PATH
            );
            this.cardBtnBGImg = new ImageIcon(cardBtnBGImgPath).getImage();
        } else {
            // Se não encontrou a imagem, imprime um erro no console
            System.err.println(
                "[CardOptionButton] Erro: Imagem de fundo das alternativas não encontrada em " + CARD_BTN_BG_IMG_PATH
            );
        }
        
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

    /**
     * CONSTRUTOR SOBRECARREGADO: 
     * Usado apenas para botões simples (como o botão "OK" das cartas de Sorte/Azar).
     */
    public CardOptionButton(String text) {
        // Preenchemos as variáveis obrigatórias com valores neutros/vazios
        this.CARD_QUESTION_TYPE = "ESPECIAL";
        this.cardAnswerLetter = ""; // Deixamos vazio pois não tem letra (A, B, C...)
        this.cardAnswerTxt = text;  // Aqui entra o "OK"
        this.CARD_COLOR = new Color(200, 200, 200); // Uma cor cinza neutra 
        
        // Configurações visuais básicas do Swing (mantenha igual ao seu construtor original)
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);
        this.setBorderPainted(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.isHovered = false;
    }

    // Método para separar a alternativa do texto da carta
    private void cardAnswerTxtConfig(String cardAnswerCompleteTxt) {
        if (cardAnswerCompleteTxt == null) return;
        
        if (cardAnswerCompleteTxt.length() > 2 && (cardAnswerCompleteTxt.charAt(1) == ')' || cardAnswerCompleteTxt.charAt(1) == '-')) {
            this.cardAnswerLetter = String.valueOf(cardAnswerCompleteTxt.charAt(0)).toUpperCase();
            this.cardAnswerTxt = cardAnswerCompleteTxt.substring(2).trim();
        } else {
            this.cardAnswerLetter = String.valueOf(cardAnswerCompleteTxt.charAt(0)).toUpperCase();
            this.cardAnswerTxt = cardAnswerCompleteTxt;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // NÃO chamamos o super.paintComponent(g) para evitar o botão padrão
        Graphics2D g2 = (Graphics2D) g.create();

        // Ativa a suavização máxima (Antialiasing) para bordas e textos perfeitos
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int raioCurva = (int) (8 * SCALE); // Curvatura nos cantos do botão

        // =========================================================================
        // 0. CORREÇÃO DA LETRA DA ALTERNATIVA (Se vier número "1", muda para "A")
        // =========================================================================
        String letraExibicao = cardAnswerLetter != null ? cardAnswerLetter.trim() : "";
        if (letraExibicao.equals("1")) letraExibicao = "A";
        else if (letraExibicao.equals("2")) letraExibicao = "B";
        else if (letraExibicao.equals("3")) letraExibicao = "C";
        else if (letraExibicao.equals("4")) letraExibicao = "D";

        // Garante o formato estético "A)"
        if (!letraExibicao.isEmpty() && !letraExibicao.contains(")")) {
            letraExibicao = letraExibicao + ")";
        }

        // =========================================================================
        // 1. CORES DO FUNDO VETORIAL (Tom de Branco translúcido com Hover)
        // =========================================================================
        Color corFundoCima;
        Color corFundoBaixo;
        Color corBorda;

        if (isHovered) {
            // Estado Hover: Branco mais sólido e brilhante, borda destaca com a cor de tema da própria carta
            corFundoCima  = new Color(255, 255, 255, 210); // Branco translúcido forte e nítido
            corFundoBaixo = new Color(245, 245, 245, 210);
            corBorda      = (CARD_COLOR != null) ? CARD_COLOR : new Color(255, 255, 255, 180); // Borda assume o tom da carta
        } else {
            // Estado Normal: Branco bem suave/sutil (estilo vidro jateado)
            corFundoCima  = new Color(255, 255, 255, 55);  // Branco transparente suave
            corFundoBaixo = new Color(255, 255, 255, 35);  // Degradê sutil para baixo
            corBorda      = new Color(255, 255, 255, 80);  // Borda branca suave transparente
        }

        // Desenha o fundo do botão com o gradiente esbranquiçado
        java.awt.GradientPaint gradienteFundo = new java.awt.GradientPaint(0, 0, corFundoCima, 0, h, corFundoBaixo);
        g2.setPaint(gradienteFundo);
        g2.fillRoundRect(0, 0, w, h, raioCurva, raioCurva);

        // Desenha a borda fina
        g2.setColor(corBorda);
        g2.setStroke(new BasicStroke((float) (1.2 * SCALE)));
        g2.drawRoundRect(1, 1, w - 2, h - 2, raioCurva, raioCurva);

        // =========================================================================
        // 2. DESENHO DA LETRA DA ALTERNATIVA (Visível sobre o fundo claro do botão)
        // =========================================================================
        g2.setFont(new Font("Arial", Font.BOLD, (int) (14 * SCALE)));
        
        // No hover, quando o fundo fica muito branco, usamos a cor escura da sua classe para dar contraste
        if (isHovered) {
            g2.setColor(COR_TEXTO_ESCURO); 
        } else {
            // Em repouso, o botão é ligeiramente transparente, então podemos usar um tom branco/claro bem visível
            g2.setColor(new Color(245, 245, 245)); 
        }

        int xLetter = (int) (12 * SCALE);
        int yLetter = (int) ((h / 2) + (g2.getFontMetrics().getAscent() / 2f) - (2 * SCALE));
        g2.drawString(letraExibicao, xLetter, yLetter);

        // =========================================================================
        // 3. DESENHO DO TEXTO DA RESPOSTA (Contraste adaptável)
        // =========================================================================
        Font fonteResposta = new Font("Arial", Font.BOLD, (int) (11.5 * SCALE));
        g2.setFont(fonteResposta);
        
        // Define a cor do texto da resposta:
        if (isHovered) {
            g2.setColor(COR_TEXTO_ESCURO); // Fica escuro quando o fundo brilha
        } else {
            g2.setColor(new Color(245, 245, 245)); // Fica branco quando o botão está transparente
        }

        int xCardAnswerTxt = (int) (34 * SCALE); // Espaço seguro para não encavalar na letra
        int larguraMaximaTexto = w - xCardAnswerTxt - (int) (12 * SCALE);

        if (cardAnswerTxt != null && !cardAnswerTxt.isEmpty()) {
            AttributedString attributedString = new AttributedString(cardAnswerTxt);
            attributedString.addAttribute(TextAttribute.FONT, fonteResposta);
            attributedString.addAttribute(TextAttribute.FOREGROUND, g2.getColor());
            
            AttributedCharacterIterator paragraph = attributedString.getIterator();
            FontRenderContext frc = g2.getFontRenderContext();
            LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);
            lineMeasurer.setPosition(paragraph.getBeginIndex());

            List<TextLayout> linhas = new ArrayList<>();
            float alturaTotal = 0;
            
            while (lineMeasurer.getPosition() < paragraph.getEndIndex()) {
                TextLayout layout = lineMeasurer.nextLayout(larguraMaximaTexto);
                linhas.add(layout);
                alturaTotal += layout.getAscent() + layout.getDescent() + layout.getLeading();
            }

            // Centraliza o bloco de texto verticalmente no espaço do botão
            float yConstrucao = (h - alturaTotal) / 2f + linhas.get(0).getAscent();

            for (TextLayout layout : linhas) {
                layout.draw(g2, xCardAnswerTxt, yConstrucao);
                yConstrucao += layout.getDescent() + layout.getLeading() + layout.getAscent();
            }
        }

        g2.dispose();
    }

    // Método auxiliar para mesclar a cor da carta com o fundo escuro do botão
    private Color mesclarCores(Color corCarta, Color corFundo, float proporcaoCarta) {
        float r = (corCarta.getRed() * proporcaoCarta) + (corFundo.getRed() * (1 - proporcaoCarta));
        float g = (corCarta.getGreen() * proporcaoCarta) + (corFundo.getGreen() * (1 - proporcaoCarta));
        float b = (corCarta.getBlue() * proporcaoCarta) + (corFundo.getBlue() * (1 - proporcaoCarta));
        return new Color((int) r, (int) g, (int) b, 235); // 235 de opacidade para um leve efeito acrílico
    }

    public String getCardAnswerCompleteTxt() {
        return cardAnswerLetter + ") " + cardAnswerTxt;
    }
    
    public String getCardAnswerTxt() {
        return cardAnswerTxt;
    }
    
    public String getCardQuestionType() {
        return CARD_QUESTION_TYPE;
    }
}