package actions;

import javax.swing.JOptionPane;
import cards.CustomCards;
import gui.windows.CardsContainer;
import control.GameManager; // Certifique-se de importar o GameManager

public class CardAnswerValidation {
    // VARIÁVEIS DE INSTÂNCIA
    private GameManager gameManager;

    /**
    ** Construtor da classe "CardAnswerValidation" que passa como parâmetro o gerenciador do jogo
    * @param gameManager Gerenciador do jogo
    */
    public CardAnswerValidation(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void validar(String opcaoEscolhida, CustomCards carta, CardsContainer painelPai) {
        if (carta == null || painelPai == null) return;

       boolean acertou = opcaoEscolhida.trim().equalsIgnoreCase(carta.getCardAnswer().trim());

        if (acertou) {
            JOptionPane.showMessageDialog(painelPai, 
                "Resposta Correta!\nVocê vai " + carta.getCardEffect() + " " + carta.getCardValueText() + " casas.", 
                "Parabéns!", 
                JOptionPane.INFORMATION_MESSAGE);
                
            // CHAMADA REAL: Aciona as regras do peão e o movimento deslizante
            this.gameManager.cardResultVerification(true, carta.getCardValueText(), carta.getCardEffect());

        } else {
            JOptionPane.showMessageDialog(painelPai, 
                "Resposta Errada!\nA resposta correta era: " + carta.getCardAnswer() + "\nVocê passa a vez.", 
                "Que pena!", 
                JOptionPane.ERROR_MESSAGE);
                
            // Passa falso para a regra de negócio processar a punição ou retenção
            this.gameManager.cardResultVerification(false, carta.getCardValueText(), carta.getCardEffect());
        }

        // --- MECÂNICA DE AVANÇAR DE CARTA ---
        int actualIndex = painelPai.getCardList().indexOf(carta);
        int nextIndex = actualIndex + 1;

        if (nextIndex < painelPai.getCardList().size()) {
            // Se ainda tem cartas, avança normalmente
            CustomCards nextCard = painelPai.getCardList().get(nextIndex);
            painelPai.displayActiveCard(nextCard);
        } else {
            // Se as cartas acabaram, avisa o jogador...
            JOptionPane.showMessageDialog(painelPai, "Fim das cartas do monte! O baralho será reiniciado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            
            // ...e dá o "reset" voltando para a carta de índice 0!
            if (painelPai.getCardList() != null && !painelPai.getCardList().isEmpty()) {
                CustomCards primeiraCarta = painelPai.getCardList().get(0);
                painelPai.displayActiveCard(primeiraCarta);
            }
        }
    }
}