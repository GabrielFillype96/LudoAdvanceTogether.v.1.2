package gui.components.buttons.cardsButton;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class CardOptionButton extends JButton {

    private static final double SCALE = 1.5;
    
    private final String CARD_QUESTION_TYPE;
    private String cardAnswerLetter = "";  
    private String cardAnswerTxt = "";  
    private boolean isHovered = false;
    
    private final Color CARD_COLOR;
    private final Color COR_BORDA = new Color(35, 45, 35, 140);
    private final Color COR_TEXTO_ESCURO = new Color(35, 35, 35);
    
    private final Color COR_BOX_LETRA = new Color(240, 240, 240, 200);
    private final Color COR_BOX_HOVER = new Color(255, 255, 255);
    private final Color COR_TEXTO_LETRA = new Color(35, 35, 35);

    // =========================================================================
    // OTIMIZAÇÃO: CACHE ESTÁTICO DE IMAGEM (Carrega no disco apenas 1 VEZ)
    // =========================================================================
    private static Image cardBtnBGImg; 
    private static boolean imageLoaded = false;
    private final static String CARD_BTN_BG_IMG_PATH = "/assets/cardBtnImg_160x40.png";

    private static synchronized void loadBackgroundImage() {
        if (!imageLoaded) {
            java.net.URL path = CardOptionButton.class.getResource(CARD_BTN_BG_IMG_PATH);
            if (path != null) {
                cardBtnBGImg = new ImageIcon(path).getImage();
                System.out.println("[CardOptionButton] Imagem de fundo carregada em CACHE ESTÁTICO: " + CARD_BTN_BG_IMG_PATH);
            } else {
                System.err.println("[CardOptionButton] Erro: Imagem de fundo não encontrada em " + CARD_BTN_BG_IMG_PATH);
            }
            imageLoaded = true;
        }
    }

    // Construtor principal
    public CardOptionButton(String cardAnswerCompleteTxt, String cardQuestionType, Color cardColor, String tipoPergunta) {
        super(cardAnswerCompleteTxt);
        this.CARD_QUESTION_TYPE = cardQuestionType;
        this.CARD_COLOR = cardColor;
        
        // Garante que a imagem seja carregada só na primeira vez
        loadBackgroundImage();
        
        cardAnswerTxtConfig(cardAnswerCompleteTxt);
        
        setText(cardAnswerTxt);
        setFont(new Font("Tahoma", Font.BOLD, (int) (10 * SCALE)));
        setForeground(COR_TEXTO_ESCURO);
        
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

    // Restante da classe (mantém o paintComponent e demais métodos iguais)...

    /**
     * NOVO CONSTRUTOR DE 2 PARÂMETROS:
     * Recebe o texto ("OK") e o TIPO DA CARTA ("SORTE", "AZAR", "SACANEAR") para definir a cor dinâmica.
     */
    public CardOptionButton(String text, String cardType) {
        // Mantemos como ESPECIAL para o paintComponent saber que deve ocultar a box e centralizar o texto
        this.CARD_QUESTION_TYPE = "ESPECIAL";
        this.cardAnswerLetter = ""; 
        this.cardAnswerTxt = text;  
        
        // =========================================================================
        // SELEÇÃO DINÂMICA DE COR DO HOVER (Baseada no Tipo da Carta)
        // =========================================================================
        String tipo = (cardType != null) ? cardType.toUpperCase() : "";
        
        if (tipo.contains("SORTE")) {
            this.CARD_COLOR = new Color(20, 160, 100); // Verde vibrante (ou Dourado: 255, 190, 20 se preferir)
        } else if (tipo.contains("AZAR")) {
            this.CARD_COLOR = new Color(190, 45, 45);  // Vermelho perigo
        } else if (tipo.contains("SACANEAR")) {
            this.CARD_COLOR = new Color(130, 40, 180); // Roxo ardiloso/neon
        } else {
            this.CARD_COLOR = new Color(200, 200, 200); // Cinza Padrão (Fallback)
        }
        
        // Configurações visuais básicas do Swing
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);
        this.setBorderPainted(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.isHovered = false;

        // Adicionando o evento de Hover
        this.addMouseListener(new java.awt.event.MouseAdapter() {
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
        // NÃO chamamos o super.paintComponent(g)
        Graphics2D g2 = (Graphics2D) g.create();

        // Ativa antialiasing máximo
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int raioCurva = (int) (8 * SCALE);

        // =========================================================================
        // VALIDAÇÃO: VERIFICA SE É O BOTÃO DA CARTA ESPECIAL
        // =========================================================================
        // O seu construtor de 1 parâmetro define CARD_QUESTION_TYPE como "ESPECIAL"
        boolean isEspecial = "ESPECIAL".equalsIgnoreCase(CARD_QUESTION_TYPE);

        // =========================================================================
        // 0. CORREÇÃO E FORMATAÇÃO DA LETRA
        // =========================================================================
        String letraExibicao = cardAnswerLetter != null ? cardAnswerLetter.trim() : "";
        if (!isEspecial) { // Só formata a letra se NÃO for uma carta especial
            if (letraExibicao.equals("1")) letraExibicao = "A";
            else if (letraExibicao.equals("2")) letraExibicao = "B";
            else if (letraExibicao.equals("3")) letraExibicao = "C";
            else if (letraExibicao.equals("4")) letraExibicao = "D";
            letraExibicao = letraExibicao.replace(")", ""); // Sem parênteses para o novo design
        }

        // =========================================================================
        // DEFINIÇÃO INTELIGENTE DA COR (Dificuldade Priorizada)
        // =========================================================================
        Color corTema = new Color(20, 160, 100); // Verde padrão de segurança (Fácil)

        if (CARD_QUESTION_TYPE != null) {
            String tipo = CARD_QUESTION_TYPE.toUpperCase();
            
            if (tipo.contains("MÉDIO") || tipo.contains("MEDIO")) {
                corTema = new Color(210, 140, 10); 
            } else if (tipo.contains("DIFÍCIL") || tipo.contains("DIFICIL")) {
                corTema = new Color(190, 45, 45); 
            } else if (tipo.contains("BOSS") || tipo.contains("DESAFIO")) {
                corTema = new Color(130, 40, 180); 
            } else if (tipo.contains("FÁCIL") || tipo.contains("FACIL")) {
                corTema = new Color(20, 160, 100); 
            } else if (CARD_COLOR != null) {
                corTema = CARD_COLOR; 
            }
        } else if (CARD_COLOR != null) {
            corTema = CARD_COLOR;
        }

        // =========================================================================
        // 1. DESENHO DO FUNDO DIREITO (Vidro Jateado Branco)
        // =========================================================================
        Color corFundoCima;
        Color corFundoBaixo;
        Color corBorda;

        if (isHovered) {
            corFundoCima  = new Color(255, 255, 255, 210); 
            corFundoBaixo = new Color(245, 245, 245, 210);
            corBorda      = corTema; // Borda inteira brilha com a cor do tema
        } else {
            corFundoCima  = new Color(255, 255, 255, 55);  
            corFundoBaixo = new Color(255, 255, 255, 35);  
            corBorda      = new Color(255, 255, 255, 80);  
        }

        java.awt.geom.RoundRectangle2D.Float fundoBotao = new java.awt.geom.RoundRectangle2D.Float(0, 0, w, h, raioCurva, raioCurva);
        
        java.awt.GradientPaint gradienteFundo = new java.awt.GradientPaint(0, 0, corFundoCima, 0, h, corFundoBaixo);
        g2.setPaint(gradienteFundo);
        g2.fill(fundoBotao);

        // =========================================================================
        // 2 E 3. DESENHO DO PAINEL ESQUERDO E DA LETRA
        // SÓ SERÃO DESENHADOS SE A CARTA NÃO FOR "ESPECIAL"
        // =========================================================================
        int larguraPainelEsquerdo = (int) (36 * SCALE); // Espessura da barra lateral
        
        if (!isEspecial) {
            // Criamos um "Clip" (Máscara).
            java.awt.Shape clipAntigo = g2.getClip();
            g2.setClip(fundoBotao);
            
            // Pinta a barra lateral esquerda inteira com a cor inteligente
            g2.setColor(corTema);
            g2.fillRect(0, 0, larguraPainelEsquerdo, h);

            // Linha branca sutil na divisão entre a barra lateral e o resto do botão
            g2.setColor(new Color(255, 255, 255, 70));
            g2.drawLine(larguraPainelEsquerdo - 1, 0, larguraPainelEsquerdo - 1, h);

            // Remove a máscara para desenhar o resto normalmente
            g2.setClip(clipAntigo);

            // DESENHO DA LETRA
            g2.setFont(new Font("Arial", Font.BOLD, (int) (14 * SCALE)));
            
            FontMetrics fm = g2.getFontMetrics();
            int letterWidth = fm.stringWidth(letraExibicao);
            int letterX = (larguraPainelEsquerdo - letterWidth) / 2;
            int letterY = (h - fm.getHeight()) / 2 + fm.getAscent();
            
            g2.setColor(new Color(0, 0, 0, 70));
            g2.drawString(letraExibicao, letterX + 1, letterY + 1);
            
            g2.setColor(Color.WHITE); 
            g2.drawString(letraExibicao, letterX, letterY);
        }

        // =========================================================================
        // DESENHO DA BORDA GERAL (Para todos os botões)
        // =========================================================================
        g2.setColor(corBorda);
        g2.setStroke(new BasicStroke((float) (1.2 * SCALE)));
        g2.drawRoundRect(1, 1, w - 2, h - 2, raioCurva, raioCurva);

        // =========================================================================
        // 4. DESENHO DO TEXTO DA RESPOSTA
        // =========================================================================
        Font fonteResposta = new Font("Arial", Font.BOLD, (int) (11.5 * SCALE));
        g2.setFont(fonteResposta);
        
        if (isHovered) {
            g2.setColor(new Color(30, 30, 30)); // Cinza grafite bem escuro
        } else {
            g2.setColor(new Color(245, 245, 245)); // Branco fosco
        }

        if (cardAnswerTxt != null && !cardAnswerTxt.isEmpty()) {
            
            if (isEspecial) {
                // SE FOR CARTA ESPECIAL: Desenha o "OK" perfeitamente centralizado no meio do botão inteiro
                FontMetrics fmTxt = g2.getFontMetrics();
                int txtWidth = fmTxt.stringWidth(cardAnswerTxt);
                int txtX = (w - txtWidth) / 2;
                int txtY = (h - fmTxt.getHeight()) / 2 + fmTxt.getAscent();
                g2.drawString(cardAnswerTxt, txtX, txtY);
                
            } else {
                // SE FOR CARTA NORMAL: Quebra de linha e alinhamento pela direita da barra colorida
                int xCardAnswerTxt = larguraPainelEsquerdo + (int) (12 * SCALE); 
                int larguraMaximaTexto = w - xCardAnswerTxt - (int) (16 * SCALE);
                
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

                float yConstrucao = (h - alturaTotal) / 2f + linhas.get(0).getAscent();

                for (TextLayout layout : linhas) {
                    layout.draw(g2, xCardAnswerTxt, yConstrucao);
                    yConstrucao += layout.getDescent() + layout.getLeading() + layout.getAscent();
                }
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