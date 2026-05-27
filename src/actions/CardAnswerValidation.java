package actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import cards.CustomCards;
import gui.windows.CardsPanel;

/**
 * Classe responsável por gerenciar e validar as respostas das cartas.
 * Ela isola a regra de negócios da interface visual.
 */
public class CardAnswerValidation {

    /**
     * Método que será chamado pelo botão da carta para validar a resposta.
     * @param opcaoEscolhida A string da opção (Ex: "B) Coleta Seletiva" ou "Sim")
     * @param carta A carta atual que contém a resposta correta e os efeitos
     * @param painelPai O CardsPanel que gerencia a exibição para podermos passar de carta
     */
    public static void validar(String opcaoEscolhida, CustomCards carta, CardsPanel painelPai) {
        if (carta == null || painelPai == null) return;

        // Compara a opção que o jogador clicou com a resposta certa do JSON
        boolean acertou = opcaoEscolhida.equalsIgnoreCase(carta.getCardAnswer());

        if (acertou) {
            // Feedback visual de sucesso
            JOptionPane.showMessageDialog(painelPai, 
                "Resposta Correta!\nVocê vai " + carta.getCardEffect() + " " + carta.getCardValueText() + " casas.", 
                "Parabéns!", 
                JOptionPane.INFORMATION_MESSAGE);
                
            // TODO: Chame aqui a lógica do seu Tabuleiro/Jogo para mover o peão do jogador
            // Exemplo: Jogo.getTabuleiro().moverJogadorAtual(carta.getCardValueText());

        } else {
            // Feedback visual de erro
            JOptionPane.showMessageDialog(painelPai, 
                "Resposta Errada!\nA resposta correta era: " + carta.getCardAnswer() + "\nVocê passa a vez.", 
                "Que pena!", 
                JOptionPane.ERROR_MESSAGE);
                
            // TODO: Chame aqui a lógica para passar o turno para a CPU ou próximo jogador
            // Exemplo: Jogo.getTurnoManager().passarVez();
        }

        // --- MECÂNICA DE AVANÇAR DE CARTA ---
        // Descobre qual o índice da carta atual na lista do JSON
        int indiceAtual = painelPai.getListaCartas().indexOf(carta);
        int proximoIndice = indiceAtual + 1;

        if (proximoIndice < painelPai.getListaCartas().size()) {
            // Avança para a próxima carta do JSON de forma limpa
            painelPai.mostrarCarta(proximoIndice);
        } else {
            // Se as cartas acabarem, reinicia o monte
            JOptionPane.showMessageDialog(painelPai, "Fim das cartas desta categoria! O monte será reiniciado.");
            painelPai.mostrarCarta(0); 
        }
    }
}