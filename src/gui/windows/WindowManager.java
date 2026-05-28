// Classe responsável por gerenciar as janelas e transições entre telas
// Packages
package gui.windows;
// Imports internos


public class WindowManager {
    // Variáveis de instância
    // variável para armazenar a referência ao painel de fundo principal do jogo, onde as telas serão adicionadas ("MainScreenInterface")
    private MainScreenInterface mainPanel;
    // Variável para armazenar a referência ao menu do modo de jogo offline ("NewGameMenuInterface")
    private NewGameMenuInterface offlineMenuGameMode; 
    // Variável para armazenar a referência à tela de jogo ("GamePanel")
    private GamePanel gameScreen; 
    // Variável para armazenar a referência ao menu principal ("MenuInterface")
    private MenuInterface menuInterface;
    private SubMenuContainer subMenuContainer; // Variável para armazenar a referência ao painel de opções do menu principal, onde os mini-menu's são exibidos

    // Construtor que recebe a tela de fundo real para podermos adicionar coisas nela
    public WindowManager(MainScreenInterface mainPanel) {
        this.mainPanel = mainPanel;
    }

    // Método para abrir o menu do modo de jogo offline (NewGameMenuInterface)
    public void openMenuOffline() {
        System.out.println("[WindowManager] Criando e centralizando o NewGameMenuInterface (Menu Offline)...");
    
        
        // Verifica se o "mainPanel" não é nulo antes de tentar adicionar o menu e se
        if (mainPanel != null && mainPanel.getComponentCount() > 0) {
            // Se o primeiro componente do "MainPanel" for uma instância de "MenuInterface", adiciona o menu do modo de jogo offline ao painel de opções do menu principal
           if (mainPanel.getComponent(0) instanceof MenuInterface) {
                // Pega o componente do menu principal (MenuInterface) e transformo ele para o tipo "MenuInterface" para acessar seus métodos
                menuInterface = (MenuInterface) mainPanel.getComponent(0);
                
                subMenuContainer = menuInterface.getSubMenuContainer(); // Pega o painel de opções do menu
                offlineMenuGameMode = new NewGameMenuInterface(this, subMenuContainer); // Cria o menu do modo de jogo offline passando o painel de opções do menu principal para o construtor
                subMenuContainer.displaySubMenu(offlineMenuGameMode); // Exibe o menu do modo de jogo offline no painel de opções do menu principal

            }
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
            gameScreen = new GamePanel(playerName, difficulty);

            // Adiciona a tela de jogo ao plano de fundo
            mainPanel.add(gameScreen);

            // Força o Swing a revalidar a árvore de componentes e redesenhar o monitor
            mainPanel.revalidate();
            mainPanel.repaint();
        }
    }
}