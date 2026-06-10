// Classe responsável por criar o tabuleiro do jogo

// Packages
package gui.windows;

// Imports internos
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
    private PawnControlManager pawnControlManager;
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

        // Loop para instanciar os peões do jogador 1 (peão 1, 2, 3 e 4)
        for (int i = 0; i < 4; i++) {
            player1Pawn[i] =  new PlayerPawn(
                player1Name,
                player1PawnImg
            );
            System.out.println("Instanciando peão " + (i+1) + " do Jogador 1 com a imagem: " + player1PawnImg);

            // Instancia um objeto da classe "BoardPawnMouseListener"
            // "MouseAdapter" é uma classe abstrata nativa do Java
            player1Pawn[i].addMouseListener(new BoardPawnMouseListener(pawnControlManager, i));
        }
        
        int j = 0;
        //Loop para atribuir a coordenada inicial dos pões do jogador 1
        for (int i = 0; i < 4; i++) {
            System.out.println(
                "Atribuindo coordenada visual para o peão " + (i+1) + " do Jogador 1 na casa " + j
            );
            if (player1Path != null && player1Path.length > 0) {

                // Coordenadas originais do path
                Point baseCoord = player1Path[j];

                // Offset para ajustar a posição de nascimento do peão
                int offsetX = 12;
                int offsetY = 5;

                // Cria uma nova coordenada para os peões da base, permitindo assim que eles nasçam na posição correta dentro do círculo
                Point pawnCurrentCoord = new java.awt.Point(baseCoord.x + offsetX, baseCoord.y + offsetY);
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

        control.GameManager.setBoardGame(this);
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

    /**
    * Mapeia as coordenadas físicas (X, Y) do centro de cada casa do circuito 
    * a partir do ponto de partida do Jogador 1 (Azul, Canto Inferior Esquerdo).
    */
    private void pawnPath() {
        
        // CORREÇÃO: Removido o "player1Path[X] =" de dentro da atribuição
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

        // Quando for criar os outros jogadores, você fará exatamente igual para eles aqui:
        // player2Path = new java.awt.Point[] { ... };
        // player3Path = new java.awt.Point[] { ... };
        // player4Path = new java.awt.Point[] { ... };
    }

    public PlayerPawn getPlayerPawn(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            return player1Pawn[pawnIndex];
        }
        return null;
    }

    public void startBoardPawnShake(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            // Se o índice do peão ("pawnIndex") for maior ou igual a 0 E menor ou igual a 4, armazena o índice do peão do tabuleiro do jogador
            PlayerPawn playerPawn = player1Pawn[pawnIndex]; // Aqui não está sendo criado um objeto novo, e sim uma referência com o "molde" da classe "PlayerPawn"
            
            // A "BoardScreen" aplica o shake no peão do tabuleiro do jogador
            if (playerPawn != null) {
                // Se o peão do tabuleiro do jogador não for nulo, executa o método do shake
                playerPawn.startBoardPawnShake();
            }
        }
    }

    public void stopBoardPawnShake(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            // Se o índice do peão ("pawnIndex") for maior ou igual a 0 E menor ou igual a 4, armazena o índice do peão do tabuleiro do jogador
            PlayerPawn playerPawn = player1Pawn[pawnIndex]; // Aqui não está sendo criado um objeto novo, e sim uma referência com o "molde" da classe "PlayerPawn"
            
            // A "BoardScreen" deixa de aplicar o shake no peão do tabuleiro do jogador
            if (playerPawn != null) {
                // Se o peão do tabuleiro do jogador não for nulo, deixa de executar o método do shake
                playerPawn.stopBoardPawnShake();
            }
        }
    }


    // Método para desenhar o tabuleiro do jogo
    // @Override indica que o método "paintComponent" está sendo sobrescrito da classe pai (JPanel). Serve como uma espécie de "guarda-costas" para garantir que estamos realmente sobrescrevendo um método existente e não criando um novo método por engano.
    @Override
    // O método "paintComponent" é chamado sempre que o painel precisa ser redesenhado, permitindo que personalizemos a aparência do tabuleiro
    // Visibilidade "protected" para que apenas classes dentro do mesmo pacote ou subclasses possam acessar este método
    protected void paintComponent(Graphics g) {
        // Estrutura padrão do "paintComponent" para garantir que o fundo seja desenhado corretamente
        super.paintComponent(g);

        // Cria um contexto gráfico 2D para aplicar renderizações avançadas (como anti-aliasing)
        Graphics2D g2 = (Graphics2D) g;

        // Habilita o anti-aliasing para suavizar as bordas das imagens desenhadas
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Base do tabuleiro
        g2.setColor(Color.WHITE);
        g2.fillRect(
            0, 
            0, 
            (int) (getWidth() * SCALE), 
            (int) (getHeight() * SCALE)
        );

        // Borda preta pequena para demarcar o tabuleiro
        g2.setColor(Color.BLACK);
        g2.drawRect(
            0, 
            0,
            (int) (getWidth() * SCALE) - 1, 
            (int) (getHeight() * SCALE) - 1
        );

        // Bases de cada jogador    
        Color colorP1 = colorName(player1Color); // P1
        Color colorP2 = colorName(player2Color); // P2
        Color colorP3 = colorName(player3Color); // P3
        Color colorP4 = colorName(player4Color); // P4
        
        // Array para as cores de cada jogador
        Color[] colors = {colorP2, colorP1, colorP3, colorP4};
        // Variável para cada índice da array
        int i = 0;
        
        
        // Loop para construir os quadrados da base e atribuir a cor
        for (int x = 0; x <= 360; x += 360) {
            for (int y = 0; y <= 360; y += 360) {
                // Print no terminal para controle
                System.out.println("x: " + x + " | y: " + y + " color: " + colors[i]);

                // Desenha e atribui a cor para cada base
                g2.setColor(colors[i]);
                g2.fillRect(
                    (int) (x * SCALE), 
                    (int) (y * SCALE), 
                    (int) (240 * SCALE), 
                    (int) (240 * SCALE)
                );

                // Desenha uma borda preta ao redor de cada base e sua espessura
                g2.setColor(Color.BLACK);
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawRect(
                    (int) (x * SCALE), 
                    (int) (y * SCALE), 
                    (int) (240 * SCALE), 
                    (int)(240 * SCALE)
                );
                i++; // Aumenta o índice para percorrer todas as cores
                
            }        
        }
        
        // Define a cor e a espessura das bordas das casas
        g2.setColor(Color.BLACK);
        g2.setStroke(new java.awt.BasicStroke(1));

        // Loop para construir os quadrados das casas horizontais
        for (int x = 0; x <= 600; x += 40) {
            for (int y = 240; y <= 320; y += 40) {
                if (x >= 240 && x <= 320) continue; // Salta uma etapa da repetição não preencher o centro do tabuleiro

                // Print no terminal para controle
                System.out.println("x: " + x + " | y: " + y);

                // Desenha os quadrados das casas horizontais
                g2.drawRect(
                    (int) (x * SCALE), 
                    (int) (y * SCALE), 
                    (int) (40 * SCALE),
                    (int) (40 * SCALE)
                );
            }
        }

        // Loop para construir os quadrados das casas verticais
        for (int x = 240; x <= 320; x += 40) {
            for (int y = 0; y <= 600; y += 40) {
                if (y >= 240 && y <= 320) continue; // Salta uma etapa da repetição não preencher o centro do tabuleiro

                // Print no terminal para controle
                System.out.println("x: " + x + " | y: " + y);

                // Desenha os quadrados das casas verticais
                g2.drawRect(
                    (int) (x * SCALE), 
                    (int) (y * SCALE), 
                    (int) (40 * SCALE),
                    (int) (40 * SCALE)
                );
            }
        }

        // Desenho do quadrado central
        // Desenha o triângulo debaixo do jogador 1
        Polygon downTrianglePolygon = new Polygon();
        Polygon leftTrianglePolygon = new Polygon();
        Polygon upTrianglePolygon = new Polygon();
        Polygon rightTrianglePolygon = new Polygon();

        // Montando o triângulo de Baixo (Geralmente associado ao P1)
        downTrianglePolygon.addPoint((int) (240 * SCALE), (int) (360 * SCALE)); // Quina inferior esquerda
        downTrianglePolygon.addPoint((int) (360 * SCALE), (int) (360 * SCALE)); // Quina inferior direita
        downTrianglePolygon.addPoint((int) (300 * SCALE), (int) (300 * SCALE)); // Centro perfeito

        // Montando o triângulo de Cima (Geralmente associado ao P3)
        upTrianglePolygon.addPoint((int) (240 * SCALE), (int) (240 * SCALE));   // Quina superior esquerda
        upTrianglePolygon.addPoint((int) (360 * SCALE), (int) (240 * SCALE));   // Quina superior direita
        upTrianglePolygon.addPoint((int) (300 * SCALE), (int) (300 * SCALE));   // Centro perfeito

        // Montando o triângulo da Direita (Geralmente associado ao P4)
        rightTrianglePolygon.addPoint((int) (360 * SCALE), (int) (240 * SCALE)); // Quina superior direita
        rightTrianglePolygon.addPoint((int) (360 * SCALE), (int) (360 * SCALE)); // Quina inferior direita
        rightTrianglePolygon.addPoint((int) (300 * SCALE), (int) (300 * SCALE)); // Centro perfeito

        // Montando o triângulo da Esquerda (Geralmente associado ao P2)
        leftTrianglePolygon.addPoint((int) (240 * SCALE), (int) (240 * SCALE));  // Quina superior esquerda
        leftTrianglePolygon.addPoint((int) (240 * SCALE), (int) (360 * SCALE));  // Quina inferior esquerda
        leftTrianglePolygon.addPoint((int) (300 * SCALE), (int) (300 * SCALE));  // Centro perfeito
      

        // Array para os triângulos de cada jogador
        Polygon[] trianglePolygon = {leftTrianglePolygon, downTrianglePolygon, upTrianglePolygon, rightTrianglePolygon};
        
        // Variável para servir de índice da array do polígono
        i = 0; // Como foi declarada anteriormente e utilizada no loop das cores das bases, seu valor estava como "4". Repassada novamente para 0 seu valor e ser utilizado no loop abaixo

        // Loop para aplicação da cor e bordas de cada triângulo
        for (int j = 0; j < trianglePolygon.length; j++) {
            g2.setColor(colors[i]);
            g2.fillPolygon(trianglePolygon[j]);
            g2.setColor(Color.BLACK);
            g2.setStroke(new java.awt.BasicStroke(2));
            g2.drawPolygon(trianglePolygon[j]);
            i++;
        }
        
        // Loop para aplicar a cor na trilha do jogador 2 e 4 (esquerda para direita)
        for (int x = 40; x <= 520; x += 40) {
            if (x >= 240 && x <= 320) continue; // Salta uma etapa da repetição não preencher o centro do tabuleiro
    
            // Se a coord. "x" for menor que 240 utilizara a cor do jogador 2. Se for maior usa a cor do jogador 4
            if (x < 240) {
                g2.setColor(colorP2); // Cor do Jogador 2
            } else {
                g2.setColor(colorP4); // Cor do Jogador 4
            }
        
            // Desenha o quadrado uma única vez no código
            g2.fillRect(
                (int) (x * SCALE), 
                (int) (280 * SCALE), 
                (int) (40 * SCALE), 
                (int) (40 * SCALE)
            );
        
            // Desenha a borda
            g2.setColor(Color.BLACK);
            g2.setStroke(new java.awt.BasicStroke(1));
            g2.drawRect(
                (int) (x * SCALE), 
                (int) (280 * SCALE), 
                (int) (40 * SCALE), 
                (int) (40 * SCALE)
            );
        }

        // Loop para aplicar a cor na trilha do jogador 1 e 3 (cima para baixo)
        for (int y = 40; y <= 520; y += 40) {
            if (y >= 240 && y <= 320) continue; // Salta uma etapa da repetição não preencher o centro do tabuleiro
            
            // Se a coord. "y" for menor que 240 utilizara a cor do jogador 1. Se for maior usa a cor do jogador 3
            if (y < 240) {
                g2.setColor(colorP3); // Cor do Jogador 1
            } else {
                g2.setColor(colorP1); // Cor do Jogador 3
            }
        
            // Desenha o quadrado uma única vez no código
            g2.fillRect(
                (int) (280 * SCALE), 
                (int) (y * SCALE),
                (int) (40 * SCALE), 
                (int) (40 * SCALE)
            );
        
            // Desenha a borda
            g2.setColor(Color.BLACK);
            g2.setStroke(new java.awt.BasicStroke(1));
            g2.drawRect(
                (int) (280 * SCALE), 
                (int) (y * SCALE),
                (int) (40 * SCALE), 
                (int) (40 * SCALE)
            );
        }

        i = 0; // Como foi declarada anteriormente e utilizada no loop das cores dos triângulos, seu valor estava como "4". Repassada novamente para 0 seu valor e ser utilizado no loop abaixo

        // Loop para construir os quadrados da casa de saída dos jogadores 2 e 4 (direita e esquerda)
        for (int x = 40; x <= 520; x += 480) { // x assume 40 e 520
            for (int y = 240; y <= 320; y += 80) { // y assume 240 e 320
                
                // Se for a combinação da esquerda (40, 240) OU a combinação da direita (520, 320)
                if ((x == 40 && y == 240) || (x == 520 && y == 320)) {

                    // Se o x for 40, é o lado esquerdo (Jogador 2)
                    if (x == 40) {
                        g2.setColor(colorP2); // Cor do jogador 2
                    } else {
                        g2.setColor(colorP4); // Cor do jogador 4
                    }

                    // Desenha o preenchimento da casa de saída
                    g2.fillRect(
                        (int) (x * SCALE),
                        (int) (y * SCALE),
                        (int) (40 * SCALE), 
                        (int) (40 * SCALE)
                    );

                    // Desenha a borda preta ao redor dela
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new java.awt.BasicStroke(1));
                    g2.drawRect(
                        (int) (x * SCALE),
                        (int) (y * SCALE),
                        (int) (40 * SCALE),
                        (int) (40 * SCALE)
                    );
                }
            }        
        }

        // Loop para construir e pintar as casas de saída verticais dos jogadores 1 e 3 (cima e baixo)
        for (int x = 240; x <= 320; x += 80) { // x assume 240 e 320
            for (int y = 40; y <= 520; y += 480) { // y assume 40 e 520
                
                // Se for a combinação de cima (320, 40) OU a combinação de baixo (240, 520)
                if ((x == 320 && y == 40) || (x == 240 && y == 520)) {
                    
                    // Se o x for 40, é o lado esquerdo (Jogador 3)
                    if (y == 40) {
                        g2.setColor(colorP3); // Cor do jogador 3
                    } else {
                        g2.setColor(colorP1); // Cor do jogador 1
                    }

                    // Desenha o preenchimento da casa de saída
                    g2.fillRect(
                        (int) (x * SCALE),
                        (int) (y * SCALE),
                        (int) (40 * SCALE),
                        (int) (40 * SCALE)
                    );

                    // Desenha a borda preta
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new java.awt.BasicStroke(1));
                    g2.drawRect(
                        (int) (x * SCALE),
                        (int) (y * SCALE),
                        (int) (40 * SCALE),
                        (int) (40 * SCALE)
                    );
                }
            }
        }

        g2.setColor(Color.WHITE); // Define a cor como Branco para o preenchimento
       
        // Garante que o caminho já foi gerado para evitar erros de NullPointer
        if (this.player1Path != null) {
            for (i = 0; i <= 3; i++) {
                java.awt.Point pontoSpawn = this.player1Path[i];
                
                if (pontoSpawn != null) {
                    // Definimos o tamanho do círculo branco (ajustado para a proporção do peão)
                    int diametroCirculo = (int) (65 * SCALE); 
                    
                    // Alinhamento preciso: centraliza o círculo exatamente na mesma lógica do quadrado da casa (40 * SCALE)
                    int offset = (int) (((40 * SCALE) - diametroCirculo) / 2);
                    
                    int xCirculo = pontoSpawn.x + offset - 9;
                    int yCirculo = pontoSpawn.y + offset - 9;

                    // fillOval pinta o círculo todo por dentro
                    g2.fillOval(xCirculo, yCirculo, diametroCirculo, diametroCirculo);
                    
                    // Opcional: Desenha uma borda preta bem fina ao redor do círculo branco para dar destaque
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new java.awt.BasicStroke(1));
                    g2.drawOval(xCirculo, yCirculo, diametroCirculo, diametroCirculo);
                    
                    // Retorna a cor para branco para o próximo ciclo do loop
                    g2.setColor(Color.WHITE); 
                }
            }
        }
    }

    // Métodos getters
    /**
    * Retorna o peão específico do jogador 1 baseado no índice (0 a 3).
    * @param index Índice do peão do jogador
    * @return O peão selecionado 
    */
    public PlayerPawn getPlayer1Pawn(int index) {
        if (index >= 0 && index < player1Pawn.length) {
            return this.player1Pawn[index];
        }
        return null;
    }


     // Permite que o GameManager consulte o vetor de coordenadas das casas do circuito. 
    public java.awt.Point[] getCaminhoCasas() {
        return this.player1Path;
    }

    public void setPawnControlManager(PawnControlManager pawnControlManager) {
    this.pawnControlManager = pawnControlManager;
    }
}
