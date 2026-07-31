package gui.components;

import gui.events.ShakeListener;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import javax.swing.JLabel;
import javax.swing.Timer;

public class PlayerPawn extends JLabel {

    private Timer shakeTimer;
    private boolean isJumpingUp = true;
    private int originalY;
    private String playerName;
    private int pawnCurrentPos;
    private boolean isMoving = false;
    private Color pawnColor;

    // --- NOVO ATRIBUTO: Controle de Coroa ---
    private boolean isCrowned = false;

    private static final double SCALE = 1.0;
    private static final double DRAW_SCALE = 0.65; 

    public PlayerPawn(String playerName, String colorName) {
        this.playerName = playerName;
        this.pawnCurrentPos = 0;
        this.pawnColor = parseColor(colorName);

        int tileSize = (int) (40 * SCALE);
        this.setSize(tileSize, tileSize);

        ShakeListener shakeListener = new ShakeListener(this);
        this.shakeTimer = new Timer(150, shakeListener);
    }

    public PlayerPawn(String playerName, Color pawnColor) {
        this.playerName = playerName;
        this.pawnCurrentPos = 0;
        this.pawnColor = pawnColor != null ? pawnColor : new Color(80, 163, 213);

        int tileSize = (int) (40 * SCALE);
        this.setSize(tileSize, tileSize);

        ShakeListener shakeListener = new ShakeListener(this);
        this.shakeTimer = new Timer(150, shakeListener);
    }

    // --- NOVOS MÉTODOS DE CONTROLE DA COROA ---
    public boolean isCrowned() {
        return isCrowned;
    }

    public void setCrowned(boolean crowned) {
        this.isCrowned = crowned;
        this.repaint();
    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        if (shakeTimer == null || !shakeTimer.isRunning()) {
            this.originalY = y;
        }
    }

    @Override
    public void setLocation(Point p) {
        super.setLocation(p);
        if (p != null && (shakeTimer == null || !shakeTimer.isRunning())) {
            this.originalY = p.y;
        }
    }

    private Color parseColor(String colorName) {
        if (colorName == null) return new Color(80, 163, 213);
        switch (colorName.toLowerCase()) {
            case "roxo": return new Color(127, 90, 190);
            case "azul": return new Color(50, 140, 220);
            case "rosa": return new Color(225, 85, 125);
            case "amarelo": return new Color(245, 175, 20);
            default: return new Color(80, 163, 213);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        draw3DPawn(g2, 0, 0, getWidth(), getHeight(), this.pawnColor, 1.0f, this.isCrowned);
        
        g2.dispose();
    }

    // Sobrecarga para manter compatibilidade com chamadas sem o argumento isCrowned
    public static void draw3DPawn(Graphics2D g2, int origX, int origY, int origWidth, int origHeight, Color baseColor, float alpha) {
        draw3DPawn(g2, origX, origY, origWidth, origHeight, baseColor, alpha, false);
    }

    public static void draw3DPawn(Graphics2D g2, int origX, int origY, int origWidth, int origHeight, Color baseColor, float alpha, boolean isCrowned) {
        if (baseColor == null) baseColor = Color.GRAY;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        if (alpha < 1.0f) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }

        double w = origWidth * DRAW_SCALE;
        double h = origHeight * DRAW_SCALE;
        double x = origX + (origWidth - w) / 2.0;
        double y = origY + (origHeight - h) / 2.0;

        Color whiteHighlight = blendColor(baseColor, Color.WHITE, 0.85f);
        Color lightColor     = blendColor(baseColor, Color.WHITE, 0.45f);
        Color darkColor      = baseColor.darker();
        Color deepShadow     = darkColor.darker();
        Color ambientColor   = blendColor(baseColor, Color.WHITE, 0.15f);

        float[] cylFractions = new float[]{0.0f, 0.22f, 0.50f, 0.82f, 1.0f};
        Color[] cylColors = new Color[]{ambientColor, whiteHighlight, baseColor, darkColor, deepShadow};

        // --- 1. SOMBRA PROJETADA EM CAMADAS ---
        g2.setColor(new Color(0, 0, 0, (int) (40 * alpha)));
        g2.fillOval((int) (x + w * 0.08), (int) (y + h * 0.84), (int) (w * 0.84), (int) (h * 0.15));
        
        g2.setColor(new Color(0, 0, 0, (int) (80 * alpha)));
        g2.fillOval((int) (x + w * 0.18), (int) (y + h * 0.86), (int) (w * 0.64), (int) (h * 0.11));

        // --- 2. BASE INFERIOR ---
        LinearGradientPaint baseGrad = new LinearGradientPaint(
            (float) (x + w * 0.10), 0, (float) (x + w * 0.90), 0,
            cylFractions, cylColors
        );
        g2.setPaint(baseGrad);
        g2.fillOval((int) (x + w * 0.10), (int) (y + h * 0.74), (int) (w * 0.80), (int) (h * 0.16));

        g2.setColor(new Color(0, 0, 0, 70));
        g2.drawOval((int) (x + w * 0.10), (int) (y + h * 0.74), (int) (w * 0.80), (int) (h * 0.16));

        // --- 3. ANEL INTERMEDIÁRIO DA BASE ---
        g2.setPaint(baseGrad);
        g2.fillOval((int) (x + w * 0.18), (int) (y + h * 0.68), (int) (w * 0.64), (int) (h * 0.12));

        g2.setColor(new Color(0, 0, 0, 60));
        g2.drawOval((int) (x + w * 0.18), (int) (y + h * 0.68), (int) (w * 0.64), (int) (h * 0.12));

        // --- 4. CORPO CÔNICO DA CINTURA ---
        Path2D.Double bodyPath = new Path2D.Double();
        double neckLeftX = x + w * 0.36;
        double neckY = y + h * 0.36;
        double neckRightX = x + w * 0.64;

        double bodyBaseLeftX = x + w * 0.22;
        double bodyBaseY = y + h * 0.72;
        double bodyBaseRightX = x + w * 0.78;

        bodyPath.moveTo(neckLeftX, neckY);
        bodyPath.quadTo(x + w * 0.30, y + h * 0.54, bodyBaseLeftX, bodyBaseY);
        bodyPath.lineTo(bodyBaseRightX, bodyBaseY);
        bodyPath.quadTo(x + w * 0.70, y + h * 0.54, neckRightX, neckY);
        bodyPath.closePath();

        LinearGradientPaint bodyGrad = new LinearGradientPaint(
            (float) (x + w * 0.22), 0, (float) (x + w * 0.78), 0,
            cylFractions, cylColors
        );
        g2.setPaint(bodyGrad);
        g2.fill(bodyPath);

        g2.setColor(new Color(0, 0, 0, 50));
        g2.draw(bodyPath);

        // --- 5. ANEL DO PESCOÇO / COLARINHO ---
        LinearGradientPaint collarGrad = new LinearGradientPaint(
            (float) (x + w * 0.28), 0, (float) (x + w * 0.72), 0,
            cylFractions, cylColors
        );
        g2.setPaint(collarGrad);
        g2.fillOval((int) (x + w * 0.28), (int) (y + h * 0.33), (int) (w * 0.44), (int) (h * 0.09));

        g2.setColor(new Color(0, 0, 0, 70));
        g2.drawOval((int) (x + w * 0.28), (int) (y + h * 0.33), (int) (w * 0.44), (int) (h * 0.09));

        // --- 6. CABEÇA ESFÉRICA ---
        float headCenterX = (float) (x + w * 0.50);
        float headCenterY = (float) (y + h * 0.22);
        float headRadius  = (float) (w * 0.25);

        float focusX = headCenterX - headRadius * 0.42f;
        float focusY = headCenterY - headRadius * 0.42f;

        RadialGradientPaint headGrad = new RadialGradientPaint(
            headCenterX, headCenterY, headRadius * 1.35f,
            focusX, focusY,
            new float[]{0.0f, 0.25f, 0.65f, 0.90f, 1.0f},
            new Color[]{Color.WHITE, lightColor, baseColor, darkColor, deepShadow},
            CycleMethod.NO_CYCLE
        );

        g2.setPaint(headGrad);
        g2.fillOval(
            (int) (headCenterX - headRadius),
            (int) (headCenterY - headRadius),
            (int) (headRadius * 2),
            (int) (headRadius * 2)
        );

        g2.setColor(new Color(0, 0, 0, 60));
        g2.drawOval(
            (int) (headCenterX - headRadius),
            (int) (headCenterY - headRadius),
            (int) (headRadius * 2),
            (int) (headRadius * 2)
        );

        // --- 7. REFLEXOS ESPECULARES ---
        g2.setColor(new Color(255, 255, 255, (int) (210 * alpha)));
        g2.fillOval(
            (int) (focusX - headRadius * 0.12),
            (int) (focusY - headRadius * 0.12),
            (int) (headRadius * 0.32),
            (int) (headRadius * 0.20)
        );

        g2.setColor(new Color(255, 255, 255, (int) (255 * alpha)));
        g2.fillOval(
            (int) (focusX - headRadius * 0.05),
            (int) (focusY - headRadius * 0.05),
            (int) (headRadius * 0.12),
            (int) (headRadius * 0.08)
        );

        g2.setColor(new Color(255, 255, 255, (int) (65 * alpha)));
        Path2D.Double highlightStreak = new Path2D.Double();
        highlightStreak.moveTo(x + w * 0.40, y + h * 0.38);
        highlightStreak.quadTo(x + w * 0.36, y + h * 0.54, x + w * 0.33, y + h * 0.70);
        highlightStreak.lineTo(x + w * 0.38, y + h * 0.70);
        highlightStreak.quadTo(x + w * 0.40, y + h * 0.54, x + w * 0.43, y + h * 0.38);
        highlightStreak.closePath();
        g2.fill(highlightStreak);

        // --- 8. DESENHO DA COROA (Caso esteja coroado) ---
        if (isCrowned) {
            drawCrown(g2, headCenterX, headCenterY, headRadius, alpha);
        }
    }

    /**
     * Renderiza a coroa dourada com rubis sobre a cabeça do peão
     */
    private static void drawCrown(Graphics2D g2, float headCenterX, float headCenterY, float headRadius, float alpha) {
        double crownWidth = headRadius * 1.8;
        double crownHeight = headRadius * 1.1;
        
        double baseX = headCenterX - (crownWidth / 2.0);
        double baseY = headCenterY - (headRadius * 0.65);

        Path2D.Double crownPath = new Path2D.Double();
        crownPath.moveTo(baseX, baseY);
        crownPath.lineTo(baseX + crownWidth, baseY);
        crownPath.lineTo(baseX + crownWidth * 0.95, baseY - crownHeight * 0.85);
        crownPath.lineTo(baseX + crownWidth * 0.70, baseY - crownHeight * 0.40);
        crownPath.lineTo(baseX + crownWidth * 0.50, baseY - crownHeight * 1.10);
        crownPath.lineTo(baseX + crownWidth * 0.30, baseY - crownHeight * 0.40);
        crownPath.lineTo(baseX + crownWidth * 0.05, baseY - crownHeight * 0.85);
        crownPath.closePath();

        Color goldLight = new Color(255, 240, 150);
        Color goldDark  = new Color(210, 150, 20);
        
        LinearGradientPaint crownGrad = new LinearGradientPaint(
            (float) baseX, (float) (baseY - crownHeight),
            (float) baseX, (float) baseY,
            new float[]{0.0f, 1.0f},
            new Color[]{goldLight, goldDark}
        );

        g2.setPaint(crownGrad);
        g2.fill(crownPath);

        g2.setColor(new Color(60, 40, 0, (int) (200 * alpha)));
        g2.draw(crownPath);

        // Rubis nas 3 pontas
        g2.setColor(new Color(220, 30, 30, (int) (255 * alpha)));
        int gemSize = (int) Math.max(3, headRadius * 0.22);

        g2.fillOval((int) (baseX + crownWidth * 0.05 - gemSize / 2.0), (int) (baseY - crownHeight * 0.85 - gemSize / 2.0), gemSize, gemSize);
        g2.fillOval((int) (baseX + crownWidth * 0.50 - gemSize / 2.0), (int) (baseY - crownHeight * 1.10 - gemSize / 2.0), gemSize, gemSize);
        g2.fillOval((int) (baseX + crownWidth * 0.95 - gemSize / 2.0), (int) (baseY - crownHeight * 0.85 - gemSize / 2.0), gemSize, gemSize);
    }

    private static Color blendColor(Color c1, Color c2, float ratio) {
        float r = c1.getRed() * (1 - ratio) + c2.getRed() * ratio;
        float g = c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio;
        float b = c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio;
        return new Color((int) r, (int) g, (int) b);
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        this.isMoving = moving;
    }

    public void startBoardPawnShake() {
        if (isMoving) return;
        if (shakeTimer != null && shakeTimer.isRunning()) return;
        
        this.originalY = getY(); 
        shakeTimer.start();
    }

    public void stopBoardPawnShake() {
        if (shakeTimer != null) {
            shakeTimer.stop();
            PlayerPawn.this.setLocation(getX(), originalY);
        }
    }

    public int getPawnCurrentPos() {
        return pawnCurrentPos;
    }

    public void setPawnCurrentPos(int pawnPosition) {
        this.pawnCurrentPos = pawnPosition;
    }

    // --- CORREÇÃO: Define a posição direta em pixels da tela ---
    public void setPawnVisualCoordinates(Point targetScreenPoint) {
        if (targetScreenPoint != null) {
            this.setLocation(targetScreenPoint.x, targetScreenPoint.y);
            this.originalY = targetScreenPoint.y;
        }
    }

    public int getOriginalY() {
        return originalY;
    }

    public boolean isJumpingUp() {
        return isJumpingUp;
    }

    public void setJumpingUp(boolean jumpingUp) {
        this.isJumpingUp = jumpingUp;
    }

    public String getPlayerName() {
        return playerName;
    }

    public Color getPawnColor() {
        return pawnColor;
    }

    public void setPawnColor(Color pawnColor) {
        this.pawnColor = pawnColor;
        this.repaint();
    }

    public void updatePawnVisual(String imagePath) {
        this.pawnColor = new Color(255, 215, 0); // Dourado
        this.repaint();
    }

    public void updatePawnVisual(Color newColor) {
        if (newColor != null) {
            this.pawnColor = newColor;
            this.repaint();
        }
    }
}