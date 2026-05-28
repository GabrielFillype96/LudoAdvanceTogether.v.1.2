// Classe responsável por construir o painel que irá exibir as cartas dinâmicas
// Package
package gui.windows;
// Import interno
import cards.CardManager;
import cards.CustomCards;
// Import externo
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

    // Variáveis
    private List<CustomCards> cardList;
    private CustomCards activeCard;
    private Image deckImg; // Imagem do baralho para mostrar atrás das cartas
    private Dimension cardSize; // Tamanho fixo para as cartas
    private final String deckImageURL = "/assets/deckCardImage_220x340.png"; // Caminho da imagem do baralho

    /**
     * Construtor atualizado: agora ele mesmo carrega as cartas do tipo solicitado
     * @param difficultyOrType Ex: "FÁCIL", "SORTE", "AZAR"
     */
    public CardsPanel(String difficultyOrType) {
        // Mantém as dimensões de 220x340 definidas no layout
        cardSize = new Dimension(200, 320);
        setPreferredSize(cardSize); 
        setSize(cardSize);
        setOpaque(false);   // Mantém transparente para o fundo amadeirado aparecer atrás
        setLayout(null);    // Permite que a CustomCards ocupe o espaço absoluto interno

        // Carrega a imagem do baralho para usar como fundo das cartas
        java.net.URL deckImagePath = getClass().getResource(deckImageURL);
        if (deckImagePath != null) {
            // Se encontrou a imagem, carrega e armazena na variável deckImg
            this.deckImg = new ImageIcon(deckImagePath).getImage();
        } else {
            // Se não encontrou a imagem, imprime um erro no console
            System.err.println("[CardsPanel] Erro: Imagem do baralho não encontrada em /assets/images/deckCardImage_220x340.png");
        }

        // O próprio painel faz a chamada ao CardManager para buscar as cartas do JSON de acordo com o filtro de dificuldade (fácil, médio, difícil) ou tipo (azar, sorte, etc)
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
        // this.activeCard.setLocation(0, 0);
        this.activeCard.setBounds(0, 0, this.getWidth(), this.getHeight()); // Faz a carta ocupar todo o espaço do CardsPanel
        // Adiciona o JPanel da carta dentro deste CardsPanel
        this.add(this.activeCard);

        this.revalidate(); // Atualiza o layout para acomodar a nova carta
        this.repaint(); // Redesenha o painel para mostrar a nova carta
    }

    // Sobrescreve o método de pintura para desenhar o fundo do baralho atrás das cartas
    @Override
    protected void paintComponent(Graphics g) {
        // Estrutura padrão do "paintComponent" para garantir que o fundo seja desenhado corretamente
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create(); 
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Se a imagem do baralho foi carregada com sucesso, desenha ela como plano de fundo do painel
        if (deckImg != null) {
            g2.drawImage(deckImg, 0, 0, getWidth(), getHeight(), this);
        }

        g2.dispose();
    }
}