// Classe responsável por construir o painel principal do jogo, onde o tabuleiro e as cartas serão exibidos

// Package
package gui.windows;

import control.CPUIManager;
import control.DeckManager;
import control.GameManager;
// Imports internos
import control.PawnControlManager;
import control.TurnManager;
import gui.components.PlayerPawn;
import gui.components.GameStatusBar; 
import gui.events.BoardPawnMouseListener;

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
    
    // CORREÇÃO: Área dos peões subiu de Y=450 para Y=390, colando logo abaixo das cartas
    private static final Rectangle PAWN_CONTROL_AREA_BOUNDS = new Rectangle(
        (int) (680 * SCALE),
        (int) (400 * SCALE), 
        (int) (300 * SCALE), 
        (int) (290 * SCALE)  // Altura expandida para preencher o restante do fundo (680 - 390)
    );
    
    // Posicionamento interno aproximado com margens limpas
    private static final Rectangle STATUS_BAR_BOUNDS = new Rectangle(
        (int) (40 * SCALE),
        (int) (8 * SCALE),   // Margem superior justa para colar próximo às cartas
        (int) (220 * SCALE),
        (int) (46 * SCALE)  
    );
    private static final Rectangle PAWN_CONTROL_CONTAINER_BOUNDS = new Rectangle(
        (int) (40 * SCALE),
        (int) (62 * SCALE),  // Segue o fluxo logo abaixo da nova barra de status
        (int) (220 * SCALE),
        (int) (120 * SCALE)
    );
    

    // MODIFICADO: Construtor atualizado para aceitar a configuração dinâmica vinda do menu
    public GameContainer(
        String player1Name, String player1Color, 
        String player2Name, String player2Color, 
        String player3Name, String player3Color, 
        String player4Name, String player4Color, 
        String cpuDifficulty
    ) {
        this.playerName = player1Name;
        this.cpuDifficulty = cpuDifficulty;

        // Dimensões fixas para o painel dos cards
        setBounds(GAME_CONTAINER_BOUNDS); // Tamanho do container do jogo (1470x1020)
        setLayout(null); // Layout nulo torna o painel absoluto
        setOpaque(false); // Fundo transparente para permitir a exibição do deck

        // MODIFICADO: Agora instancia a BoardScreen usando as strings reais enviadas do menu
        this.boardScreen = new BoardScreen(
            player1Name, player1Color, 
            player2Name, player2Color, 
            player3Name, player3Color, 
            player4Name, player4Color
        );
        
        // =========================================================================
        // INTEGRAÇÃO DA MOLDURA: Encapsula o tabuleiro dentro do painel com moldura
        // =========================================================================
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
        cardsArea.setBackground(new Color(255, 0, 16)); // Tom amadeirado escuro
        cardsArea.setLayout(null);

        // Instancia a validação das respostas através do clique do jogador
        CardAnswerValidation cardAnswerValidation = new CardAnswerValidation(gameManager);

        // Instancia o "CardsContainer" que será responsável por conter e renderizar as cartas do jogo
        CardsContainer cardsContainer = new CardsContainer(gameManager, cardAnswerValidation);
        cardsContainer.setBounds(CARDS_CONTAINER_BOUNDS);
        cardsContainer.setBackground(new Color(0, 255, 16));
        cardsArea.add(cardsContainer);

        cardsContainer.setDeckManager(this.deckManager);
        cardsContainer.setTurnManager(turnManager);

        // Área reservada ao layout do controle dos peões
        JPanel pawnControlArea = new JPanel();
        pawnControlArea.setBounds(PAWN_CONTROL_AREA_BOUNDS);
        pawnControlArea.setBackground(new Color(38, 24, 16)); // Tom amadeirado escuro
        pawnControlArea.setLayout(null);

        // Instancia e adiciona a barra de status no painel de controle dos peões
        this.statusBar = new GameStatusBar(SCALE);
        this.statusBar.setBounds(STATUS_BAR_BOUNDS);
        turnManager.sortearPrimeiroJogador();
        
        gameManager.setGameStatusBar(statusBar);
        pawnControlArea.add(this.statusBar);

        // Instancia o "PawnControlManager" para servir de parâmetro na instância do "PawnControlContainer"
        this.pawnControlManager = new PawnControlManager(this.boardScreen, gameManager);

        // Faz o GameManager "conhecer" o PawnControlManager
        gameManager.setPawnControlManager(this.pawnControlManager);
        
        
        // Instancia o "PawnControlContainer" que será responsável por conter e renderizar os peões de controle do jogo
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
        add(boardWithFrame); // Adiciona a moldura completa (que já carrega o tabuleiro dentro)
        add(pawnControlArea);

        /*
         * O GameContainer atua como orquestrador para unir a interface visual e as regras de jogo.
         * Como a classe "BoardScreen" é puramente visual, ela não deve gerenciar dependências. 
         * Portanto, extraímos cada peão do tabuleiro e injetamos o "PawnControlManager" diretamente 
         * nos seus respectivos ouvintes de evento (BoardPawnMouseListener). 
         * Isso elimina dependências circulares (antigo setter) e habilita a funcionalidade wobble perfeitamente.
         */
        for (int i = 0; i < 4; i++) {
            PlayerPawn playerPawn = boardScreen.getPlayerPawn(0, i);
            if (playerPawn != null) {
                playerPawn.addMouseListener(new BoardPawnMouseListener(pawnControlManager, i));
            }
        }   
    }

    // Método getter para acessar as dimensões do GameContainer
    public static Rectangle getGameContainerBounds() {
        return GAME_CONTAINER_BOUNDS; // Retorna as dimensões do GameContainer
    }
}