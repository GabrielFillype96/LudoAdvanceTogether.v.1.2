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
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
 * Utilizamos um "JLabel" para que as interações de mouse ("hitbox" e "hover") pudessem ser feitas de forma segura, já que essa classe permite definir os limites do "label".
*/
public class ReferencePawn extends JLabel {
    // VARIÁVEIS DE INSTÂNCIA
    private ImageIcon stdReferencePawnIcon; // Imagem padrão do peão para permitir
    private ImageIcon disabledReferencePawnIcon; // Imagem do peão desabilitados;
    private ImageIcon goldenReferencePawnIcon; // Imagem do peão dourado ao chegar no centro do tabuleiro
    private double actualAngle = 0;
    private boolean isInclinedToRight = true;
    private Timer wobbleTimer;
    
    // Variável para rastrear o estado visual atual do peão
    private String currentVisualState = "NORMAL";
    
    // Novas variáveis para identificação e escala do número de fundo
    private int pawnNumber;
    private double scale;
    
    /**
     ** Construtor do Peão de Referência
     * @param stdReferencePawnImgPath Path da imagem padrão do peão
     * @param pawnNumber O número identificador do peão (1, 2, 3 ou 4)
     * @param scale Fator de escala da interface
    */
    public ReferencePawn(String stdReferencePawnImgPath, String disabledReferencePawnImgPath, String goldenReferencePawnImgPath, int pawnNumber, double scale) {
        this.pawnNumber = pawnNumber;
        this.scale = scale;

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

        // CONFIGURAÇÃO DO CURSOR DINÂMICO (Mãozinha)
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!"DESABILITADO".equals(currentVisualState)) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
            }
        });
    }


    /**
     * Método setter com switch para controlar os estados que o peão pode assumir
     * @param pawnState O estado visual: "NORMAL", "DESABILITADO" ou "DOURADO"
     */
    public void setVisualState(String pawnState) {
        this.currentVisualState = pawnState; // Salva o novo estado na variável de controlo
        
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

        // CASO DE BORDA: Se o estado mudar e o rato já estiver posicionado em cima do peão
        if (getMousePosition() != null) {
            if ("DESABILITADO".equals(pawnState)) {
                setCursor(Cursor.getDefaultCursor());
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        } else if ("DESABILITADO".equals(pawnState)) {
            setCursor(Cursor.getDefaultCursor());
        }

        repaint(); // Força o peão a se redesenhar com o novo ícone
    }

    public void startReferencePawnWobble() {
        if (wobbleTimer != null && wobbleTimer.isRunning()) return;
        wobbleTimer.start();
    }

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
        
        // Ativa o Antialiasing completo (incluindo texto) para curvas suaves
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // --- 1. RENDERIZAÇÃO DO NÚMERO DE FUNDO ---
        String numStr = String.valueOf(this.pawnNumber);
        
        // Define o tamanho da fonte dinamicamente com base no scale (ex: tamanho base 25)
        g2.setFont(new Font("SansSerif", Font.BOLD, (int) (45 * scale)));
        FontMetrics fm = g2.getFontMetrics();
        
        // Centraliza o texto no JLabel
        int textX = (this.getWidth() - fm.stringWidth(numStr)) / 2;
        // Ajusta levemente a altura para o número sobressair perfeitamente na parte superior/central
        int textY = (this.getHeight() + fm.getAscent() - fm.getDescent()) / 2 - (int)(1 * scale);

        // Desenha contorno fino (1px) em cruz usando um cinza escuro discreto
        g2.setColor(new Color(45, 45, 45, 180));
        g2.drawString(numStr, textX - 1, textY);
        g2.drawString(numStr, textX + 1, textY);
        g2.drawString(numStr, textX, textY - 1);
        g2.drawString(numStr, textX, textY + 1);

        // Desenha o preenchimento interno do número em Branco
        g2.setColor(Color.WHITE);
        g2.drawString(numStr, textX, textY);
        
        // --- 2. RENDERIZAÇÃO DO PEÃO (SOBRE O NÚMERO) ---
        int centerX = this.getWidth() / 2;
        int centerY = this.getHeight() / 2;

        ImageIcon currentIcon = (ImageIcon) this.getIcon();
        if (currentIcon != null) {
            // Aplica a rotação apenas na imagem do peão, mantendo o número reto ao fundo
            g2.rotate(Math.toRadians(actualAngle), centerX, centerY);
            g2.drawImage(currentIcon.getImage(), 0, 0, this);
        }
        
        super.paintComponent(g2);
        g2.dispose();
    }

    public double getActualAngle() {
        return this.actualAngle;
    }

    public void setActualAngle(double actualAngle) {
        this.actualAngle = actualAngle;
    }

    public boolean isInclinedToRight() {
        return this.isInclinedToRight;
    }

    public void setInclinedToRight(boolean isInclinedToRight) {
        this.isInclinedToRight = isInclinedToRight; 
    }
}