// Classe responsável por criar e configurar os peões de referência que serão inseridos no PawnControlContainer

//Packages
package gui.components;

// Imports internos
import control.ImageLoaderManager;
import gui.events.WobbleListener;

//Imports externos
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


public class ReferencePawn extends JLabel {
    // VARIÁVEIS DE INSTÂNCIA
    private ImageIcon stdReferencePawnIcon; // Imagem padrão do peão para permitir
    private ImageIcon disabledReferencePawnIcon; // Imagem do peão desabilitados;
    private ImageIcon goldenReferencePawnIcon; // Imagem do peão dourado ao chegar no centro do tabuleiro
    private double actualAngle = 0;
    private boolean isInclinedToRight = true;
    private Timer wobbleTimer;
    

    /**
     * Construtor do Peão de Referência
     */
    public ReferencePawn(String stdReferencePawnImgPath, String disabledReferencePawnImgPath, String goldenReferencePawnImgPath, double scale) {
        // Define o tamanho dos peões de referência com base no JLabel criado no "PawnControlContainer"
        int referencePawnWidth = (int) (30 * scale);
        int referencePawnHeight = (int) (30 * scale);

        // Carrega e redimensiona as imagens dos peões de referência em seus respectivos estados
        this.stdReferencePawnIcon = ImageLoaderManager.loadIcon( // Peão padrão
            stdReferencePawnImgPath, 
            referencePawnWidth, 
            referencePawnHeight
        );
        this.disabledReferencePawnIcon = ImageLoaderManager.loadIcon( // Peão desabilitado
            disabledReferencePawnImgPath, 
            referencePawnWidth, 
            referencePawnHeight
        );
        this.goldenReferencePawnIcon = ImageLoaderManager.loadIcon( // Peão dourado
            goldenReferencePawnImgPath,
            referencePawnWidth, 
            referencePawnHeight
        );

        // Aplica o ícone inicial padrão
        if (this.stdReferencePawnIcon != null) {
            this.setIcon(this.stdReferencePawnIcon);
        }
        // Define o próprio tamanho do JLabel
        this.setSize(
            referencePawnWidth, 
            referencePawnHeight
        );

        // Instancia um novo objeto da classe "WobbleListener" para que possa ser utilizada a funcionalidade wobble
        WobbleListener wobbleListener = new WobbleListener(this); // "this" passa o próprio peão
        this.wobbleTimer = new Timer(50, wobbleListener);
    }


    /**
     *  Método setter com switch para controlar os estados que o peão pode assumir
     *  @param pawnState O estado visual: "NORMAL", "DESABILITADO" ou "DOURADO"
     */
    public void setVisualState(String pawnState) {
        switch (pawnState) {
            case "NORMAL":
                if (this.stdReferencePawnIcon != null) this.setIcon(this.stdReferencePawnIcon);
                break;
            case "DESABILITADO":
                if (this.disabledReferencePawnIcon != null) this.setIcon(this.disabledReferencePawnIcon);
                break;
            case "DOURADO":
                if (this.goldenReferencePawnIcon != null) this.setIcon(this.goldenReferencePawnIcon);
                break;
        }
        repaint(); // Força o peão a se redesenhar com o novo ícone
    }

    /* 
    * Método para que o peão de referência execute a funcionalidade wobble (tremer). Este método apenas dá o "play" no timer para que a funcionalidade inicie. A construção e operacionalidade do wobble foi construída na classe "WobbleListener"
    */ 
    public void startReferencePawnWobble() {
        if (wobbleTimer != null && wobbleTimer.isRunning()) return;
        wobbleTimer.start();
    }

    /* 
    * Método para que o peão de referência pare de executar a funcionalidade wobble (tremer). Este método apenas dá o "stop" no timer para que a funcionalidade pare. A construção e operacionalidade do wobble foi construída na classe "WobbleListener"
    */ 
    public void stopReferencePawnWobble() {
        if (wobbleTimer != null) {
            wobbleTimer.stop();
        }
        actualAngle = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON)
            ;
        g2.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION, 
            RenderingHints.VALUE_INTERPOLATION_BILINEAR
        );

        int centerX = this.getWidth() / 2;
        int centerY = this.getHeight() / 2;

        ImageIcon currentIcon = (ImageIcon) this.getIcon();
        if (currentIcon != null) {
        // Aplica a rotação da animação
        g2.rotate(Math.toRadians(actualAngle), centerX, centerY);
        
        // Desenha a imagem correspondente ao estado atual
        g2.drawImage(currentIcon.getImage(), 0, 0, this);
        }
        
        super.paintComponent(g2);
        g2.dispose();
    }

    // Método getter para que outras classes consigam acessar a variável privada "actualAngle" e pegar o seu valor
    public double getActualAngle() {
        return this.actualAngle;
    }

    // Método setter para que outras classes consigam alterar a variável privada "actualAngle" e modifiquem seu valor
    public void setActualAngle(double actualAngle) {
        this.actualAngle = actualAngle;
    }

    // Método getter para que outras classes consigam acessar a variável privada "isInclinedToRight" e pegar o valor
    public boolean isInclinedToRight() {
        return this.isInclinedToRight;
    }

    // Método setter para que outras classes consigam alterar a variável privada "isInclinedToRight" e modifiquem seu valor
    public void setInclinedToRight(boolean isInclinedToRight) {
        this.isInclinedToRight = isInclinedToRight; 
    }
}