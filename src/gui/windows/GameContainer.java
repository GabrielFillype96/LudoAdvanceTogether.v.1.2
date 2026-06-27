// Classe responsável por construir o painel principal do jogo, onde o tabuleiro e as cartas serão exibidos

// Package
package gui.windows;

import control.GameManager;
// Imports internos
import control.PawnControlManager;
import gui.components.PlayerPawn;
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
    private BoardScreen boardScreen;
    private static final double SCALE = 1.5;
    private static final Rectangle GAME_CONTAINER_BOUNDS = new Rectangle(
        0, 
        0, 
        (int) (900 * SCALE), 
        (int) (600 * SCALE)
    );
    private static final Rectangle CARDS_AREA_BOUNDS = new Rectangle(
        (int) (600* SCALE), 
        0, 
        (int) (300 * SCALE), 
        (int) (400 * SCALE) // anteriormente estava em 600
    );
    private static final Rectangle CARDS_CONTAINER_BOUNDS = new Rectangle(
        (int) (40 * SCALE), 
        (int) (110 * SCALE), 
        (int) (220 * SCALE), 
        (int) (340 * SCALE)
    );
    private static final Rectangle PAWN_CONTROL_AREA_BOUNDS = new Rectangle(
        (int) (600 * SCALE),
        (int) (400 * SCALE),
        (int) (300 * SCALE),
        (int) (150 * SCALE)
    );
    private static final Rectangle PAWN_CONTROL_CONTAINER_BOUNDS = new Rectangle(
        (int) (40 * SCALE),
        (int) (35 * SCALE),
        (int) (220 * SCALE),
        (int) (100 * SCALE)
    );
    private static final Rectangle BOARD_SCREEN_BOUNDS = new Rectangle(
        (int) (0 * SCALE),
        (int) (0 * SCALE),
        (int) (600 * SCALE),
        (int) (600 * SCALE)
    );
    

    // Construtor da classe "GamContainer"
    public GameContainer(String playerName, String cpuDifficulty) {
        this.playerName = playerName;
        this.cpuDifficulty = cpuDifficulty;

        // Dimensões fixas para o painel dos cards
        setBounds(GAME_CONTAINER_BOUNDS); // Tamanho do container do jogo (1350x900)
        setLayout(null); // Layout nulo torna o painel absoluto 
        setOpaque(false); // Fundo transparente para permitir a exibição do deck

        // Instancia o painel do tabuleiro
        this.boardScreen = new BoardScreen("Gabriel", "Azul", "Fernanda", "Roxo", "Raimunda", "Rosa", "Paulo", "Amarelo");
        this.boardScreen.setBounds(BOARD_SCREEN_BOUNDS); // Tamanho e posição do tabuleiro (0, 0) (900x900)

        // Instancia o manager global do jogo
        GameManager gameManager = new GameManager(this.boardScreen);

        // Área reservada ao layout das cartas, controles e informações de jogador (450x600)
        JPanel cardsArea = new JPanel(); // Instancia o painel completo que irá conter as cartas, controles e informações do jogador
        cardsArea.setBounds(CARDS_AREA_BOUNDS); // Tamanho e posição da área das cartas (900, 0) (450x600)
        cardsArea.setBackground(new Color(38, 24, 16)); // Tom amadeirado escuro
        cardsArea.setLayout(null);

        // Instancia a validação das respostas através do clique do jogador
        CardAnswerValidation cardAnswerValidation = new CardAnswerValidation(gameManager);

        // Instancia o "CardsContainer" que será responsável por conter e renderizar as cartas do jogo
        // Ele mesmo vai carregar o JSON e inicializar a primeira carta.
        CardsContainer cardsContainer = new CardsContainer(gameManager, cardAnswerValidation);
        cardsContainer.setBounds(CARDS_CONTAINER_BOUNDS); // Tamanho e posição do container das cartas (60, 165) (330x510)
        cardsArea.add(cardsContainer);

        // Área reservada ao layout do controle dos peões
        JPanel pawnControlArea = new JPanel();
        pawnControlArea.setBounds(PAWN_CONTROL_AREA_BOUNDS); // Tamanho e posição da área do controle dos peões (0, 0) (450x225)
        pawnControlArea.setBackground(new Color(18, 50, 25)); // Tom amadeirado escuro
        pawnControlArea.setLayout(null);

        // Instancia o "PawnControlManager" para servir de parâmetro na instância do "PawnControlContainer"
        this.pawnControlManager = new PawnControlManager(this.boardScreen, gameManager);

        // Faz o GameManager "conhecer" o PawnControlManager
        gameManager.setPawnControlManager(this.pawnControlManager);
        
        
        // Instancia o "PawnControlContainer" que será responsável por conter e renderizar os peões de controle do jogo
        PawnControlContainer pawnControlContainer = new PawnControlContainer(pawnControlManager);
        pawnControlContainer.setBounds(PAWN_CONTROL_CONTAINER_BOUNDS); // Tamanho e posição do container de controle dos peões (60, 22.5) (330x150)
        pawnControlContainer.setBackground(new Color(38, 24, 16));
        pawnControlArea.setLayout(null);
        pawnControlArea.add(pawnControlContainer);
        
        // Força a atualização do fluxo gráfico do Swing
        cardsContainer.revalidate();
        cardsContainer.repaint();
        cardsArea.revalidate();
        pawnControlArea.repaint();
        pawnControlArea.revalidate();
        cardsArea.repaint();
        this.boardScreen.revalidate();
        this.boardScreen.repaint();
    
        // Adiciona os componentes ao container do jogo
        add(cardsArea);
        add(this.boardScreen);
        add(pawnControlArea);

        /*
         * O GameContainer atua como orquestrador para unir a interface visual e as regras de jogo.
         * Como a classe "BoardScreen" é puramente visual, ela não deve gerenciar dependências. 
         * Portanto, extraímos cada peão do tabuleiro e injetamos o "PawnControlManager" diretamente 
         * nos seus respectivos ouvintes de evento (BoardPawnMouseListener). 
         * Isso elimina dependências circulares (antigo setter) e habilita a funcionalidade wobble perfeitamente.
         */
        for (int i = 0; i < 4; i++) {
            PlayerPawn playerPawn = boardScreen.getPlayer1Pawn(i);
            if (playerPawn != null) {
                playerPawn.addMouseListener(new BoardPawnMouseListener(pawnControlManager, i));
            }
        }   
      

        
        
        
        // Perguntar sobre classe anonima no PlayerPawn
        // Perguntar pq o método timer é diferente no PlayerPawn do ReferencePawn
        // Perguntar sobre o implements
        // Perguntar sobre o super
        // Perguntar a ordem certa do "add" entre o BoardScreen e PawnControlContainer
        // Perguntar a diferenca da classe CardAnswerValidation e a GameManager, especificamente no metodo cardResultVerification do GameManager
       
    }

    // Método getter para acessar as dimensões do GameContainer
    public static Rectangle getGameContainerBounds() {
        return GAME_CONTAINER_BOUNDS; // Retorna as dimensões do GameContainer
    }
}