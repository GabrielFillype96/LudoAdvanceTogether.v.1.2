// Package
package gui.windows;

// Import interno
import cards.CustomCards;
import control.GameManager;
import control.DeckManager;
import control.TurnManager;
import gui.components.CardDeckBackground;
import gui.components.DeckStackBackground; // <--- NOVO IMPORT AQUI
import gui.components.buttons.cardsButton.CardOptionButton;
import actions.CardAnswerValidation;


// Import externo
import java.awt.event.ActionListener;
import javax.swing.JPanel;

public class CardsContainer extends JPanel {
    // VARIÁVEIS DE INSTÂNCIA
    private CustomCards activeCard;                  // Camada 1 (Topo/Frente)
    private CardDeckBackground cardDeckBackground;   // Camada 2 (Meio/Costas)
    private DeckStackBackground deckStackBackground; // Camada 3 (Base/Pilha) <-- NOVA VARIÁVEL
    
    private GameManager gameManager;
    private CardAnswerValidation cardAnswerValidation;
    
    private DeckManager deckManager;
    private TurnManager turnManager;

    
    private static final double SCALE = 1.5; 

    public CardsContainer(GameManager gameManager, CardAnswerValidation cardAnswerValidation) {
        this.gameManager = gameManager;
        this.cardAnswerValidation = cardAnswerValidation;

        this.setOpaque(false);
        this.setLayout(null);
        
        // CAMADA 2: A CARTA DE COSTAS INTERATIVA (CardDeckBackground)
        this.cardDeckBackground = new CardDeckBackground("/assets/cardCapaRoxo.png");
        
        int glowMargin = 25; 
        this.cardDeckBackground.setBounds(
            (int) (10 * SCALE) + 2 - glowMargin,   
            (int) (10 * SCALE) + 2 - glowMargin,   
            (int) (250 * SCALE) + (glowMargin * 2), 
            (int) (375 * SCALE) + (glowMargin * 2)  
        );
        this.add(this.cardDeckBackground); // <--- ADICIONE ESTA LINHA QUE ESTÁ FALTANDO!
        
        // CAMADA 3: O EFEITO DE PILHA DO DECK (DeckStackBackground)
        this.deckStackBackground = new DeckStackBackground("/assets/cardCapaRoxo.png");
        this.deckStackBackground.setBounds(
            (int) (10 * SCALE), 
            (int) (10 * SCALE), 
            (int) (250 * SCALE) + 20, 
            (int) (375 * SCALE) + 20
        );
        this.add(this.deckStackBackground);
        
        // ORGANIZAÇÃO INICIAL DAS CAMADAS:
        this.setComponentZOrder(this.cardDeckBackground, 0); // Fica por cima na inicialização
        this.setComponentZOrder(this.deckStackBackground, 1); // Fica embaixo do CardDeckBackground

        // Adiciona um MouseListener ao cardDeckBackground para simular a puxada de carta
        this.cardDeckBackground.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (gameManager != null && gameManager.getTurnManager() != null) {
                    System.out.println("[CardsContainer] Clique detectado no baralho.");
                    if (activeCard == null) {
                        
                        // <-- NOVO: Pára a animação assim que o jogador clica no baralho
                        cardDeckBackground.stopTurnHighlight(); 
                        
                        transitionToNextCard();
                    }
                }
            }
        });
    }

    public void setDeckManager(DeckManager deckManager) {
        this.deckManager = deckManager;
    }

    public void setTurnManager(TurnManager turnManager) {
        this.turnManager = turnManager;

        if (this.turnManager != null) {
            this.turnManager.setCardsContainer(this);
        }
    }

    public void startDeckHighlight() {
        // Só liga a animação se NÃO houver nenhuma carta já puxada na mesa
        if (this.cardDeckBackground != null && this.activeCard == null) {
            this.cardDeckBackground.startTurnHighlight();
        }
    }

    public void transitionToNextCard() {
        if (this.deckManager == null || this.turnManager == null) {
            System.err.println("[CardsContainer] Erro: DeckManager ou TurnManager não injetados!");
            return;
        }

        int activePlayerId = this.turnManager.getCurrentTurn();
        if (activePlayerId != 0) {
            System.out.println("[CardsContainer] Não é a vez do jogador humano. Ignore o clique.");
            return;
        }

        if (this.activeCard != null) {
            this.remove(this.activeCard);
        }

        // PUXAR CARTA
        this.activeCard = this.deckManager.drawCard(activePlayerId);
        
        if (this.activeCard == null) {
            System.err.println("[CardsContainer] Erro: O DeckManager devolveu uma carta nula.");
            return;
        }

        this.activeCard.setBounds(
            (int) (10 * SCALE) + 2, 
            (int) (10 * SCALE) + 2, 
            (int) (250 * SCALE),
            (int) (375 * SCALE)
        );
        
        // (A LÓGICA DE BOTÕES QUE VOCÊ TINHA SE MANTÉM IGUAL AQUI)
        if ("SORTE".equalsIgnoreCase(this.activeCard.getCardType()) || "AZAR".equalsIgnoreCase(this.activeCard.getCardType())) {
            CardOptionButton botaoConfirmar = this.activeCard.getConfirmButton();
            if (botaoConfirmar != null) {
                for (ActionListener al : botaoConfirmar.getActionListeners()) {
                    botaoConfirmar.removeActionListener(al);
                }
                
                botaoConfirmar.addActionListener(e -> {
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
                            this.deckManager.discardCard(activePlayerId, cartaParaDescartar);
                            this.clearActiveCard();
                        }
                    });
                }
            }
        }
        
        gui.animations.CardFlipAnimation flipAnimation = new gui.animations.CardFlipAnimation(
            this, this.cardDeckBackground, this.activeCard, SCALE, false, null
        );
        flipAnimation.start();
        
        this.revalidate(); 
        this.repaint(); 
        System.out.println("[CardsContainer] Transição concluída. Exibindo carta ID: " + activeCard.getCardID());
    }

    public void clearActiveCard() {
        if (this.activeCard == null) return;

        // Oculta ou desativa os botões internos da carta para evitar cliques acidentais durante o sumiço
        this.activeCard.setEnabled(false);

        // Cria a animação passando 'true' (Modo Descartar) 
        // O último parâmetro é o código que vai rodar assim que a animação terminar de rodar
        gui.animations.CardFlipAnimation discardAnimation = new gui.animations.CardFlipAnimation(
            this, this.cardDeckBackground, this.activeCard, SCALE, true, () -> {
                
                // =========================================================
                // ESTE BLOCO SÓ EXECUTA QUANDO A CARTA TERMINAR O FLIP DE VOLTA!
                // =========================================================
                if (this.activeCard != null) {
                    this.remove(this.activeCard);
                }
                this.activeCard = null;

                // Restaura o tamanho e posição estável do baralho de costas original
                int glowMargin = 25;
                this.cardDeckBackground.setBounds(
                    (int) (10 * SCALE) + 2 - glowMargin,   
                    (int) (10 * SCALE) + 2 - glowMargin,   
                    (int) (250 * SCALE) + (glowMargin * 2), 
                    (int) (375 * SCALE) + (glowMargin * 2)  
                );
                this.cardDeckBackground.setVisible(true);
                this.setComponentZOrder(this.cardDeckBackground, 0); 

                this.revalidate();
                this.repaint();
            }
        );
        
        discardAnimation.start();
    }

    public CustomCards getActiveCard() {
        return activeCard;
    }
}