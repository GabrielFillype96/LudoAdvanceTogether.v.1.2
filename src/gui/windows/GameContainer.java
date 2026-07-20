// Classe responsável por construir o painel principal do jogo, onde o tabuleiro e as cartas serão exibidos

// Package
package gui.windows;

// Imports internos
import control.CPUIManager;
import control.DeckManager;
import control.GameManager;
import control.PawnControlManager;
import control.TurnManager;
import gui.components.PlayerPawn;
import gui.components.GameStatusBar; 
import gui.events.BoardPawnMouseListener;
import network.GameClient; // IMPORT DE REDE ADICIONADO

// Imports externos
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.JPanel;

import actions.CardAnswerValidation;

public class GameContainer extends JPanel {
    // VARIÁVEIS DE INSTÂNCIA
    private String playerName;
    private String cpuDifficulty;
    private PawnControlManager pawnControlManager;
    private DeckManager deckManager;
    private BoardScreen boardScreen;
    private GameStatusBar statusBar; 
    private static final double SCALE = 1.5;

    // ATRIBUTOS DE REDE / MULTIPLAYER
    private WindowManager windowManager;
    private GameClient client;
    private int myPlayerId;
    private boolean[] slotIsCPU;
    
    // =========================================================================
    // DIMENSÕES E POSICIONAMENTOS RECALCULADOS PARA ELIMINAR O ESPAÇO VAZIO
    // =========================================================================
    private static final Rectangle GAME_CONTAINER_BOUNDS = new Rectangle(
        0, 
        0, 
        (int) (980 * SCALE), 
        (int) (680 * SCALE)
    );
    
    // A moldura ocupa de 0 a 680 de largura e altura
    private static final Rectangle BOARD_FRAME_BOUNDS = new Rectangle(
        0,
        0,
        (int) (680 * SCALE),
        (int) (680 * SCALE)
    );

    // Área das cartas ajustada para 390px de altura útil
    private static final Rectangle CARDS_AREA_BOUNDS = new Rectangle(
        (int) (680 * SCALE), 
        0, 
        (int) (300 * SCALE), 
        (int) (400 * SCALE) 
    );
    
    // Container das cartas acompanhando a nova altura de 390px
    private static final Rectangle CARDS_CONTAINER_BOUNDS = new Rectangle(
        12, 
        0, 
        (int) (300 * SCALE), 
        (int) (400 * SCALE)
    );
    
    // Área dos peões
    private static final Rectangle PAWN_CONTROL_AREA_BOUNDS = new Rectangle(
        (int) (680 * SCALE),
        (int) (400 * SCALE), 
        (int) (300 * SCALE), 
        (int) (290 * SCALE)
    );
    
    // Posicionamento interno aproximado com margens limpas
    private static final Rectangle STATUS_BAR_BOUNDS = new Rectangle(
        (int) (40 * SCALE),
        (int) (8 * SCALE),
        (int) (220 * SCALE),
        (int) (46 * SCALE)  
    );
    private static final Rectangle PAWN_CONTROL_CONTAINER_BOUNDS = new Rectangle(
        (int) (40 * SCALE),
        (int) (62 * SCALE),
        (int) (220 * SCALE),
        (int) (120 * SCALE)
    );

    // =========================================================================
    // NOVO CONSTRUTOR MULTIPLAYER (GERENCIA A REDE E CHAMA O CONSTRUTOR PADRÃO)
    // =========================================================================
    public GameContainer(
        WindowManager windowManager, 
        GameClient client, 
        int myPlayerId, 
        boolean[] slotIsCPU, 
        String cpuDifficulty
    ) {
        // Encadeia a inicialização utilizando as informações tratadas para cada vaga
        this(
            "Jogador 1" + (slotIsCPU != null && slotIsCPU.length > 0 && slotIsCPU[0] ? " (CPU)" : ""), "azul",
            "Jogador 2" + (slotIsCPU != null && slotIsCPU.length > 1 && slotIsCPU[1] ? " (CPU)" : ""), "roxo",
            "Jogador 3" + (slotIsCPU != null && slotIsCPU.length > 2 && slotIsCPU[2] ? " (CPU)" : ""), "rosa",
            "Jogador 4" + (slotIsCPU != null && slotIsCPU.length > 3 && slotIsCPU[3] ? " (CPU)" : ""), "amarelo",
            cpuDifficulty
        );

        this.windowManager = windowManager;
        this.client = client;
        this.myPlayerId = myPlayerId;
        this.slotIsCPU = slotIsCPU;
    }

    // =========================================================================
    // CONSTRUTOR PADRÃO (OFFLINE / INICIALIZAÇÃO COMPLETA DA INTERFACE)
    // =========================================================================
    public GameContainer(
        String player1Name, String player1Color, 
        String player2Name, String player2Color, 
        String player3Name, String player3Color, 
        String player4Name, String player4Color, 
        String cpuDifficulty
    ) {
        this.playerName = player1Name;
        this.cpuDifficulty = cpuDifficulty;

        setBounds(GAME_CONTAINER_BOUNDS);
        setLayout(null);
        setOpaque(false);

        // Instancia a BoardScreen mantendo-a focada apenas na exibição visual
        this.boardScreen = new BoardScreen(
            player1Name, player1Color, 
            player2Name, player2Color, 
            player3Name, player3Color, 
            player4Name, player4Color
        );
        
        // INTEGRAÇÃO DA MOLDURA: Encapsula o tabuleiro dentro do painel com moldura
        BoardWithFrame boardWithFrame = new BoardWithFrame(this.boardScreen, SCALE);
        boardWithFrame.setBounds(BOARD_FRAME_BOUNDS);

        // Instancia o manager global do jogo
        GameManager gameManager = new GameManager(this.boardScreen);
        
        CPUIManager cpuManager = new CPUIManager(gameManager);
        gameManager.setCPUIManager(cpuManager);

        TurnManager turnManager = new TurnManager(gameManager);
        gameManager.setTurnManager(turnManager);

        cards.CardManager cardManager = new cards.CardManager();
        this.deckManager = new DeckManager(cardManager);
        this.deckManager.initializeDecks();

        cpuManager.setDeckManager(this.deckManager);
        turnManager.setCPUIManager(cpuManager);

        // Área reservada ao layout das cartas, controles e informações de jogador
        JPanel cardsArea = new JPanel();
        cardsArea.setBounds(CARDS_AREA_BOUNDS);
        cardsArea.setBackground(new Color(255, 0, 16));
        cardsArea.setLayout(null);

        // Instancia a validação das respostas através do clique do jogador
        CardAnswerValidation cardAnswerValidation = new CardAnswerValidation(gameManager);

        // Instancia o "CardsContainer"
        CardsContainer cardsContainer = new CardsContainer(gameManager, cardAnswerValidation);
        cardsContainer.setBounds(CARDS_CONTAINER_BOUNDS);
        cardsContainer.setBackground(new Color(0, 255, 16));
        cardsArea.add(cardsContainer);

        cardsContainer.setDeckManager(this.deckManager);
        cardsContainer.setTurnManager(turnManager);

        // Área reservada ao layout do controle dos peões
        JPanel pawnControlArea = new JPanel();
        pawnControlArea.setBounds(PAWN_CONTROL_AREA_BOUNDS);
        pawnControlArea.setBackground(new Color(38, 24, 16));
        pawnControlArea.setLayout(null);

        // Instancia e adiciona a barra de status no painel de controle dos peões
        this.statusBar = new GameStatusBar(SCALE);
        this.statusBar.setBounds(STATUS_BAR_BOUNDS);
        turnManager.sortearPrimeiroJogador();
        
        gameManager.setGameStatusBar(statusBar);
        pawnControlArea.add(this.statusBar);

        // Instancia o "PawnControlManager"
        this.pawnControlManager = new PawnControlManager(this.boardScreen, gameManager);

        // Faz o GameManager "conhecer" o PawnControlManager
        gameManager.setPawnControlManager(this.pawnControlManager);
        
        // Instancia o "PawnControlContainer"
        PawnControlContainer pawnControlContainer = new PawnControlContainer(pawnControlManager, player1Color);
        pawnControlContainer.setBounds(PAWN_CONTROL_CONTAINER_BOUNDS);
        pawnControlContainer.setBackground(new Color(38, 24, 16));
        pawnControlArea.add(pawnControlContainer);
        
        // Força a atualização do fluxo gráfico do Swing
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
    
        // Adiciona os componentes ao container do jogo
        add(cardsArea);
        add(boardWithFrame);
        add(pawnControlArea);

        // Configura os ouvintes dos peões
        for (int i = 0; i < 4; i++) {
            PlayerPawn playerPawn = boardScreen.getPlayerPawn(0, i);
            if (playerPawn != null) {
                playerPawn.addMouseListener(new BoardPawnMouseListener(pawnControlManager, i));
            }
        }   
    }

    // Método getter para acessar as dimensões do GameContainer
    public static Rectangle getGameContainerBounds() {
        return GAME_CONTAINER_BOUNDS;
    }
}