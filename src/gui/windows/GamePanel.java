package gui.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GamePanel extends JPanel {

    private String playerName;
    private String cpuDifficulty;

    public GamePanel(String playerName, String cpuDifficulty) {
        this.playerName = playerName;
        this.cpuDifficulty = cpuDifficulty;

        // Ocupa o tamanho total da janela (900x600)
        setBounds(0, 0, 900, 600);
        setLayout(null);
        setOpaque(false);

        // --- PAINEL LATERAL DIREITO (Espaço restante: 300x600) ---
        // Começa na posição X=600 (onde o tabuleiro termina) e vai até o fim da tela
        JPanel boardPanel = new JPanel();
        boardPanel.setBounds(600, 0, 300, 600);
        boardPanel.setBackground(new Color(38, 24, 16)); // Tom amadeirado escuro para combinar com o menu
        boardPanel.setLayout(null);

        // 1. INSTANCIA O SEU CARDSPANEL
        gui.windows.CardsPanel painelCartas = new gui.windows.CardsPanel();
        
        // Centraliza o painel de largura 220 no menu de largura 300
        painelCartas.setBounds((300 - 220) / 2, 25, 220, 340);
        boardPanel.add(painelCartas);

        // 2. BUSCA A PRIMEIRA CARTA DA LISTA DO SEU CARDMANAGER
        Cards.CustomCards cartaAtual = Cards.CardManager.criarCartasFaceis().get(0);
        
        // Configura o tamanho dela e adiciona no painel
        cartaAtual.setBounds(0, 0, 220, 340);
        painelCartas.add(cartaAtual);

        // 3. FORÇA A ATUALIZAÇÃO IMEDIATA DO FLUXO GRÁFICO
        painelCartas.revalidate();
        painelCartas.repaint();
        boardPanel.revalidate();
        boardPanel.repaint();

        add(boardPanel);
    }

    // Método responsável por desenhar a arte do tabuleiro no lado esquerdo
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        try {
            // Busca a imagem do tabuleiro no seu pacote de assets
            // CERTIFIQUE-SE DE QUE O NOME DO ARQUIVO ESTÁ CORRETO AQUI:
            ImageIcon boardIcon = new ImageIcon(GamePanel.class.getResource("/assets/gameBackground_600x600.jpg"));
            Image boardImage = boardIcon.getImage();

            // Desenha o tabuleiro perfeitamente quadrado (600x600) colado no canto esquerdo (0,0)
            g.drawImage(boardImage, 0, 0, 600, 600, this);

        } catch (Exception e) {
            // Fallback: Se a imagem sumir ou der erro, desenha um quadrado verde com borda para não travar o teste
            System.err.println("Erro ao carregar a imagem do tabuleiro: " + e.getMessage());
            
            g.setColor(new Color(20, 53, 36)); // feltro verde
            g.fillRect(0, 0, 600, 600);
            
            g.setColor(new Color(222, 179, 102)); // borda dourada
            g.drawRect(5, 5, 590, 590);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString("[Arte do Tabuleiro Oculta ou Erro no Link]", 130, 300);
        }
    }
}