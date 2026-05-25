package actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gui.windows.NewGameMenuInterface;
import gui.windows.WindowManager;

public class StartGameAction implements ActionListener {

    private NewGameMenuInterface menuOffline;
    private WindowManager windowManager;

    // O construtor recebe o menu offline para poder ler os inputs e o gerenciador para mudar a tela
    public StartGameAction(NewGameMenuInterface menuOffline, WindowManager windowManager) {
        this.menuOffline = menuOffline;
        this.windowManager = windowManager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("[Action] Botão JOGAR detectado! Processando dados da partida...");

        if (menuOffline != null && windowManager != null) {
            // 1. Extrai o nome do jogador do JTextField (vamos criar esses métodos no menu já já)
            String nomeP1 = menuOffline.getNomeJogador();

            // 2. Extrai a dificuldade selecionada nos RadioButtons
            String dificuldade = menuOffline.getDificuldadeSelecionada();

            System.out.println("[Action] Dados Coletados -> Jogador: " + nomeP1 + " | Dificuldade: " + dificuldade);

            // 3. Ordena ao WindowManager para carregar o jogo com esses parâmetros
            windowManager.iniciarPartidaOffline(nomeP1, dificuldade);
        }
    }
}