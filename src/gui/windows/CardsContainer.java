package gui.windows;

import cards.CustomCards;
import control.GameManager;
import control.DeckManager;
import control.TurnManager;
import gui.components.CardDeckBackground;
import gui.components.DeckStackBackground;
import gui.components.buttons.cardsButton.CardOptionButton;
import actions.CardAnswerValidation;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

public class CardsContainer extends JPanel {
    private CustomCards activeCard;                  // Camada 1 (Topo/Frente)
    private CardDeckBackground cardDeckBackground;   // Camada 2 (Meio/Costas)
    private DeckStackBackground deckStackBackground; // Camada 3 (Base/Pilha)
    
    private GameManager gameManager;
    private CardAnswerValidation cardAnswerValidation;
    
    private DeckManager deckManager;
    private TurnManager turnManager;
    private static final Color COLOR_ACTION = new Color(230, 126, 34);
    
    private static final double SCALE = 1.5; 

    public CardsContainer(GameManager gameManager, CardAnswerValidation cardAnswerValidation) {
        this.gameManager = gameManager;
        this.cardAnswerValidation = cardAnswerValidation;

        this.setOpaque(false);
        this.setLayout(null);
        
        this.cardDeckBackground = new CardDeckBackground("/assets/cardCapaRoxo.png");
        
        int glowMargin = 25; 
        this.cardDeckBackground.setBounds(
            (int) (10 * SCALE) + 2 - glowMargin,   
            (int) (10 * SCALE) + 2 - glowMargin,   
            (int) (250 * SCALE) + (glowMargin * 2), 
            (int) (375 * SCALE) + (glowMargin * 2)  
        );
        this.add(this.cardDeckBackground);
        
        this.deckStackBackground = new DeckStackBackground("/assets/cardCapaRoxo.png");
        this.deckStackBackground.setBounds(
            (int) (10 * SCALE), 
            (int) (10 * SCALE), 
            (int) (250 * SCALE) + 20, 
            (int) (375 * SCALE) + 20
        );
        this.add(this.deckStackBackground);
        
        this.setComponentZOrder(this.cardDeckBackground, 0); 
        this.setComponentZOrder(this.deckStackBackground, 1); 

        // Cria o adaptador único para escutar cliques, entradas e movimentos do mouse
        java.awt.event.MouseAdapter deckMouseAdapter = new java.awt.event.MouseAdapter() {
           @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (gameManager != null && gameManager.getTurnManager() != null) {
                    // TRAVA DE SEGURANÇA: Cancela o clique se o jogo estiver processando animações, sorteios ou peões
                    if (!gameManager.canPlayerDrawCard()) return;

                    // Bloqueia clique se não for a vez do humano
                    if (turnManager.getCurrentTurn() != 0) return;

                    System.out.println("[CardsContainer] Clique detectado no baralho.");
                    if (activeCard == null) {
                        cardDeckBackground.stopTurnHighlight(); 
                        cardDeckBackground.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        transitionToNextCard();
                    }
                }
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                updateDeckCursor();
            }

            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                // CORREÇÃO: Qualquer micro-movimento do mouse recalcula se ainda é o turno do humano
                updateDeckCursor();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                cardDeckBackground.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        };

        // Registra o adaptador nas duas frentes de escuta do baralho
        this.cardDeckBackground.addMouseListener(deckMouseAdapter);
        this.cardDeckBackground.addMouseMotionListener(deckMouseAdapter);
        
        // Força o cursor padrão inicial seguro
        updateDeckCursor();
    }

    /**
     * CORREÇÃO CENTRAL: Controla rigidamente o cursor do baralho baseado no turno real do jogo.
     * Pode ser chamada internamente ou externamente quando os turnos mudarem.
     */
    public void updateDeckCursor() {
        if (this.cardDeckBackground == null) return;
        
        // Só ganha a mãozinha se o GameManager liberar a segurança E for o turno do humano E não houver carta aberta
        boolean segurancaLiberada = (this.gameManager != null && this.gameManager.canPlayerDrawCard());
        boolean ehTurnoDoHumano = (this.turnManager != null && this.turnManager.getCurrentTurn() == 0);
        
        if (segurancaLiberada && ehTurnoDoHumano && this.activeCard == null) {
            this.cardDeckBackground.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            this.cardDeckBackground.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    public void setDeckManager(DeckManager deckManager) {
        this.deckManager = deckManager;
    }

    public void setTurnManager(TurnManager turnManager) {
        this.turnManager = turnManager;

        if (this.turnManager != null) {
            this.turnManager.setCardsContainer(this);
        }
        updateDeckCursor();
    }

    public void startDeckHighlight() {
        if (this.cardDeckBackground != null && this.activeCard == null) {
            this.cardDeckBackground.startTurnHighlight();
            
            // Força a checagem caso o mouse já estivesse posicionado aqui antes do turno começar
            if (this.cardDeckBackground.getMousePosition() != null) {
                updateDeckCursor();
            }
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

        boolean todosNaBase = (gameManager.getFurthestPawnIndex(activePlayerId) == -1);
        this.activeCard = this.deckManager.drawCard(activePlayerId, todosNaBase);
        
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
        
        if (this.gameManager != null) {
            String tipoCarta = this.activeCard.getCardType();
            if ("PERGUNTA".equalsIgnoreCase(tipoCarta)) {
                this.gameManager.emitirStatus("🧠 Desafio! Selecione a alternativa correta.", java.awt.Color.WHITE);
            } else if ("SORTE".equalsIgnoreCase(tipoCarta)) {
                this.gameManager.emitirStatus("🍀 Parece que alguém aqui tem muita sorte!", java.awt.Color.GREEN);
            } else if ("AZAR".equalsIgnoreCase(tipoCarta)) {
                this.gameManager.emitirStatus("💀 Que azar! Parece que alguém vai voltar algumas casas!", java.awt.Color.RED);
            } else if ("PEGADINHA".equalsIgnoreCase(tipoCarta)) {
                this.gameManager.emitirStatus("🃏 Seu espertinho! Escolha um jogador para sacanear.", COLOR_ACTION);
            }
        }
    
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

        this.activeCard.setEnabled(false);

        gui.animations.CardFlipAnimation discardAnimation = new gui.animations.CardFlipAnimation(
            this, this.cardDeckBackground, this.activeCard, SCALE, true, () -> {
                if (this.activeCard != null) {
                    this.remove(this.activeCard);
                }
                this.activeCard = null;

                int glowMargin = 25;
                this.cardDeckBackground.setBounds(
                    (int) (10 * SCALE) + 2 - glowMargin,   
                    (int) (10 * SCALE) + 2 - glowMargin,   
                    (int) (250 * SCALE) + (glowMargin * 2), 
                    (int) (375 * SCALE) + (glowMargin * 2)  
                );
                this.cardDeckBackground.setVisible(true);
                this.setComponentZOrder(this.cardDeckBackground, 0); 

                // Atualiza o cursor assim que a carta é limpa da tela
                updateDeckCursor();

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