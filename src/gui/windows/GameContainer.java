// Classe responsável por construir o painel principal do jogo, onde o tabuleiro e as cartas serão exibidos

// Package
package gui.windows;

// Import externos
import java.awt.Color;
import java.awt.Rectangle;
import javax.swing.JPanel;

public class GameContainer extends JPanel {

    // VARIÁVEIS DE INSTÂNCIA
    private String playerName;
    private String cpuDifficulty;
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
        (int) (600 * SCALE)
    );
    private static final Rectangle CARDS_CONTAINER_BOUNDS = new Rectangle(
        (int) (40 * SCALE), 
        (int) (110 * SCALE), 
        (int) (220 * SCALE), 
        (int) (340 * SCALE)
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

        // Área reservada ao layout das cartas, controles e informações de jogador (450x900)
        JPanel cardsArea = new JPanel(); // Instancia o painel completo que irá conter as cartas, controles e informações do jogador
        cardsArea.setBounds(CARDS_AREA_BOUNDS); // Tamanho e posição da área das cartas (900, 0) (450x900)
        cardsArea.setBackground(new Color(38, 24, 16)); // Tom amadeirado escuro
        cardsArea.setLayout(null);

        // Instancia o "CardsContainer" que será responsável por conter e renderizar as cartas do jogo
        // Ele mesmo vai carregar o JSON e inicializar a primeira carta.
        gui.windows.CardsContainer cardsContainer = new gui.windows.CardsContainer("FÁCIL");
        cardsContainer.setBounds(CARDS_CONTAINER_BOUNDS); // Tamanho e posição do container das cartas (60, 165) (330x510)
        cardsArea.add(cardsContainer);

        // Força a atualização do fluxo gráfico do Swing
        cardsContainer.revalidate();
        cardsContainer.repaint();
        cardsArea.revalidate();
        cardsArea.repaint();
        
        // Instancia o painel do tabuleiro
        JPanel boardScreen = new BoardScreen("Gabriel", "Azul", "Fernanda", "Roxo", "Raimunda", "Rosa", "Paulo", "Amarelo");
        boardScreen.setBounds(BOARD_SCREEN_BOUNDS); // Tamanho e posição do tabuleiro (0, 0) (900x900)
        
        // Adiciona os componentes ao container do jogo
        add(cardsArea);
        add(boardScreen);
    }

    // Método getter para acessar as dimensões do GameContainer
    public static Rectangle getGameContainerBounds() {
        return GAME_CONTAINER_BOUNDS; // Retorna as dimensões do GameContainer
    }
}