// Classe responsável por criar o tabuleiro do jogo

// Packages
package gui.windows;

// Imports internos
import gui.animations.AlphaPulseAnimator;
import gui.components.PlayerPawn;
import control.PawnControlManager;
import gui.events.BoardPawnMouseListener;

// Imports externos
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Dimension;
import java.awt.Point;

public class BoardScreen extends JPanel {

    // VARIÁVEIS DE INSTÂNCIA
    private String player1Name, player2Name, player3Name, player4Name;
    private String player1Color, player2Color, player3Color, player4Color;
    private static final double SCALE = 1.5;
    private static final Rectangle BOARD_SCREEN_BOUNDS = new Rectangle(
        0, 
        0, 
        (int)(600 * SCALE), 
        (int)(600 * SCALE)
    );
    private PlayerPawn[] player1Pawn;
    private java.awt.Point[] player1Path, player2Path, player3Path, player4Path;
    
    // Variáveis da Pré-visualização
    private int previewPawnIndex = -1; // -1 significa que não há o peão fantasma
    private int previewDestinationIndex = -1;
    private java.util.List<Point> previewPath = null;
    
    // O nosso novo animador modularizado
    private AlphaPulseAnimator pulseAnimator;

    
    // Construtor do tabuleiro
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
        
        // Instancia o animador passando este painel como referência para o repaint()
        this.pulseAnimator = new AlphaPulseAnimator(this);

        setBounds(BOARD_SCREEN_BOUNDS); // Tamanho e posição do tabuleiro (0, 0) (900x900)
        Dimension boardDimension = new Dimension(BOARD_SCREEN_BOUNDS.width, BOARD_SCREEN_BOUNDS.height);
        setPreferredSize(boardDimension);
        setOpaque(true);  // Mantém o fundo transparente para a imagem de fundo do jogo aparecer
        setLayout(null); // Layout nulo torna o painel absoluto 

        // Mapeia as coordenadas das casas
        pawnPath();

        JLabel player1Label = new JLabel(
            "JOGADOR 1: " + player1Name, 
            SwingConstants.CENTER
        );
         player1Label.setFont( new Font(
            "SansSerif",
             Font.BOLD,
             22
        ));

        String player1PawnImg = "/assets/peaoAzul_90x90.png"; // Substitua pelo nome real dos seus arquivos PNG
        
        /*
        * Instancia um array de tamanho 4 para armazenar objetos do tipo "PlayerPawn"
        * O array nasce vazio (null) e só serão ocupados pelos peões após o loop "for" abaixo
        */
        this.player1Pawn = new PlayerPawn[4]; 

        int j = 0;
        // Loop para instanciar os peões do jogador 1 (peão 1, 2, 3 e 4)
        for (int i = 0; i < 4; i++) {
            player1Pawn[i] =  new PlayerPawn(
                player1Name,
                player1PawnImg
            );
            System.out.println("Instanciando peão " + (i+1) + " do Jogador 1 com a imagem: " + player1PawnImg);
        }
        
        // Loop para atribuir a coordenada inicial dos pões do jogador 1
        for (int i = 0; i < 4; i++) {
            System.out.println(
                "Atribuindo coordenada visual para o peão " + (i+1) + " do Jogador 1 na casa " + j
            );
            if (player1Path != null && player1Path.length > 0) {

                // Coordenadas originais do path (sem offset)
                Point baseCoord = player1Path[j];
                Point pawnCurrentCoord = new java.awt.Point(baseCoord.x, baseCoord.y);
                
                player1Pawn[i].setPawnVisualCoordinates(pawnCurrentCoord);
                player1Pawn[i].setPawnCurrentPos(j);

                // Adiciona o peão ao tabuleiro
                this.add(player1Pawn[i]);
                System.out.println(
                    "-> Peão " + (i+1) + " adicionado. Coordenadas: X=" + pawnCurrentCoord.x + ", Y=" + pawnCurrentCoord.y + " | Tamanho: " + player1Pawn[i].getSize()
                );
            };
            j++;
        }
    }

    private Color colorName(String colorName) {
        if (colorName == null) return Color.GRAY;

        switch(colorName.toLowerCase()) {
            case "roxo": return new Color(107, 86, 165);
            case "azul": return new Color(80, 163, 213);
            case "rosa": return new Color(218, 99, 127);
            case "amarelo": return new Color(243, 177, 28);
            default: return Color.GRAY;
        }
    }

    // Ativa a pré-visualização visual, liga a animação e repinta a tela
    public void setPreviewData(int pawnIndex, int destIndex, java.util.List<Point> path) {
        this.previewPawnIndex = pawnIndex;
        this.previewDestinationIndex = destIndex;
        this.previewPath = path;
        
        if (this.pulseAnimator != null) {
            this.pulseAnimator.start(); // Liga o pulsar das bolinhas
        }
        
        this.repaint(); 
    }

    // Limpa a pré-visualização visual, desliga a animação e repinta a tela
    public void clearPreview() {
        this.previewPawnIndex = -1;
        this.previewDestinationIndex = -1;
        this.previewPath = null;
        
        if (this.pulseAnimator != null) {
            this.pulseAnimator.stop(); // Desliga o pulsar para economizar memória
        }
        
        this.repaint(); 
    }

    // Remove apenas a primeira bolinha da lista (Efeito Pac-Man)
    public void consumePreviewDot() {
        if (this.previewPath != null && !this.previewPath.isEmpty()) {
            this.previewPath.remove(0);
            this.repaint();
        }
    }

    // Esconde o fantasma, mas mantém as bolinhas intactas
    public void hidePreviewGhost() {
        this.previewDestinationIndex = -1;
        this.repaint();
    }

    /*
     * Mapeia as coordenadas físicas (X, Y) do centro de cada casa do circuito 
     * a partir do ponto de partida do Jogador 1 (Azul, Canto Inferior Esquerdo).
    */
    private void pawnPath() {
        
        player1Path = new java.awt.Point[] {
            // Local de "nascimento" / Spawn dos peões do jogador 1
            new java.awt.Point((int)(40 * SCALE), (int)(400 * SCALE)),  // [0] Casa inicial peão 1
            new java.awt.Point((int)(40 * SCALE), (int)(520 * SCALE)),  // [1] Casa inicial peão 2
            new java.awt.Point((int)(160 * SCALE), (int)(400 * SCALE)), // [2] Casa inicial peão 3
            new java.awt.Point((int)(160 * SCALE), (int)(520 * SCALE)), // [3] Casa inicial peão 4

            // Circuito do tabuleiro
            // Caminho do jogador 1 - subindo em direção ao centro do tabuleiro
            new java.awt.Point((int)(240 * SCALE), (int)(520 * SCALE)), // [4]
            new java.awt.Point((int)(240 * SCALE), (int)(480 * SCALE)), // [5]
            new java.awt.Point((int)(240 * SCALE), (int)(440 * SCALE)), // [6]
            new java.awt.Point((int)(240 * SCALE), (int)(400 * SCALE)), // [7]
            new java.awt.Point((int)(240 * SCALE), (int)(360 * SCALE)), // [8]
            
            // Caminho do jogador 1 - indo para esquerda em direção a borda esquerda do tabuleiro
            new java.awt.Point((int)(200 * SCALE), (int)(320 * SCALE)),  // [9]
            new java.awt.Point((int)(160 * SCALE), (int)(320 * SCALE)),  // [10]
            new java.awt.Point((int)(120 * SCALE), (int)(320 * SCALE)),  // [11]
            new java.awt.Point((int)(80 * SCALE),  (int)(320 * SCALE)),  // [12]
            new java.awt.Point((int)(40* SCALE),   (int)(320 * SCALE)),  // [13]
            new java.awt.Point((int)(0 * SCALE),    (int)(320 * SCALE)), // [14]

            // Caminho do jogador 1 - subindo no braço esquerdo do tabuleiro
            new java.awt.Point((int)(0 * SCALE),    (int)(280 * SCALE)), // [15]
            new java.awt.Point((int)(0 * SCALE),    (int)(240 * SCALE)), // [16]
            
            // Caminho do jogador 1 - indo para direita em direção ao centro do tabuleiro
            new java.awt.Point((int)(40 * SCALE),  (int)(240 * SCALE)), // [17]
            new java.awt.Point((int)(80 * SCALE),  (int)(240 * SCALE)), // [18]
            new java.awt.Point((int)(120 * SCALE), (int)(240 * SCALE)), // [19]
            new java.awt.Point((int)(160 * SCALE), (int)(240 * SCALE)), // [20]
            new java.awt.Point((int)(200 * SCALE), (int)(240 * SCALE)), // [21]

            // Caminho do jogador 1 - subindo em direção a borda superior do tabuleiro
            new java.awt.Point((int)(240 * SCALE), (int)(200 * SCALE)), // [22]
            new java.awt.Point((int)(240 * SCALE), (int)(160 * SCALE)), // [23]
            new java.awt.Point((int)(240 * SCALE), (int)(120 * SCALE)), // [24]
            new java.awt.Point((int)(240 * SCALE), (int)(80 * SCALE)),  // [25]
            new java.awt.Point((int)(240 * SCALE), (int)(40 * SCALE)),  // [26]
            new java.awt.Point((int)(240 * SCALE), (int)(0 * SCALE)),   // [27]
            
            // Caminho do jogador 1 - indo para direita no braço superior do tabuleiro
            new java.awt.Point((int)(280 * SCALE), (int)(0 * SCALE)),   // [28]
            new java.awt.Point((int)(320 * SCALE), (int)(0 * SCALE)),   // [29]

            // Caminho do jogador 1 - descendo para o centro do tabuleiro
            new java.awt.Point((int)(320 * SCALE), (int)(40 * SCALE)),  // [30]
            new java.awt.Point((int)(320 * SCALE), (int)(80 * SCALE)),  // [31]
            new java.awt.Point((int)(320 * SCALE), (int)(120 * SCALE)), // [32]
            new java.awt.Point((int)(320 * SCALE), (int)(160 * SCALE)), // [33]
            new java.awt.Point((int)(320 * SCALE), (int)(200 * SCALE)), // [34]
            
            // Caminho do jogador 1 - indo para direita em direção a borda do direita do tabuleiro
            new java.awt.Point((int)(360 * SCALE), (int)(240 * SCALE)), // [35]
            new java.awt.Point((int)(400 * SCALE), (int)(240 * SCALE)), // [36]
            new java.awt.Point((int)(440 * SCALE), (int)(240 * SCALE)), // [37]
            new java.awt.Point((int)(480 * SCALE), (int)(240 * SCALE)), // [38]
            new java.awt.Point((int)(520 * SCALE), (int)(240 * SCALE)), // [39]
            new java.awt.Point((int)(560 * SCALE), (int)(240 * SCALE)), // [40]
            
            // Caminho do jogador 1 - descendo no braço direito do tabuleiro
            new java.awt.Point((int)(560 * SCALE), (int)(280 * SCALE)), // [41]
            new java.awt.Point((int)(560 * SCALE), (int)(320 * SCALE)), // [42]
            
            // Caminho do jogador 1 - indo para esquerda em direção ao centro do tabuleiro
            new java.awt.Point((int)(520 * SCALE), (int)(320 * SCALE)), // [43]
            new java.awt.Point((int)(480 * SCALE), (int)(320 * SCALE)), // [44]
            new java.awt.Point((int)(440 * SCALE), (int)(320 * SCALE)), // [45]
            new java.awt.Point((int)(400 * SCALE), (int)(320 * SCALE)), // [46]
            new java.awt.Point((int)(360 * SCALE), (int)(320 * SCALE)), // [47]

            // Caminho do jogador 1 - descendo em direção a borda inferior do tabuleiro
            new java.awt.Point((int)(320 * SCALE), (int)(360 * SCALE)), // [48]
            new java.awt.Point((int)(320 * SCALE), (int)(400 * SCALE)), // [49]   
            new java.awt.Point((int)(320 * SCALE), (int)(440 * SCALE)), // [50]
            new java.awt.Point((int)(320 * SCALE), (int)(480 * SCALE)), // [51]
            new java.awt.Point((int)(320 * SCALE), (int)(520 * SCALE)), // [52]
            new java.awt.Point((int)(320 * SCALE), (int)(560 * SCALE)), // [53]
            
            // Caminho do jogador 1 - indo para esquerda no braço inferior do tabuleiro
            new java.awt.Point((int)(280 * SCALE), (int)(560 * SCALE)), // [54]
            
            // Caminho do jogador 1 - subindo em direção ao centro do tabuleiro (caminho final do jogador 1)
            new java.awt.Point((int)(280 * SCALE), (int)(520 * SCALE)), // [55]
            new java.awt.Point((int)(280 * SCALE), (int)(480 * SCALE)), // [56]
            new java.awt.Point((int)(280 * SCALE), (int)(440 * SCALE)), // [57]
            new java.awt.Point((int)(280 * SCALE), (int)(400 * SCALE)), // [58]
            new java.awt.Point((int)(280 * SCALE), (int)(360 * SCALE)), // [59]
            new java.awt.Point((int)(280 * SCALE), (int)(320 * SCALE))  // [60] Casa final (centro)
        };
    }

    public PlayerPawn getPlayerPawn(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            return player1Pawn[pawnIndex];
        }
        return null;
    }

    public void startBoardPawnShake(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            PlayerPawn playerPawn = player1Pawn[pawnIndex]; 
            if (playerPawn != null) {
                playerPawn.startBoardPawnShake();
            }
        }
    }

    public void stopBoardPawnShake(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            PlayerPawn playerPawn = player1Pawn[pawnIndex]; 
            if (playerPawn != null) {
                playerPawn.stopBoardPawnShake();
            }
        }
    }


    /**
    ** Método para desenhar o tabuleiro do jogo
    */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Base do tabuleiro
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, (int) (getWidth() * SCALE), (int) (getHeight() * SCALE));

        // Borda preta pequena para demarcar o tabuleiro
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, (int) (getWidth() * SCALE) - 1, (int) (getHeight() * SCALE) - 1);

        // Bases de cada jogador    
        Color colorP1 = colorName(player1Color); 
        Color colorP2 = colorName(player2Color); 
        Color colorP3 = colorName(player3Color); 
        Color colorP4 = colorName(player4Color); 
        
        Color[] colors = {colorP2, colorP1, colorP3, colorP4};
        int i = 0;
        
        // Loop para construir os quadrados da base
        for (int x = 0; x <= 360; x += 360) {
            for (int y = 0; y <= 360; y += 360) {
                g2.setColor(colors[i]);
                g2.fillRect((int) (x * SCALE), (int) (y * SCALE), (int) (240 * SCALE), (int) (240 * SCALE));

                g2.setColor(Color.BLACK);
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawRect((int) (x * SCALE), (int) (y * SCALE), (int) (240 * SCALE), (int)(240 * SCALE));
                i++; 
            }        
        }
        
        g2.setColor(Color.BLACK);
        g2.setStroke(new java.awt.BasicStroke(1));

        // Casas horizontais e verticais...
        for (int x = 0; x <= 600; x += 40) {
            for (int y = 240; y <= 320; y += 40) {
                if (x >= 240 && x <= 320) continue; 
                g2.drawRect((int) (x * SCALE), (int) (y * SCALE), (int) (40 * SCALE), (int) (40 * SCALE));
            }
        }

        for (int x = 240; x <= 320; x += 40) {
            for (int y = 0; y <= 600; y += 40) {
                if (y >= 240 && y <= 320) continue; 
                g2.drawRect((int) (x * SCALE), (int) (y * SCALE), (int) (40 * SCALE), (int) (40 * SCALE));
            }
        }

        // Triângulos centrais
        Polygon downTrianglePolygon = new Polygon();
        Polygon leftTrianglePolygon = new Polygon();
        Polygon upTrianglePolygon = new Polygon();
        Polygon rightTrianglePolygon = new Polygon();

        downTrianglePolygon.addPoint((int) (240 * SCALE), (int) (360 * SCALE)); 
        downTrianglePolygon.addPoint((int) (360 * SCALE), (int) (360 * SCALE)); 
        downTrianglePolygon.addPoint((int) (300 * SCALE), (int) (300 * SCALE)); 

        upTrianglePolygon.addPoint((int) (240 * SCALE), (int) (240 * SCALE));   
        upTrianglePolygon.addPoint((int) (360 * SCALE), (int) (240 * SCALE));   
        upTrianglePolygon.addPoint((int) (300 * SCALE), (int) (300 * SCALE));   

        rightTrianglePolygon.addPoint((int) (360 * SCALE), (int) (240 * SCALE)); 
        rightTrianglePolygon.addPoint((int) (360 * SCALE), (int) (360 * SCALE)); 
        rightTrianglePolygon.addPoint((int) (300 * SCALE), (int) (300 * SCALE)); 

        leftTrianglePolygon.addPoint((int) (240 * SCALE), (int) (240 * SCALE));  
        leftTrianglePolygon.addPoint((int) (240 * SCALE), (int) (360 * SCALE));  
        leftTrianglePolygon.addPoint((int) (300 * SCALE), (int) (300 * SCALE));  

        Polygon[] trianglePolygon = {leftTrianglePolygon, downTrianglePolygon, upTrianglePolygon, rightTrianglePolygon};
        i = 0; 
        for (int j = 0; j < trianglePolygon.length; j++) {
            g2.setColor(colors[i]);
            g2.fillPolygon(trianglePolygon[j]);
            g2.setColor(Color.BLACK);
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.drawPolygon(trianglePolygon[j]);
            i++;
        }
        
        // Trilha colorida horizontal e vertical...
        for (int x = 40; x <= 520; x += 40) {
            if (x >= 240 && x <= 320) continue; 
            if (x < 240) g2.setColor(colorP2); 
            else g2.setColor(colorP4); 
        
            g2.fillRect((int) (x * SCALE), (int) (280 * SCALE), (int) (40 * SCALE), (int) (40 * SCALE));
            g2.setColor(Color.BLACK);
            g2.setStroke(new java.awt.BasicStroke(1));
            g2.drawRect((int) (x * SCALE), (int) (280 * SCALE), (int) (40 * SCALE), (int) (40 * SCALE));
        }

        for (int y = 40; y <= 520; y += 40) {
            if (y >= 240 && y <= 320) continue; 
            if (y < 240) g2.setColor(colorP3); 
            else g2.setColor(colorP1); 
            
            g2.fillRect((int) (280 * SCALE), (int) (y * SCALE), (int) (40 * SCALE), (int) (40 * SCALE));
            g2.setColor(Color.BLACK);
            g2.setStroke(new java.awt.BasicStroke(1));
            g2.drawRect((int) (280 * SCALE), (int) (y * SCALE), (int) (40 * SCALE), (int) (40 * SCALE));
        }

        // Casas de saída...
        for (int x = 40; x <= 520; x += 480) { 
            for (int y = 240; y <= 320; y += 80) { 
                if ((x == 40 && y == 240) || (x == 520 && y == 320)) {
                    if (x == 40) g2.setColor(colorP2); 
                    else g2.setColor(colorP4); 

                    g2.fillRect((int) (x * SCALE), (int) (y * SCALE), (int) (40 * SCALE), (int) (40 * SCALE));
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new java.awt.BasicStroke(1));
                    g2.drawRect((int) (x * SCALE), (int) (y * SCALE), (int) (40 * SCALE), (int) (40 * SCALE));
                }
            }        
        }

        for (int x = 240; x <= 320; x += 80) { 
            for (int y = 40; y <= 520; y += 480) { 
                if ((x == 320 && y == 40) || (x == 240 && y == 520)) {
                    if (y == 40) g2.setColor(colorP3); 
                    else g2.setColor(colorP1); 

                    g2.fillRect((int) (x * SCALE), (int) (y * SCALE), (int) (40 * SCALE), (int) (40 * SCALE));
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new java.awt.BasicStroke(1));
                    g2.drawRect((int) (x * SCALE), (int) (y * SCALE), (int) (40 * SCALE), (int) (40 * SCALE));
                }
            }
        }

        g2.setColor(Color.WHITE); 
       
        g2.setColor(Color.WHITE); 
       
        if (this.player1Path != null) {
            int tileSize = (int) (40 * SCALE);
            int diametroCirculo = (int) (65 * SCALE); 
            
            for (i = 0; i <= 3; i++) {
                java.awt.Point pontoSpawn = this.player1Path[i];
                if (pontoSpawn != null) {
                    // O centro da casa é a ponta (X/Y) + metade do tamanho da casa
                    int centerX = pontoSpawn.x + (tileSize / 2);
                    int centerY = pontoSpawn.y + (tileSize / 2);

                    int xCirculo = centerX - (diametroCirculo / 2);
                    int yCirculo = centerY - (diametroCirculo / 2);

                    g2.fillOval(xCirculo, yCirculo, diametroCirculo, diametroCirculo);
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new java.awt.BasicStroke(1));
                    g2.drawOval(xCirculo, yCirculo, diametroCirculo, diametroCirculo);
                    g2.setColor(Color.WHITE); 
                }
            }
        }

        // --- NOVO BLOCO: DESENHAR A PRÉ-VISUALIZAÇÃO (SE ESTIVER ATIVA) ---
        java.awt.Graphics2D g2dPreview = (java.awt.Graphics2D) g.create();
        int tamanhoCasa = (int) (40 * SCALE);
        
       // 1. Desenhar o caminho (Pontos pretos) - Desenha sempre que houver um caminho
        if (previewPath != null && !previewPath.isEmpty()) {
            int diametroPonto = 12;
            
            for (int k = 0; k < previewPath.size(); k++) {
                // Se for o último ponto da lista (destino), não desenha a bolinha
                if (k == previewPath.size() - 1) { continue; }
                
                // CORREÇÃO: Pega a transparência individual da onda para a bolinha 'k'
                int dotAlpha = pulseAnimator.getAlphaForIndex(k);
                g2dPreview.setColor(new java.awt.Color(0, 0, 0, dotAlpha));
                
                Point p = previewPath.get(k);
                int dotX = p.x + (tamanhoCasa / 2) - (diametroPonto / 2);
                int dotY = p.y + (tamanhoCasa / 2) - (diametroPonto / 2);
                g2dPreview.fillOval(dotX, dotY, diametroPonto, diametroPonto);
            }
        }

        // 2. Desenhar o Peão Fantasma (Apenas se o índice dele não tiver sido apagado)
        if (previewPawnIndex != -1 && previewDestinationIndex != -1) {
            Point destinationPoint = getCaminhoCasas()[previewDestinationIndex];
            PlayerPawn pawnToClone = getPlayer1Pawn(previewPawnIndex);
            
            if (pawnToClone != null && destinationPoint != null) {
                g2dPreview.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.5f));
                
                java.awt.Image pawnImg = pawnToClone.getPawnImage(); 
                if (pawnImg != null) {
                    int larguraPeao = (int) (12 * SCALE);
                    int alturaPeao = (int) (17 * SCALE);
                    int ghostX = destinationPoint.x + (tamanhoCasa / 2) - (larguraPeao / 2);
                    int ghostY = destinationPoint.y + (tamanhoCasa / 2) - (alturaPeao / 2);
                    g2dPreview.drawImage(pawnImg, ghostX, ghostY, larguraPeao, alturaPeao, null);
                }
            }
        }
        g2dPreview.dispose();
    }

    public PlayerPawn getPlayer1Pawn(int index) {
        if (index >= 0 && index < player1Pawn.length) {
            return this.player1Pawn[index];
        }
        return null;
    }

    public java.awt.Point[] getCaminhoCasas() {
        return this.player1Path;
    }
}