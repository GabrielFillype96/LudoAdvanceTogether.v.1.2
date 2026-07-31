package gui.windows;

import gui.animations.AlphaPulseAnimator;
import gui.components.PlayerBoardSlot;
import gui.components.PlayerPawn;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

public class BoardScreen extends JPanel {

    private String player1Name, player2Name, player3Name, player4Name;
    private String player1Color, player2Color, player3Color, player4Color;
    
    private PlayerPawn[][] playersPawns;
    private Point[] player1Path, player2Path, player3Path, player4Path;
    
    private PlayerBoardSlot slotP1, slotP2, slotP3, slotP4;
    
    private int previewPawnIndex = -1;
    private int previewDestinationIndex = -1;
    private List<Point> previewPath = null;
    
    private AlphaPulseAnimator pulseAnimator;

    public BoardScreen(
        String player1Name, String player1Color, String player2Name, String player2Color,
        String player3Name, String player3Color, String player4Name, String player4Color
    ) {
        this.player1Name = player1Name; 
        this.player1Color = player1Color;
        this.player2Name = player2Name;
        this.player2Color = player2Color;
        this.player3Name = player3Name;
        this.player3Color = player3Color;
        this.player4Name = player4Name;
        this.player4Color = player4Color;
        
        this.pulseAnimator = new AlphaPulseAnimator(this);

        setOpaque(false);
        setLayout(null); 

        pawnPath();

        this.playersPawns = new PlayerPawn[4][4]; 
        
        String[] playerNames = {player1Name, player2Name, player3Name, player4Name};
        String[] playerColors = {player1Color, player2Color, player3Color, player4Color};
        
        Point[][] allPaths = {player1Path, player2Path, player3Path, player4Path};

        for (int p = 0; p < 4; p++) {
            for (int i = 0; i < 4; i++) {
                playersPawns[p][i] = new PlayerPawn(playerNames[p], playerColors[p]);
                if (allPaths[p] != null && allPaths[p].length > 0) {
                    Point baseCoord = allPaths[p][i];
                    Point isoPos = convertTileToScreenPoint(baseCoord);
                    playersPawns[p][i].setPawnVisualCoordinates(isoPos);
                    playersPawns[p][i].setPawnCurrentPos(i);
                }
                this.add(playersPawns[p][i]);
            }
        }

        addPlayerNameSlots();
        repositionAllPawns();

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                repositionPlayerSlots();
                repositionAllPawns();
            }
        });
    }

    private double getDynamicScale() {
        int w = getWidth() > 0 ? getWidth() : 1000;
        int h = getHeight() > 0 ? getHeight() : 800;
        return Math.min(w / 960.0, h / 620.0);
    }

    public Point convertToIsoPoint(Point p2d) {
        int panelW = getWidth() > 0 ? getWidth() : 1000;
        int panelH = getHeight() > 0 ? getHeight() : 800;
        
        double scale = getDynamicScale();
        int centerX = panelW / 2;
        int centerY = panelH / 2 + (int)(15 * scale);

        double relX = p2d.x - 300;
        double relY = p2d.y - 300;

        double rad = Math.toRadians(45);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double isoX = centerX + (relX * cos - relY * sin) * scale;
        double isoY = centerY + (relX * sin + relY * cos) * 0.44 * scale;

        return new Point((int) isoX, (int) isoY);
    }

    public Point convertTileToScreenPoint(Point basePoint) {
        if (basePoint == null) return new Point(0, 0);
        double scale = getDynamicScale();
        Point isoBase = convertToIsoPoint(basePoint);
        
        int pawnW = (int) (20 * scale); 
        int pawnH = (int) (28 * scale); 
        
        return new Point(
            isoBase.x - (pawnW / 2),
            isoBase.y - (int) (pawnH * 0.75)
        );
    }

    public Point getPawnScreenPosition(int playerId, int tileIndex) {
        Point[] path = getCaminhoCasas(playerId);
        if (path == null || tileIndex < 0 || tileIndex >= path.length) {
            return new Point(0, 0);
        }
        return convertTileToScreenPoint(path[tileIndex]);
    }

    public void repositionAllPawns() {
        Map<Point, List<PlayerPawn>> pawnsOnSameTile = new HashMap<>();

        for (int p = 0; p < 4; p++) {
            Point[] path = getCaminhoCasas(p);
            if (path == null) continue;

            for (int i = 0; i < 4; i++) {
                PlayerPawn pawn = playersPawns[p][i];
                if (pawn == null) continue;

                int pos = pawn.getPawnCurrentPos();
                if (pos >= 0 && pos < path.length) {
                    Point basePoint = path[pos];
                    pawnsOnSameTile.computeIfAbsent(basePoint, k -> new ArrayList<>()).add(pawn);
                }
            }
        }

        double scale = getDynamicScale();
        int displacement = (int) (10 * scale); 

        for (Map.Entry<Point, List<PlayerPawn>> entry : pawnsOnSameTile.entrySet()) {
            Point basePoint = entry.getKey();
            List<PlayerPawn> pawns = entry.getValue();
            int count = pawns.size();

            Point isoBase = convertToIsoPoint(basePoint);

            for (int index = 0; index < count; index++) {
                PlayerPawn pawn = pawns.get(index);
                int offsetX = 0, offsetY = 0;

                if (count == 2) {
                    offsetX = (index == 0) ? -displacement : displacement;
                    offsetY = (index == 0) ? -displacement / 2 : displacement / 2;
                } else if (count >= 3) {
                    switch (index) {
                        case 0: offsetX = -displacement; offsetY = -displacement / 2; break; 
                        case 1: offsetX = displacement;  offsetY = -displacement / 2; break; 
                        case 2: offsetX = -displacement; offsetY = displacement / 2; break;  
                        case 3: offsetX = displacement;  offsetY = displacement / 2; break;  
                    }
                }

                int pawnW = (int) (20 * scale); 
                int pawnH = (int) (28 * scale); 
                pawn.setSize(pawnW, pawnH);

                pawn.setLocation(
                    isoBase.x + offsetX - (pawnW / 2),
                    isoBase.y + offsetY - (int)(pawnH * 0.75)
                );
            }
        }

        sortPawnsByDepth();
        this.repaint();
    }

    private Color colorName(String colorName) {
        if (colorName == null) return Color.GRAY;
        switch(colorName.toLowerCase().trim()) {
            case "rosa": return new Color(225, 85, 125);
            case "roxo": return new Color(127, 90, 190);
            case "azul": return new Color(50, 140, 220);
            case "amarelo": return new Color(245, 175, 20);
            case "cinza": return new Color(130, 130, 130);
            case "verde": return new Color(40, 160, 70);
            case "vermelho": return new Color(220, 50, 50);
            default:
                try {
                    return Color.decode(colorName);
                } catch (Exception e) {
                    return Color.GRAY;
                }
        }
    }

    public void setPreviewData(int pawnIndex, int destIndex, List<Point> path) {
        this.previewPawnIndex = pawnIndex;
        this.previewDestinationIndex = destIndex;
        this.previewPath = path;
        if (this.pulseAnimator != null) this.pulseAnimator.start();
        this.repaint(); 
    }

    public void clearPreview() {
        this.previewPawnIndex = -1;
        this.previewDestinationIndex = -1;
        this.previewPath = null;
        if (this.pulseAnimator != null) this.pulseAnimator.stop();
        this.repaint(); 
    }

    public void consumePreviewDot() {
        if (this.previewPath != null && !this.previewPath.isEmpty()) {
            this.previewPath.remove(0);
            this.repaint();
        }
    }

    public void hidePreviewGhost() {
        this.previewDestinationIndex = -1;
        this.repaint();
    }

    private void pawnPath() {
        int pathLength = 61; 
        player1Path = new Point[pathLength];
        player2Path = new Point[pathLength];
        player3Path = new Point[pathLength];
        player4Path = new Point[pathLength];

        // 1. Spawns das Bases (Casas Iniciais 0..3)
        // Disposição Isométrica em Tela:
        // [0] Esquerda (Peão 1) | [1] Ao lado/Topo (Peão 2)
        // [2] Embaixo (Peão 3)  | [3] Ao lado/Direita (Peão 4)

        // P1: Roxo (Base Inferior)
        player1Path[0] = new Point(440, 520); // Peão 1: Borda Esquerda
        player1Path[1] = new Point(440, 440); // Peão 2: Ao lado do Peão 1
        player1Path[2] = new Point(520, 520); // Peão 3: Embaixo do Peão 1
        player1Path[3] = new Point(520, 440); // Peão 4: Ao lado do Peão 3

        // P2: Azul (Base Esquerda)
        player2Path[0] = new Point(80, 520);  // Peão 1: Borda Esquerda
        player2Path[1] = new Point(80, 440);  // Peão 2: Ao lado do Peão 1
        player2Path[2] = new Point(160, 520); // Peão 3: Embaixo do Peão 1
        player2Path[3] = new Point(160, 440); // Peão 4: Ao lado do Peão 3

        // P3: Amarelo (Base Superior)
        player3Path[0] = new Point(80, 160);  // Peão 1: Borda Esquerda
        player3Path[1] = new Point(80, 80);   // Peão 2: Ao lado do Peão 1
        player3Path[2] = new Point(160, 160); // Peão 3: Embaixo do Peão 1
        player3Path[3] = new Point(160, 80);  // Peão 4: Ao lado do Peão 3

        // P4: Rosa (Base Direita)
        player4Path[0] = new Point(440, 160); // Peão 1: Borda Esquerda
        player4Path[1] = new Point(440, 80);  // Peão 2: Ao lado do Peão 1
        player4Path[2] = new Point(520, 160); // Peão 3: Embaixo do Peão 1
        player4Path[3] = new Point(520, 80);  // Peão 4: Ao lado do Peão 3

        // 2. Trilha Principal do Tabuleiro
        Point[] circuitoTrilha = new Point[] {
            new Point(260, 580), new Point(260, 540), new Point(260, 500), new Point(260, 460), new Point(260, 420), new Point(260, 380),
            new Point(220, 340), new Point(180, 340), new Point(140, 340), new Point(100, 340), new Point(60, 340),  new Point(20, 340),
            new Point(20, 300),
            new Point(20, 260),  new Point(60, 260),  new Point(100, 260), new Point(140, 260), new Point(180, 260), new Point(220, 260),
            new Point(260, 220), new Point(260, 180), new Point(260, 140), new Point(260, 100), new Point(260, 60),  new Point(260, 20),
            new Point(300, 20),
            new Point(340, 20),  new Point(340, 60),  new Point(340, 100), new Point(340, 140), new Point(340, 180), new Point(340, 220),
            new Point(380, 260), new Point(420, 260), new Point(460, 260), new Point(500, 260), new Point(540, 260), new Point(580, 260),
            new Point(580, 300),
            new Point(580, 340), new Point(540, 340), new Point(500, 340), new Point(460, 340), new Point(420, 340), new Point(380, 340),
            new Point(340, 380), new Point(340, 420), new Point(340, 460), new Point(340, 500), new Point(340, 540), new Point(340, 580),
            new Point(300, 580),
            new Point(300, 540), new Point(300, 500), new Point(300, 460), new Point(300, 420), new Point(300, 380)
        };

        System.arraycopy(circuitoTrilha, 0, player2Path, 4, circuitoTrilha.length);

        int centroX = 300, centroY = 300;
        for (int i = 4; i < player2Path.length; i++) {
            Point p2 = player2Path[i];
            if (p2 == null) continue;
            int dx = p2.x - centroX;
            int dy = p2.y - centroY;

            this.player3Path[i] = new Point(centroX - dy, centroY + dx);
            this.player4Path[i] = new Point(centroX - dx, centroY - dy);
            this.player1Path[i] = new Point(centroX + dy, centroY - dx);
        }
    }

    public void startBoardPawnShake(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4 && playersPawns[0][pawnIndex] != null) {
            playersPawns[0][pawnIndex].startBoardPawnShake();
        }
    }

    public void stopBoardPawnShake(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4 && playersPawns[0][pawnIndex] != null) {
            playersPawns[0][pawnIndex].stopBoardPawnShake();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelW = getWidth();
        int panelH = getHeight();
        double scale = getDynamicScale();
        
        int centerX = panelW / 2;
        int centerY = panelH / 2 + (int)(15 * scale);

        int frameMargin = 35; 

        Point pTop = convertToIsoPoint(new Point(-frameMargin, -frameMargin));
        Point pRight = convertToIsoPoint(new Point(600 + frameMargin, -frameMargin));
        Point pBottom = convertToIsoPoint(new Point(600 + frameMargin, 600 + frameMargin));
        Point pLeft = convertToIsoPoint(new Point(-frameMargin, 600 + frameMargin));

        int depth = (int) (48 * scale); 

        // 1. Sombra da base
        int shadowOffset = (int) (12 * scale);
        Polygon dropShadow = new Polygon();
        dropShadow.addPoint(pLeft.x, pLeft.y + depth + shadowOffset);
        dropShadow.addPoint(pBottom.x, pBottom.y + depth + shadowOffset);
        dropShadow.addPoint(pRight.x, pRight.y + depth + shadowOffset);
        dropShadow.addPoint(pRight.x, pRight.y + shadowOffset);
        dropShadow.addPoint(pTop.x, pTop.y + shadowOffset);
        dropShadow.addPoint(pLeft.x, pLeft.y + shadowOffset);
        
        g2.setColor(new Color(0, 0, 0, 80)); 
        g2.fillPolygon(dropShadow);

        // 2. Faces verticais 3D
        Polygon left3DFace = new Polygon();
        left3DFace.addPoint(pLeft.x, pLeft.y);
        left3DFace.addPoint(pBottom.x, pBottom.y);
        left3DFace.addPoint(pBottom.x, pBottom.y + depth);
        left3DFace.addPoint(pLeft.x, pLeft.y + depth);
        g2.setColor(new Color(38, 18, 8));
        g2.fillPolygon(left3DFace);

        Polygon right3DFace = new Polygon();
        right3DFace.addPoint(pBottom.x, pBottom.y);
        right3DFace.addPoint(pRight.x, pRight.y);
        right3DFace.addPoint(pRight.x, pRight.y + depth);
        right3DFace.addPoint(pBottom.x, pBottom.y + depth);
        g2.setColor(new Color(62, 32, 16));
        g2.fillPolygon(right3DFace);

        g2.setColor(new Color(20, 9, 3));
        g2.setStroke(new BasicStroke((float) (1.8 * scale)));
        g2.drawPolygon(left3DFace);
        g2.drawPolygon(right3DFace);
        g2.drawLine(pBottom.x, pBottom.y, pBottom.x, pBottom.y + depth); 

        // 3. Transformação isométrica
        AffineTransform originalTransform = g2.getTransform();
        
        g2.translate(centerX, centerY);
        g2.scale(scale, scale * 0.44); 
        g2.rotate(Math.toRadians(45));
        g2.translate(-300, -300);

        // 4. Moldura de Madeira
        int b = frameMargin;

        Polygon topPlank = new Polygon(new int[]{-b, 600 + b, 600, 0}, new int[]{-b, -b, 0, 0}, 4);
        Polygon leftPlank = new Polygon(new int[]{-b, -b, 0, 0}, new int[]{-b, 600 + b, 600, 0}, 4);
        Polygon rightPlank = new Polygon(new int[]{600 + b, 600 + b, 600, 600}, new int[]{-b, 600 + b, 600, 0}, 4);
        Polygon bottomPlank = new Polygon(new int[]{-b, 600 + b, 600, 0}, new int[]{600 + b, 600 + b, 600, 600}, 4);

        g2.setColor(new Color(110, 58, 30)); g2.fillPolygon(topPlank);
        g2.setColor(new Color(98, 52, 26));  g2.fillPolygon(leftPlank);
        g2.setColor(new Color(60, 30, 14));  g2.fillPolygon(rightPlank);
        g2.setColor(new Color(48, 24, 10));  g2.fillPolygon(bottomPlank);

        g2.setColor(new Color(25, 12, 5));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(-b, -b, 0, 0);
        g2.drawLine(600 + b, -b, 600, 0);
        g2.drawLine(600 + b, 600 + b, 600, 600);
        g2.drawLine(-b, 600 + b, 0, 600);
        g2.drawRect(-b, -b, 600 + 2 * b, 600 + 2 * b);

        g2.setColor(new Color(35, 16, 7));
        g2.drawRect(-3, -3, 606, 606);

        // 5. Superfície do Tabuleiro
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 600, 600);
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, 600, 600);

        Color colorP1 = colorName(player1Color);
        Color colorP2 = colorName(player2Color);
        Color colorP3 = colorName(player3Color);
        Color colorP4 = colorName(player4Color);

        int S = 40; 
        int areaCanto = 240; 

        // P1: Base Inferior (360, 360)
        g2.setColor(colorP1); g2.fillRect(360, 360, areaCanto, areaCanto);
        g2.setColor(Color.BLACK); g2.setStroke(new BasicStroke(2)); g2.drawRect(360, 360, areaCanto, areaCanto);

        // P2: Base Esquerda (0, 360)
        g2.setColor(colorP2); g2.fillRect(0, 360, areaCanto, areaCanto);
        g2.setColor(Color.BLACK); g2.drawRect(0, 360, areaCanto, areaCanto);

        // P3: Base Superior (0, 0)
        g2.setColor(colorP3); g2.fillRect(0, 0, areaCanto, areaCanto);
        g2.setColor(Color.BLACK); g2.drawRect(0, 0, areaCanto, areaCanto);

        // P4: Base Direita (360, 0)
        g2.setColor(colorP4); g2.fillRect(360, 0, areaCanto, areaCanto);
        g2.setColor(Color.BLACK); g2.drawRect(360, 0, areaCanto, areaCanto);

        // Grade de Casas
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1.8f));

        for (int col = 0; col < 3; col++) {
            int x = 240 + col * S;
            for (int row = 0; row < 6; row++) {
                int yTop = row * S;
                g2.drawRect(x, yTop, S, S);

                int yBottom = 360 + row * S;
                g2.drawRect(x, yBottom, S, S);
            }
        }

        for (int row = 0; row < 3; row++) {
            int y = 240 + row * S;
            for (int col = 0; col < 6; col++) {
                int xLeft = col * S;
                g2.drawRect(xLeft, y, S, S);

                int xRight = 360 + col * S;
                g2.drawRect(xRight, y, S, S);
            }
        }

        // Triângulos Centrais
        Polygon downTriangle  = new Polygon(new int[]{240, 360, 300}, new int[]{360, 360, 300}, 3);
        Polygon leftTriangle  = new Polygon(new int[]{240, 240, 300}, new int[]{240, 360, 300}, 3);
        Polygon upTriangle    = new Polygon(new int[]{240, 360, 300}, new int[]{240, 240, 300}, 3);
        Polygon rightTriangle = new Polygon(new int[]{360, 360, 300}, new int[]{240, 360, 300}, 3);

        Polygon[] triangles = {downTriangle, leftTriangle, upTriangle, rightTriangle};
        Color[] colors = {colorP2, colorP3, colorP4, colorP1};
        for (int j = 0; j < triangles.length; j++) {
            g2.setColor(colors[j]);
            g2.fillPolygon(triangles[j]);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawPolygon(triangles[j]);
        }

        // Caminhos Finais
        for (int step = 1; step < 6; step++) {
            g2.setColor(colorP2); g2.fillRect(280, 360 + (step - 1) * S, S, S);
            g2.setColor(colorP3); g2.fillRect(step * S, 280, S, S);
            g2.setColor(colorP4); g2.fillRect(280, step * S, S, S);
            g2.setColor(colorP1); g2.fillRect(360 + (step - 1) * S, 280, S, S);
        }

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1.8f));
        for (int step = 1; step < 6; step++) {
            g2.drawRect(280, 360 + (step - 1) * S, S, S);
            g2.drawRect(step * S, 280, S, S);
            g2.drawRect(280, step * S, S, S);
            g2.drawRect(360 + (step - 1) * S, 280, S, S);
        }

        // Casas de Saída
        g2.setColor(colorP2); g2.fillRect(240, 520, S, S);
        g2.setColor(Color.BLACK); g2.drawRect(240, 520, S, S);

        g2.setColor(colorP3); g2.fillRect(40, 240, S, S);
        g2.setColor(Color.BLACK); g2.drawRect(40, 240, S, S);

        g2.setColor(colorP4); g2.fillRect(320, 40, S, S);
        g2.setColor(Color.BLACK); g2.drawRect(320, 40, S, S);

        g2.setColor(colorP1); g2.fillRect(520, 320, S, S);
        g2.setColor(Color.BLACK); g2.drawRect(520, 320, S, S);

        // Círculos de Spawn
        Point[][] todosCaminhos = {player1Path, player2Path, player3Path, player4Path};
        for (int p = 0; p < 4; p++) {
            if (todosCaminhos[p] != null) {
                for (int i = 0; i < 4; i++) {
                    Point pSpawn = todosCaminhos[p][i];
                    if (pSpawn != null) {
                        g2.setColor(Color.WHITE);
                        g2.fillOval(pSpawn.x - 15, pSpawn.y - 15, 30, 30);
                        g2.setColor(Color.BLACK);
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.drawOval(pSpawn.x - 15, pSpawn.y - 15, 30, 30);
                    }
                }
            }
        }

        // Estrelas Safe Zone
        int[] indicesSeguros = {5, 13};
        for (int p = 0; p < 4; p++) {
            Point[] caminho = getCaminhoCasas(p);
            if (caminho != null && caminho.length > 13) {
                for (int index : indicesSeguros) {
                    Point casaSegura = caminho[index];
                    desenharEstrelaSafeZone(g2, casaSegura.x, casaSegura.y);
                }
            }
        }

        g2.setTransform(originalTransform);
        g2.dispose();
    }

    @Override
    protected void paintChildren(Graphics g) {
        // 1. Desenha os peões reais
        for (int i = getComponentCount() - 1; i >= 0; i--) {
            Component comp = getComponent(i);
            if (comp != slotP1 && comp != slotP2 && comp != slotP3 && comp != slotP4) {
                if (comp.isVisible()) {
                    Graphics cg = g.create(comp.getX(), comp.getY(), comp.getWidth(), comp.getHeight());
                    comp.paint(cg);
                    cg.dispose();
                }
            }
        }

        // 2. Inclinação exata da borda isométrica do tabuleiro (-0.44)
        double shearFactor = -0.44;

        paintRotatedSlot(g, slotP1, shearFactor);
        paintRotatedSlot(g, slotP2, shearFactor);
        paintRotatedSlot(g, slotP3, shearFactor);
        paintRotatedSlot(g, slotP4, shearFactor);

        // 3. CAMADA DE OVERLAY DO PREVIEW (Desenha sobre TODOS os peões!)
        drawPreviewOverlay(g);
    }

    /**
     * Desenha as bolinhas pulsantes, o anel luminoso e o peão fantasma
     * garantindo que eles fiquem POR CIMA dos peões reais.
     */
    private void drawPreviewOverlay(Graphics g) {
        if ((previewPath == null || previewPath.isEmpty()) && previewDestinationIndex == -1) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double scale = getDynamicScale();

        // A) DESENHA AS BOLINHAS PULSANTES DO CAMINHO
        if (previewPath != null && !previewPath.isEmpty()) {
            for (int k = 0; k < previewPath.size() - 1; k++) {
                int dotAlpha = pulseAnimator != null ? pulseAnimator.getAlphaForIndex(k) : 200;
                Point dotIso = convertToIsoPoint(previewPath.get(k));

                int dotX = dotIso.x;
                int dotY = dotIso.y - (int)(5 * scale); // Ligeira elevação para sobrepor peões
                int size = (int)(7 * scale); // REDUZIDO: de 12 para 7 para focar na discrição
                int halfSize = size / 2;

                // Contorno Branco Suave para Manter o Destaque em Casas Escuras
                g2.setColor(new Color(255, 255, 255, Math.min(255, dotAlpha + 50)));
                g2.fillOval(dotX - halfSize - 1, dotY - halfSize - 1, size + 2, size + 2);

                // Miolo Pulsante em Tom Cinza Escuro / Preto
                g2.setColor(new Color(50, 50, 50, dotAlpha));
                g2.fillOval(dotX - halfSize, dotY - halfSize, size, size);
            }
        }

        // B) ANEL DE DESTAQUE E PEÃO FANTASMA NA CASA DESTINO
        if (previewPawnIndex != -1 && previewDestinationIndex != -1) {
            Point destinationPoint = getCaminhoCasas(0)[previewDestinationIndex];
            PlayerPawn pawnToClone = getPlayerPawn(0, previewPawnIndex);

            if (destinationPoint != null) {
                Point isoDest = convertToIsoPoint(destinationPoint);

                // Anel / Aura Pulsante na base da casa
                int ringW = (int) (32 * scale);
                int ringH = (int) (16 * scale);
                int alphaPulse = pulseAnimator != null ? pulseAnimator.getAlphaForIndex(0) : 180;

                g2.setColor(new Color(255, 215, 0, Math.min(180, alphaPulse)));
                g2.fillOval(isoDest.x - ringW / 2, isoDest.y - ringH / 2, ringW, ringH);
                
                g2.setColor(new Color(255, 255, 255, 220));
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawOval(isoDest.x - ringW / 2, isoDest.y - ringH / 2, ringW, ringH);

                // Desenha o Peão Fantasma Transparente (65% Alpha)
                if (pawnToClone != null) {
                    int wP = (int) (20 * scale);
                    int hP = (int) (28 * scale);

                    PlayerPawn.draw3DPawn(
                        g2,
                        isoDest.x - (wP / 2),
                        isoDest.y - (int) (hP * 0.75),
                        wP,
                        hP,
                        pawnToClone.getPawnColor(),
                        0.65f
                    );
                }
            }
        }

        g2.dispose();
    }

    private void paintRotatedSlot(Graphics g, PlayerBoardSlot slot, double shearY) {
        if (slot == null || !slot.isVisible()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int cX = slot.getX() + slot.getWidth() / 2;
        int cY = slot.getY() + slot.getHeight() / 2;

        g2.translate(cX, cY);
        g2.shear(0.0, shearY); 
        g2.translate(-slot.getWidth() / 2, -slot.getHeight() / 2);

        slot.drawSlot(g2);

        g2.dispose();
    }

    private void desenharEstrelaSafeZone(Graphics2D g2d, int centerX, int centerY) {
        int radius = 12;
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];
        double angle = Math.PI / 2;

        for (int i = 0; i < 10; i++) {
            double r = (i % 2 == 0) ? radius : radius / 2.0;
            xPoints[i] = centerX + (int) (Math.cos(angle) * r);
            yPoints[i] = centerY - (int) (Math.sin(angle) * r);
            angle += Math.PI / 5;
        }

        g2d.setColor(new Color(255, 215, 0, 220)); 
        g2d.fillPolygon(xPoints, yPoints, 10);
        g2d.setColor(new Color(180, 80, 0)); 
        g2d.drawPolygon(xPoints, yPoints, 10);
    }

    private void addPlayerNameSlots() {
        Color colorP1 = colorName(player1Color);
        Color colorP2 = colorName(player2Color);
        Color colorP3 = colorName(player3Color);
        Color colorP4 = colorName(player4Color);

        slotP1 = new PlayerBoardSlot(player1Name, colorP1, 1.0);
        slotP2 = new PlayerBoardSlot(player2Name, colorP2, 1.0);
        slotP3 = new PlayerBoardSlot(player3Name, colorP3, 1.0);
        slotP4 = new PlayerBoardSlot(player4Name, colorP4, 1.0);

        this.add(slotP1);
        this.add(slotP2);
        this.add(slotP3);
        this.add(slotP4);

        repositionPlayerSlots();
    }

    private void repositionPlayerSlots() {
        if (slotP1 == null || slotP2 == null || slotP3 == null || slotP4 == null) return;

        double scale = getDynamicScale();

        int slotW = (int) (105 * scale); 
        int slotH = (int) (22 * scale);  

        Point posP1 = convertToIsoPoint(new Point(580, 470));
        slotP1.setBounds(posP1.x - slotW / 2, posP1.y - slotH / 2, slotW, slotH);

        Point posP2 = convertToIsoPoint(new Point(-5, 470));
        slotP2.setBounds(posP2.x - slotW / 2, posP2.y - slotH / 2, slotW, slotH);

        Point posP3 = convertToIsoPoint(new Point(-5, 105));
        slotP3.setBounds(posP3.x - slotW / 2, posP3.y - slotH / 2, slotW, slotH);

        Point posP4 = convertToIsoPoint(new Point(580, 105));
        slotP4.setBounds(posP4.x - slotW / 2, posP4.y - slotH / 2, slotW, slotH);
    }   

    private void sortPawnsByDepth() {
        List<PlayerPawn> allPawns = new ArrayList<>();
        for (int p = 0; p < 4; p++) {
            for (int i = 0; i < 4; i++) {
                if (playersPawns[p][i] != null) {
                    allPawns.add(playersPawns[p][i]);
                }
            }
        }

        allPawns.sort((p1, p2) -> Integer.compare(p1.getY(), p2.getY()));

        int totalPawns = allPawns.size();
        for (int i = 0; i < totalPawns; i++) {
            PlayerPawn pawn = allPawns.get(i);
            setComponentZOrder(pawn, totalPawns - 1 - i);
        }
    }

    public void updateActivePlayerSlot(int activePlayerId) {
        if (slotP1 != null) slotP1.setActiveTurn(activePlayerId == 0);
        if (slotP2 != null) slotP2.setActiveTurn(activePlayerId == 1);
        if (slotP3 != null) slotP3.setActiveTurn(activePlayerId == 2);
        if (slotP4 != null) slotP4.setActiveTurn(activePlayerId == 3);
    }

    public PlayerPawn getPlayerPawn(int playerId, int pawnIndex) {
        if (playerId >= 0 && playerId < 4 && pawnIndex >= 0 && pawnIndex < 4) {
            return this.playersPawns[playerId][pawnIndex];
        }
        return null;
    }

    public Point[] getCaminhoCasas(int playerId) {
        switch (playerId) {
            case 0: return this.player1Path;
            case 1: return this.player2Path;
            case 2: return this.player3Path;
            case 3: return this.player4Path;
            default: return this.player1Path;
        }
    }

    public String getGoldenPawnImagePath(int playerId) {
        return "/assets/pawn_gold.png";
    }
}