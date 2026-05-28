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
import java.text.AttributedString;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import gui.components.buttons.cardsButton.CardOptionButton;
import actions.CardAnswerValidation;


public class CustomCards extends JPanel {
    
   // Variáveis
    private int cardID;
    private String cardType;       
    private String textoPrincipal;  
    private String cardEffect;     
    private String cardValue;      
    
    private String dificuldade;    
    private String tipoPergunta;   
    private String[] alternativas; 
    private String cardAnswer;     
    
   
    private Color corFundo;
    private Image imgPeao;
    private Image imgEfeito;
    private Image backImgCard;
    private Image frontImgCard;
    private boolean displayBackImgCard = true;

    private final String backImgCardURL = "/assets/backImgCard_220x340.png"; // Caminho da imagem de fundo da carta (verso)
    
    // --- CONSTANTES DE CORES PERSONALIZADAS ---
    private final Color MOLDURA_PRETA = new Color(20, 20, 20);
    private final Color TEXTO_ESCURO = new Color(35, 35, 35); 
    
    // Molduras Internas em Preto
    private final Color COR_MOLDURA_EXTERNA = MOLDURA_PRETA;
    private final Color COR_MOLDURA_INTERNA = MOLDURA_PRETA; 
    
    // Cores de fundo (Categorias)
    private final Color COR_FACIL = Color.decode("#546B41");     
    private final Color COR_MEDIO = new Color(222, 179, 102);    
    private final Color COR_DIFICIL = new Color(178, 34, 34);    
    
    // Tons Metálicos para as Cartas Especiais
    private final Color COR_SORTE = new Color(212, 175, 55);     
    private final Color COR_SACANEAR = new Color(150, 165, 175); 
    private final Color COR_AZAR = new Color(165, 90, 45);      

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================
    
    // Construtor 1: Cartas Especiais (Sorte, Azar, Sacanear)
    public CustomCards(int cardID, String cardType, String textoPrincipal, String cardEffect, String cardValue, String nomeIconePeao) {
        this.cardID = cardID;
        this.cardType = cardType.toUpperCase();
        this.textoPrincipal = textoPrincipal;
        this.cardEffect = cardEffect.toUpperCase();
        this.cardValue = cardValue;
        this.dificuldade = "";
        this.tipoPergunta = "";
        this.alternativas = null;
        this.cardAnswer = "";
        definirCorFundo();
        carregarIcones(nomeIconePeao);
        configurarComponente();

        setOpaque(false);
        setLayout(null);
        setSize(220, 340);

        java.net.URL backImgCardPath = getClass().getResource(backImgCardURL);
        if (backImgCardPath != null) {
            this.backImgCard = new ImageIcon(backImgCardPath).getImage();
        } else {
            System.err.println("[CustomCards] Erro: Imagem de fundo da carta não encontrada em /assets/images/deckImage_220x340.png");
            this.backImgCard = null;
        }
        // Chama o método que irá carregar a imagem da frente da carta certa (Nesse construtor deve ser de carta especial)
        selectFrontCardImage();
    }

    // Construtor 2: Pergunta SIM / NÃO
    public CustomCards(int cardID, String cardType, String textoPrincipal, String cardEffect, String cardValue, String nomeIconePeao, String dificuldade, String cardAnswer) {
        this.cardID = cardID;
        this.cardType = cardType.toUpperCase();
        this.textoPrincipal = textoPrincipal;
        this.cardEffect = cardEffect.toUpperCase();
        this.cardValue = cardValue;
        this.dificuldade = dificuldade != null ? dificuldade.toUpperCase() : "";
        this.tipoPergunta = "SIM_NAO";
        this.cardAnswer = cardAnswer;
        this.alternativas = new String[]{"Sim", "Não"}; // Inicializa automaticamente as duas opções
        definirCorFundo();
        carregarIcones(nomeIconePeao);
        configurarComponente();
        inicializarBotoesAlternativas(); // Cria e adiciona os botões físicos

        java.net.URL backImgCardPath = getClass().getResource(backImgCardURL);
        if (backImgCardPath != null) {
            this.backImgCard = new ImageIcon(backImgCardPath).getImage();
        } else {
            System.err.println("[CustomCards] Erro: Imagem de fundo da carta não encontrada em /assets/images/deckImage_220x340.png");
            this.backImgCard = null;
        }
        // Chama o método que irá carregar a imagem da frente da carta certa (Nesse construtor deve ser de carta de pergunta sim/não)
        selectFrontCardImage();
    }


    // Construtor 3: Pergunta de MÚLTIPLA ESCOLHA
    public CustomCards(int cardID, String cardType, String textoPrincipal, String cardEffect, String cardValue, String nomeIconePeao, String dificuldade, String[] alternativas, String cardAnswer) {
        this.cardID = cardID;
        this.cardType = cardType.toUpperCase();
        this.textoPrincipal = textoPrincipal;
        this.cardEffect = cardEffect.toUpperCase();
        this.cardValue = cardValue;
        this.dificuldade = dificuldade != null ? dificuldade.toUpperCase() : "";
        this.tipoPergunta = "MULTIPLA_ESCHLE";
        this.alternativas = alternativas;
        this.cardAnswer = cardAnswer;
        definirCorFundo();
        carregarIcones(nomeIconePeao);
        configurarComponente();
        inicializarBotoesAlternativas(); // Cria e adiciona os botões físicos

        java.net.URL backImgCardPath = getClass().getResource(backImgCardURL);
        if (backImgCardPath != null) {
            this.backImgCard = new ImageIcon(backImgCardPath).getImage();
        } else {
            System.err.println("[CustomCards] Erro: Imagem de fundo da carta não encontrada em /assets/images/deckImage_220x340.png");
            this.backImgCard = null;
        }

        // Chama o método que irá carregar a imagem da frente da carta certa (Nesse construtor deve ser de carta de pergunta multipla escolha)
        selectFrontCardImage();
    }

    private void configurarComponente() {
        setSize(200, 340);
        setBounds(0, 0, 200, 340);
        setOpaque(false);
        setLayout(null); // Ativa o posicionamento absoluto para podermos alinhar os botões via código
    }

    /**
     * Instancia e adiciona os componentes reais de botão dentro do painel da carta
     */
    /**
 * Instancia e adiciona os componentes reais de botão centralizados
 * de forma reta (sem inclinação/skew).
 */
private void inicializarBotoesAlternativas() {
    if (this.alternativas == null || this.alternativas.length == 0) return;

    int larguraBotao = 152; 
    int alturaBotao = 32;
    int espacamento = 6;
    int yInicial = 165; 
    int xCentralizado = 29; 

    Color corInterna = new Color(255, 255, 255, 195);
    if (this.cardType.equals("PERGUNTA") && this.dificuldade.equals("FÁCIL")) {
        corInterna = Color.decode("#99AD7A"); 
    }

    for (int i = 0; i < alternativas.length; i++) {
        CardOptionButton btn = new CardOptionButton(alternativas[i], this.tipoPergunta, corInterna);
        int yBotao = yInicial + (i * (alturaBotao + espacamento));
        btn.setBounds(xCentralizado, yBotao, larguraBotao, alturaBotao);

        // =========================================================================
        // REDIRECIONANDO O EVENTO PARA O PACOTE ACTIONS
        // =========================================================================
        btn.addActionListener(e -> {
            // Verifica se a carta está anexada ao CardsPanel antes de disparar
            if (getParent() instanceof gui.windows.CardsPanel) {
                gui.windows.CardsPanel painelPai = (gui.windows.CardsPanel) getParent();
                
                // Envia os dados para a sua classe especializada processar
                actions.CardAnswerValidation.validar(btn.getTextoCompleto(), this, painelPai);
            }
        });
        // =========================================================================

        this.add(btn);
    }
}

    private void definirCorFundo() {
        if (this.cardType.equals("PERGUNTA")) {
            switch (this.dificuldade) {
                case "FÁCIL" -> this.corFundo = COR_FACIL;
                case "MÉDIO" -> this.corFundo = COR_MEDIO;
                case "DIFÍCIL" -> this.corFundo = COR_DIFICIL;
                default -> this.corFundo = COR_MEDIO;
            }
        } else {
            switch (this.cardType) {
                case "SORTE" -> this.corFundo = COR_SORTE;
                case "SACANEAR" -> this.corFundo = COR_SACANEAR;
                case "AZAR" -> this.corFundo = COR_AZAR;
                default -> this.corFundo = COR_SORTE;
            }
        }
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(); 
        int h = getHeight();
        int raioCarta = 24;

        if (displayBackImgCard) {
            if (backImgCard != null) {
                g2.drawImage(backImgCard, 0, 0, w, h, this);
            } else {
                // Caso a imagem falte, pinta um fundo cinza de segurança
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f}, 0.0f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);

                // Escreve um aviso discreto na tela
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString("Verso não encontrado", 40, h / 2);
            }
            return; // INTERROMPE O DESENHO AQUI (Não desenha os textos por cima do verso!)
        }

        if (frontImgCard != null) {
            g2.drawImage(frontImgCard, 0, 0, w, h, this);
        } else {
            // Fundo roxo clássico de segurança caso a imagem do molde falte
            g2.setColor(corFundo);
            g2.fillRoundRect(0, 0, w, h, 18, 18);
        }

        // 1. FUNDO PRINCIPAL ESCURO
        g2.setColor(corFundo);
        g2.fillRoundRect(0, 0, w, h, raioCarta, raioCarta);

        // 2. MOLDURA EXTERNA DE CONTENÇÃO PRETA (Borda Fina)
        g2.setColor(MOLDURA_PRETA);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(1, 1, w - 2, h - 2, raioCarta, raioCarta);

        int offsetExt = 22;      
        int cruzExt = 14;        
        int pequenoOffset = 6;   
        int offsetInt = offsetExt + pequenoOffset; 
        int cruzInt = cruzExt - 2;

        // 3. MAPEAMENTO DO POLÍGONO DE RECORTE DAS QUINAS INVERTIDAS
        Polygon areaInternaClara = new Polygon();
        areaInternaClara.addPoint(offsetInt + cruzInt, offsetInt);
        areaInternaClara.addPoint(w - offsetInt - cruzInt, offsetInt);
        areaInternaClara.addPoint(w - offsetInt - cruzInt, offsetInt + cruzInt);
        areaInternaClara.addPoint(w - offsetInt, offsetInt + cruzInt);
        areaInternaClara.addPoint(w - offsetInt, h - offsetInt - cruzInt);
        areaInternaClara.addPoint(w - offsetInt - cruzInt, h - offsetInt - cruzInt);
        areaInternaClara.addPoint(w - offsetInt - cruzInt, h - offsetInt);
        areaInternaClara.addPoint(offsetInt + cruzInt, h - offsetInt);
        areaInternaClara.addPoint(offsetInt + cruzInt, h - offsetInt - cruzInt);
        areaInternaClara.addPoint(offsetInt, h - offsetInt - cruzInt);
        areaInternaClara.addPoint(offsetInt, offsetInt + cruzInt);
        areaInternaClara.addPoint(offsetInt + cruzInt, offsetInt + cruzInt);

        // Preenchimento do centro claro
        if (this.cardType.equals("PERGUNTA") && this.dificuldade.equals("FÁCIL")) {
            g2.setColor(Color.decode("#99AD7A"));
        } else {
            g2.setColor(new Color(255, 255, 255, 195)); 
        }
        g2.fillPolygon(areaInternaClara);

        // 4. DESENHO DAS MOLDURAS INTERNAS 
        g2.setColor(COR_MOLDURA_EXTERNA);
        g2.setStroke(new BasicStroke(2.0f));
        desenharMolduraCruzInvertida(g2, offsetExt, cruzExt, w, h);

        g2.setColor(COR_MOLDURA_INTERNA);
        g2.setStroke(new BasicStroke(1.2f)); 
        desenharMolduraCruzInvertida(g2, offsetInt, cruzInt, w, h);

        // 5. ICONE DO PEÃO (Texto removido conforme solicitado)
        if (imgPeao != null) {
            g2.drawImage(imgPeao, w - 46, 24, 24, 24, this);
        }

        // 6. ENUNCIADO DO TEXTO (FORMATAÇÃO E FONTE)
        g2.setColor(TEXTO_ESCURO);
        String nomeDaFonte = "Georgia"; 
        int estiloDaFonte = Font.BOLD; 
        int tamanhoDaFonte = 13;       
        g2.setFont(new Font(nomeDaFonte, estiloDaFonte, tamanhoDaFonte)); 
        
        int xTexto = offsetInt + 12;
        int yTexto = offsetInt + 18; 
        int larguraMaxTexto = w - (xTexto * 2);
        
        desenharTextoComQuebra(g2, textoPrincipal, xTexto, yTexto, larguraMaxTexto);

        // 7. RODAPÉ DA CARTA
        if (imgEfeito != null) {
            g2.drawImage(imgEfeito, 34, h - 72, 42, 42, this);
        }

        g2.setFont(new Font("Arial", Font.BOLD, 32));
        g2.setColor(Color.WHITE);
        g2.drawString(cardValue, 88, h - 38);
        
        g2.setFont(new Font("Arial", Font.ITALIC, 10));
        g2.setColor(new Color(230, 230, 230));
        g2.drawString("#" + cardID, w - 54, h - 32);

        g2.dispose();
    }

    private void desenharMolduraCruzInvertida(Graphics2D g2, int offset, int tamCruz, int w, int h) {
        g2.drawLine(offset + tamCruz, offset, w - offset - tamCruz, offset);
        g2.drawLine(offset + tamCruz, h - offset, w - offset - tamCruz, h - offset);
        g2.drawLine(offset, offset + tamCruz, offset, h - offset - tamCruz);
        g2.drawLine(w - offset, offset + tamCruz, w - offset, h - offset - tamCruz);
        
        g2.drawLine(offset, offset + tamCruz, offset + tamCruz, offset + tamCruz);
        g2.drawLine(offset + tamCruz, offset, offset + tamCruz, offset + tamCruz);
        g2.drawLine(w - offset, offset + tamCruz, w - offset - tamCruz, offset + tamCruz);
        g2.drawLine(w - offset - tamCruz, offset, w - offset - tamCruz, offset + tamCruz);
        g2.drawLine(offset, h - offset - tamCruz, offset + tamCruz, h - offset - tamCruz);
        g2.drawLine(offset + tamCruz, h - offset, offset + tamCruz, h - offset - tamCruz);
        g2.drawLine(w - offset, h - offset - tamCruz, w - offset - tamCruz, h - offset - tamCruz);
        g2.drawLine(w - offset - tamCruz, h - offset, w - offset - tamCruz, h - offset - tamCruz);
    }

    private void desenharTextoComQuebra(Graphics2D g2, String texto, int x, int y, int larguraMax) {
        if (texto == null || texto.isEmpty()) return;

        FontRenderContext frc = g2.getFontRenderContext();
        AttributedString attrString = new AttributedString(texto);
        attrString.addAttribute(TextAttribute.FONT, g2.getFont());
        attrString.addAttribute(TextAttribute.FOREGROUND, g2.getColor());
        
        LineBreakMeasurer measurer = new LineBreakMeasurer(attrString.getIterator(), frc);
        int endIndex = texto.length();

        while (measurer.getPosition() < endIndex) {
            TextLayout layout = measurer.nextLayout(larguraMax);
            y += layout.getAscent();
            layout.draw(g2, x, y);
            y += layout.getDescent() + layout.getLeading();
        }
    }

    // --- GETTERS ---
    public int getCardID() { return cardID; }
    public String getCardType() { return cardType; }
    public String getCardEffect() { return cardEffect; }
    public String getCardValueText() { return cardValue; }
    public String getDificuldade() { return dificuldade; }
    public String getTipoPergunta() { return tipoPergunta; }
    public String[] getAlternativas() { return alternativas; }
    public String getCardAnswer() { return cardAnswer; }
    
    public boolean isDisplayingBackImgCard() { return displayBackImgCard; }
    public void setDisplayingBackImgCard(boolean displayingBackImgCard) { 
        this.displayBackImgCard = displayingBackImgCard;
        this.repaint(); // Força a repintura imediata ao mudar de estado
    }

    // Método para selecionar a frente da carta de acordo com o tipo (pergunta/efeito)
    private void selectFrontCardImage() {
        String imgReference = ""; // Variável para armazenar o caminho da imagem

        // Lógica para escolher a imagem da frente com base no tipo e dificuldade
        // Se a carta for do tipo "PERGUNTA", escolhe a imagem de acordo com a dificuldade
        if ("PERGUNTA".equalsIgnoreCase(this.cardType)) {
            // Se a dificuldade for "FÁCIL", usa a imagem fácil
            if ("FÁCIL".equalsIgnoreCase(this.dificuldade)) {
                imgReference = "easyCardFrontImg_220x340.png";
                // Se a dificuldade for "MÉDIO", usa a imagem média
            } else if ("MÉDIO".equalsIgnoreCase(this.dificuldade)) {
                imgReference = "mediumCardFronImg_220x340.png";
                // Se a dificuldade for "DIFÍCIL", usa a imagem difícil
            } else if ("DIFÍCIL".equalsIgnoreCase(this.dificuldade)) {
                imgReference = "hardCardFrontImg_220x340.png";
            }
        } else {
            // Se a carta for do tipo "SORTE", "AZAR" ou "SACANEAR", escolhe a imagem correspondente
            if ("SORTE".equalsIgnoreCase(this.cardType)) {
            imgReference = "goodLuckCardFrontImg_220x340.png";
            } else if ("AZAR".equalsIgnoreCase(this.cardType)) {
                imgReference = "badLuckCardFrontImg_220x340.png";
            } else if ("SACANEAR".equalsIgnoreCase(this.cardType)) {
                imgReference = "trickCardFrontImg_220x340.png";
            }
        }

        // Se o caminho da imagem foi definido (diferente de vazio), tenta carregar a imagem da frente
        if (!imgReference.isEmpty()) {
            final java.net.URL frontImgURL = getClass().getResource("/assets/" + imgReference);
            // Se a imagem for encontrada, insere a imagem
            if (frontImgURL != null) {
                this.frontImgCard = new ImageIcon(frontImgURL).getImage();
            } else {
                // Se a imagem não for encontrada, exibe um erro no console e mantém a frente da carta como null (fundo roxo de segurança)
                System.err.println("[CustomCards] Erro: Moldura '/assets/" + imgReference + "' não encontrada.");
                this.frontImgCard = null;
            }
        }
    }
}

