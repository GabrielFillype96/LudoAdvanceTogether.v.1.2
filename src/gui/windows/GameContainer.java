// Classe responsável por construir o painel principal do jogo, onde o tabuleiro e as cartas serão exibidos

// Package
package gui.windows;

// Import externos
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GameContainer extends JPanel {

    private String playerName;
    private String cpuDifficulty;
    private Image boardImg; // Imagem do tabuleiro para mostrar atrás dos componentes
    private final static String boardImgURL = "/assets/gameBackground_600x600.jpg"; // Caminho da imagem do tabuleiro
    private final static Rectangle GAME_CONTAINER_BOUNDS = new Rectangle(0, 0, 900, 600);

    public GameContainer(String playerName, String cpuDifficulty) {
        this.playerName = playerName;
        this.cpuDifficulty = cpuDifficulty;

        // Ocupa o tamanho total da janela (900x600)
        // Dimensões fixas para o painel dos cards
        setBounds(GAME_CONTAINER_BOUNDS);
        setLayout(null); // Layout nulo torna o painel absoluto 
        setOpaque(false); // Fundo transparente para permitir a exibição do deck

         // Procura o path da imagem de fundo do deck
        java.net.URL boardImgPath = getClass().getResource(boardImgURL);
        if (boardImgPath != null) {
            System.out.println(
                "[GameContainer] Imagem do tabuleiro encontrada em: " + boardImgURL
            );
            this.boardImg = new ImageIcon(boardImgPath).getImage();
        } else {
            System.err.println(
                "[GameContainer] Erro: Imagem do tabuleiro não encontrada em /assets/images/gameBackground_600x600.jpg"
            );
        }

        // --- PAINEL LATERAL DIREITO (Espaço restante: 300x600) ---
        JPanel boardPanel = new JPanel();
        boardPanel.setBounds(600, 0, 300, 600);
        boardPanel.setBackground(new Color(38, 24, 16)); // Tom amadeirado escuro
        boardPanel.setLayout(null);

        // 1. INSTANCIA O SEU CARDSPANEL PASSANDO O FILTRO DIRETAMENTE NO CONSTRUTOR
        // Ele mesmo vai carregar o JSON e inicializar a primeira carta.
        gui.windows.CardsPanel painelCartas = new gui.windows.CardsPanel("FÁCIL");
        
        // Centraliza o painel de largura 220 dentro do menu lateral de largura 300
        painelCartas.setBounds(40, 110, 220, 340);
        boardPanel.add(painelCartas);

        // --- LABELS DE INFORMAÇÕES ADICIONAIS DO PAINEL LATERAL ---
        JLabel lblPlayer = new JLabel("Jogador: " + this.playerName);
        lblPlayer.setFont(new Font("Arial", Font.BOLD, 16));
        lblPlayer.setForeground(new Color(222, 179, 102)); // Dourado fosco
        lblPlayer.setBounds(20, 30, 260, 25);
        boardPanel.add(lblPlayer);

        JLabel lblDiff = new JLabel("Dificuldade CPU: " + this.cpuDifficulty);
        lblDiff.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDiff.setForeground(Color.LIGHT_GRAY);
        lblDiff.setBounds(20, 60, 260, 20);
        boardPanel.add(lblDiff);

        // Força a atualização do fluxo gráfico do Swing
        painelCartas.revalidate();
        painelCartas.repaint();
        boardPanel.revalidate();
        boardPanel.repaint();

        add(boardPanel);

        JPanel boardScreen = new BoardScreen("Gabriel", "Azul", "Fernanda", "Roxo", "Raimunda", "Rosa", "Paulo", "Amarelo");
        boardScreen.setBounds(0, 0, 600, 600);
        add(boardScreen);
    }



    public static Rectangle getGameContainerBounds() {
        return GAME_CONTAINER_BOUNDS;
    }
}