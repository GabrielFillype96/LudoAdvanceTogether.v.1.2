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

        // Nota: Se você tiver um MouseListener para efeito de Hover no construtor original, 
        // você pode copiá-lo e colá-lo aqui também!
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


    // Sobrescreve o método paintComponent do JPanel para desenhar a imagem de fundo personalizada
    // @Override indica que o método "paintComponent" está sendo sobrescrito da classe pai (JPanel). Serve como uma espécie de "guarda-costas" para garantir que estamos realmente sobrescrevendo um método existente e não criando um novo método por engano.
    @Override
    // O método "paintComponent" é chamado sempre que o painel precisa ser redesenhado, permitindo que personalizemos a aparência do fundo do menu offline.
    // Visibilidade "protected" para que apenas classes dentro do mesmo pacote ou subclasses possam acessar este método
    protected void paintComponent(Graphics g) {

        // Estrutura padrão do "paintComponent" para garantir que o fundo seja desenhado corretamente
        Graphics2D g2 = (Graphics2D) g.create();

        // Cria um contexto gráfico 2D para aplicar renderizações avançadas (como anti-aliasing)
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING, 
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );
        

        // Se a imagem de fundo foi carregada com sucesso, desenha ela como plano de fundo das alternativas
        if (cardBtnBGImg != null) {
            // Desenha a imagem de fundo da tela principal
            g2.drawImage(
                cardBtnBGImg, 
                0, 
                0, 
                this.getWidth(), 
                this.getHeight(), 
                this
            );
            if (isHovered) {
                g2.setColor(new Color(255, 255, 255, 30)); // Camada branca semi-transparente
                int margemHover = 4; 
                g2.fillRoundRect(
                    margemHover, 
                    margemHover, 
                    getWidth() - (margemHover * 2), 
                    getHeight() - (margemHover * 2), 
                    10, 10
                );
            }
            System.out.println(
                "[CardOptionButton] Imagem de fundo das alternativas desenhada com sucesso."
            );
        } else {
            // Fallback: fundo cinza caso a imagem de fundo das alternativas falhe, e imprime um erro no console
            Color corTopo = isHovered ? Color.WHITE : new Color(255, 255, 255, 220);
            Color corBase = isHovered ? CARD_COLOR : new Color(
                CARD_COLOR.getRed(), 
                CARD_COLOR.getGreen(), 
                CARD_COLOR.getBlue(), 
                180
            );
            
            java.awt.GradientPaint gp = new java.awt.GradientPaint(0, 0, corTopo, 0, getHeight(), corBase);
            g2.setPaint(gp);
            g2.fillRoundRect(
                0, 
                0, 
                getWidth(), 
                getHeight(), 
                12, 
                12
            );

            // Borda sutil apenas no fallback
            g2.setColor(COR_BORDA);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(
                0, 
                0, 
                getWidth() - 1, 
                getHeight() - 1, 
                12, 
                12
            );
            // Se a imagem de fundo das alternativas não foi carregada, imprime um erro no console
            System.err.println(
                "[CardOptionButton] Erro: Imagem de fundo das alternativas não carregada, usando fallback cinza."
            );
        }
        
        // 3. DESENHA O MINI BOX DA LETRA DO LADO ESQUERDO
        int letterBoxSize = getHeight() - 6; 
        int xBox = 3;
    
        // 5. DESENHA O TEXTO DA ALTERNATIVA
        g2.setFont(getFont());
        g2.setColor(getForeground());

        int xCardAnswerTxt;

        // Remove espaços em branco nas pontas por segurança
        String textoTratado = cardAnswerTxt.trim();

        // Verifica se o texto é exatamente "SIM" ou "NÃO" (ignorando maiúsculas e minúsculas)
        if (textoTratado.equalsIgnoreCase("SIM") || textoTratado.equalsIgnoreCase("NÃO")) {
            // Mede a largura exata que o texto ocupa em pixels na tela
            int larguraTexto = g2.getFontMetrics().stringWidth(cardAnswerTxt);
            // Centraliza horizontalmente: (Largura do botão - Largura do texto) / 2
            xCardAnswerTxt = (getWidth() - larguraTexto) / 2;
        } else {
            // Se for uma frase ou alternativa longa, mantém alinhado mais próximo da borda esquerda
            xCardAnswerTxt = xBox + letterBoxSize + 10;
        }

        int yCardAnswerTxt = (getHeight() + g2.getFontMetrics().getAscent() - g2.getFontMetrics().getDescent()) / 2;

        g2.drawString(cardAnswerTxt, xCardAnswerTxt, yCardAnswerTxt);

        // Libera os recursos do contexto gráfico 2D para evitar vazamentos de memória
        g2.dispose();
        
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