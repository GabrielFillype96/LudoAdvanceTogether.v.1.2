// Classe responsável por construir o painel que irá exibir as cartas dinâmicas

// Package
package gui.windows;

// Import interno
import cards.CardManager;
import cards.CustomCards;
import control.GameManager;
import gui.components.CardDeckBackground;
import gui.components.buttons.cardsButton.CardOptionButton;

// Import externo
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import actions.CardAnswerValidation;

/*
 * Painel base responsável por carregar e posicionar
 * a exibição visual das novas cartas dinâmicas vindas do JSON.
 */
public class CardsContainer extends JPanel {
    // VARIÁVEIS DE INSTÂNCIA
    private List<CustomCards> cardList;
    private CustomCards activeCard;
    private CardDeckBackground cardDeckBackground;
    private GameManager gameManager;
    private CardAnswerValidation cardAnswerValidation;
    private static final double SCALE = 1.5; // Fator de escala para ajustar as dimensões do painel e das cartas
    //private static final Dimension CARD_DIMENSION = Dimension((int) (200 * SCALE), (int) (320 * SCALE));

    /**
     ** Construtor atualizado: agora ele mesmo carrega as cartas do tipo solicitado
     * @param gameManager Gerenciador do jogo
     * @param cardAnswerValidation Validação das respostas
     */
    public CardsContainer(GameManager gameManager, CardAnswerValidation cardAnswerValidation) {
        this.gameManager = gameManager;
        this.cardAnswerValidation = cardAnswerValidation;
        setOpaque(false); // Mantém transparente para o fundo amadeirado aparecer atrás
        setLayout(null); // Layout nulo para posicionamento absoluto dos componentes filhos
        
        // Instancia o deck das cartas
        this.cardDeckBackground = new CardDeckBackground("/assets/deckCardImage_220x340.png");
        // Perguntar sobre a diferença do "Dimension" para o "setBounds"
        this.cardDeckBackground.setBounds(
            0, 
            0, 
            (int) (300 * SCALE), 
            (int) (400 * SCALE)
        );

        // Adiciona o deck das cartas ao container
        this.add(cardDeckBackground);

        CardManager cardManager = new CardManager();
        this.cardList = cardManager.loadCard("FÁCIL");

        if (this.cardList != null && !this.cardList.isEmpty()) {
            displayActiveCard(this.cardList.get(0));
        }
    }

    /**
     ** Método auxiliar para alternar a carta ativa dentro do painel
     * @param cardDrawn Carta sorteada para aparacer
     */
    public void displayActiveCard(CustomCards cardDrawn) {
        // Trava de segurança para caso a carta sorteada seja "null", o jogo não travar
        if (cardDrawn == null) return;
        
        // Se já existia uma carta visual na tela, remove antes de colocar a nova
        if (this.activeCard != null) {
            this.remove(this.activeCard);
        }

        // Passa a carta corteada ("cardDrawn") para a variável "activeCard"
        this.activeCard = cardDrawn;

        // Ajuste de posição para a carta se encaixar em cima do baralho
        this.activeCard.setBounds(
            20, 
            20, 
            (int)(200 * SCALE), 
            (int)(320 * SCALE)
        );

        if (this.activeCard.getBotoesOpcao() != null) {
            for (CardOptionButton btn : this.activeCard.getBotoesOpcao()) {
                
                // Remove listeners antigos anexados para evitar execuções duplicadas na mesma carta
                for (ActionListener al : btn.getActionListeners()) {
                    btn.removeActionListener(al);
                }
                
                // Validação da opção escolhida pelo jogador através do clique
                btn.addActionListener(e -> {
                    // 🛡️ ESCUDO DE TURNO: Se não for a vez do humano (Jogador 0), ignora o clique!
                    if (this.gameManager != null && this.gameManager.getTurnManager() != null) {
                        if (!this.gameManager.getTurnManager().isHumanTurn()) {
                            System.out.println("[CardsContainer] Clique bloqueado! Não é o turno do jogador humano (A vez é do Jogador " + this.gameManager.getTurnManager().getCurrentTurn() + ").");
                            return; // Interrompe a execução aqui, impedindo a validação da resposta
                        }
                    }

                    String respostaEscolhida = btn.getCardAnswerTxt().trim();
                    System.out.println("[CardsContainer] O jogador clicou na resposta: " + respostaEscolhida);
                    
                    if (this.cardAnswerValidation != null) {
                        this.cardAnswerValidation.validar(respostaEscolhida, this.activeCard, this);
                    }
                });
            }
        }
        
        this.add(this.activeCard); // Adiciona o JPanel da carta dentro deste CardsPanel
        
        // Colocamos a carta na tela (ZOrder 0 = na frente de tudo, ZOrder 1 = atrás da carta)
        this.setComponentZOrder(this.activeCard, 0);
        this.setComponentZOrder(this.cardDeckBackground, 1);
        
        this.revalidate(); // Atualiza o layout para acomodar a nova carta
        this.repaint(); // Redesenha o painel para mostrar a nova carta
        System.out.println(
            "[CardsPanel] Transição concluída. Exibindo carta ID: " + activeCard.getCardID()
        );
    }

    // Métodos getters
    /**
     ** Retorna a lista de cartas carregadas no painel.
     * @return Card list da classe "CustomCards"
    */
    public List<CustomCards> getCardList() {
        return this.cardList;
    }
    
    /**
     ** Retorna a carta ativa no momento.
     * @return Carta ativa
    */
    public CustomCards getActiveCard() {
        return activeCard;
    }

    // Métodos setters
    public void setCardList(List<CustomCards> cardList) {
        this.cardList = cardList;
    }

    public void setActiveCard(CustomCards activeCard) {
        this.activeCard = activeCard;
    }
}