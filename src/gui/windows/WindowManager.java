package gui.windows;

import java.awt.Component;
import network.GameClient;

// Classe responsável por gerenciar as janelas e transições entre telas
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

    /**
     * Verifica se existe um Lobby ativo e se o usuário confirma a saída dele 
     * antes de mudar para qualquer outra tela ou fechar o jogo.
     * @return true se puder trocar de tela; false se o usuário cancelar.
     */
    public boolean podeTrocarDeTela() {
        LobbyScreen lobbyAtual = getLobbyScreenAtual();
        if (lobbyAtual != null) {
            return lobbyAtual.confirmarSaidaDoLobby();
        }
        return true;
    }

    /**
     * Procura se o LobbyScreen está aberto no container de submenus.
     */
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

    // Método para abrir o menu do modo de jogo offline (NewGameMenuScreen)
    public void openMenuOffline() {
        // Trava de confirmação caso o jogador esteja em uma sala multiplayer
        if (!podeTrocarDeTela()) {
            return;
        }

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

    /**
     * Método para abrir a tela SOBRE (AboutSubMenuPanel).
     * Interceptado pela confirmação de saída caso esteja no Lobby.
     */
    public void openMenuAbout() {
        if (!podeTrocarDeTela()) {
            return;
        }

        System.out.println("[WindowManager] Exibindo tela SOBRE...");

        if (mainPanel != null && mainPanel.getComponentCount() > 0) {
            if (mainPanel.getComponent(0) instanceof MainMenuScreen) {
                MainMenuScreen mainMenuScreen = (MainMenuScreen) mainPanel.getComponent(0);
                SubMenuContainer subMenuContainer = mainMenuScreen.getSubMenuContainer();
                
                // Instanciação corrigida (sem passar 'this')
                AboutSubMenuPanel aboutScreen = new AboutSubMenuPanel();
                subMenuContainer.displaySubMenu(aboutScreen);
            }
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }

    /**
     * Método para fechar o jogo (botão SAIR).
     * Pede confirmação antes de encerrar se estiver no Lobby Multiplayer.
     */
    public void fecharJogo() {
        if (!podeTrocarDeTela()) {
            return;
        }

        System.out.println("[WindowManager] Encerrando o aplicativo...");
        System.exit(0);
    }

    // Método para iniciar o jogo offline com dados customizados
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

        System.out.println(
            "[WindowManager] Iniciando transição para a tela de jogo..."
        );

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

    // Reinicia a partida offline utilizando as mesmas configurações
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

    // Retorna para a tela do menu principal
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

    /**
     * Força a exibição de uma tela sem confirmação (utilizado na desconexão remota por rede).
     */
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

    // Método para abrir a tela de Lobby Multiplayer
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

    /**
     * Inicia a tela do jogo em modo Multiplayer Online.
     */
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