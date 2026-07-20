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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JButton;
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

    private GameManager gameManager;
    private TurnManager turnManager;

    private WindowManager windowManager;
    private GameClient client;
    private int myPlayerId;
    private boolean[] slotIsCPU;
    
    private static final Rectangle GAME_CONTAINER_BOUNDS = new Rectangle(0, 0, (int) (980 * SCALE), (int) (680 * SCALE));
    private static final Rectangle BOARD_FRAME_BOUNDS = new Rectangle(0, 0, (int) (680 * SCALE), (int) (680 * SCALE));
    private static final Rectangle CARDS_AREA_BOUNDS = new Rectangle((int) (680 * SCALE), 0, (int) (300 * SCALE), (int) (400 * SCALE));
    private static final Rectangle CARDS_CONTAINER_BOUNDS = new Rectangle(12, 0, (int) (300 * SCALE), (int) (400 * SCALE));
    private static final Rectangle PAWN_CONTROL_AREA_BOUNDS = new Rectangle((int) (680 * SCALE), (int) (400 * SCALE), (int) (300 * SCALE), (int) (290 * SCALE));
    private static final Rectangle STATUS_BAR_BOUNDS = new Rectangle((int) (40 * SCALE), (int) (8 * SCALE), (int) (220 * SCALE), (int) (46 * SCALE));
    private static final Rectangle PAWN_CONTROL_CONTAINER_BOUNDS = new Rectangle((int) (40 * SCALE), (int) (62 * SCALE), (int) (220 * SCALE), (int) (120 * SCALE));
    
    // Posição do botão de menu abaixo do carrossel
    private static final Rectangle MENU_BUTTON_BOUNDS = new Rectangle((int) (40 * SCALE), (int) (192 * SCALE), (int) (220 * SCALE), (int) (36 * SCALE));

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

        setBounds(GAME_CONTAINER_BOUNDS);
        setLayout(null);
        setOpaque(false);

        this.boardScreen = new BoardScreen(
            player1Name, player1Color, 
            player2Name, player2Color, 
            player3Name, player3Color, 
            player4Name, player4Color
        );
        
        BoardWithFrame boardWithFrame = new BoardWithFrame(this.boardScreen, SCALE);
        boardWithFrame.setBounds(BOARD_FRAME_BOUNDS);

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

        JPanel cardsArea = new JPanel();
        cardsArea.setBounds(CARDS_AREA_BOUNDS);
        cardsArea.setBackground(new Color(255, 0, 16));
        cardsArea.setLayout(null);

        CardAnswerValidation cardAnswerValidation = new CardAnswerValidation(this.gameManager);

        CardsContainer cardsContainer = new CardsContainer(this.gameManager, cardAnswerValidation);
        cardsContainer.setBounds(CARDS_CONTAINER_BOUNDS);
        cardsContainer.setBackground(new Color(0, 255, 16));
        cardsArea.add(cardsContainer);

        cardsContainer.setDeckManager(this.deckManager);
        cardsContainer.setTurnManager(this.turnManager);

        JPanel pawnControlArea = new JPanel();
        pawnControlArea.setBounds(PAWN_CONTROL_AREA_BOUNDS);
        pawnControlArea.setBackground(new Color(38, 24, 16));
        pawnControlArea.setLayout(null);

        this.statusBar = new GameStatusBar(SCALE);
        this.statusBar.setBounds(STATUS_BAR_BOUNDS);
        
        if (this.client == null) {
            this.turnManager.sortearPrimeiroJogador();
        }
        
        this.gameManager.setGameStatusBar(statusBar);
        pawnControlArea.add(this.statusBar);

        this.pawnControlManager = new PawnControlManager(this.boardScreen, this.gameManager);
        this.gameManager.setPawnControlManager(this.pawnControlManager);
        
        PawnControlContainer pawnControlContainer = new PawnControlContainer(pawnControlManager, player1Color);
        pawnControlContainer.setBounds(PAWN_CONTROL_CONTAINER_BOUNDS);
        pawnControlContainer.setBackground(new Color(38, 24, 16));
        pawnControlArea.add(pawnControlContainer);

        // BOTÃO DE MENU POSICIONADO ABAIXO DO CARROSSEL
        CustomButton btnMenu = new CustomButton("MENU");
        btnMenu.setBounds(MENU_BUTTON_BOUNDS);
        btnMenu.addActionListener(e -> abrirMenuPausa());
        pawnControlArea.add(btnMenu);

        cardsContainer.revalidate();
        cardsContainer.repaint();
        cardsArea.revalidate();
        pawnControlArea.repaint();
        pawnControlArea.revalidate();
        cardsArea.repaint();
        this.statusBar.revalidate();
        this.statusBar.repaint();   
        this.boardScreen.revalidate();
        this.boardScreen.repaint();
        boardWithFrame.revalidate();
        boardWithFrame.repaint();
    
        add(cardsArea);
        add(boardWithFrame);
        add(pawnControlArea);

        configurarListenersDePeao(0);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();
                int cut = 10;

                Polygon polygon = new Polygon();
                polygon.addPoint(0, 0);
                polygon.addPoint(w - cut, 0);
                polygon.addPoint(w, cut);
                polygon.addPoint(w, h);
                polygon.addPoint(0, h);

                Color bg = getModel().isPressed() ? new Color(200, 200, 200) : 
                          (getModel().isRollover() ? new Color(255, 255, 255) : new Color(240, 240, 240));
                g2.setColor(bg);
                g2.fillPolygon(polygon);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(new Color(38, 24, 16));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }

    private void abrirMenuPausa() {
        InGameMenuDialog menuDialog = new InGameMenuDialog(this, this.windowManager);
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
        return GAME_CONTAINER_BOUNDS;
    }
}