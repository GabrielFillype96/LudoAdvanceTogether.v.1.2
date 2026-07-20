package cards;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import gui.components.buttons.cardsButton.CardOptionButton;
import actions.CardAnswerValidation;
import gui.windows.CardsContainer;
import java.awt.image.BufferedImage;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;

public class CustomCards extends JPanel {
    
    // Variáveis
    private int cardID;
    private String cardType;       
    private String mainTxt;  
    private String cardEffect;     
    private String cardValue;      
    
    private String dificuldade;    
    private String tipoPergunta;   
    private String[] alternativas; 
    private String cardAnswer;     
    private boolean isHovered = false; // Controla se o mouse está sobre a carta
   
    private Color corFundo;
    private Image imgPeao;
    private Image imgEfeito;
    private Image backImgCard;
    private Image frontImgCard;
    private boolean displayBackImgCard = false;
    private CardOptionButton[] botoesOpcao;
    private CardOptionButton botaoConfirmar;
    private static final double SCALE = 1.5;

    private CardsContainer painelPai;

    private static final String backImgCardURL = "/assets/backImgCard_220x340.png";
    
    private static Font fontAwesomeSolid = null;

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================
    
    // Construtor 1: Cartas Especiais (Sorte, Azar, Sacanear, Pegadinha)
    public CustomCards(int cardID, String cardType, String mainTxt, String cardEffect, String cardValue, String nomeIconePeao) {
        this.cardID = cardID;
        this.cardType = cardType != null ? cardType.trim().toUpperCase() : "";
        this.mainTxt = mainTxt;
        this.cardEffect = cardEffect != null ? cardEffect.trim().toUpperCase() : "";
        this.cardValue = cardValue;
        this.dificuldade = "";
        this.tipoPergunta = "";
        this.alternativas = null;
        this.cardAnswer = "";
        
        carregarIcones(nomeIconePeao);
        configurarComponente();

        setOpaque(false);
        setLayout(null);
        setSize(220, 340);

        java.net.URL backImgCardPath = getClass().getResource(backImgCardURL);
        if (backImgCardPath != null) {
            this.backImgCard = new ImageIcon(backImgCardPath).getImage();
        } else {
            System.err.println(
                "[CustomCards] Erro: Imagem de fundo da carta não encontrada em /assets/images/deckImage_220x340.png"
            );
            this.backImgCard = null;
        }
        
        selectFrontCardImage();
        inicializarBotaoConfirmar();
    }

    // Construtor 2: Pergunta SIM / NÃO
    public CustomCards(int cardID, String cardType, String mainTxt, String cardEffect, String cardValue, String nomeIconePeao, String dificuldade, String cardAnswer) {
        this.cardID = cardID;
        this.cardType = cardType != null ? cardType.trim().toUpperCase() : "";
        this.mainTxt = mainTxt;
        this.cardEffect = cardEffect != null ? cardEffect.trim().toUpperCase() : "";
        this.cardValue = cardValue;
        this.dificuldade = dificuldade != null ? dificuldade.trim().toUpperCase() : "";
        this.tipoPergunta = "SIM_NAO";
        this.cardAnswer = cardAnswer;
        this.alternativas = new String[]{"Sim", "Não"};
        inicializarBotoesAlternativas();
        carregarIcones(nomeIconePeao);
        configurarComponente();

        java.net.URL backImgCardPath = getClass().getResource(backImgCardURL);
        if (backImgCardPath != null) {
            this.backImgCard = new ImageIcon(backImgCardPath).getImage();
        } else {
            System.err.println(
                "[CustomCards] Erro: Imagem de fundo da carta não encontrada em /assets/images/deckImage_220x340.png"
            );
            this.backImgCard = null;
        }

        selectFrontCardImage();
    }

    // Construtor 3: Pergunta de MÚLTIPLA ESCOLHA
    public CustomCards(int cardID, String cardType, String mainTxt, String cardEffect, String cardValue, String nomeIconePeao, String dificuldade, String[] alternativas, String cardAnswer) {
        this.cardID = cardID;
        this.cardType = cardType != null ? cardType.trim().toUpperCase() : "";
        this.mainTxt = mainTxt;
        this.cardEffect = cardEffect != null ? cardEffect.trim().toUpperCase() : "";
        this.cardValue = cardValue;
        this.dificuldade = dificuldade != null ? dificuldade.trim().toUpperCase() : "";
        this.tipoPergunta = "MULTIPLA_ESCHLE";
        this.alternativas = alternativas;
        this.cardAnswer = cardAnswer;
        inicializarBotoesAlternativas();
        carregarIcones(nomeIconePeao);
        configurarComponente();

        java.net.URL backImgCardPath = getClass().getResource(backImgCardURL);
        if (backImgCardPath != null) {
            this.backImgCard = new ImageIcon(backImgCardPath).getImage();
        } else {
            System.err.println(
                "[CustomCards] Erro: Imagem de fundo da carta não encontrada em /assets/images/deckImage_220x340.png"
            );
            this.backImgCard = null;
        }

        selectFrontCardImage();
    }

    public void setPainelPai(gui.windows.CardsContainer painelPai) {
        this.painelPai = painelPai;
    }

    private void configurarComponente() {
        setSize(200, 340);
        setBounds(0, 0, 200, 340);
        setOpaque(false);
        setLayout(null);

        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!isHovered) {
                    isHovered = true;
                    setLocation(getX(), getY() - 8);
                    repaint();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (contains(e.getPoint())) {
                    return; 
                }
                
                if (isHovered) {
                    isHovered = false;
                    setLocation(getX(), getY() + 8);
                    repaint();
                }
            }
        });
    }

    private void inicializarBotoesAlternativas() {
        if (this.alternativas == null || this.alternativas.length == 0) return;

        this.botoesOpcao = new CardOptionButton[alternativas.length];

        int larguraCarta = (int) (250 * SCALE); 
        int larguraBotao = (int) (215 * SCALE); 
        int alturaBotao = (int) (42 * SCALE); 
        int espacamento = (int) (5 * SCALE);  
        
        int xCentralizado = (larguraCarta - larguraBotao) / 2; 

        int textoStartY = (int) (72 * SCALE); 
        int larguraMaximaTexto = larguraCarta - (int) (40 * SCALE); 
        Font fonteRealTexto = new Font("Arial", Font.BOLD, (int) (14 * SCALE));
        
        int alturaTexto = calcularAlturaTexto(
            this.mainTxt, 
            fonteRealTexto, 
            larguraMaximaTexto
        );
        
        int textoEndY = textoStartY + alturaTexto;
        int margemDeSeguranca = (int) (30 * SCALE);
        
        int yIdeal = textoEndY + margemDeSeguranca;
        int yMinimo = (int) (185 * SCALE); 
        
        int yInicial = Math.max(yMinimo, yIdeal);

        Color corInterna = new Color(255, 255, 255, 195);
        if (this.cardType.equals("PERGUNTA") && this.dificuldade.equals("FÁCIL")) {
            corInterna = Color.decode("#99AD7A");
        }

        String[] letras;
        if (this.tipoPergunta != null && this.tipoPergunta.toUpperCase().contains("SIM_NAO")) {
            letras = new String[]{"S", "N"};
        } else {
            letras = new String[]{"A", "B", "C", "D"};
        }
        
        for (int i = 0; i < alternativas.length; i++) {
            String letraAtual = (i < letras.length) ? letras[i] : String.valueOf((char)('A' + i));
            String textoCompleto = letraAtual + ") " + alternativas[i];
            
            CardOptionButton btn = new CardOptionButton(
                textoCompleto, 
                this.dificuldade, 
                corInterna, 
                this.tipoPergunta
            );
            
            int yBotao = yInicial + (i * (alturaBotao + espacamento));
            btn.setBounds(xCentralizado, yBotao, larguraBotao, alturaBotao);
            
            this.botoesOpcao[i] = btn;
            adicionarListenerHoverBotoes(btn);
            this.add(btn); 
        }
    }

    private void inicializarBotaoConfirmar() {
        this.botaoConfirmar = new CardOptionButton("OK", this.cardType); 
        
        this.botaoConfirmar.setBounds(
            35, 
            (int) (195 * SCALE), 
            (int) (200 * SCALE), 
            (int) (32 * SCALE)
        ); 
        
        adicionarListenerHoverBotoes(this.botaoConfirmar);
        this.add(this.botaoConfirmar);
    }

    private void carregarIcones(String nomeIconePeao) {
        try {
            if (nomeIconePeao != null && !nomeIconePeao.isEmpty()) {
                java.net.URL urlPeao = getClass().getResource("/assets/" + nomeIconePeao);
                if (urlPeao == null) urlPeao = getClass().getResource("/assets/icons/" + nomeIconePeao);
                if (urlPeao != null) this.imgPeao = new ImageIcon(urlPeao).getImage();
            }
            
            String iconeEfeito = this.cardEffect.equals("AVANÇAR") ? "setA_avancar.png" : "setA_retroceder.png";
            java.net.URL urlEfeito = getClass().getResource("/assets/" + iconeEfeito);
            if (urlEfeito == null) urlEfeito = getClass().getResource("/assets/icons/" + iconeEfeito);
            if (urlEfeito != null) this.imgEfeito = new ImageIcon(urlEfeito).getImage();
        } catch (Exception e) {
            System.err.println("[CustomCards] Aviso ao carregar ícones: " + e.getMessage());
        }
    }

    private int calcularAlturaTexto(String texto, Font fonte, int larguraMaxima) {
        if (texto == null || texto.isEmpty()) return 0;

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        FontRenderContext frc = g2.getFontRenderContext();

        AttributedString attrStr = new AttributedString(texto);
        attrStr.addAttribute(TextAttribute.FONT, fonte);
        AttributedCharacterIterator iterator = attrStr.getIterator();

        LineBreakMeasurer measurer = new LineBreakMeasurer(iterator, frc);
        float alturaTotal = 0;

        while (measurer.getPosition() < iterator.getEndIndex()) {
            TextLayout layout = measurer.nextLayout(larguraMaxima);
            alturaTotal += layout.getAscent() + layout.getDescent() + layout.getLeading();
        }

        g2.dispose();
        return (int) alturaTotal;
    }

    private void cardMainTxt(Graphics2D g2) {
        float marginX = (int) (20 * SCALE);
        float maxWidth = this.getWidth() - (marginX * 2);

        if (maxWidth <= 0) {
            return;
        }
        
        g2.setColor(new Color(245, 245, 245));
        Font mainTxtFont = new Font("Arial", Font.BOLD, (int) (14 * SCALE));
        g2.setFont(mainTxtFont);

        float posY = (int) (72 * SCALE);

        AttributedString attributedString = new AttributedString(mainTxt);
        attributedString.addAttribute(TextAttribute.FONT, mainTxtFont);
        AttributedCharacterIterator paragraph = attributedString.getIterator();
        
        FontRenderContext frc = g2.getFontRenderContext();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);
        lineMeasurer.setPosition(paragraph.getBeginIndex());
           
        while (lineMeasurer.getPosition() < paragraph.getEndIndex()) {
            TextLayout layout = lineMeasurer.nextLayout(maxWidth);
            posY += layout.getAscent();
            
            layout.draw(g2, marginX, posY);
            posY += layout.getDescent() + layout.getLeading();
        }
    }

    private void drawCardHeader(Graphics2D g2) {
        if (displayBackImgCard) return;

        float marginX = (int) (20 * SCALE);
        float posY = (int) (42 * SCALE);

        g2.setFont(new Font("Arial", Font.BOLD, (int) (9 * SCALE)));
        g2.setColor(new Color(245, 245, 245, 180));

        String textoHeader = this.cardType;
        if (this.dificuldade != null && !this.dificuldade.isEmpty()) {
            textoHeader += " • " + this.dificuldade;
        }
        g2.drawString(textoHeader, marginX, posY);

        if ("PERGUNTA".equalsIgnoreCase(this.cardType)) {
            String estrelas = "";
            String dif = this.dificuldade != null ? this.dificuldade.toUpperCase() : "";

            if (dif.contains("FÁCIL") || dif.contains("FACIL")) {
                estrelas = "\u2605";
            } else if (dif.contains("MÉDIO") || dif.contains("MEDIO")) {
                estrelas = "\u2605\u2605";
            } else if (dif.contains("DIFÍCIL") || dif.contains("DIFICIL")) {
                estrelas = "\u2605\u2605\u2605";
            }

            if (!estrelas.isEmpty()) {
                g2.setFont(new Font("Dialog", Font.PLAIN, (int) (12 * SCALE))); 
                int larguraEstrelas = g2.getFontMetrics().stringWidth(estrelas);
                
                float estrelasX = this.getWidth() - (int) (22 * SCALE) - larguraEstrelas;
                
                g2.setColor(new Color(255, 215, 0, 220)); 
                g2.drawString(estrelas, estrelasX, posY + (int)(1 * SCALE));
            }
        }
    }

    private void cardEffectValue(Graphics2D g2) {
        if (this.cardValue == null || this.cardValue.trim().isEmpty()) {
            return;
        }

        float posY = (int) (355 * SCALE);

        Font iconeFont = getFontAwesome();
        Font textoFont = new Font("Arial", Font.BOLD, (int) (14 * SCALE));

        String iconeCasa = "\uf015"; 
        String textoExibicao = this.cardValue;
        if (!textoExibicao.contains("-") && !textoExibicao.contains("+")) {
            textoExibicao = "+" + textoExibicao;
        }

        g2.setFont(iconeFont);
        int larguraIcone = g2.getFontMetrics().stringWidth(iconeCasa);

        g2.setFont(textoFont);
        int larguraTexto = g2.getFontMetrics().stringWidth(textoExibicao);

        int espacamento = (int) (8 * SCALE);

        int larguraTotalConjunto = larguraIcone + espacamento + larguraTexto;

        float posX = (this.getWidth() - larguraTotalConjunto) / 2f;

        g2.setColor(new Color(245, 245, 245));

        g2.setFont(iconeFont);
        g2.drawString(iconeCasa, posX, posY);

        g2.setFont(textoFont);
        float textoX = posX + larguraIcone + espacamento; 
        float textoY = posY - (int) (2 * SCALE);

        g2.drawString(textoExibicao, textoX, textoY);
    }

    private Font getFontAwesome() {
        if (fontAwesomeSolid == null) {
            try {
                java.io.InputStream is = getClass().getResourceAsStream("/assets/fonts/Font Awesome 7 Free-Solid-900.otf");
                if (is != null) {
                    Font fontBase = Font.createFont(Font.TRUETYPE_FONT, is);
                    fontAwesomeSolid = fontBase.deriveFont(Font.PLAIN, (int) (18 * SCALE));
                } else {
                    fontAwesomeSolid = new Font("SansSerif", Font.PLAIN, (int) (18 * SCALE));
                }
            } catch (Exception e) {
                e.printStackTrace();
                fontAwesomeSolid = new Font("SansSerif", Font.PLAIN, (int) (18 * SCALE));
            }
        }
        return fontAwesomeSolid;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        if (width <= 30) {
            if (displayBackImgCard && backImgCard != null) {
                g2.drawImage(backImgCard, 0, 0, width, height, this);
            } else if (frontImgCard != null) {
                g2.drawImage(frontImgCard, 0, 0, width, height, this);
            }
            g2.dispose();
            return;
        }

        if (isHovered && !displayBackImgCard) {
            g2.setColor(new Color(0, 0, 0, 70));
            int arcSize = (int) (20 * SCALE);
            g2.fillRoundRect(2, 6, width - 4, height - 4, arcSize, arcSize);
        }

        if (displayBackImgCard && backImgCard != null) {
            g2.drawImage(backImgCard, 0, 0, width, height, this);
        } else if (frontImgCard != null) {
            g2.drawImage(frontImgCard, 0, 0, width, height, this);
        }

        drawCardHeader(g2);

        if (!displayBackImgCard && mainTxt != null) {
            cardMainTxt(g2);
        }

        if (!displayBackImgCard) {
            cardEffectValue(g2);
        }

        g2.dispose();
    }

    private void adicionarListenerHoverBotoes(CardOptionButton btn) {
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                java.awt.Point p = javax.swing.SwingUtilities.convertPoint(btn, e.getPoint(), CustomCards.this);
                
                if (!CustomCards.this.contains(p)) {
                    if (isHovered) {
                        isHovered = false;
                        setLocation(getX(), getY() + 8);
                        repaint();
                    }
                }
            }
        });
    }

    // --- GETTERS ---
    public int getCardID() { return cardID; }
    public String getCardType() { return cardType; }
    public String getCardEffect() { return cardEffect; }
    public String getCardValueText() { return this.cardValue != null ? this.cardValue : "1"; }
    public String getDificuldade() { return dificuldade; }
    public String getTipoPergunta() { return tipoPergunta; }
    public String[] getAlternativas() { return alternativas; }
    public String getCardAnswer() { return this.cardAnswer != null ? this.cardAnswer.trim() : ""; }
    
    public boolean isDisplayingBackImgCard() { return displayBackImgCard; }
    public void setDisplayingBackImgCard(boolean displayingBackImgCard) { 
        this.displayBackImgCard = displayingBackImgCard;
        this.repaint();
    }

    public CardOptionButton[] getBotoesOpcao() {
        return this.botoesOpcao;
    }

    public CardOptionButton getConfirmButton() {
        return this.botaoConfirmar;
    }

    private void selectFrontCardImage() {
        int width = (int) (250 * SCALE);
        int height = (int) (375 * SCALE);

        BufferedImage moldImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = moldImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(width, height);
        float[] dist = {0.0f, 0.5f, 1.0f};

        if ("PERGUNTA".equalsIgnoreCase(this.cardType)) {
            Color corFundoCarta = new Color(140, 82, 255); // Roxo de segurança
            if ("FÁCIL".equalsIgnoreCase(this.dificuldade) || "FACIL".equalsIgnoreCase(this.dificuldade)) {
                corFundoCarta = Color.decode("#99AD7A"); // Verde suave
            } else if ("MÉDIO".equalsIgnoreCase(this.dificuldade) || "MEDIO".equalsIgnoreCase(this.dificuldade)) {
                corFundoCarta = Color.decode("#E8A857"); // Laranja/Amarelo médio
            } else if ("DIFÍCIL".equalsIgnoreCase(this.dificuldade) || "DIFICIL".equalsIgnoreCase(this.dificuldade)) {
                corFundoCarta = Color.decode("#C75B5B"); // Vermelho escuro
            }
            g2.setColor(corFundoCarta);
        } else {
            // Lógica das Cartas Especiais com Efeito Metálico
            if ("SORTE".equalsIgnoreCase(this.cardType)) {
                // OURO PREMIUM
                Color[] colorsOuro = {
                    Color.decode("#AA771C"),
                    Color.decode("#FFDF00"),
                    Color.decode("#D4AF37")
                };
                LinearGradientPaint gradientOuro = new LinearGradientPaint(start, end, dist, colorsOuro);
                g2.setPaint(gradientOuro);
                
            } else if ("AZAR".equalsIgnoreCase(this.cardType)) {
                // BRONZE ACOBREADO
                Color[] colorsBronze = {
                    Color.decode("#593114"),
                    Color.decode("#DDA066"),
                    Color.decode("#8C5026")
                };
                LinearGradientPaint gradientBronze = new LinearGradientPaint(start, end, dist, colorsBronze);
                g2.setPaint(gradientBronze);
                
            } else if ("SACANEAR".equalsIgnoreCase(this.cardType) || "PEGADINHA".equalsIgnoreCase(this.cardType)) {
                // PRATA POLIDA
                Color[] colorsPrata = {
                    Color.decode("#707070"),
                    Color.decode("#F0F0F0"),
                    Color.decode("#B0B0B0")
                };
                LinearGradientPaint gradientPrata = new LinearGradientPaint(start, end, dist, colorsPrata);
                g2.setPaint(gradientPrata);
                
            } else {
                // COR DE SEGURANÇA
                Color[] colorsPadrao = {
                    Color.decode("#3A3D40"),
                    Color.decode("#686C70"),
                    Color.decode("#4A4D50")
                };
                LinearGradientPaint gradientPadrao = new LinearGradientPaint(start, end, dist, colorsPadrao);
                g2.setPaint(gradientPadrao);
            }
        }

        int arcSize = (int) (20 * SCALE);
        g2.fillRoundRect(0, 0, width, height, arcSize, arcSize);

        // MOLDE INTERNO PRETO FOSCO
        int margem = (int) (12 * SCALE); 
        int internoWidth = width - (margem * 2);
        int internoHeight = height - (margem * 2);
        int arcSizeInterno = (int) (12 * SCALE);

        g2.setColor(new Color(20, 20, 20, 150)); 
        g2.fillRoundRect(margem, margem, internoWidth, internoHeight, arcSizeInterno, arcSizeInterno);

        g2.setColor(new Color(255, 255, 255, 25)); 
        g2.setStroke(new BasicStroke((float) (1.0 * SCALE)));
        g2.drawRoundRect(margem, margem, internoWidth, internoHeight, arcSizeInterno, arcSizeInterno);

        // Contorno externo final
        g2.setColor(new Color(30, 30, 30, 120)); 
        g2.setStroke(new BasicStroke((float) (2.5 * SCALE)));
        g2.drawRoundRect(
            (int) (1.2 * SCALE), 
            (int) (1.2 * SCALE), 
            width - (int) (2.4 * SCALE), 
            height - (int) (2.4 * SCALE), 
            arcSize, 
            arcSize
        );

        g2.dispose();
        this.frontImgCard = moldImg;
    }
}