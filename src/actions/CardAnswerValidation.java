package actions;

import javax.swing.JOptionPane;
import cards.CustomCards;
import gui.windows.CardsPanel;
import control.GameManager; // Certifique-se de importar o GameManager

public class CardAnswerValidation {

    public static void validar(String opcaoEscolhida, CustomCards carta, CardsPanel painelPai) {
        if (carta == null || painelPai == null) return;

       boolean acertou = opcaoEscolhida.trim().equalsIgnoreCase(carta.getCardAnswer().trim());

        if (acertou) {
            JOptionPane.showMessageDialog(painelPai, 
                "Resposta Correta!\nVocê vai " + carta.getCardEffect() + " " + carta.getCardValueText() + " casas.", 
                "Parabéns!", 
                JOptionPane.INFORMATION_MESSAGE);
                
            // CHAMADA REAL: Aciona as regras do peão e o movimento deslizante
            GameManager.processarResultadoCarta(true, carta.getCardValueText(), carta.getCardEffect());

        } else {
            JOptionPane.showMessageDialog(painelPai, 
                "Resposta Errada!\nA resposta correta era: " + carta.getCardAnswer() + "\nVocê passa a vez.", 
                "Que pena!", 
                JOptionPane.ERROR_MESSAGE);
                
            // Passa falso para a regra de negócio processar a punição ou retenção
            GameManager.processarResultadoCarta(false, carta.getCardValueText(), carta.getCardEffect());
        }

        // --- MECÂNICA DE AVANÇAR DE CARTA ---
        int indiceAtual = painelPai.getCardList().indexOf(carta);
        int proximoIndice = indiceAtual + 1;

        if (proximoIndice < painelPai.getCardList().size()) {
            painelPai.displayCard(proximoIndice);
        } else {
            JOptionPane.showMessageDialog(painelPai, "Fim das cartas do monte! Reiniciando...", "Baralho", JOptionPane.WARNING_MESSAGE);
            painelPai.displayCard(0);
        }
    }
}