// Classe responsável por criar os peões
// Packages
package gui.components;

//Imports internos
import control.ImageLoaderManager;

// Imports externos

import java.awt.Point;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

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
            // Se a imagem carregada não for nula, então insere ela
            // Aplica a imagem redimensionada ao "JLabel"
            this.setIcon(boardPawnIcon);
            // Define o tamanho da imagem 
            this.setSize(
                boardPawnWidth, 
                boardPawnHeight
            );
        } else {
            this.setSize( // Tamanho de segurança caso a imagem falhe
                (int) (20 * SCALE),
                (int) (25 * SCALE)
            ); 
        }
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        this.isMoving = moving;
    }

    // Método para inciar a animação de pulo do peão
    public void startBoardPawnShake() {
        // Trave de segurança - se o peão estiver se movendo não permite que ele "pule"
        if (isMoving) return;

        // Se já estiver pulando, não faz nada para evitar sobreposição de Timers
        if (shakeTimer != null && shakeTimer.isRunning()) return; 

        // Guarda a posição Y atual (o local onde o peão está) antes de começar a saltar
        this.originalY = this.getY(); // "getY()" é um método nativo do Swing 

        // Cria uma classe anônima "hoverTimer" que "escuta" quando o mouse está sobre o peão para executar a ação de pular
        // Cria um timer que roda a cada 150 milissegundos
        shakeTimer = new javax.swing.Timer(150, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int currentX = PlayerPawn.this.getX();// "getX()" é um método nativo do Swing 
                
                // Se o peão está se movendo impede que impede que "pule"
                if (isMoving) {
                    stopBoardPawnShake();
                    return;
                }

                if (isJumpingUp) {
                    // Se o peão deve "pular" então sobe 8 pixeis
                    PlayerPawn.this.setLocation(currentX, originalY - 8); 
                } else {
                    // Volta o peão para a posição original
                    PlayerPawn.this.setLocation(currentX, originalY); 
                }
                
                // Inverte a direção para o próximo ciclo
                isJumpingUp = !isJumpingUp;
            }
        });
        shakeTimer.start(); // Liga o motor do salto
    }

    // Método para o peão parar de "pular"
    public void stopBoardPawnShake() {
        if (shakeTimer != null) {
            // Se o mouse não está em cima de peão, então para de "pular"
            shakeTimer.stop();
            // Volta o peão para posição original
            PlayerPawn.this.setLocation(getX(), originalY);
        }
    }

    // --- GETTERS E SETTERS ---
    public int getPawnCurrentPos() { 
        return pawnCurrentPos; 
    }
    
    public void setPawnCurrentPos(int pawnPosition) { 
        this.pawnCurrentPos = pawnPosition; 
    }
    
    public void setPawnVisualCoordinates(Point visualCoordinates) { 
        if (visualCoordinates != null) {
            // Cria um ponto totalmente novo na memória com os mesmos valores de X e Y
            this.setLocation(visualCoordinates.x, visualCoordinates.y);
            this.originalY = visualCoordinates.y;
        }
    }

    public boolean isJumpingUp() {
        return isJumpingUp;
    }
    
    public String getPlayerName() { 
        return playerName; 
    }
}