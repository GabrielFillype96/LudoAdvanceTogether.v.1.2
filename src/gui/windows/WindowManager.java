package gui.windows;

import java.awt.Component;
import network.GameClient;
import network.PlayerInfo;

public class WindowManager {
    private MainScreenContainer mainPanel;

    private String lastP1Name, lastP1Color;
    private String lastP2Name, lastP2Color;
    private String lastP3Name, lastP3Color;
    private String lastP4Name, lastP4Color;
    private String lastDifficulty;

    public WindowManager(MainScreenContainer mainPanel) {
        this.mainPanel = mainPanel;
    }

    public boolean podeTrocarDeTela() {
        LobbyScreen lobbyAtual = getLobbyScreenAtual();
        if (lobbyAtual != null) {
            return lobbyAtual.confirmarSaidaDoLobby();
        }
        return true;
    }

    private LobbyScreen getLobbyScreenAtual() {
        if (mainPanel != null && mainPanel.getComponentCount() > 0) {
            if (mainPanel.getComponent(0) instanceof MainMenuScreen) {
                MainMenuScreen mainMenuScreen = (MainMenuScreen) mainPanel.getComponent(0);
                SubMenuContainer subMenuContainer = mainMenuScreen.getSubMenuContainer();
                
                if (subMenuContainer != null && subMenuContainer.getComponentCount() > 0) {
                    Component subMenu = subMenuContainer.getComponent(0);
                    if (subMenu instanceof LobbyScreen) {
                        return (LobbyScreen) subMenu;
                    }
                }
            }
        }
        return null;
    }

    public void openMenuOffline() {
        if (!podeTrocarDeTela()) {
            return;
        }

        System.out.println("[WindowManager] Criando e centralizando o NewGameMenuScreen (Menu Offline)...");
    
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
            System.err.println("[WindowManager Erro] O plano de fundo está nulo!");
        }
    }

    public void openMenuAbout() {
        if (!podeTrocarDeTela()) {
            return;
        }

        System.out.println("[WindowManager] Exibindo tela SOBRE...");

        if (mainPanel != null && mainPanel.getComponentCount() > 0) {
            if (mainPanel.getComponent(0) instanceof MainMenuScreen) {
                MainMenuScreen mainMenuScreen = (MainMenuScreen) mainPanel.getComponent(0);
                SubMenuContainer subMenuContainer = mainMenuScreen.getSubMenuContainer();
                
                AboutSubMenuPanel aboutScreen = new AboutSubMenuPanel();
                subMenuContainer.displaySubMenu(aboutScreen);
            }
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    public void fecharJogo() {
        if (!podeTrocarDeTela()) {
            return;
        }

        System.out.println("[WindowManager] Encerrando o aplicativo...");
        System.exit(0);
    }

    public void startOfflineGameMode(
        String player1Name, String player1Color,
        String player2Name, String player2Color,
        String player3Name, String player3Color,
        String player4Name, String player4Color,
        String difficulty
    ) {
        this.lastP1Name = player1Name;
        this.lastP1Color = player1Color;
        this.lastP2Name = player2Name;
        this.lastP2Color = player2Color;
        this.lastP3Name = player3Name;
        this.lastP3Color = player3Color;
        this.lastP4Name = player4Color;
        this.lastDifficulty = difficulty;

        System.out.println("[WindowManager] Iniciando transição para a tela de jogo...");

        if (mainPanel != null) {
            mainPanel.removeAll();

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

    public void iniciarNovoJogoOffline() {
        if (!podeTrocarDeTela()) {
            return;
        }

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

    public void exibirMenuPrincipal() {
        if (!podeTrocarDeTela()) {
            return;
        }

        if (mainPanel != null) {
            mainPanel.removeAll();
            mainPanel.add(new MainMenuScreen(this));
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    public void forceShowScreen(String screenName) {
        if ("MAIN_MENU".equalsIgnoreCase(screenName)) {
            if (mainPanel != null) {
                mainPanel.removeAll();
                mainPanel.add(new MainMenuScreen(this));
                mainPanel.revalidate();
                mainPanel.repaint();
            }
        }
    }

    public void openLobbyMultiplayer() {
        if (!podeTrocarDeTela()) {
            return;
        }

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

    public void startOnlineGame(GameClient client, int myPlayerId, PlayerInfo[] players) {
        if (mainPanel != null) {
            mainPanel.removeAll();

            GameContainer gameContainer = new GameContainer(this, client, myPlayerId, players, "Médio");

            mainPanel.add(gameContainer);
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    public void startOnlineGame(GameClient client, int myPlayerId, boolean[] slotIsCPU) {
        if (mainPanel != null) {
            mainPanel.removeAll();

            GameContainer gameContainer = new GameContainer(this, client, myPlayerId, slotIsCPU, "Médio");

            mainPanel.add(gameContainer);
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }
}