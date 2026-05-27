// Classe responsável por gerenciar as janelas e transições entre telas
// Packages
package gui.windows;

public class WindowManager {
    // Variáveis
    private MainScreenInterface mainPanel; // A tela de fundo principal onde tudo acontece

    // Construtor que recebe a tela de fundo real para podermos adicionar coisas nela
    public WindowManager(MainScreenInterface mainPanel) {
        this.mainPanel = mainPanel;
    }

    // Método para abrir o menu do modo de jogo offline (NewGameMenuInterface)
    public void openMenuOffline() {
        System.out.println("[WindowManager] Criando e centralizando o NewGameMenuInterface (Menu Offline)...");
        
        // Cria a instância do menu passando este gerenciador para ele
        NewGameMenuInterface offlineMenuGameMode = new NewGameMenuInterface(this);
        
        // Centraliza o menu baseado nas dimensões dele (560x420) na tela (900x600)
        offlineMenuGameMode.setBounds((900 - 560) / 2, (600 - 420) / 2, 560, 420);
        
        // Verifica se o "mainPanel" não é nulo antes de tentar adicionar o menu
        if (mainPanel != null) {
            // Se o "mainPanel" for diferente de "null", adiciona o menu do modo de jogo offline ("offlineMenuGameMode") ao plano de fundo
            mainPanel.add(offlineMenuGameMode);
            
            // Força a placa roxa a ficar na camada mais alta da frente
            mainPanel.setComponentZOrder(offlineMenuGameMode, 0); 
            
            // Atualiza o Swing para redesenhar a tela imediatamente com o novo menu visível
            mainPanel.revalidate();
            mainPanel.repaint();
        } else {
            // Se o "mainPanel" for "null", imprime um erro no console para ajudar na depuração
            System.err.println("[WindowManager Erro] O plano de fundo está nulo!");
        }
    }

    // Método para iniciar a partida offline, recebendo o nome do jogador e a dificuldade escolhida
    public void startOfflineGameMode(String playerName, String difficulty) {
        // Imprime no console para confirmar que a transição está sendo iniciada
        System.out.println("[WindowManager] Iniciando transição para a tela de jogo...");

        if (mainPanel != null) {
            // Se o "mainPanel" for diferente de "null", inicia a transição para a tela de jogo ("GamePanel")
            mainPanel.removeAll(); // Limpa o plano de fundo para remover o menu e preparar para a tela de jogo

            // Cria a tela de jogo ("GamePanel") passando o nome do jogador e a dificuldade escolhida
            GamePanel gameScreen = new GamePanel(playerName, difficulty);

            // Adiciona a tela de jogo ao plano de fundo
            mainPanel.add(gameScreen);

            // Força o Swing a revalidar a árvore de componentes e redesenhar o monitor
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }
}