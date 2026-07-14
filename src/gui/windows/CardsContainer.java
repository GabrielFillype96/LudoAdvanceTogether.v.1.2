// Classe responsável por construir o painel que irá exibir as cartas dinâmicas

// Package
package gui.windows;

// Import interno
import cards.CustomCards;
import control.GameManager;
import control.DeckManager;
import control.TurnManager;
import gui.components.CardDeckBackground;
import gui.components.buttons.cardsButton.CardOptionButton;
import actions.CardAnswerValidation;

// Import externo
import java.awt.event.ActionListener;
import javax.swing.JPanel;

/*
 * Painel base responsável por carregar e posicionar
 * a exibição visual das novas cartas dinâmicas vindas do JSON.
 */
public class CardsContainer extends JPanel {
    // VARIÁVEIS DE INSTÂNCIA
    private CustomCards activeCard;
    private CardDeckBackground cardDeckBackground;
    private GameManager gameManager;
    private CardAnswerValidation cardAnswerValidation;
    
    // NOVOS MANAGERS INJETADOS
    private DeckManager deckManager;
    private TurnManager turnManager;
    
    private static final double SCALE = 1.5; 

    /**
     ** Construtor restaurado e atualizado:
     * Recebe as dependências (gameManager e cardAnswerValidation) via Injeção de Dependência
     */
    public CardsContainer(GameManager gameManager, CardAnswerValidation cardAnswerValidation) {
        this.gameManager = gameManager;
        this.cardAnswerValidation = cardAnswerValidation;

        //this.setBackground(java.awt.Color.RED);
        this.setOpaque(false);
        this.setLayout(null);
        
        // =========================================================
        // RESTAURAÇÃO DO PATH DA IMAGEM DO BARALHO
        // =========================================================
        this.cardDeckBackground = new CardDeckBackground("/assets/deckCardImage_220x340.png");
        this.cardDeckBackground.setBounds(
            (int) (10 * SCALE),   // X (Posição horizontal)
            (int) (10 * SCALE),   // Y (Posição vertical)
            (int) (280 * SCALE),  // Largura (mesmo tamanho da carta)
            (int) (420 * SCALE)   // Altura (mesmo tamanho da carta)
        );
        this.add(this.cardDeckBackground);
        
        // Adiciona um MouseListener ao cardDeckBackground para simular a puxada de carta
        this.cardDeckBackground.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Previne que clique no baralho caso a animação do jogo ainda esteja a correr
                if (gameManager != null && gameManager.getTurnManager() != null) {
                    System.out.println("[CardsContainer] Clique detectado no baralho.");
                    if (activeCard == null) {
                        transitionToNextCard();
                    }
                }
            }
        });
    }

    // NOVOS SETTERS PARA OS MANAGERS
    public void setDeckManager(DeckManager deckManager) {
        this.deckManager = deckManager;
    }

    public void setTurnManager(TurnManager turnManager) {
        this.turnManager = turnManager;
    }

    /**
     ** Método responsável por alternar para a próxima carta.
     * Puxa a carta diretamente do DeckManager baseado no jogador atual!
     */
    public void transitionToNextCard() {
        if (this.deckManager == null || this.turnManager == null) {
            System.err.println("[CardsContainer] Erro: DeckManager ou TurnManager não injetados!");
            return;
        }

        // Apenas permite que o Humano (ID 0) puxe carta clicando. A CPU fará isso automaticamente depois.
        int activePlayerId = this.turnManager.getCurrentTurn();
        if (activePlayerId != 0) {
            System.out.println("[CardsContainer] Não é a vez do jogador humano. Ignore o clique.");
            return;
        }

        // Se já existir uma carta ativa, remove do painel para dar espaço à próxima
        if (this.activeCard != null) {
            this.remove(this.activeCard);
        }

        // === PASSO B: PUXAR A CARTA DO DECK DO JOGADOR ===
        this.activeCard = this.deckManager.drawCard(activePlayerId);
        
        if (this.activeCard == null) {
            System.err.println("[CardsContainer] Erro: O DeckManager devolveu uma carta nula.");
            return;
        }

        // Reconfigura e reposiciona a nova carta usando os tamanhos fixos do seu CustomCards
        this.activeCard.setBounds(
            (int) (14 * SCALE), 
            (int) (5 * SCALE), 
            (int) (250 * SCALE), // Largura real da carta
            (int) (375 * SCALE)  // Altura real da carta
        );
        
        // =========================================================
        // CORREÇÃO 2: LIGAÇÃO DOS BOTÕES COM O DESCARTE + BACKUP DA CARTA
        // =========================================================
        if ("SORTE".equalsIgnoreCase(this.activeCard.getCardType()) || "AZAR".equalsIgnoreCase(this.activeCard.getCardType())) {
            CardOptionButton botaoConfirmar = this.activeCard.getConfirmButton();
            if (botaoConfirmar != null) {
                for (ActionListener al : botaoConfirmar.getActionListeners()) {
                    botaoConfirmar.removeActionListener(al);
                }
                
                botaoConfirmar.addActionListener(e -> {
                    // === SALVA A CARTA ANTES QUE ELA SEJA APAGADA ===
                    CustomCards cartaParaDescartar = this.activeCard;
                    
                    try {
                        if (this.cardAnswerValidation != null) {
                            this.cardAnswerValidation.validar("ESPECIAL", cartaParaDescartar, this);
                        }
                    } catch (Exception ex) {
                        System.err.println("[CardsContainer] Erro ao validar a carta especial: " + ex.getMessage());
                        if (this.turnManager != null) {
                            this.turnManager.nextTurn();
                        }
                    } finally {
                        // === USA A CARTA SALVA PARA DESCARTAR ===
                        this.deckManager.discardCard(activePlayerId, cartaParaDescartar);
                        this.clearActiveCard();
                    }
                });
            }
        } else {
            if (this.activeCard.getBotoesOpcao() != null) {
                for (CardOptionButton botao : this.activeCard.getBotoesOpcao()) {
                    for (ActionListener al : botao.getActionListeners()) {
                        botao.removeActionListener(al);
                    }
                    
                    botao.addActionListener(e -> {
                        // === SALVA A CARTA ANTES QUE ELA SEJA APAGADA ===
                        CustomCards cartaParaDescartar = this.activeCard;
                        
                        try {
                            String respostaEscolhida = botao.getText();
                            if (this.cardAnswerValidation != null) {
                                this.cardAnswerValidation.validar(respostaEscolhida, cartaParaDescartar, this);
                            }
                        } catch (Exception ex) {
                            System.err.println("[CardsContainer] Erro ao validar a carta: " + ex.getMessage());
                            if (this.turnManager != null) {
                                this.turnManager.nextTurn();
                            }
                        } finally {
                            // === USA A CARTA SALVA PARA DESCARTAR ===
                            this.deckManager.discardCard(activePlayerId, cartaParaDescartar);
                            this.clearActiveCard();
                        }
                    });
                }
            }
        }
        
        this.add(this.activeCard); 
        this.setComponentZOrder(this.activeCard, 0);
        this.setComponentZOrder(this.cardDeckBackground, 1);
        
        this.revalidate(); 
        this.repaint(); 
        System.out.println("[CardsContainer] Transição concluída. Exibindo carta ID: " + activeCard.getCardID());
    }

    /**
     * Método para limpar a carta da tela e liberar o painel para o próximo turno
     */
    public void clearActiveCard() {
        this.activeCard = null;
        this.repaint();
    }

    public CustomCards getActiveCard() {
        return activeCard;
    }
}