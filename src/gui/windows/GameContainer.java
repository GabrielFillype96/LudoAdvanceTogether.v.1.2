package gui.windows;

import control.CPUIManager;
import control.DeckManager;
import control.GameManager;
import control.PawnControlManager;
import control.TurnManager;
import gui.components.PlayerPawn;
import gui.components.buttons.CustomButton;
import gui.components.GameStatusBar; 
import gui.events.BoardPawnMouseListener;
import network.GameClient;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.JPanel;
import actions.CardAnswerValidation;

public class GameContainer extends JPanel {
    private String playerName;
    private String cpuDifficulty;
    private PawnControlManager pawnControlManager;
    private DeckManager deckManager;
    private BoardScreen boardScreen;
    private GameStatusBar statusBar; 

    private static final double SCALE = 1.5;

    // Cor roxa padrão do painel lateral
    private static final Color ROXO_PAINEL = new Color(42, 24, 54);

    private GameManager gameManager;
    private TurnManager turnManager;

    private WindowManager windowManager;
    private GameClient client;
    private int myPlayerId;
    private boolean[] slotIsCPU;
    
    // CONSTRUTOR MULTIPLAYER ONLINE
    public GameContainer(
        WindowManager windowManager, 
        GameClient client, 
        int myPlayerId, 
        boolean[] slotIsCPU, 
        String cpuDifficulty
    ) {
        this(
            windowManager,
            "Jogador 1" + (slotIsCPU != null && slotIsCPU.length > 0 && slotIsCPU[0] ? " (CPU)" : ""), "azul",
            "Jogador 2" + (slotIsCPU != null && slotIsCPU.length > 1 && slotIsCPU[1] ? " (CPU)" : ""), "roxo",
            "Jogador 3" + (slotIsCPU != null && slotIsCPU.length > 2 && slotIsCPU[2] ? " (CPU)" : ""), "rosa",
            "Jogador 4" + (slotIsCPU != null && slotIsCPU.length > 3 && slotIsCPU[3] ? " (CPU)" : ""), "amarelo",
            cpuDifficulty
        );

        this.client = client;
        this.myPlayerId = myPlayerId;
        this.slotIsCPU = slotIsCPU;

        if (this.client != null) {
            this.client.setGameManager(this.gameManager);
            this.gameManager.setGameClient(this.client);
            this.turnManager.setGameClient(this.client);
        }

        configurarListenersDePeao(this.myPlayerId);
    }

    // CONSTRUTOR PADRÃO / OFFLINE
    public GameContainer(
        WindowManager windowManager,
        String player1Name, String player1Color, 
        String player2Name, String player2Color, 
        String player3Name, String player3Color, 
        String player4Name, String player4Color, 
        String cpuDifficulty
    ) {
        this.windowManager = windowManager;
        this.playerName = player1Name;
        this.cpuDifficulty = cpuDifficulty;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screenSize.width, screenSize.height);
        setLayout(null);
        
        // Define o container principal como opaco com a cor roxa do painel
        setOpaque(true);
        setBackground(ROXO_PAINEL);

        // Largura escalada original (300 * 1.5 = 450px) para acomodar o baralho sem cortes
        int sidebarWidth = (int) (300 * SCALE); 
        int boardWidth = screenSize.width - sidebarWidth;

        int cardsHeight = (int) (400 * SCALE);
        int pawnControlHeight = screenSize.height - cardsHeight;

        // Painel de fundo para a área do tabuleiro
        JPanel boardBackgroundArea = new JPanel();
        boardBackgroundArea.setBounds(0, 0, boardWidth, screenSize.height);
        boardBackgroundArea.setBackground(ROXO_PAINEL);
        boardBackgroundArea.setLayout(null);

        this.boardScreen = new BoardScreen(
            player1Name, player1Color, 
            player2Name, player2Color, 
            player3Name, player3Color, 
            player4Name, player4Color
        );
        this.boardScreen.setBounds(0, 0, boardWidth, screenSize.height);
        this.boardScreen.setOpaque(false); // Permite visualizar o fundo roxo caso o BoardScreen seja transparente

        this.gameManager = new GameManager(this.boardScreen);
        
        CPUIManager cpuManager = new CPUIManager(this.gameManager);
        this.gameManager.setCPUIManager(cpuManager);

        this.turnManager = new TurnManager(this.gameManager);
        this.gameManager.setTurnManager(this.turnManager);

        cards.CardManager cardManager = new cards.CardManager();
        this.deckManager = new DeckManager(cardManager);
        this.deckManager.initializeDecks();

        cpuManager.setDeckManager(this.deckManager);
        this.turnManager.setCPUIManager(cpuManager);

        // Área das cartas (Painel Lateral Direito)
        JPanel cardsArea = new JPanel();
        cardsArea.setBounds(boardWidth, 0, sidebarWidth, cardsHeight);
        cardsArea.setBackground(ROXO_PAINEL);
        cardsArea.setLayout(null);

        CardAnswerValidation cardAnswerValidation = new CardAnswerValidation(this.gameManager);

        CardsContainer cardsContainer = new CardsContainer(this.gameManager, cardAnswerValidation);
        cardsContainer.setBounds((int) (12 * SCALE), 0, (int) (300 * SCALE), (int) (400 * SCALE));
        cardsContainer.setBackground(ROXO_PAINEL);
        cardsArea.add(cardsContainer);

        cardsContainer.setDeckManager(this.deckManager);
        cardsContainer.setTurnManager(this.turnManager);

        // Área de controle dos peões
        JPanel pawnControlArea = new JPanel();
        pawnControlArea.setBounds(boardWidth, cardsHeight, sidebarWidth, pawnControlHeight);
        pawnControlArea.setBackground(ROXO_PAINEL);
        pawnControlArea.setLayout(null);

        this.statusBar = new GameStatusBar(SCALE);
        this.statusBar.setBounds((int) (40 * SCALE), (int) (8 * SCALE), (int) (220 * SCALE), (int) (46 * SCALE));
        
        if (this.client == null) {
            this.turnManager.sortearPrimeiroJogador();
        }
        
        this.gameManager.setGameStatusBar(statusBar);
        pawnControlArea.add(this.statusBar);

        this.pawnControlManager = new PawnControlManager(this.boardScreen, this.gameManager);
        this.gameManager.setPawnControlManager(this.pawnControlManager);
        
        PawnControlContainer pawnControlContainer = new PawnControlContainer(pawnControlManager, player1Color);
        pawnControlContainer.setBounds((int) (40 * SCALE), (int) (62 * SCALE), (int) (220 * SCALE), (int) (120 * SCALE));
        pawnControlContainer.setBackground(new Color(38, 24, 16));
        pawnControlArea.add(pawnControlContainer);

        CustomButton btnMenu = new CustomButton("MENU");
        btnMenu.setBounds((int) (40 * SCALE), (int) (192 * SCALE), (int) (220 * SCALE), (int) (36 * SCALE));
        btnMenu.addActionListener(e -> abrirMenuPausa());
        pawnControlArea.add(btnMenu);

        // Adiciona o tabuleiro e os painéis ao container
        add(this.boardScreen);
        add(boardBackgroundArea); // Fundo roxo atrás do tabuleiro
        add(cardsArea);
        add(pawnControlArea);

        configurarListenersDePeao(0);
    }

    private void abrirMenuPausa() {
        InGameMenu menuDialog = new InGameMenu(this, this.windowManager);
        menuDialog.setVisible(true);
    }

    private void configurarListenersDePeao(int targetPlayerId) {
        for (int i = 0; i < 4; i++) {
            PlayerPawn playerPawn = boardScreen.getPlayerPawn(targetPlayerId, i);
            if (playerPawn != null) {
                for (java.awt.event.MouseListener ml : playerPawn.getMouseListeners()) {
                    if (ml instanceof BoardPawnMouseListener) {
                        playerPawn.removeMouseListener(ml);
                    }
                }
                playerPawn.addMouseListener(new BoardPawnMouseListener(pawnControlManager, i));
            }
        }
    }

    public static Rectangle getGameContainerBounds() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Rectangle(0, 0, screenSize.width, screenSize.height);
    }
}