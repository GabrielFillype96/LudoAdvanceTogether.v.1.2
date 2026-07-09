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
    private PlayerPawn[][] playersPawns;
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

        // Instancia a matriz para 4 jogadores, cada um com 4 peões
        this.playersPawns = new PlayerPawn[4][4]; 
        
        String[] playerNames = {player1Name, player2Name, player3Name, player4Name};
        
        // ATENÇÃO: Substitua os nomes dos arquivos PNG de acordo com as cores reais que tem na sua pasta assets!
        String[] pawnImages = {
            "/assets/peaoAzul_90x90.png",    // Jogador 1
            "/assets/peaoRoxo_90x90.png", // Jogador 2 (CPU 1)
            "/assets/peaoRosa_90x90.png",    // Jogador 3 (CPU 2)
            "/assets/peaoAmarelo_90x90.png"   // Jogador 4 (CPU 3)
        };
        
        // Array com os caminhos (paths) de todos os jogadores para facilitar a leitura no loop
        java.awt.Point[][] allPaths = {player1Path, player2Path, player3Path, player4Path};

        // Loop Duplo: Cria os 16 peões e coloca-os no ecrã
        for (int p = 0; p < 4; p++) { // 'p' é o ID do Jogador (0 a 3)
            for (int i = 0; i < 4; i++) { // 'i' é o ID do Peão (0 a 3)
                
                // 1. Instancia o peão com o nome e imagem correta
                playersPawns[p][i] = new PlayerPawn(playerNames[p], pawnImages[p]);
                
                // 2. Coloca o peão na coordenada da sua respectiva base (se o path já existir)
                if (allPaths[p] != null && allPaths[p].length > 0) {
                    Point baseCoord = allPaths[p][i]; // As posições 0, 1, 2 e 3 do path são a base
                    Point pawnCurrentCoord = new java.awt.Point(baseCoord.x, baseCoord.y);
                    
                    playersPawns[p][i].setPawnVisualCoordinates(pawnCurrentCoord);
                    playersPawns[p][i].setPawnCurrentPos(i); // Casa 0, 1, 2 ou 3 da base
                }
                
                // 3. Adiciona o peão ao painel
                this.add(playersPawns[p][i]);
            }
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

       // =========================================================================
        // AUTOMAÇÃO DOS CAMINHOS DAS CPUS (Com correção de alinhamento pelo centro)
        // =========================================================================
        int centroTabuleiro = (int) (300 * SCALE); 
        int offsetCentroCasa = (int) (20 * SCALE); // A casa tem 40, então o centro é 20
        
        this.player2Path = new Point[player1Path.length];
        this.player3Path = new Point[player1Path.length];
        this.player4Path = new Point[player1Path.length];

        for (int i = 0; i < player1Path.length; i++) {
            Point p1 = player1Path[i];
            if (p1 == null) continue;

            // 1. Acha o centro exato da casa do Jogador 1
            int centroX = p1.x + offsetCentroCasa;
            int centroY = p1.y + offsetCentroCasa;

            // 2. Calcula a distância do centro da casa até o centro do tabuleiro
            int dx = centroX - centroTabuleiro;
            int dy = centroY - centroTabuleiro;

            // 3. Rotaciona e subtrai o offset para devolver ao canto superior esquerdo
            this.player2Path[i] = new Point((centroTabuleiro - dy) - offsetCentroCasa, (centroTabuleiro + dx) - offsetCentroCasa);
            this.player3Path[i] = new Point((centroTabuleiro - dx) - offsetCentroCasa, (centroTabuleiro - dy) - offsetCentroCasa);
            this.player4Path[i] = new Point((centroTabuleiro + dy) - offsetCentroCasa, (centroTabuleiro - dx) - offsetCentroCasa);
        }
    } 

    public void startBoardPawnShake(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            // Usa o ID 0 para pegar o peão do Jogador 1 na matriz
            PlayerPawn playerPawn = playersPawns[0][pawnIndex]; 
            if (playerPawn != null) {
                playerPawn.startBoardPawnShake();
            }
        }
    }

    public void stopBoardPawnShake(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            // Usa o ID 0 para pegar o peão do Jogador 1 na matriz
            PlayerPawn playerPawn = playersPawns[0][pawnIndex]; 
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
       
        // Junta os caminhos de todos os jogadores num array para facilitar
        java.awt.Point[][] todosCaminhos = {
            this.player1Path, 
            this.player2Path, 
            this.player3Path, 
            this.player4Path
        };
        
        int tileSize = (int) (40 * SCALE);
        int diametroCirculo = (int) (65 * SCALE); 
        
        // Loop pelos 4 jogadores
        for (int p = 0; p < 4; p++) {
            if (todosCaminhos[p] != null) {
                // Loop pelas 4 bases de cada jogador (posições 0, 1, 2, 3)
                for (i = 0; i <= 3; i++) {
                    java.awt.Point pontoSpawn = todosCaminhos[p][i];
                    
                    if (pontoSpawn != null) {
                        // O centro da casa é a ponta (X/Y) + metade do tamanho da casa
                        int centerX = pontoSpawn.x + (tileSize / 2);
                        int centerY = pontoSpawn.y + (tileSize / 2);

                        int xCirculo = centerX - (diametroCirculo / 2);
                        int yCirculo = centerY - (diametroCirculo / 2);

                        g2.setColor(Color.WHITE);
                        g2.fillOval(xCirculo, yCirculo, diametroCirculo, diametroCirculo);
                        
                        g2.setColor(Color.BLACK);
                        g2.setStroke(new java.awt.BasicStroke(1));
                        g2.drawOval(xCirculo, yCirculo, diametroCirculo, diametroCirculo);
                    }
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
            Point destinationPoint = getCaminhoCasas(0)[previewDestinationIndex];
            PlayerPawn pawnToClone = getPlayerPawn(0, previewPawnIndex);
            
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

        // Transforma o Graphics normal em Graphics2D (se já não estiver feito)
    Graphics2D g2d = (Graphics2D) g;

    // Desenha as Estrelas de Zona Segura usando os índices 4 (Saída) e 12 (Estrela)
    int[] indicesSeguros = {4, 12};
    for (int p = 0; p < 4; p++) {
        Point[] caminho = getCaminhoCasas(p);
        if (caminho != null && caminho.length > 12) {
            for (int index : indicesSeguros) {
                Point casaSegura = caminho[index];
                // Se a sua casa tem 30x30, some metade do tamanho para centralizar a estrela
                desenharEstrelaSafeZone(g2d, casaSegura.x + (tamanhoCasa / 2), casaSegura.y + (tamanhoCasa / 2));
            }
        }
    }
    }

    /**
     * Desenha uma estrela dourada translúcida numa coordenada (X,Y).
     * Ideal para marcar as Zonas Seguras do Ludo.
     */
    private void desenharEstrelaSafeZone(Graphics2D g2d, int centerX, int centerY) {
        int radius = (int) (12 * SCALE); // Tamanho da estrela ajustável
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];
        double angle = Math.PI / 2; // Começa no topo

        for (int i = 0; i < 10; i++) {
            double r = (i % 2 == 0) ? radius : radius / 2.0;
            xPoints[i] = centerX + (int) (Math.cos(angle) * r);
            yPoints[i] = centerY - (int) (Math.sin(angle) * r);
            angle += Math.PI / 5;
        }

        g2d.setColor(new Color(255, 215, 0, 200)); // Dourado brilhante com transparência
        g2d.fillPolygon(xPoints, yPoints, 10);
        g2d.setColor(Color.ORANGE);
        g2d.drawPolygon(xPoints, yPoints, 10);
    }

    /**
     * Método universal para aceder a qualquer peão de qualquer jogador.
     * @param playerId O índice do jogador (0 = J1, 1 = J2, 2 = J3, 3 = J4)
     * @param pawnIndex O índice do peão (0 a 3)
     */
    public PlayerPawn getPlayerPawn(int playerId, int pawnIndex) {
        if (playerId >= 0 && playerId < 4 && pawnIndex >= 0 && pawnIndex < 4) {
            return this.playersPawns[playerId][pawnIndex];
        }
        return null;
    }

    /**
     * Retorna o caminho de casas (rota) específico para cada jogador.
     * @param playerId O índice do jogador (0 = Azul/J1, 1 = Roxo/CPU1, 2 = Rosa/CPU2, 3 = Amarelo/CPU3)
     */
    public java.awt.Point[] getCaminhoCasas(int playerId) {
        switch (playerId) {
            case 0: return this.player1Path;
            case 1: return this.player2Path;
            case 2: return this.player3Path;
            case 3: return this.player4Path;
            default: return this.player1Path; // Retorno de segurança
        }
    }
}