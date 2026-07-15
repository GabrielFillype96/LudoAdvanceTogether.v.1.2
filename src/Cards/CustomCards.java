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

    private static final String backImgCardURL = "/assets/backImgCard_220x340.png"; // Caminho da imagem de fundo da carta (verso)
    
    private static Font fontAwesomeSolid = null;
    
       

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
        
        inicializarBotaoConfirmar();
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

       // Adiciona o efeito visual de "descolar da mesa" sem usar CPU
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!isHovered) {
                    isHovered = true;
                    // Move a carta suavemente 8 pixels para cima e redesenha a sombra
                    setLocation(getX(), getY() - 8);
                    repaint();
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // CORREÇÃO: Ignora o evento se o mouse apenas passou para cima de um botão interno
                if (contains(e.getPoint())) {
                    return; 
                }
                
                if (isHovered) {
                    isHovered = false;
                    // Retorna a carta para a posição original no tabuleiro
                    setLocation(getX(), getY() + 8);
                    repaint();
                }
            }
        });
    }

   
    /**
     * Instancia e adiciona os componentes reais de botão centralizados
     * de forma reta (sem inclinação/skew).
     */
    private void inicializarBotoesAlternativas() {
        if (this.alternativas == null || this.alternativas.length == 0) return;

        // 1. Inicializa o array com o tamanho exato da quantidade de alternativas
        this.botoesOpcao = new CardOptionButton[alternativas.length];

        int larguraBotao = (int) (215 * SCALE); 
        int alturaBotao = (int) (42 * SCALE); // Aumentado em 40% para caber mais linhas
        int espacamento = (int) (5 * SCALE);  // Levemente reduzido para compensar a altura
        int yInicial = (int) (185 * SCALE);   // Bloco movido para cima para liberar espaço 
        int xCentralizado = 29; 

        // Definimos uma cor padrão para o botão
        Color corInterna = new Color(255, 255, 255, 195);
        if (this.cardType.equals("PERGUNTA") && this.dificuldade.equals("FÁCIL")) {
            corInterna = Color.decode("#99AD7A");
        }

        // =========================================================================
        // DEFINIÇÃO DINÂMICA DAS LETRAS (A, B, C, D ou S, N)
        // =========================================================================
        String[] letras;
        if (this.tipoPergunta != null && this.tipoPergunta.toUpperCase().contains("SIM_NAO")) {
            letras = new String[]{"S", "N"};
        } else {
            letras = new String[]{"A", "B", "C", "D"};
        }
        
        for (int i = 0; i < alternativas.length; i++) {
            // Garante que não estoure o array de letras caso haja mais alternativas que o esperado
            String letraAtual = (i < letras.length) ? letras[i] : String.valueOf((char)('A' + i));
            
            // Formata o texto para o botão (ex: "S) Sim" ou "A) Alternativa")
            String textoCompleto = letraAtual + ") " + alternativas[i];
            
            // Instancia passando as variáveis alinhadas com o construtor do botão
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
            this.add(btn); // Adiciona o botão à carta
        }
    }

    /*
     * Método responsável por inicializar e posicionar o botão "OK"
     * para as cartas do tipo SORTE, AZAR ou SACANEAR.
     */
    private void inicializarBotaoConfirmar() {
        this.botaoConfirmar = new CardOptionButton("OK", this.cardType); 
        
        // Ajuste o x, y, largura e altura conforme o design da sua carta
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

    // Método para inserir o texto principal do card (conteúdo vem de um arquivo JSON)
    // Método para inserir o texto principal do card alinhado à esquerda com margens seguras
    private void cardMainTxt(Graphics2D g2) {
        g2.setColor(new Color(245, 245, 245)); // Branco fosco elegante
        Font mainTxtFont = new Font("Arial", Font.BOLD, (int) (14 * SCALE));
        g2.setFont(mainTxtFont);

        // Define a margem esquerda e direita (isso centraliza o "bloco" de texto na carta)
        float marginX = (int) (20 * SCALE);
        float maxWidth = this.getWidth() - (marginX * 2);
        float posY = (int) (72 * SCALE); // Altura onde o texto começa

        AttributedString attributedString = new AttributedString(mainTxt);
        attributedString.addAttribute(TextAttribute.FONT, mainTxtFont);
        AttributedCharacterIterator paragraph = attributedString.getIterator();
        
        FontRenderContext frc = g2.getFontRenderContext();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);
        lineMeasurer.setPosition(paragraph.getBeginIndex());
           
        while (lineMeasurer.getPosition() < paragraph.getEndIndex()) {
            // Divide o texto em linhas respeitando a largura máxima (maxWidth)
            TextLayout layout = lineMeasurer.nextLayout(maxWidth);
            posY += layout.getAscent();
            
            // Desenha cada linha encostada na margem esquerda de forma natural
            layout.draw(g2, marginX, posY);
            posY += layout.getDescent() + layout.getLeading();
        }
    }

    // Desenha o rótulo do tipo de carta e as estrelas de dificuldade no topo
private void drawCardHeader(Graphics2D g2) {
    if (displayBackImgCard) return; // Não desenha nada se a carta estiver virada

    float marginX = (int) (20 * SCALE);
    float posY = (int) (42 * SCALE); // Posicionado logo acima do enunciado

    // 1. DESENHO DO TEXTO DO CABEÇALHO (Ex: PERGUNTA • DIFÍCIL)
    g2.setFont(new Font("Arial", Font.BOLD, (int) (9 * SCALE)));
    g2.setColor(new Color(245, 245, 245, 180)); // Branco suave semi-transparente

    String textoHeader = this.cardType;
    if (this.dificuldade != null && !this.dificuldade.isEmpty()) {
        textoHeader += " • " + this.dificuldade;
    }
    g2.drawString(textoHeader, marginX, posY);

    // 2. DESENHO DAS ESTRELAS DE DIFICULDADE (Ex: ★★★)
    if ("PERGUNTA".equalsIgnoreCase(this.cardType)) {
        String estrelas = "";
        String dif = this.dificuldade != null ? this.dificuldade.toUpperCase() : "";

        // Usando o código Unicode (\u2605) em vez do símbolo colado para evitar erros de leitura da IDE
        if (dif.contains("FÁCIL") || dif.contains("FACIL")) {
            estrelas = "\u2605";
        } else if (dif.contains("MÉDIO") || dif.contains("MEDIO")) {
            estrelas = "\u2605\u2605";
        } else if (dif.contains("DIFÍCIL") || dif.contains("DIFICIL")) {
            estrelas = "\u2605\u2605\u2605";
        }

        if (!estrelas.isEmpty()) {
            // Trocando "Arial" por "Dialog", que é a fonte lógica do Java com melhor suporte a símbolos gráficos
            g2.setFont(new Font("Dialog", Font.PLAIN, (int) (12 * SCALE))); 
            int larguraEstrelas = g2.getFontMetrics().stringWidth(estrelas);
            
            // Margem direita levemente ajustada para garantir que o símbolo não fique fora do card
            float estrelasX = this.getWidth() - (int) (22 * SCALE) - larguraEstrelas;
            
            // Amarelo dourado suave para destacar as estrelas
            g2.setColor(new Color(255, 215, 0, 220)); 
            g2.drawString(estrelas, estrelasX, posY + (int)(1 * SCALE));
        }
    }
}

    // Método para inserir o texto referente ao valor da carta (conteúdo vem de um arquivo JSON)
    private void cardEffectValue(Graphics2D g2) {
        // Verifica se há um valor válido a ser desenhado (Ex: "2", "3", "-1")
        if (this.cardValue == null || this.cardValue.trim().isEmpty()) {
            return;
        }

        // Posição Y mantida exatamente como você já definiu
        float posY = (int) (355 * SCALE); // Ajuste para 355 * SCALE ou o valor que preferir

        // 1. Prepara as fontes para medição
        Font iconeFont = getFontAwesome();
        Font textoFont = new Font("Arial", Font.BOLD, (int) (14 * SCALE));

        // Símbolo do FontAwesome e formatação do texto
        String iconeCasa = "\uf015"; 
        String textoExibicao = this.cardValue;
        if (!textoExibicao.contains("-") && !textoExibicao.contains("+")) {
            textoExibicao = "+" + textoExibicao;
        }

        // 2. Mede a largura exata de cada componente individualmente
        g2.setFont(iconeFont);
        int larguraIcone = g2.getFontMetrics().stringWidth(iconeCasa);

        g2.setFont(textoFont);
        int larguraTexto = g2.getFontMetrics().stringWidth(textoExibicao);

        // Espaçamento entre o ícone e o número
        int espacamento = (int) (8 * SCALE);

        // Largura total combinada do conjunto (Ícone + Espaço + Texto)
        int larguraTotalConjunto = larguraIcone + espacamento + larguraTexto;

        // 3. Calcula o X inicial para centralizar perfeitamente no painel da carta
        float posX = (this.getWidth() - larguraTotalConjunto) / 2f;

        g2.setColor(new Color(245, 245, 245)); // Define a cor branca fosca

        // =========================================================================
        // 4. DESENHA O ÍCONE DA CASA
        // =========================================================================
        g2.setFont(iconeFont);
        g2.drawString(iconeCasa, posX, posY);

        // =========================================================================
        // 5. DESENHA A QUANTIDADE (Alinhado dinamicamente ao lado)
        // =========================================================================
        g2.setFont(textoFont);
        float textoX = posX + larguraIcone + espacamento; 
        float textoY = posY - (int) (2 * SCALE); // Sobe levemente para alinhar visualmente ao centro do ícone

        g2.drawString(textoExibicao, textoX, textoY);
    }

    // Método que carrega a fonte customizada na memória
    private Font getFontAwesome() {
        if (fontAwesomeSolid == null) {
            try {
                // OLHA O NOME DO ARQUIVO AQUI ABAIXO:
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

        // =========================================================================
        // INSERÇÃO: SOMBRA PROJETADA 3D (Efeito realista com custo zero de lag)
        // =========================================================================
        if (isHovered && !displayBackImgCard) {
            // Desenha uma silhueta escura deslocada para baixo simulando a sombra da carta no ar
            g2.setColor(new Color(0, 0, 0, 70)); // Preto translúcido bem suave
            int arcSize = (int) (20 * SCALE);
            // Desenha a sombra levemente deslocada (X+2, Y+6) e um pouco menor para dar profundidade
            g2.fillRoundRect(2, 6, width - 4, height - 4, arcSize, arcSize);
        }
        // =========================================================================

        // 1. Desenha o fundo estático (Sua lógica original intocada)
        if (displayBackImgCard && backImgCard != null) {
            g2.drawImage(backImgCard, 0, 0, width, height, this);
        } else if (frontImgCard != null) {
            g2.drawImage(frontImgCard, 0, 0, width, height, this);
        }

        drawCardHeader(g2);

        // 2. Desenha o texto principal por cima de tudo (Sua lógica original intocada)
        if (!displayBackImgCard && mainTxt != null) {
            cardMainTxt(g2);
        }

        if (!displayBackImgCard) {
            cardEffectValue(g2);
        }

        g2.dispose();
    }

    // Método para garantir que a carta perca o hover se o mouse sair do botão direto para fora dela
    private void adicionarListenerHoverBotoes(CardOptionButton btn) {
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Converte as coordenadas do botão para o sistema de coordenadas da carta
                java.awt.Point p = javax.swing.SwingUtilities.convertPoint(btn, e.getPoint(), CustomCards.this);
                
                // Se o mouse saiu do botão e as coordenadas já estão fora da carta, desce a carta
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
        this.repaint(); // Força a repintura imediata ao mudar de estado
    }

    // Método para o CardsContainer conseguir acessar os botões
    public CardOptionButton[] getBotoesOpcao() {
        return this.botoesOpcao;
    }

    public CardOptionButton getConfirmButton() {
        return this.botaoConfirmar;
    }

    // Método para selecionar a frente da carta de acordo com o tipo (pergunta/efeito)
    // Import complementar necessário no topo da classe (caso não tenha):


/**
 * Método adaptado para construir dinamicamente o molde gráfico da frente da carta
 * com bordas metálicas (Ouro, Prata e Bronze) para as cartas especiais.
 */
private void selectFrontCardImage() {
    // Define as dimensões reais baseadas na escala matemática (250x375 conforme constante da classe)
    int width = (int) (250 * SCALE);
    int height = (int) (375 * SCALE);

    // Cria uma imagem vazia em memória com suporte a transparência (Alpha)
    BufferedImage moldImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = moldImg.createGraphics();

    // Ativa suavização de serrilhado (Anti-aliasing) para bordas perfeitas
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Definição dos pontos do gradiente para cruzar a carta diagonalmente (Efeito de reflexo de luz)
    Point2D start = new Point2D.Float(0, 0);
    Point2D end = new Point2D.Float(width, height);
    float[] dist = {0.0f, 0.5f, 1.0f}; // Posições das cores no gradiente (início, meio, fim)

    // 1. Aplica as cores padrão ou os gradientes metálicos nas especiais
    if ("PERGUNTA".equalsIgnoreCase(this.cardType)) {
        Color corFundoCarta = new Color(140, 82, 255); // Roxo de segurança
        if ("FÁCIL".equalsIgnoreCase(this.dificuldade)) {
            corFundoCarta = Color.decode("#99AD7A"); // Verde suave
        } else if ("MÉDIO".equalsIgnoreCase(this.dificuldade)) {
            corFundoCarta = Color.decode("#E8A857"); // Laranja/Amarelo médio
        } else if ("DIFÍCIL".equalsIgnoreCase(this.dificuldade)) {
            corFundoCarta = Color.decode("#C75B5B"); // Vermelho escuro
        }
        g2.setColor(corFundoCarta);
    } else {
        // Lógica das Cartas Especiais com Efeito Metálico
        if ("SORTE".equalsIgnoreCase(this.cardType)) {
            // OURO PREMIUM
            Color[] colorsOuro = {
                Color.decode("#AA771C"), // Ouro Escuro / Sombra
                Color.decode("#FFDF00"), // Dourado Brilhante / Luz
                Color.decode("#D4AF37")  // Dourado Clássico
            };
            LinearGradientPaint gradientOuro = new LinearGradientPaint(start, end, dist, colorsOuro);
            g2.setPaint(gradientOuro);
            
        } else if ("AZAR".equalsIgnoreCase(this.cardType)) {
            // PRATA POLIDA
            Color[] colorsPrata = {
                Color.decode("#707070"), // Cinza Metálico / Sombra
                Color.decode("#F0F0F0"), // Prata Claro / Luz
                Color.decode("#B0B0B0")  // Cinza Prateado
            };
            LinearGradientPaint gradientPrata = new LinearGradientPaint(start, end, dist, colorsPrata);
            g2.setPaint(gradientPrata);
            
        } else if ("SACANEAR".equalsIgnoreCase(this.cardType)) {
            // BRONZE ACOBREADO
            Color[] colorsBronze = {
                Color.decode("#593114"), // Bronze Escuro / Sombra
                Color.decode("#DDA066"), // Bronze Claro / Luz
                Color.decode("#8C5026")  // Bronze Clássico
            };
            LinearGradientPaint gradientBronze = new LinearGradientPaint(start, end, dist, colorsBronze);
            g2.setPaint(gradientBronze);
        }
    }

    // 2. Desenha o preenchimento da base externa (Será a borda colorida ou metálica brilhante)
    int arcSize = (int) (20 * SCALE); // Curvatura dos cantos
    g2.fillRoundRect(0, 0, width, height, arcSize, arcSize);

    // =========================================================================
    // MOLDE INTERNO PRETO FOSCO (DESIGN MODERNO)
    // =========================================================================
    // Uma margem para afastar a caixa preta, revelando o metal brilhante nas bordas
    int margem = (int) (12 * SCALE); 
    int internoWidth = width - (margem * 2);
    int internoHeight = height - (margem * 2);
    int arcSizeInterno = (int) (12 * SCALE);

    // Preto fosco elegante e semitransparente (Glassmorphism)
    g2.setColor(new Color(20, 20, 20, 150)); 
    g2.fillRoundRect(margem, margem, internoWidth, internoHeight, arcSizeInterno, arcSizeInterno);

    // Linha fina interna sutil para dar acabamento premium no contêiner preto
    g2.setColor(new Color(255, 255, 255, 25)); 
    g2.setStroke(new BasicStroke((float) (1.0 * SCALE)));
    g2.drawRoundRect(margem, margem, internoWidth, internoHeight, arcSizeInterno, arcSizeInterno);
    // =========================================================================

    // 3. Desenha o contorno escuro final da carta inteira para dar profundidade
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

    // Libera os recursos gráficos da imagem em memória
    g2.dispose();

    // Aplica o molde gerado na variável da classe
    this.frontImgCard = moldImg;
}
}

