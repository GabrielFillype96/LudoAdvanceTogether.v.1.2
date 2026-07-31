package control;

import cards.CustomCards;
import cards.CardManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckManager {
    
    // Substituído array bruto por List<List<...>> para evitar avisos de generics
    private List<List<CustomCards>> drawPiles;    // Montes de Compra
    private List<List<CustomCards>> discardPiles; // Montes de Descarte
    
    private CardManager cardManager;
    private GameManager gameManager;

    public DeckManager(CardManager cardManager) {
        this.cardManager = cardManager;
        this.drawPiles = new ArrayList<>();
        this.discardPiles = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            this.drawPiles.add(new ArrayList<>());
            this.discardPiles.add(new ArrayList<>());
        }
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    /**
     * Inicializa os baralhos de forma assíncrona (em segundo plano).
     */
    public void initializeDecksAsync(Runnable onComplete) {
        new Thread(() -> {
            initializeDecks();
            if (onComplete != null) {
                javax.swing.SwingUtilities.invokeLater(onComplete);
            }
        }).start();
    }

    public void initializeDecks() {
        long startTime = System.currentTimeMillis();
        
        // Carrega todas as cartas uma única vez para otimizar desempenho
        for (int i = 0; i < 4; i++) {
            this.drawPiles.get(i).clear();
            this.discardPiles.get(i).clear();
            
            this.drawPiles.get(i).addAll(cardManager.loadCard("FÁCIL"));
            this.drawPiles.get(i).addAll(cardManager.loadCard("MÉDIO"));
            this.drawPiles.get(i).addAll(cardManager.loadCard("DIFÍCIL"));
            this.drawPiles.get(i).addAll(cardManager.loadCard("SORTE"));
            this.drawPiles.get(i).addAll(cardManager.loadCard("AZAR"));
            
            Collections.shuffle(this.drawPiles.get(i));
            System.out.println("[DeckManager] Baralho do Jogador " + i + " inicializado com " + this.drawPiles.get(i).size() + " cartas!");
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("[DeckManager] Carregamento de TODOS os baralhos concluído em " + (endTime - startTime) + "ms!");
    }

    /**
     * Puxa a carta do topo filtrando por cartas válidas baseadas no estado dos peões e no Cooldown de Azar.
     */
        public CustomCards drawCard(int playerId, boolean allPawnsInBase) {
        if (playerId < 0 || playerId >= 4) return null;

        List<CustomCards> drawPile = drawPiles.get(playerId);
        List<CustomCards> discardPile = discardPiles.get(playerId);

        if (drawPile.isEmpty()) {
            if (discardPile.isEmpty()) return null;
            drawPile.addAll(discardPile);
            discardPile.clear();
            Collections.shuffle(drawPile);
        }

        for (int i = 0; i < drawPile.size(); i++) {
            CustomCards card = drawPile.get(i);
            String tipoCarta = card.getCardType() != null ? card.getCardType() : "";

            boolean ehSorteOuAzar = "SORTE".equalsIgnoreCase(tipoCarta) || "AZAR".equalsIgnoreCase(tipoCarta);
            boolean ehPegadinha = "PEGADINHA".equalsIgnoreCase(tipoCarta);
            boolean ehAzar = "AZAR".equalsIgnoreCase(tipoCarta);
            boolean devePular = false;

            // FILTRAGEM DE COOLDOWN DE AZAR
            if (ehAzar && gameManager != null && gameManager.isAzarInCooldown(playerId)) {
                devePular = true;
            }

            // --- RESTRIÇÃO DE INÍCIO DE JOGO ---
            // Esta condição só entra se TODOS os 4 peões estiverem na base (allPawnsInBase == true)
            if (!devePular && allPawnsInBase) {
                if (ehSorteOuAzar) {
                    devePular = true;
                } else if (!ehPegadinha) {
                    int valorDado = extrairValorNumerico(card.getCardValueText());

                    if (valorDado != 1 && valorDado != 6) {
                        devePular = true;
                    }
                }
            }

            if (!devePular) {
                return drawPile.remove(i);
            }
        }

        System.out.println("[DeckManager] Nenhuma carta válida encontrada no monte para o Jogador " + playerId);
        return null;
    }

    public void discardCard(int playerId, CustomCards card) {
        if (playerId < 0 || playerId >= 4 || card == null) return;
        
        discardPiles.get(playerId).add(card);
        System.out.println("[DeckManager] Carta movida para o descarte do Jogador " + playerId);
    }

    /**
     * Extrai com segurança o primeiro valor numérico de uma string de valor da carta.
     */
    private int extrairValorNumerico(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        
        try {
            String tratado = text.trim();
            if (tratado.contains("/")) {
                tratado = tratado.split("/")[0].trim();
            }
            // Remove tudo que não for dígito
            String apenasDigitos = tratado.replaceAll("[^0-9]", "");
            if (apenasDigitos.isEmpty()) return 0;
            
            return Math.abs(Integer.parseInt(apenasDigitos));
        } catch (Exception e) {
            return 0;
        }
    }
}