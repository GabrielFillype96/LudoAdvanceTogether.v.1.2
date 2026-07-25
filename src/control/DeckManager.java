package control;

import cards.CustomCards;
import cards.CardManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckManager {
    
    private List<CustomCards>[] drawPiles;    // Montes de Compra
    private List<CustomCards>[] discardPiles; // Montes de Descarte
    
    private CardManager cardManager;
    private GameManager gameManager;

    @SuppressWarnings("unchecked")
    public DeckManager(CardManager cardManager) {
        this.cardManager = cardManager;
        this.drawPiles = new ArrayList[4];
        this.discardPiles = new ArrayList[4];

        for (int i = 0; i < 4; i++) {
            this.drawPiles[i] = new ArrayList<>();
            this.discardPiles[i] = new ArrayList<>();
        }
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void initializeDecks() {
        for (int i = 0; i < 4; i++) {
            this.drawPiles[i].clear();
            this.discardPiles[i].clear();
            
            this.drawPiles[i].addAll(cardManager.loadCard("FÁCIL"));
            this.drawPiles[i].addAll(cardManager.loadCard("MÉDIO"));
            this.drawPiles[i].addAll(cardManager.loadCard("DIFÍCIL"));
            this.drawPiles[i].addAll(cardManager.loadCard("SORTE"));
            this.drawPiles[i].addAll(cardManager.loadCard("AZAR"));
            
            java.util.Collections.shuffle(this.drawPiles[i]);
            System.out.println("[DeckManager] Baralho do Jogador " + i + " inicializado com " + this.drawPiles[i].size() + " cartas!");
        }
    }

    /**
     * Puxa a carta do topo filtrando por cartas válidas baseadas no estado dos peões e no Cooldown de Azar.
     */
    public CustomCards drawCard(int playerId, boolean allPawnsInBase) {
        if (playerId < 0 || playerId >= 4) return null;

        List<CustomCards> drawPile = drawPiles[playerId];
        List<CustomCards> discardPile = discardPiles[playerId];

        if (drawPile.isEmpty()) {
            if (discardPile.isEmpty()) return null;
            drawPile.addAll(discardPile);
            discardPile.clear();
            Collections.shuffle(drawPile);
        }

        for (int i = 0; i < drawPile.size(); i++) {
            CustomCards card = drawPile.get(i);
            String tipoCarta = card.getCardType();

            boolean ehSorteOuAzar = "SORTE".equalsIgnoreCase(tipoCarta) || "AZAR".equalsIgnoreCase(tipoCarta);
            boolean ehPegadinha = "PEGADINHA".equalsIgnoreCase(tipoCarta);
            boolean ehAzar = "AZAR".equalsIgnoreCase(tipoCarta);
            boolean devePular = false;

            // FILTRAGEM DE COOLDOWN DE AZAR (Por dificuldade)
            if (ehAzar && gameManager != null && gameManager.isAzarInCooldown(playerId)) {
                devePular = true;
            }

            // FILTRAGEM DE INÍCIO DE JOGO (Todos na base)
            if (!devePular && allPawnsInBase) {
                if (ehSorteOuAzar) {
                    devePular = true; // Pula Sorte/Azar na base
                } else if (!ehPegadinha) {
                    int valorDado = 0;
                    try {
                        String cardValueTreated = card.getCardValueText().trim();
                        if (cardValueTreated.contains("/")) {
                            cardValueTreated = cardValueTreated.split("/")[0].trim();
                        }
                        valorDado = Math.abs(Integer.parseInt(cardValueTreated));
                    } catch (Exception e) {
                        System.err.println("[DeckManager] Erro ao processar valor da carta: " + e.getMessage());
                    }

                    if (valorDado != 1 && valorDado != 6) {
                        devePular = true; // Pula cartas com valor diferente de 1 ou 6
                    }
                }
            }

            if (!devePular) {
                return drawPile.remove(i);
            }
        }

        return drawPile.remove(0);
    }

    public void discardCard(int playerId, CustomCards card) {
        if (playerId < 0 || playerId >= 4 || card == null) return;
        
        discardPiles[playerId].add(card);
        System.out.println("[DeckManager] Carta movida para o descarte do Jogador " + playerId);
    }
}