// Classe responsável por gerenciar as janelas e transições entre telas

package gui.windows;

import network.GameClient;

public class WindowManager {
    // VARIÁVEIS DE INSTÂNCIA
    private MainScreenContainer mainPanel; // referência ao painel de fundo principal

    // Armazena as configurações do último jogo offline para permitir reiniciar a partida
    private String lastP1Name, lastP1Color;
    private String lastP2Name, lastP2Color;
    private String lastP3Name, lastP3Color;
    private String lastP4Name, lastP4Color;
    private String lastDifficulty;

    /**
    * @param mainPanel Tela principal do jogo
    */
    public WindowManager(MainScreenContainer mainPanel) {
        this.mainPanel = mainPanel;
    }

    // Método para abrir o menu do modo de jogo offline (NewGameMenuScreen)
    public void openMenuOffline() {
        System.out.println(
            "[WindowManager] Criando e centralizando o NewGameMenuScreen (Menu Offline)..."
        );
    
        if (mainPanel != null && mainPanel.getComponentCount() > 0) {
           if (mainPanel.getComponent(0) instanceof MainMenuScreen) {
                MainMenuScreen mainMenuScreen = (MainMenuScreen) mainPanel.getComponent(0);
                
                SubMenuContainer subMenuContainer = mainMenuScreen.getSubMenuContainer();
                NewGameMenuScreen offlineMenuGameMode = new NewGameMenuScreen(this, subMenuContainer);
                subMenuContainer.displaySubMenu(offlineMenuGameMode);
            }
            mainPanel.revalidate();
            mainPanel.repaint();
        } else {
            System.err.println(
                "[WindowManager Erro] O plano de fundo está nulo!"
            );
        }
    }

    // Método para iniciar o jogo offline com dados customizados
    public void startOfflineGameMode(
        String player1Name, String player1Color,
        String player2Name, String player2Color,
        String player3Name, String player3Color,
        String player4Name, String player4Color,
        String difficulty
    ) {
        // Salva as configurações atuais para permitir reiniciar a partida
        this.lastP1Name = player1Name;
        this.lastP1Color = player1Color;
        this.lastP2Name = player2Name;
        this.lastP2Color = player2Color;
        this.lastP3Name = player3Name;
        this.lastP3Color = player3Color;
        this.lastP4Name = player4Name;
        this.lastP4Color = player4Color;
        this.lastDifficulty = difficulty;

        System.out.println(
            "[WindowManager] Iniciando transição para a tela de jogo..."
        );

        if (mainPanel != null) {
            mainPanel.removeAll();

            // Instancia o GameContainer passando o WindowManager (this) como primeiro argumento
            GameContainer gameScreen = new GameContainer(
                this,
                player1Name, player1Color,
                player2Name, player2Color,
                player3Name, player3Color,
                player4Name, player4Color,
                difficulty
            );

            mainPanel.add(gameScreen);
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    // Reinicia a partida offline utilizando as mesmas configurações
    public void iniciarNovoJogoOffline() {
        if (lastP1Name != null) {
            startOfflineGameMode(
                lastP1Name, lastP1Color,
                lastP2Name, lastP2Color,
                lastP3Name, lastP3Color,
                lastP4Name, lastP4Color,
                lastDifficulty
            );
        } else {
            openMenuOffline();
        }
    }

    // Retorna para a tela do menu principal
    public void exibirMenuPrincipal() {
        if (mainPanel != null) {
            mainPanel.removeAll();
            mainPanel.add(new MainMenuScreen(this));
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    // Método para abrir a tela de Lobby Multiplayer
    public void openLobbyMultiplayer() {
        if (mainPanel != null && mainPanel.getComponentCount() > 0) {
            if (mainPanel.getComponent(0) instanceof MainMenuScreen) {
                MainMenuScreen mainMenuScreen = (MainMenuScreen) mainPanel.getComponent(0);
                SubMenuContainer subMenuContainer = mainMenuScreen.getSubMenuContainer();
                
                LobbyScreen lobbyScreen = new LobbyScreen(this); 
                subMenuContainer.displaySubMenu(lobbyScreen);
            }
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    /**
     * Inicia a tela do jogo em modo Multiplayer Online.
     */
    public void startOnlineGame(GameClient client, int myPlayerId, boolean[] slotIsCPU) {
        mainPanel.removeAll();

        GameContainer gameContainer = new GameContainer(this, client, myPlayerId, slotIsCPU, "Médio");

        mainPanel.add(gameContainer);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}