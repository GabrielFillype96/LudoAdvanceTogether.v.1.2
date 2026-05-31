// Classe responsável por construir o painel que irá exibir as cartas dinâmicas

// Package
package gui.windows;

// Import interno
import cards.CardManager;
import cards.CustomCards;
import gui.theme.GameColors;

// Import externo
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Painel base responsável por carregar, posicionar e gerenciar 
 * a exibição visual das novas cartas dinâmicas vindas do JSON.
 */
public class CardsPanel extends JPanel {

    // VARIÁVEIS DE INSTÂNCIA
    private List<CustomCards> cardList;
    private CustomCards activeCard;
    private Image deckImg; // Imagem do baralho para mostrar atrás das cartas
    private static final String deckImageURL = "/assets/deckCardImage_220x340.png"; // Caminho da imagem do baralho
    

    /**
     * Construtor atualizado: agora ele mesmo carrega as cartas do tipo solicitado
     * @param difficultyOrType Ex: "FÁCIL", "SORTE", "AZAR"
     */
    public CardsPanel(String difficultyOrType) {
        // Mantém as dimensões de 200x340 definidas no layout
        Dimension cardDimension = new Dimension( // Define as dimensões do painel que conterá as cartas
            200, 
            320
        ); 
        setPreferredSize(cardDimension);
        setSize(cardDimension); 
        setOpaque(false);   // Mantém transparente para o fundo amadeirado aparecer atrás
        setLayout(null);    // Permite que a CustomCards ocupe o espaço absoluto interno

        // Poderia ser utilizado o método "try/catch" para tratamento de erros, mas o if/else é mais sútil e simples
        // Carrega a imagem do baralho para usar como fundo das cartas
        java.net.URL deckImagePath = getClass().getResource(deckImageURL);
        if (deckImagePath != null) {
            // Se encontrou a imagem, carrega e armazena na variável deckImg
            System.out.println(
                "[CardsPanel] Imagem do baralho encontrada em: " + deckImageURL
            );
            this.deckImg = new ImageIcon(deckImagePath).getImage();
        } else {
            // Se não encontrou a imagem, imprime um erro no console
            System.err.println(
                "[CardsPanel] Erro: Imagem do baralho não encontrada em /assets/images/deckCardImage_220x340.png"
            );
        }

        // O próprio painel faz a chamada ao CardManager para buscar as informações das cartas do JSON de acordo com o filtro de dificuldade (fácil, médio, difícil) ou tipo (azar, sorte, etc)
        this.cardList = CardManager.loadCard(difficultyOrType);

        // Se encontrou cartas no JSON, exibe a primeira da lista por padrão
        if (cardList != null && !cardList.isEmpty()) {
            displayCard(0);
        }
    }

    /**
     * Método auxiliar para alternar a carta ativa dentro do painel
     */
    public void displayCard(int index) {
        // Verifica se o índice é válido antes de tentar exibir a carta
        if (cardList == null || index < 0 || index >= cardList.size()) return;
        // Se já existia uma carta visual na tela, remove antes de colocar a nova
        if (this.activeCard != null) {
            this.remove(this.activeCard);
        }

        // Pega a nova carta da lista
        this.activeCard = this.cardList.get(index);

        // Garante que a carta comece posicionada no canto superior esquerdo do painel
        this.activeCard.setBounds( // Faz a carta ocupar todo o espaço do CardsPanel
            0, 
            0, 
            this.getWidth(), 
            this.getHeight()
        ); 
        
        this.add(this.activeCard); // Adiciona o JPanel da carta dentro deste CardsPanel

        this.revalidate(); // Atualiza o layout para acomodar a nova carta
        this.repaint(); // Redesenha o painel para mostrar a nova carta
    }

    // Sobrescreve o método de pintura para desenhar o fundo do baralho atrás das cartas
    @Override
    protected void paintComponent(Graphics g) {
        // Estrutura padrão do "paintComponent" para garantir que o fundo seja desenhado corretamente
        super.paintComponent(g);
        // Cria um contexto gráfico 2D para aplicar renderizações avançadas (como anti-aliasing)
        Graphics2D g2 = (Graphics2D) g.create(); 
        // Habilita o anti-aliasing para suavizar as bordas das imagens desenhadas
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Se a imagem do baralho foi carregada com sucesso, desenha ela como plano de fundo do painel
        if (deckImg != null) {
            // Desenha a imagem do baralho no container do "CardsPanel", escalando para preencher todo o painel
            g2.drawImage(
                deckImg, 
                0, 
                0, 
                this.getWidth(), 
                this.getHeight(), 
                this
            );
            System.out.println(
                "[CardsPanel] Imagem do baralho desenhada com sucesso."
            );
        } else {
            // Fallback: fundo cinza caso a imagem do baralho falhe, e imprime um erro no console
            g.setColor(gui.theme.GameColors.PURPLE_BG);
            g.fillRect(
                0, 
                0, 
                this.getWidth(), 
                this.getHeight()
            );
            // Se a imagem do baralho não foi carregada, imprime um erro no console
            System.err.println(
                "[CardsPanel] Erro: A imagem do baralho não foi carregada."
            );
        }

        // Libera os recursos gráficos usados para desenhar o fundo, garantindo que não haja vazamentos de memória
        g2.dispose();
    }
}