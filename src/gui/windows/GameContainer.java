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
import network.PlayerInfo;

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
    private static final Color ROXO_PAINEL = new Color(42, 24, 54);

    private GameManager gameManager;
    private TurnManager turnManager;

    private WindowManager windowManager;
    private GameClient client;
    private int myPlayerId;
    private boolean[] slotIsCPU;
    
    // CONSTRUTOR MULTIPLAYER ONLINE (com PlayerInfo[])
    public GameContainer(
        WindowManager windowManager,
        GameClient client,
        int myPlayerId,
        PlayerInfo[] players,
        String cpuDifficulty
    ) {
        this(
            windowManager,
            client, // Passa o cliente para o construtor mestre
            extrairNomeSlot(players, 0), extrairCorSlot(players, 0),
            extrairNomeSlot(players, 1), extrairCorSlot(players, 1),
            extrairNomeSlot(players, 2), extrairCorSlot(players, 2),
            extrairNomeSlot(players, 3), extrairCorSlot(players, 3),
            cpuDifficulty,
            myPlayerId
        );

        if (players != null) {
            this.slotIsCPU = new boolean[players.length];
            for (int i = 0; i < players.length; i++) {
                this.slotIsCPU[i] = (players[i] != null && players[i].isCPU());
            }
        }
    }

    // CONSTRUTOR MULTIPLAYER ONLINE (Legado / Fallback)
    public GameContainer(
        WindowManager windowManager, 
        GameClient client, 
        int myPlayerId, 
        boolean[] slotIsCPU, 
        String cpuDifficulty
    ) {
        this(
            windowManager,
            client, // Passa o cliente para o construtor mestre
            "Jogador 1" + (slotIsCPU != null && slotIsCPU.length > 0 && slotIsCPU[0] ? " (CPU)" : ""), "azul",
            "Jogador 2" + (slotIsCPU != null && slotIsCPU.length > 1 && slotIsCPU[1] ? " (CPU)" : ""), "roxo",
            "Jogador 3" + (slotIsCPU != null && slotIsCPU.length > 2 && slotIsCPU[2] ? " (CPU)" : ""), "rosa",
            "Jogador 4" + (slotIsCPU != null && slotIsCPU.length > 3 && slotIsCPU[3] ? " (CPU)" : ""), "amarelo",
            cpuDifficulty,
            myPlayerId
        );

        this.slotIsCPU = slotIsCPU;
    }

    // CONSTRUTOR OFFLINE
    public GameContainer(
        WindowManager windowManager,
        String player1Name, String player1Color, 
        String player2Name, String player2Color, 
        String player3Name, String player3Color, 
        String player4Name, String player4Color, 
        String cpuDifficulty
    ) {
        this(
            windowManager,
            null, // Modo offline: sem GameClient
            player1Name, player1Color,
            player2Name, player2Color,
            player3Name, player3Color,
            player4Name, player4Color,
            cpuDifficulty,
            0
        );
    }

    // CONSTRUTOR MESTRE INTERNO (AGORA RECEBE O GAMECLIENT DIRECTAMENTE)
    private GameContainer(
        WindowManager windowManager,
        GameClient client,
        String player1Name, String player1Color, 
        String player2Name, String player2Color, 
        String player3Name, String player3Color, 
        String player4Name, String player4Color, 
        String cpuDifficulty,
        int activeLocalPlayerId
    ) {
        this.windowManager = windowManager;
        this.client = client;
        this.myPlayerId = activeLocalPlayerId;
        this.playerName = player1Name;
        this.cpuDifficulty = cpuDifficulty;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screenSize.width, screenSize.height);
        setLayout(null);
        
        setOpaque(true);
        setBackground(ROXO_PAINEL);

        int sidebarWidth = (int) (300 * SCALE); 
        int boardWidth = screenSize.width - sidebarWidth;

        int cardsHeight = (int) (400 * SCALE);
        int pawnControlHeight = screenSize.height - cardsHeight;

        // 1. Tabuleiro
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
        this.boardScreen.setOpaque(false);

        // 2. Gerenciadores do jogo
        this.gameManager = new GameManager(this.boardScreen);
        this.gameManager.setGameDifficulty(cpuDifficulty);

        if (this.client != null) {
            this.client.setGameManager(this.gameManager);
            this.gameManager.setGameClient(this.client);
        }
        
        CPUIManager cpuManager = new CPUIManager(this.gameManager);
        this.gameManager.setCPUIManager(cpuManager);

        this.turnManager = new TurnManager(this.gameManager);
        this.turnManager.setLocalPlayerId(activeLocalPlayerId);
        this.gameManager.setTurnManager(this.turnManager);

        // VINCULA O GAMECLIENT NO TURNMANAGER ANTES DE QUALQUER AÇÃO DE TURNO
        if (this.client != null) {
            this.turnManager.setGameClient(this.client);
        }

        cards.CardManager cardManager = new cards.CardManager();
        this.deckManager = new DeckManager(cardManager);
        this.gameManager.setDeckManager(this.deckManager);

        cpuManager.setDeckManager(this.deckManager);
        this.turnManager.setCPUIManager(cpuManager);

        // 3. Painel Superior Lateral (Cartas)
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
        this.turnManager.setCardsContainer(cardsContainer);

        // 4. Painel Inferior Lateral (Status, Controle de Peões e Menu)
        JPanel pawnControlArea = new JPanel();
        pawnControlArea.setBounds(boardWidth, cardsHeight, sidebarWidth, pawnControlHeight);
        pawnControlArea.setBackground(ROXO_PAINEL);
        pawnControlArea.setLayout(null);

        // Barra de Status
        this.statusBar = new GameStatusBar(SCALE);
        this.statusBar.setBounds((int) (40 * SCALE), (int) (8 * SCALE), (int) (220 * SCALE), (int) (46 * SCALE));
        this.gameManager.setGameStatusBar(this.statusBar);
        pawnControlArea.add(this.statusBar);

        // Gerenciador e Container de Peões
        this.pawnControlManager = new PawnControlManager(this.boardScreen, this.gameManager);
        if (this.client != null) {
            this.pawnControlManager.setGameClient(this.client);
        }
        this.gameManager.setPawnControlManager(this.pawnControlManager);
        
        String localPlayerColor = player1Color;
        if (activeLocalPlayerId == 1) localPlayerColor = player2Color;
        else if (activeLocalPlayerId == 2) localPlayerColor = player3Color;
        else if (activeLocalPlayerId == 3) localPlayerColor = player4Color;

        PawnControlContainer pawnControlContainer = new PawnControlContainer(pawnControlManager, localPlayerColor);
        pawnControlContainer.setBounds((int) (40 * SCALE), (int) (62 * SCALE), (int) (220 * SCALE), (int) (120 * SCALE));
        pawnControlContainer.setBackground(new Color(38, 24, 16));
        pawnControlArea.add(pawnControlContainer);

        CustomButton btnMenu = new CustomButton("MENU");
        btnMenu.setBounds((int) (40 * SCALE), (int) (192 * SCALE), (int) (220 * SCALE), (int) (36 * SCALE));
        btnMenu.addActionListener(e -> abrirMenuPausa());
        pawnControlArea.add(btnMenu);

        // Montagem final do Layout
        add(this.boardScreen);
        add(boardBackgroundArea);
        add(cardsArea);
        add(pawnControlArea);

        configurarListenersDePeao(activeLocalPlayerId);

        // Inicializa a partida chamando o SORTEIO real
        iniciarPartida();
    }

    private void iniciarPartida() {
        if (this.turnManager != null) {
            // Roda o sorteio sincronizado em vez de fixar setTurn(0)
            this.turnManager.sortearPrimeiroJogador();
        }
    }

    private static String extrairNomeSlot(PlayerInfo[] players, int slot) {
        if (players != null && slot >= 0 && slot < players.length && players[slot] != null) {
            String name = players[slot].getName();
            if (name != null && !name.trim().isEmpty()) {
                return name;
            }
        }
        return "Jogador " + (slot + 1);
    }

    private static String extrairCorSlot(PlayerInfo[] players, int slot) {
        if (players != null && slot >= 0 && slot < players.length && players[slot] != null) {
            return converterColorIndexParaString(players[slot].getColorIndex());
        }
        switch (slot) {
            case 0: return "azul";
            case 1: return "roxo";
            case 2: return "rosa";
            case 3: return "amarelo";
            default: return "azul";
        }
    }

    private static String converterColorIndexParaString(int colorIndex) {
        switch (colorIndex) {
            case 0: return "azul";
            case 1: return "roxo";
            case 2: return "rosa";
            case 3: return "amarelo";
            default: return "azul";
        }
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