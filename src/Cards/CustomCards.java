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
    
   
    private Color corFundo;
    private Image imgPeao;
    private Image imgEfeito;
    private Image backImgCard;
    private Image frontImgCard;
    private boolean displayBackImgCard = false;
    private CardOptionButton[] botoesOpcao;


    private CardsContainer painelPai;

    private static final String backImgCardURL = "/assets/backImgCard_220x340.png"; // Caminho da imagem de fundo da carta (verso)
    
    
    
       

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================
    
    // Construtor 1: Cartas Especiais (Sorte, Azar, Sacanear)
    public CustomCards(int cardID, String cardType, String mainTxt, String cardEffect, String cardValue, String nomeIconePeao) {
        this.cardID = cardID;
        this.cardType = cardType.toUpperCase();
        this.mainTxt = mainTxt;
        this.cardEffect = cardEffect.toUpperCase();
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
        
        // Chama o método que irá carregar a imagem da frente da carta certa (Nesse construtor deve ser de carta especial)
        selectFrontCardImage();
    }

    // Construtor 2: Pergunta SIM / NÃO
    public CustomCards(int cardID, String cardType, String mainTxt, String cardEffect, String cardValue, String nomeIconePeao, String dificuldade, String cardAnswer) {
        this.cardID = cardID;
        this.cardType = cardType.toUpperCase();
        this.mainTxt = mainTxt;
        this.cardEffect = cardEffect.toUpperCase();
        this.cardValue = cardValue;
        this.dificuldade = dificuldade != null ? dificuldade.toUpperCase() : "";
        this.tipoPergunta = "SIM_NAO";
        this.cardAnswer = cardAnswer;
        this.alternativas = new String[]{"Sim", "Não"}; // Inicializa automaticamente as duas opções
        inicializarBotoesAlternativas();
        carregarIcones(nomeIconePeao);
        configurarComponente();
        //inicializarBotoesAlternativas(); // Cria e adiciona os botões físicos

        java.net.URL backImgCardPath = getClass().getResource(backImgCardURL);
        if (backImgCardPath != null) {
            this.backImgCard = new ImageIcon(backImgCardPath).getImage();
        } else {
            System.err.println(
                "[CustomCards] Erro: Imagem de fundo da carta não encontrada em /assets/images/deckImage_220x340.png"
            );
            this.backImgCard = null;
        }

        // Chama o método que irá carregar a imagem da frente da carta certa (Nesse construtor deve ser de carta de pergunta sim/não)
        selectFrontCardImage();
    }


    // Construtor 3: Pergunta de MÚLTIPLA ESCOLHA
    public CustomCards(int cardID, String cardType, String mainTxt, String cardEffect, String cardValue, String nomeIconePeao, String dificuldade, String[] alternativas, String cardAnswer) {
        this.cardID = cardID;
        this.cardType = cardType.toUpperCase();
        this.mainTxt = mainTxt;
        this.cardEffect = cardEffect.toUpperCase();
        this.cardValue = cardValue;
        this.dificuldade = dificuldade != null ? dificuldade.toUpperCase() : "";
        this.tipoPergunta = "MULTIPLA_ESCHLE";
        this.alternativas = alternativas;
        this.cardAnswer = cardAnswer;
        inicializarBotoesAlternativas();
        carregarIcones(nomeIconePeao);
        configurarComponente();
        // inicializarBotoesAlternativas(); // Cria e adiciona os botões físicos

        java.net.URL backImgCardPath = getClass().getResource(backImgCardURL);
        if (backImgCardPath != null) {
            this.backImgCard = new ImageIcon(backImgCardPath).getImage();
        } else {
            System.err.println(
                "[CustomCards] Erro: Imagem de fundo da carta não encontrada em /assets/images/deckImage_220x340.png"
            );
            this.backImgCard = null;
        }

        // Chama o método que irá carregar a imagem da frente da carta certa (Nesse construtor deve ser de carta de pergunta multipla escolha)
        selectFrontCardImage();
    }

    public void setPainelPai(gui.windows.CardsContainer painelPai) {
        this.painelPai = painelPai;
    }

    private void configurarComponente() {
        setSize(200, 340);
        setBounds(0, 0, 200, 340);
        setOpaque(false);
        setLayout(null); // Ativa o posicionamento absoluto para podermos alinhar os botões via código
    }

   
    /**
     * Instancia e adiciona os componentes reais de botão centralizados
     * de forma reta (sem inclinação/skew).
     */
    private void inicializarBotoesAlternativas() {
            if (this.alternativas == null || this.alternativas.length == 0) return;

            // 1. Inicializa o array com o tamanho exato da quantidade de alternativas
            this.botoesOpcao = new CardOptionButton[alternativas.length];

            int larguraBotao = 152; 
            int alturaBotao = 32;
            int espacamento = 6;
            int yInicial = 165; 
            int xCentralizado = 29; 

            Color corInterna = new Color(255, 255, 255, 195);
            if (this.cardType.equals("PERGUNTA") && this.dificuldade.equals("FÁCIL")) {
                corInterna = Color.decode("#99AD7A"); 
            }

            // Letras dinâmicas de A a D
            String[] letras = {"A", "B", "C", "D"};

            for (int i = 0; i < alternativas.length; i++) {
                // 1. Cria o botão
                CardOptionButton btn = new CardOptionButton(alternativas[i], letras[i], corInterna, this.tipoPergunta);
                
                int yBotao = yInicial + (i * (alturaBotao + espacamento));
                btn.setBounds(xCentralizado, yBotao, larguraBotao, alturaBotao);

                this.botoesOpcao[i] = btn;

                this.add(btn); // Adiciona o botão à carta
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

    // Método para inserir o texto principal do card (conteúdo vem de um arquivo JSON)
    private void cardMainTxt(Graphics2D g2) {
        // Se não houver texto, não faz nada
        if (mainTxt == null || mainTxt.trim().isEmpty()) return;

        // Configuração da Fonte (Ajuste o tamanho e estilo como preferir)
        Font mainTxtFont = new Font("Arial", Font.BOLD, 14);
        g2.setFont(mainTxtFont);
        g2.setColor(Color.WHITE); // Cor do texto

        // Configuração da área onde o texto pode ser escrito
        float marginX = 25; // Distância da borda esquerda
        float maxWidth = this.getWidth() - (marginX * 2); // Largura total menos as margens
        float posY = 70; // Posição Y inicial (ajuste para descer ou subir o texto na sua arte)

        // Prepara o texto para a quebra de linha automática
        AttributedString attributedString = new AttributedString(mainTxt);
        attributedString.addAttribute(TextAttribute.FONT, mainTxtFont);
        AttributedCharacterIterator paragraph = attributedString.getIterator();
        int paragraphStart = paragraph.getBeginIndex();
        int paragraphEnd = paragraph.getEndIndex();

        FontRenderContext frc = new FontRenderContext(null, true, true);
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);
        lineMeasurer.setPosition(paragraphStart);
           
       // Loop que escreve linha por linha até o texto acabar
        while (lineMeasurer.getPosition() < paragraphEnd) {
            TextLayout layout = lineMeasurer.nextLayout(maxWidth);
            
            posY += layout.getAscent(); // Desce a altura da letra
            layout.draw(g2, marginX, posY); // Desenha a linha atual
            posY += layout.getDescent() + layout.getLeading(); // Prepara o Y para a próxima linha
        }
    }

    // Método para inserir o texto referente ao valor da carta (conteúdo vem de um arquivo JSON)
    private void cardEffectValue(Graphics2D g2) {
        // Verifica se há um valor válido a ser desenhado (Ex: "2", "3", "-1")
        if (this.cardValue == null || this.cardValue.trim().isEmpty()) {
            return;
        }

        // Configuração estética do texto do efeito
        Font cardEffectValueFont = new Font("Arial", Font.BOLD, 14);
        g2.setFont(cardEffectValueFont);
        g2.setColor(Color.WHITE);

        // Altere os valores abaixo de acordo com a posição real do seu ícone de casa
        float cardEffectValueSymbolX = 75;       // Posição X horizontal onde o seu símbolo começa
        float cardEffectValueSymbolWidth = 22; // Largura aproximada do seu desenho/ícone de casa
        float margin = 6;     // Distância em pixels entre o símbolo e o início do texto
        
        // O texto começará exatamente onde o símbolo termina + o espaçamento definido
        float cardEffectValueTxtX = cardEffectValueSymbolX + cardEffectValueSymbolWidth + margin;
        
        // Alinhamento vertical (Y). Mude para subir ou descer a linha do texto
        float cardEffectValueTxtY = 302.5f; 

        // Desenha o valor extraído do JSON (ex: +3) na posição horizontal calculada
        g2.drawString(this.cardValue, cardEffectValueTxtX, cardEffectValueTxtY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(); 
        int h = getHeight();
        

        //*perguntar o pq desse displayBackImgCard sem condição dentro do if */
        if (displayBackImgCard) {
            if (backImgCard != null) {
                g2.drawImage(backImgCard, 0, 0, getWidth(), getHeight(), this);
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
            cardMainTxt(g2);
            cardEffectValue(g2);
        } else {
            // Fundo roxo clássico de segurança caso a imagem do molde falte
            g2.setColor(corFundo);
            g2.fillRoundRect(0, 0, w, h, 18, 18);   
        }
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
        this.repaint(); // Força a repintura imediata ao mudar de estado
    }

    // Método para o CardsContainer conseguir acessar os botões
    public CardOptionButton[] getBotoesOpcao() {
        return this.botoesOpcao;
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
                imgReference = "mediumCardFrontImg_220x340.png";
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

