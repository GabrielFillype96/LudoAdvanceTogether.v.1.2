// Classe responsável por construir o painel principal do jogo, onde o tabuleiro e as cartas serão exibidos
package gui.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GamePanel extends JPanel {

    private String playerName;
    private String cpuDifficulty;
    private final static String boardImageURL = "/assets/gameBackground_600x600.jpg"; // Caminho da imagem do tabuleiro
    private Graphics2D g2; // Variável para o contexto gráfico 2D, usada no paintComponent;

    public GamePanel(String playerName, String cpuDifficulty) {
        this.playerName = playerName;
        this.cpuDifficulty = cpuDifficulty;

        // Ocupa o tamanho total da janela (900x600)
        setBounds(0, 0, 900, 600);
        setLayout(null);
        setOpaque(false);

        // --- PAINEL LATERAL DIREITO (Espaço restante: 300x600) ---
        JPanel boardPanel = new JPanel();
        boardPanel.setBounds(600, 0, 300, 600);
        boardPanel.setBackground(new Color(38, 24, 16)); // Tom amadeirado escuro
        boardPanel.setLayout(null);

        // 1. INSTANCIA O SEU CARDSPANEL PASSANDO O FILTRO DIRETAMENTE NO CONSTRUTOR
        // Ele mesmo vai carregar o JSON e inicializar a primeira carta.
        gui.windows.CardsPanel painelCartas = new gui.windows.CardsPanel("SORTE");
        
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
    }

    // Sobrescreve o método de pintura para desenhar o fundo do tabuleiro
    @Override
    protected void paintComponent(Graphics g) {
         // Estrutura padrão do "paintComponent" para garantir que o fundo seja desenhado corretamente
        super.paintComponent(g);
        // Cria um contexto gráfico 2D para aplicar renderizações avançadas (como anti-aliasing)
        g2 = (Graphics2D) g.create(); 
        // Habilita o anti-aliasing para suavizar as bordas das imagens desenhadas
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    


        try {
            ImageIcon boardIcon = new ImageIcon(GamePanel.class.getResource("/assets/gameBackground_600x600.jpg"));
            Image boardImage = boardIcon.getImage();
            g.drawImage(boardImage, 0, 0, getWidth(), getHeight(), this);
            // *perguntar o pq de esse getWidth e getHeight aqui fazer a imagem esticar pra 900x600 mesmo o painel sendo 600x600

        } catch (Exception e) {
            System.err.println("Erro ao carregar a imagem do tabuleiro: " + e.getMessage());
            
            // Fallback: fundo verde caso a imagem falhe
            g.setColor(new Color(34, 139, 34));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}