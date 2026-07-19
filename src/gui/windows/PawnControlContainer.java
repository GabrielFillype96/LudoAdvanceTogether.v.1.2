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

public class PawnControlContainer extends JPanel {
    private PawnControlManager pawnControlManager;
    private static final String STD_PAWN_IMG_PATH = "/assets/peaoAzul_90x90.png"; 
    private static final String DISABLED_PAWN_IMG_PATH = "/assets/peaoRosa_90x90.png"; 
    private static final String GOLDEN_PAWN_IMG_PATH = "/assets/peaoAmarelo_90x90.png"; 
    private ReferencePawn[] pawnLabels;
    private static final double SCALE = 1.5;
    
    // AJUSTADO: Altura do quadro aumentada de 100 para 120
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

        // AJUSTADO: Tamanho dos peões aumentado de 30 para 40
        int labelWidth = (int) (40 * SCALE);
        int labelHeight = (int) (40 * SCALE);

        for (int i = 0; i < 4; i++) {
            pawnLabels[i] = new ReferencePawn(
                STD_PAWN_IMG_PATH, 
                DISABLED_PAWN_IMG_PATH, 
                GOLDEN_PAWN_IMG_PATH, 
                (i + 1), 
                SCALE
            );
            add(pawnLabels[i]);
            pawnLabels[i].addMouseListener(new ReferencePawnMouseListener(pawnControlManager, i));
        }
        
        // AJUSTADO: Coordenadas recalculadas para centralizar os peões de tamanho 40x40 de forma simétrica
        pawnLabels[0].setBounds((int) (45 * SCALE), (int) (15 * SCALE), labelWidth, labelHeight);
        pawnLabels[1].setBounds((int) (135 * SCALE), (int) (15 * SCALE), labelWidth, labelHeight);
        pawnLabels[2].setBounds((int) (45 * SCALE), (int) (65 * SCALE), labelWidth, labelHeight);
        pawnLabels[3].setBounds((int) (135 * SCALE), (int) (65 * SCALE), labelWidth, labelHeight);

        pawnVisualState(0, "NORMAL");
        pawnVisualState(1, "NORMAL");
        pawnVisualState(2, "NORMAL");
        pawnVisualState(3, "NORMAL");

        this.pawnControlManager.setPawnControlContainer(this);
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

    public void pawnVisualState(int pawnIndex, String pawnState) {
        if (pawnIndex < 0 || pawnIndex >= 4) return;
        pawnControlManager.setPawnState(pawnIndex, pawnState);
        pawnLabels[pawnIndex].setVisualState(pawnState);
        repaint();
    }
    
    public ReferencePawn getReferencePawn(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            return pawnLabels[pawnIndex];
        }
        return null;
    }

    public void startReferencePawnWobble(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
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