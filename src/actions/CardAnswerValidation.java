package actions;

import javax.swing.JOptionPane;
import cards.CustomCards;
import gui.windows.CardsContainer;
import control.GameManager;

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

        // Remove a carta visualmente da tela para limpar o espaço
        painelPai.remove(carta);
        painelPai.clearActiveCard();

        // =======================================================
        // 1. TRATAMENTO PARA CARTAS ESPECIAIS (SORTE / AZAR)
        // =======================================================
        if (opcaoEscolhida.equals("ESPECIAL")) {
            String tipo = carta.getCardType();
            String titulo = tipo.equalsIgnoreCase("SORTE") ? "Sorte!" : "Azar!";
            int icone = tipo.equalsIgnoreCase("SORTE") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE;
                
            // Passamos 'true' para o motor do jogo aplicar o efeito (avançar, voltar, etc.)
           gameManager.cardResultVerification(true, carta.getCardValueText(), carta.getCardEffect(), carta.getCardType());
            return; // Encerra o método aqui, pois não há resposta para validar
        }

        // =======================================================
        // 2. TRATAMENTO PARA CARTAS DE PERGUNTA (Múltipla Escolha / Sim ou Não)
        // =======================================================
        boolean acertou = opcaoEscolhida.trim().equalsIgnoreCase(carta.getCardAnswer().trim());

        if (acertou) {
            // Aciona as regras do peão e o movimento deslizante
            this.gameManager.cardResultVerification(true, carta.getCardValueText(), carta.getCardEffect());
        } else {
            // Passa falso para a regra de negócio processar a punição ou retenção
            this.gameManager.cardResultVerification(false, carta.getCardValueText(), carta.getCardEffect());
        }
    }
}