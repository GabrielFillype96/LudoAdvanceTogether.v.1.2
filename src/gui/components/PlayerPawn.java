// Classe responsável por criar os peões
// Packages
package gui.components;

// Imports externos
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;

public class PlayerPawn {
    private String nomeJogador;
    private int posicaoLogicaAtual; // Índice da casa de 0 até o fim do circuito
    private Point coordenadaVisual; // X e Y atuais na tela
    private Image imagemPeao;       // Guarda o arquivo de imagem do peão

    /**
     * Construtor do Peão usando Imagem.
     * @param nomeJogador Nome do jogador dono deste peão.
     * @param nomeArquivoImagem Nome do arquivo dentro de /assets/ (Ex: "peao_vermelho.png")
     */
    public PlayerPawn(String nomeJogador, String nomeArquivoImagem) {
        this.nomeJogador = nomeJogador;
        this.posicaoLogicaAtual = 0;
        this.coordenadaVisual = new Point(0, 0); // Começa na origem ou posição inicial
        
        carregarImagem(nomeArquivoImagem);
    }

    /**
     * Carrega a imagem do peão a partir da pasta de recursos (assets).
     */
    private void carregarImagem(String nomeArquivoImagem) {
        try {
            java.net.URL imgURL = getClass().getResource("/assets/" + nomeArquivoImagem);
            if (imgURL != null) {
                this.imagemPeao = new ImageIcon(imgURL).getImage();
            } else {
                System.err.println("[Peao] Erro: Imagem do peão não encontrada em assets: " + nomeArquivoImagem);
            }
        } catch (Exception e) {
            System.err.println("[Peao] Falha crítica ao carregar imagem do peão: " + e.getMessage());
        }
    }

    /**
     * Desenha a imagem artística do peão centralizada na casa do tabuleiro.
     */
    public void desenhar(Graphics2D g2) {
        if (imagemPeao == null) return;

        // Ativa suavização para o redimensionamento da imagem ficar bonito
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = coordenadaVisual.x;
        int y = coordenadaVisual.y;
        
        // Como as casas do seu BoardScreen medem 40x40 pixels:
        int larguraPeao = 26; 
        int alturaPeao = 32;  // Um pouco mais alto para dar efeito de peça em pé
        
        // Centraliza horizontalmente o peão dentro do quadrado de 40px
        int xCentralizado = x + ((40 - larguraPeao) / 2);
        // Posiciona verticalmente (deixando uma pequena margem na base da casa)
        int yCentralizado = y + ((40 - alturaPeao) / 2);

        // Desenha a imagem do peão na tela
        g2.drawImage(imagemPeao, xCentralizado, yCentralizado, larguraPeao, alturaPeao, null);
    }

    // --- GETTERS E SETTERS ---
    public int getPosicaoLogicaAtual() { 
        return posicaoLogicaAtual; 
    }
    
    public void setPosicaoLogicaAtual(int posicao) { 
        this.posicaoLogicaAtual = posicao; 
    }

    public Point getCoordenadaVisual() { 
        return coordenadaVisual; 
    }
    
    public void setCoordenadaVisual(Point coordenada) { 
        this.coordenadaVisual = coordenada; 
    }
    
    public String getNomeJogador() { 
        return nomeJogador; 
    }
}