package gui.windows;

import control.PawnControlManager;
import gui.components.ReferencePawn;
import gui.events.ReferencePawnMouseListener;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.Timer;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PawnControlContainer extends JPanel {
    private PawnControlManager pawnControlManager;
    private String stdPawnImgPath = "/assets/peaoAzul_90x90.png"; 
    private String disabledPawnImgPath = "/assets/peaoRosa_90x90.png"; 
    private String goldenPawnImgPath = "/assets/peaoAmarelo_90x90.png"; 
    private ReferencePawn[] pawnLabels;
    private static final double SCALE = 1.5;
    private boolean locked = true;
    
    // Controle do Carrossel e Animação
    private int currentIndex = 0;
    private JButton btnLeft;
    private JButton btnRight;
    private Timer animTimer;
    private boolean isAnimating = false;

    private static final Rectangle PAWN_CONTROL_CONTAINER_BOUNDS = new Rectangle(
        (int) (0 * SCALE),
        (int) (0 * SCALE),
        (int) (220 * SCALE),
        (int) (120 * SCALE)
    );

    public PawnControlContainer(PawnControlManager pawnControlManager, String playerColor) {
        this.pawnControlManager = pawnControlManager;
        setBounds(PAWN_CONTROL_CONTAINER_BOUNDS); 
        setOpaque(false);   
        setLayout(null);    

        if (playerColor == null) playerColor = "azul";
        switch (playerColor.toLowerCase()) {
            case "roxo":
                this.stdPawnImgPath = "/assets/img/purplePawn_90x90.png";
                this.goldenPawnImgPath = "/assets/img/purplePawnWinner_90x90.png";
                break;
            case "rosa":
                this.stdPawnImgPath = "/assets/img/pinkPawn_90x90.png";
                this.goldenPawnImgPath = "/assets/img/pinkPawnWinner_90x90.png";
                break;
            case "amarelo":
                this.stdPawnImgPath = "/assets/img/yellowPawn_90x90.png";
                this.goldenPawnImgPath = "/assets/img/yellowPawnWinner_90x90.png";
                break;
            default:
                this.stdPawnImgPath = "/assets/img/bluePawn_90x90.png";
                this.goldenPawnImgPath = "/assets/img/bluePawnWinner_90x90.png";
                break;
        }

        this.disabledPawnImgPath = "/assets/img/greyPawn_90x90.png"; 
        this.goldenPawnImgPath = "/assets/peaoAmarelo_90x90.png"; 

        this.pawnLabels = new ReferencePawn[4];
        for (int i = 0; i < 4; i++) {
            pawnLabels[i] = new ReferencePawn(
                this.stdPawnImgPath, 
                this.disabledPawnImgPath, 
                this.goldenPawnImgPath, 
                i + 1, 
                SCALE
            );
            add(pawnLabels[i]);
            
            final int pIndex = i;
            pawnLabels[i].addMouseListener(new ReferencePawnMouseListener(pawnControlManager, i));
            pawnLabels[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // TRAVA APLICADA: só permite rolar o carrossel ao clicar no peão se NÃO estiver bloqueado (locked)
                    if (!locked && pIndex != currentIndex && !isAnimating) {
                        int dir = (pIndex - currentIndex + 4) % 4 == 1 ? 1 : -1;
                        navegarComAnimacao(dir);
                    }
                }
            });
        }

        btnLeft = criarBotaoSeta("◀");
        btnRight = criarBotaoSeta("▶");
        
        btnLeft.setBounds((int)(8 * SCALE), (int)(45 * SCALE), (int)(28 * SCALE), (int)(32 * SCALE));
        btnRight.setBounds((int)(184 * SCALE), (int)(45 * SCALE), (int)(28 * SCALE), (int)(32 * SCALE));

        btnLeft.addActionListener(e -> navegarComAnimacao(-1));
        btnRight.addActionListener(e -> navegarComAnimacao(1));

        add(btnLeft);
        add(btnRight);

        posicionarEstatico();

        pawnVisualState(0, "NORMAL");
        pawnVisualState(1, "NORMAL");
        pawnVisualState(2, "NORMAL");
        pawnVisualState(3, "NORMAL");

        setLocked(true);
        this.pawnControlManager.setPawnControlContainer(this);
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    private JButton criarBotaoSeta(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, (int) (15 * SCALE)));
        btn.setForeground(new Color(212, 160, 23));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private Rectangle getSlotBounds(int slot) {
        int sideW = (int) (34 * SCALE);
        int sideH = (int) (40 * SCALE);
        int centerW = (int) (52 * SCALE);
        int centerH = (int) (62 * SCALE);

        int sideY = (int) (40 * SCALE);
        int centerY = (int) (28 * SCALE);

        switch (slot) {
            case 0: return new Rectangle((int) (42 * SCALE), sideY, sideW, sideH);
            case 1: return new Rectangle((int) (84 * SCALE), centerY, centerW, centerH);
            case 2: return new Rectangle((int) (144 * SCALE), sideY, sideW, sideH);
            default: return new Rectangle((int) (90 * SCALE), sideY, sideW, sideH);
        }
    }

    private float getSlotAlpha(int slot) {
        if (slot == 1) return 1.0f;
        if (slot == 0 || slot == 2) return 0.65f;
        return 0.0f;
    }

    private int getPawnSlotIndex(int pawnIndex, int centerIdx) {
        int diff = (pawnIndex - centerIdx + 4) % 4;
        if (diff == 0) return 1; 
        if (diff == 1) return 2; 
        if (diff == 3) return 0; 
        return 3; 
    }

    private void posicionarEstatico() {
        for (int i = 0; i < 4; i++) {
            int slot = getPawnSlotIndex(i, currentIndex);
            Rectangle b = getSlotBounds(slot);
            pawnLabels[i].setBounds(b);
            pawnLabels[i].setAlpha(getSlotAlpha(slot));
            pawnLabels[i].setCenterPawn(slot == 1);
            pawnLabels[i].setVisible(slot != 3);
        }
        setComponentZOrder(pawnLabels[currentIndex], 0);
        repaint();
    }

    private void navegarComAnimacao(int direcao) {
        // TRAVA APLICADA: se estiver bloqueado (locked) ou já animando, ignora o comando
        if (locked || isAnimating) return;
        isAnimating = true;

        int oldIndex = currentIndex;
        currentIndex = (currentIndex + direcao + 4) % 4;

        Rectangle[] startBounds = new Rectangle[4];
        Rectangle[] targetBounds = new Rectangle[4];
        float[] startAlpha = new float[4];
        float[] targetAlpha = new float[4];

        for (int i = 0; i < 4; i++) {
            int oldSlot = getPawnSlotIndex(i, oldIndex);
            int newSlot = getPawnSlotIndex(i, currentIndex);

            startBounds[i] = pawnLabels[i].getBounds();
            targetBounds[i] = getSlotBounds(newSlot);

            startAlpha[i] = getSlotAlpha(oldSlot);
            targetAlpha[i] = getSlotAlpha(newSlot);

            pawnLabels[i].setVisible(true);
            pawnLabels[i].setCenterPawn(i == currentIndex);
        }

        setComponentZOrder(pawnLabels[currentIndex], 0);

        final long startTime = System.currentTimeMillis();
        final int duration = 220; 

        animTimer = new Timer(15, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float rawT = Math.min(1.0f, (float) elapsed / duration);
            
            float t = (float) (1.0 - Math.pow(1.0 - rawT, 2));

            for (int i = 0; i < 4; i++) {
                int x = (int) (startBounds[i].x + (targetBounds[i].x - startBounds[i].x) * t);
                int y = (int) (startBounds[i].y + (targetBounds[i].y - startBounds[i].y) * t);
                int w = (int) (startBounds[i].width + (targetBounds[i].width - startBounds[i].width) * t);
                int h = (int) (startBounds[i].height + (targetBounds[i].height - startBounds[i].height) * t);
                
                pawnLabels[i].setBounds(x, y, w, h);

                float alpha = startAlpha[i] + (targetAlpha[i] - startAlpha[i]) * t;
                pawnLabels[i].setAlpha(alpha);
            }

            repaint();

            if (rawT >= 1.0f) {
                ((Timer) e.getSource()).stop();
                isAnimating = false;
                posicionarEstatico();

                if (pawnControlManager != null && !locked) {
                    pawnControlManager.onCentralPawnFocused(currentIndex);
                }
            }
        });

        animTimer.start();
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        
        btnLeft.setEnabled(!locked);
        btnRight.setEnabled(!locked);

        for (int i = 0; i < 4; i++) {
            ReferencePawn pawn = pawnLabels[i];
            if (pawn != null) {
                if (locked) {
                    pawn.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                } else if (pawn.getMousePosition() != null) {
                    String state = (pawnControlManager != null) ? pawnControlManager.getPawnState(i) : "NORMAL";
                    if (!"DOURADO".equalsIgnoreCase(state) && !"DESABILITADO".equalsIgnoreCase(state)) {
                        pawn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        if (pawnControlManager != null) {
                            pawnControlManager.onReferencePawnHoverEntered(i);
                        }
                    }
                }
            }
        }

        if (!locked && pawnControlManager != null) {
            pawnControlManager.onCentralPawnFocused(currentIndex);
        }

        repaint();
    }

    public boolean isLocked() { return this.locked; }

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

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (locked) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int margin = (int) (2 * SCALE);
            int arc = (int) (12 * SCALE);
            
            g2.setColor(new Color(0, 0, 0, 110)); 
            g2.fillRoundRect(margin, margin, getWidth() - (margin * 2), getHeight() - (margin * 2), arc, arc);
            g2.dispose();
        }
    }

    public void pawnVisualState(int pawnIndex, String pawnState) {
        if (pawnIndex < 0 || pawnIndex >= 4) return;
        pawnControlManager.setPawnState(pawnIndex, pawnState);
        pawnLabels[pawnIndex].setVisualState(pawnState);
        
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
        if (pawnIndex >= 0 && pawnIndex < 4 && !locked) {
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