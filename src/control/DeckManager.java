package control;

import cards.CustomCards;
import cards.CardManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckManager {
    
    // Arrays para guardar as listas de cada um dos 4 jogadores (IDs 0 a 3)
    private List<CustomCards>[] drawPiles;    // Montes de Compra
    private List<CustomCards>[] discardPiles; // Montes de Descarte
    
    private CardManager cardManager;

    @SuppressWarnings("unchecked")
    public DeckManager(CardManager cardManager) {
        this.cardManager = cardManager;
        this.drawPiles = new ArrayList[4];
        this.discardPiles = new ArrayList[4];

        // Inicializa as listas para os 4 jogadores
        for (int i = 0; i < 4; i++) {
            this.drawPiles[i] = new ArrayList<>();
            this.discardPiles[i] = new ArrayList<>();
        }
    }

    /**
     * Inicializa os 4 baralhos individuais com instâncias ÚNICAS de cartas.
     */
    public void initializeDecks() {
        for (int i = 0; i < 4; i++) {
            this.drawPiles[i].clear();
            this.discardPiles[i].clear();
            
            // ATENÇÃO: Carregamos as cartas diretamente dentro do loop!
            // Isso obriga o sistema a ler o JSON 4 vezes e criar 4 conjuntos 
            // totalmente independentes, blindando a memória do Java.
            this.drawPiles[i].addAll(cardManager.loadCard("FÁCIL"));
            this.drawPiles[i].addAll(cardManager.loadCard("MÉDIO"));
            this.drawPiles[i].addAll(cardManager.loadCard("DIFÍCIL"));
            this.drawPiles[i].addAll(cardManager.loadCard("SORTE"));
            this.drawPiles[i].addAll(cardManager.loadCard("AZAR"));
            
            // Embaralha o baralho individual do jogador 'i'
            java.util.Collections.shuffle(this.drawPiles[i]);
            System.out.println("[DeckManager] Baralho do Jogador " + i + " inicializado com " + this.drawPiles[i].size() + " cartas!");
        }
    }

    /**
     * Puxa a carta do topo para o jogador atual, gerenciando o reabastecimento automático.
     */
    public CustomCards drawCard(int playerId) {
        if (playerId < 0 || playerId >= 4) return null;

        List<CustomCards> drawPile = drawPiles[playerId];
        List<CustomCards> discardPile = discardPiles[playerId];

        // === O GATILHO DE REABASTECIMENTO AUTOMÁTICO ===
        if (drawPile.isEmpty()) {
            System.out.println("[DeckManager] Baralho do Jogador " + playerId + " vazio! Reabastecendo com o descarte...");
            
            if (discardPile.isEmpty()) {
                System.err.println("[DeckManager] Erro: Não há cartas no descarte para reabastecer!");
                return null; 
            }

            // 1. Move todas as cartas do descarte de volta para a compra
            drawPile.addAll(discardPile);
            
            // 2. Limpa o monte de descarte
            discardPile.clear();

            // 3. Embaralha a nova pilha de compra
            Collections.shuffle(drawPile);
        }

        // Remove e devolve a primeira carta da lista (o topo do baralho)
        return drawPile.remove(0);
    }

    /**
     * Envia uma carta respondida/usada para a pilha de descarte do jogador correspondente.
     */
    public void discardCard(int playerId, CustomCards card) {
        if (playerId < 0 || playerId >= 4 || card == null) return;
        
        discardPiles[playerId].add(card);
        System.out.println("[DeckManager] Carta movida para o descarte do Jogador " + playerId);
    }
}