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

    /*
     * Puxa a carta do topo filtrando por cartas válidas baseadas no estado dos peões.
    */
    public CustomCards drawCard(int playerId, boolean allPawnsInBase) {
        if (playerId < 0 || playerId >= 4) return null;

        List<CustomCards> drawPile = drawPiles[playerId];
        List<CustomCards> discardPile = discardPiles[playerId];

        // Se o baralho estiver vazio, junta o descarte e reembaralha
        if (drawPile.isEmpty()) {
            if (discardPile.isEmpty()) return null;
            drawPile.addAll(discardPile);
            discardPile.clear();
            Collections.shuffle(drawPile);
        }

        // Busca do topo para baixo a PRIMEIRA carta válida para o estado atual
        for (int i = 0; i < drawPile.size(); i++) {
            CustomCards card = drawPile.get(i);
            String tipoCarta = card.getCardType();

            boolean ehSorteOuAzar = "SORTE".equalsIgnoreCase(tipoCarta) || "AZAR".equalsIgnoreCase(tipoCarta);
            boolean ehPegadinha = "PEGADINHA".equalsIgnoreCase(tipoCarta);
            boolean devePular = false;

            // FILTRAGEM DE INÍCIO DE JOGO (Todos na base)
            if (allPawnsInBase) {
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

            // Se a carta for válida, remove diretamente do índice 'i' e retorna
            // As cartas puladas continuam intactas no topo do baralho!
            if (!devePular) {
                return drawPile.remove(i);
            }
        }

        // Caso de emergência: se não houver NENHUMA carta de valor 1 ou 6 no baralho todo,
        // retira a do topo para não travar o jogo.
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