package gui.windows;

import control.PawnControlManager;
import gui.components.ReferencePawn;
import gui.events.ReferencePawnMouseListener;

import javax.swing.JPanel;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Cursor;

public class PawnControlContainer extends JPanel {
    private PawnControlManager pawnControlManager;
    private static final String STD_PAWN_IMG_PATH = "/assets/peaoAzul_90x90.png"; 
    private static final String DISABLED_PAWN_IMG_PATH = "/assets/peaoRosa_90x90.png"; 
    private static final String GOLDEN_PAWN_IMG_PATH = "/assets/peaoAmarelo_90x90.png"; 
    private ReferencePawn[] pawnLabels;
    private static final double SCALE = 1.5;
    private boolean locked = true; // Inicia bloqueado até que o jogo libere
    
    private static final Rectangle PAWN_CONTROL_CONTAINER_BOUNDS = new Rectangle(
        (int) (0 * SCALE),
        (int) (0 * SCALE),
        (int) (220 * SCALE),
        (int) (120 * SCALE)
    );
    
    public PawnControlContainer(PawnControlManager pawnControlManager) {
        this.pawnControlManager = pawnControlManager;
        setBounds(PAWN_CONTROL_CONTAINER_BOUNDS); 
        setOpaque(false);   
        setLayout(null);    

        this.pawnLabels = new ReferencePawn[4];

        int labelWidth = (int) (40 * SCALE);
        int labelHeight = (int) (40 * SCALE);

        int[] pawnNumbers = {1, 3, 2, 4};
        int[] posX = {45, 45, 135, 135};
        int[] posY = {15, 65, 15, 65};

        for (int i = 0; i < 4; i++) {
            pawnLabels[i] = new ReferencePawn(
                STD_PAWN_IMG_PATH, 
                DISABLED_PAWN_IMG_PATH, 
                GOLDEN_PAWN_IMG_PATH, 
                pawnNumbers[i], 
                SCALE
            );
            add(pawnLabels[i]);
            pawnLabels[i].addMouseListener(new ReferencePawnMouseListener(pawnControlManager, i));
            pawnLabels[i].setBounds((int) (posX[i] * SCALE), (int) (posY[i] * SCALE), labelWidth, labelHeight);
        }

        pawnVisualState(0, "NORMAL");
        pawnVisualState(1, "NORMAL");
        pawnVisualState(2, "NORMAL");
        pawnVisualState(3, "NORMAL");

        // Aplica o cursor padrão inicial
        setLocked(true);

        this.pawnControlManager.setPawnControlContainer(this);
    }

    // Gerencia o estado de bloqueio e altera os cursores apenas dos componentes necessários
    public void setLocked(boolean locked) {
        this.locked = locked;
        
        // O quadro/painel de fundo em si nunca deve mudar o cursor, mantendo o padrão
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        
        for (int i = 0; i < 4; i++) {
            ReferencePawn pawn = pawnLabels[i];
            if (pawn != null) {
                if (locked) {
                    pawn.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                } else {
                    // CORREÇÃO: Se o painel foi liberado e o mouse JÁ ESTÁ parado sobre este peão
                    if (pawn.getMousePosition() != null) {
                        String state = (pawnControlManager != null) ? pawnControlManager.getPawnState(i) : "NORMAL";
                        
                        if (!"DOURADO".equalsIgnoreCase(state) && !"DESABILITADO".equalsIgnoreCase(state)) {
                            pawn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            
                            // Força o gatilho de hover para ligar o tremor (shake) no tabuleiro instantaneamente
                            if (pawnControlManager != null) {
                                pawnControlManager.onReferencePawnHoverEntered(i);
                            }
                        }
                    }
                }
            }
        }
        repaint();
    }

    public boolean isLocked() {
        return this.locked;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        int margin = (int) (2 * SCALE);
        int arc = (int) (12 * SCALE); 
        
        g2.setColor(new Color(43, 33, 24, 245)); 
        g2.fillRoundRect(margin, margin, w - (margin * 2), h - (margin * 2), arc, arc);
        
        g2.setStroke(new BasicStroke((float) (2.5 * SCALE)));
        g2.setColor(new Color(212, 160, 23)); 
        g2.drawRoundRect(margin, margin, w - (margin * 2), h - (margin * 2), arc, arc);
        
        g2.setStroke(new BasicStroke((float) (0.8 * SCALE)));
        g2.setColor(new Color(255, 255, 255, 25)); 
        int innerOffset = (int) (5 * SCALE);
        g2.drawRoundRect(innerOffset, innerOffset, w - (innerOffset * 2), h - (innerOffset * 2), arc - 4, arc - 4);
        
        g2.dispose();
    }

    // Sobrescrita do paint para desenhar a película TRANSPARENTE por CIMA dos componentes filhos (peões)
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (locked) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int margin = (int) (2 * SCALE);
            int arc = (int) (12 * SCALE);
            
            // Preto com 110 de opacidade para criar o efeito esmaecido de desabilitado
            g2.setColor(new Color(0, 0, 0, 110)); 
            g2.fillRoundRect(margin, margin, getWidth() - (margin * 2), getHeight() - (margin * 2), arc, arc);
            g2.dispose();
        }
    }

    public void pawnVisualState(int pawnIndex, String pawnState) {
        if (pawnIndex < 0 || pawnIndex >= 4) return;
        pawnControlManager.setPawnState(pawnIndex, pawnState);
        pawnLabels[pawnIndex].setVisualState(pawnState);
        
        // Se estiver bloqueado, garante o cursor padrão.
        // Se estiver liberado, o PawnControlManager cuidará de injetar a mãozinha no evento Hover.
        if (locked) {
            pawnLabels[pawnIndex].setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        
        repaint();
    }

    public void resetAllPawnsToNormal() {
        for (int i = 0; i < 4; i++) {
            if (pawnControlManager != null && !"DOURADO".equalsIgnoreCase(pawnControlManager.getPawnState(i))) {
                pawnVisualState(i, "NORMAL");
            }
        }
    }
    
    public ReferencePawn getReferencePawn(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            return pawnLabels[pawnIndex];
        }
        return null;
    }

    public void startReferencePawnWobble(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4 && !locked) { // Só balança se não estiver bloqueado
            ReferencePawn referencePawn = pawnLabels[pawnIndex];
            if (referencePawn != null) {
                referencePawn.startReferencePawnWobble();
            }
        }
    }

    public void stopReferencePawnWobble(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            ReferencePawn referencePawn = pawnLabels[pawnIndex];
            if (referencePawn != null) {
                referencePawn.stopReferencePawnWobble();
            }
        }
    }
}