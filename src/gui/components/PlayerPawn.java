// Classe responsável por criar os peões do tabuleiro do jogador

// Packages
package gui.components;

//Imports internos
import control.ImageLoaderManager;
import gui.events.ShakeListener;

// Imports externos
import java.awt.Point;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;

public class PlayerPawn extends JLabel {
    // VARIÁVEIS DE INSTÂNCIA
    private javax.swing.Timer shakeTimer;
    private boolean isJumpingUp = true; // Controla se o próximo movimento é para cima ou para baixo
    private int originalY; // Guarda a posição original para o peão não se perder no ar
    private String playerName;
    private int pawnCurrentPos; // Índice da casa de 0 até o fim do circuito
    private boolean isMoving = false;
    private static final double SCALE = 1.5;

    
    /**
     * Construtor do peão no tabuleiro
     * @param playerName Nome do jogador dono deste peão.
     * @param pawnImg Nome do arquivo dentro de /assets/ (Ex: "bluePawnImg_90x90.png")
     */
    public PlayerPawn(String playerName, String pawnImgPath) {
        this.playerName = playerName;
        this.pawnCurrentPos = 0;

        // Define a largura e altura que o peão deve ter no tabuleiro, já que ao utilizar o "setSize" o Java lê o tamanho do JLabel e não diminui a imagem dentro em si
        int boardPawnWidth = (int) (12 * SCALE);
        int boardPawnHeight = (int) (17 * SCALE);
        
        // Chama o método na classe "ImageLoaderManager" para redimensionar a imagem
        ImageIcon boardPawnIcon = ImageLoaderManager.loadIcon(
            pawnImgPath, 
            boardPawnWidth, 
            boardPawnHeight
        );

        // Poderia ser utilizado o método "try/catch" para tratamento de erros, mas o if/else é mais sútil e simples
        if (boardPawnIcon != null) {
            this.setIcon(boardPawnIcon);
            
            // O JLabel agora tem o tamanho exato de uma casa do tabuleiro (40 * 1.5 = 60)
            int tileSize = (int) (40 * SCALE);
            this.setSize(tileSize, tileSize);
            
            // Pede ao Java para centralizar a imagem perfeitamente dentro desse quadrado
            this.setHorizontalAlignment(JLabel.CENTER);
            this.setVerticalAlignment(JLabel.CENTER);
        } else {
            // Tamanho de segurança caso a imagem falhe
            int tileSize = (int) (40 * SCALE);
            this.setSize(tileSize, tileSize); 
        }

        // Instancia um novo objeto da classe "ShakeListener" para que possa ser utilizada a funcionalidade shake
        ShakeListener shakeListener = new ShakeListener(this); // "this" passa o próprio peão
        this.shakeTimer = new Timer(150, shakeListener); // Cria um timer que roda a cada 150 milissegundos
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        this.isMoving = moving;
    }

    /* 
    * Método para que o peão do tabuleiro do jogador execute a funcionalidade shake (pular). Este método apenas dá o "play" no timer para que a funcionalidade inicie. A construção e operacionalidade do shake foi construída na classe "ShakeListener"
    */ 
    public void startBoardPawnShake() {
        // Trave de segurança - se o peão estiver se movendo não permite que ele "pule"
        if (isMoving) return;

        // Se já estiver pulando, não faz nada para evitar sobreposição de Timers
        if (shakeTimer != null && shakeTimer.isRunning()) return; 

        // Liga o motor do salto
        shakeTimer.start(); 
    }

    /**
     ** Método para que o peão de referência pare de executar a funcionalidade shake (pular). Este método apenas dá o "stop" no timer para que a funcionalidade pare. A construção e operacionalidade do shake foi construída na classe "ShakeListener"
    */
    public void stopBoardPawnShake() {
        if (shakeTimer != null) {
            // Se o mouse não está em cima de peão, então para de "pular"
            shakeTimer.stop();
            // Volta o peão para posição original
            PlayerPawn.this.setLocation(getX(), originalY);
        }
    }

    // Métodos Getters and Setters
    // Método getter para que outras classes consigam acessar a variável privada "getPawnCurrentPos" e pegar o seu valor
    public int getPawnCurrentPos() { 
        return pawnCurrentPos; 
    }
    
    // Método setter para que outras classes consigam acessar a variável privada "setPawnCurrentPos" e modifiquem seu valor
    public void setPawnCurrentPos(int pawnPosition) { 
        this.pawnCurrentPos = pawnPosition; 
    }
    
    // Método setter para que outras classes consigam acessar a variável privada "setPawnVisualCoordinates" e modifiquem seu valor
    public void setPawnVisualCoordinates(Point tileTopLeft) { 
        if (tileTopLeft != null) {
            // O tamanho padrão de uma casa do seu tabuleiro (40 * 1.5 = 60 pixels)
            int tileSize = (int) (40 * SCALE); 
            
            // Calcula o centro da casa e subtrai metade do tamanho do peão para alinhar o meio
            int centeredX = tileTopLeft.x + (tileSize / 2) - (this.getWidth() / 2);
            int centeredY = tileTopLeft.y + (tileSize / 2) - (this.getHeight() / 2);
            
            this.setLocation(centeredX, centeredY);
            this.originalY = centeredY;
        }
    }

    // Método getter para que outras classes consigam acessar a variável privada "getOriginalY" e pegar o seu valor
    public int getOriginalY() {
        return originalY;
    }

    // Método getter para que outras classes consigam acessar a variável privada "isJumpingUp" e pegar o seu valor
    public boolean isJumpingUp() {
        return isJumpingUp;
    }

    // Método setter para que outras classes consigam acessar a variável privada "isJumpingUp" e modifiquem seu valor
    public void setJumpingUp(boolean jumpingUp) {
        this.isJumpingUp = jumpingUp;
    }
    
    // Método getter para que outras classes consigam acessar a variável privada "getPlayerName" e pegar o seu valor
    public String getPlayerName() { 
        return playerName; 
    }

    /**
     * Método que recupera a imagem do peão para ser usada na pré-visualização (fantasma)
     * @return A imagem (java.awt.Image) do peão, ou null se não houver imagem
     */
    public java.awt.Image getPawnImage() {
        javax.swing.Icon currentIcon = this.getIcon();
        
        // Verifica se o ícone atual é uma instância de ImageIcon (que é o que usamos no construtor)
        if (currentIcon instanceof javax.swing.ImageIcon) {
            return ((javax.swing.ImageIcon) currentIcon).getImage();
        }
        
        return null;
    }
}