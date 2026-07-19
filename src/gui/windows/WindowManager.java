// Classe responsável por gerenciar as janelas e transições entre telas

// Packages
package gui.windows;

public class WindowManager {
    // VARIÁVEIS DE INSTÂNCIA
    private MainScreenContainer mainPanel; // variável para armazenar a referência ao painel de fundo principal do jogo

    /**
    * @param MainScreenContainer Tela principal do jogo[cite: 13]
    * Construtor da classe "WindowManager" que recebe a "MainScreenContainer" para podermos adicionar coisas nela[cite: 13]
    */
    public WindowManager(MainScreenContainer mainPanel) {
        this.mainPanel = mainPanel;
    }

    // Método para abrir o menu do modo de jogo offline (NewGameMenuScreen)[cite: 13]
    public void openMenuOffline() {
        System.out.println(
            "[WindowManager] Criando e centralizando o NewGameMenuScreen (Menu Offline)..."
        );
    
        // Verifica se o "mainPanel" não é nulo antes de tentar adicionar o menu[cite: 13]
        if (mainPanel != null && mainPanel.getComponentCount() > 0) {
            // Se o primeiro componente do "MainPanel" for uma instância de "MainMenuScreen", adiciona o menu do modo de jogo offline ao painel de opções do menu principal[cite: 13]
           if (mainPanel.getComponent(0) instanceof MainMenuScreen) {
                // Pega o componente do menu principal (MainMenuScreen) e transformo ele para o tipo "MainMenuScreen" para acessar seus métodos[cite: 13]
                MainMenuScreen mainMenuScreen = (MainMenuScreen) mainPanel.getComponent(0);
                
                SubMenuContainer subMenuContainer = mainMenuScreen.getSubMenuContainer(); // Pega o painel de opções do menu[cite: 13]
                NewGameMenuScreen offlineMenuGameMode = new NewGameMenuScreen(this, subMenuContainer); // Cria o menu do modo de jogo offline passando o painel de opções do menu principal para o construtor[cite: 13]
                subMenuContainer.displaySubMenu(offlineMenuGameMode); // Exibe o menu do modo de jogo offline no painel de opções do menu principal[cite: 13]

            }
            // Atualiza o Swing para redesenhar a tela imediatamente com o novo menu visível[cite: 13]
            mainPanel.revalidate();
            mainPanel.repaint();
        } else {
            // Se o "mainPanel" for "null", imprime um erro no console para ajudar na depuração[cite: 13]
            System.err.println(
                "[WindowManager Erro] O plano de fundo está nulo!"
            );
        }
    }

    // MODIFICADO: Método atualizado para receber todos os dados customizados dos jogadores
    public void startOfflineGameMode(
        String player1Name, String player1Color,
        String player2Name, String player2Color,
        String player3Name, String player3Color,
        String player4Name, String player4Color,
        String difficulty
    ) {
        // Imprime no console para confirmar que a transição está sendo iniciada[cite: 13]
        System.out.println(
            "[WindowManager] Iniciando transição para a tela de jogo..."
        );

        if (mainPanel != null) {
            // Se o "mainPanel" for diferente de "null", inicia a transição para a tela de jogo ("GameContainer")[cite: 13]
            mainPanel.removeAll(); // Limpa o plano de fundo para remover o menu e preparar para a tela de jogo[cite: 13]

            // MODIFICADO: Cria a tela de jogo ("GameContainer") passando todos os parâmetros recebidos do menu
            GameContainer gameScreen = new GameContainer(
                player1Name, player1Color,
                player2Name, player2Color,
                player3Name, player3Color,
                player4Name, player4Color,
                difficulty
            );

            // Adiciona a tela de jogo ao plano de fundo[cite: 13]
            mainPanel.add(gameScreen);

            // Força o Swing a revalidar a árvore de componentes e redesenhar o monitor[cite: 13]
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }
}