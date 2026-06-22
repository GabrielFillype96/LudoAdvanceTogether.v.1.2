// Classe responsável por gerenciar as janelas e transições entre telas

// Packages
package gui.windows;

public class WindowManager {
    // VARIÁVEIS DE INSTÂNCIA
    private MainScreenContainer mainPanel; // variável para armazenar a referência ao painel de fundo principal do jogo, onde as telas serão adicionadas ("MainScreenContainer")

    /**
    * @param MainScreenContainer Tela principal do jogo
    * Construtor da classe "WindowManager" que recebe a "MainScreenContainer" para podermos adicionar coisas nela
    */
    public WindowManager(MainScreenContainer mainPanel) {
        this.mainPanel = mainPanel;
    }

    // Método para abrir o menu do modo de jogo offline (NewGameMenuScreen)
    public void openMenuOffline() {
        System.out.println(
            "[WindowManager] Criando e centralizando o NewGameMenuScreen (Menu Offline)..."
        );
    
        // Verifica se o "mainPanel" não é nulo antes de tentar adicionar o menu e se
        if (mainPanel != null && mainPanel.getComponentCount() > 0) {
            // Se o primeiro componente do "MainPanel" for uma instância de "MainMenuScreen", adiciona o menu do modo de jogo offline ao painel de opções do menu principal
           if (mainPanel.getComponent(0) instanceof MainMenuScreen) {
                // Pega o componente do menu principal (MainMenuScreen) e transformo ele para o tipo "MainMenuScreen" para acessar seus métodos
                MainMenuScreen mainMenuScreen = (MainMenuScreen) mainPanel.getComponent(0);
                
                SubMenuContainer subMenuContainer = mainMenuScreen.getSubMenuContainer(); // Pega o painel de opções do menu
                NewGameMenuScreen offlineMenuGameMode = new NewGameMenuScreen(this, subMenuContainer); // Cria o menu do modo de jogo offline passando o painel de opções do menu principal para o construtor
                subMenuContainer.displaySubMenu(offlineMenuGameMode); // Exibe o menu do modo de jogo offline no painel de opções do menu principal

            }
            // Atualiza o Swing para redesenhar a tela imediatamente com o novo menu visível
            mainPanel.revalidate();
            mainPanel.repaint();
        } else {
            // Se o "mainPanel" for "null", imprime um erro no console para ajudar na depuração
            System.err.println(
                "[WindowManager Erro] O plano de fundo está nulo!"
            );
        }
    }

    // Método para iniciar a partida offline, recebendo o nome do jogador e a dificuldade escolhida
    public void startOfflineGameMode(String playerName, String difficulty) {
        // Imprime no console para confirmar que a transição está sendo iniciada
        System.out.println(
            "[WindowManager] Iniciando transição para a tela de jogo..."
        );

        if (mainPanel != null) {
            // Se o "mainPanel" for diferente de "null", inicia a transição para a tela de jogo ("GameContainer")
            mainPanel.removeAll(); // Limpa o plano de fundo para remover o menu e preparar para a tela de jogo

            // Cria a tela de jogo ("GameContainer") passando o nome do jogador e a dificuldade escolhida
            GameContainer gameScreen = new GameContainer(playerName, difficulty);

            // Adiciona a tela de jogo ao plano de fundo
            mainPanel.add(gameScreen);

            // Força o Swing a revalidar a árvore de componentes e redesenhar o monitor
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }
}