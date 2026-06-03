// Classe responsável por criar o tabuleiro do jogo

// Packages
package gui.windows;

import gui.components.PlayerPawn;
// Imports internos
import gui.windows.GameContainer;

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
    private PlayerPawn pawn1P1, pawn2P1, pawn3P1, pawn4P1;
    private PlayerPawn[] player1Pawns = {pawn1P1, pawn2P1, pawn3P1, pawn4P1};
        
    private java.awt.Point[] housesTrack;

    
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
        inicializarCaminhoTabuleiro();

        JLabel player1Label = new JLabel(
            "JOGADOR 1: " + player1Name, 
            SwingConstants.CENTER
        );
         player1Label.setFont( new Font(
            "SansSerif",
             Font.BOLD,
             22
        ));

        String player1PawnImg = "peaoAzul_90x90.png"; // Substitua pelo nome real dos seus arquivos PNG
        
        
        // Loop para instanciar os peões do jogador 1 (peão 1, 2, 3 e 4)
        for (int i = 0; i < player1Pawns.length; i++) {
            player1Pawns[i] =  new PlayerPawn(
                player1Name,
                player1PawnImg
            );
            System.out.println("Instanciando peão " + (i+1) + " do Jogador 1 com a imagem: " + player1PawnImg);
        }
        
        // Atribui as coordenadas do tabuleiro
        inicializarCaminhoTabuleiro();


        
        int j = 0;
        //Loop para atribuir a coordenada inicial dos pões do jogador 1
        for (int i = 0; i < player1Pawns.length; i++) {
            System.out.println("Atribuindo coordenada visual para o peão " + (i+1) + " do Jogador 1 na casa " + j);
            if (housesTrack != null && housesTrack.length > 0) {
            player1Pawns[i].setCoordenadaVisual(housesTrack[j]);
            };
            j++;
        }

        control.GameManager.setTabuleiro(this);
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
    private void inicializarCaminhoTabuleiro() {
    // Vamos mapear um circuito de teste com as casas principais transitáveis.
    // Você pode aumentar o tamanho do array conforme adicionar mais casas ao circuito.
    housesTrack = new java.awt.Point[61];

    // --- TRACKING DO CIRCUITO (A partir do canto inferior esquerdo) ---
    // Cada ponto representa o canto superior esquerdo (X, Y) do quadrado de 60x60.
   // --- OS 4 ÚLTIMOS PASSADOS PARA SEREM OS 4 PRIMEIROS ---
    housesTrack[0]  = new java.awt.Point((int)(40 * SCALE), (int)(440 * SCALE)); // Casa final do jogador 1 (casa 58 - centro do tabuleiro)
    housesTrack[1]  = new java.awt.Point((int)(40 * SCALE), (int)(520 * SCALE)); // Casa Inicial (Saída do Azul)
    housesTrack[2]  = new java.awt.Point((int)(120 * SCALE), (int)(440 * SCALE)); // Casa Inicial (Saída do Azul)
    housesTrack[3]  = new java.awt.Point((int)(120 * SCALE), (int)(520 * SCALE)); // Casa Inicial (Saída do Azul)

    // --- CIRCUITO ORIGINAL (SEGUIDO DOS ÍNDICES [4] EM DIANTE, ATUALIZADOS) ---
    // Caminho do jogador 1 - subindo em direção ao centro do tabuleiro
    housesTrack[4]  = new java.awt.Point((int)(240 * SCALE), (int)(520 * SCALE));
    housesTrack[5]  = new java.awt.Point((int)(240 * SCALE), (int)(480 * SCALE));
    housesTrack[6]  = new java.awt.Point((int)(240 * SCALE), (int)(440 * SCALE));
    housesTrack[7]  = new java.awt.Point((int)(240 * SCALE), (int)(400 * SCALE));
    housesTrack[8]  = new java.awt.Point((int)(240 * SCALE), (int)(360 * SCALE));
    
    // Caminho do jogador 1 - indo para esquerda em direção a borda esquerda do tabuleiro
    housesTrack[9]  = new java.awt.Point((int)(200 * SCALE), (int)(320 * SCALE));
    housesTrack[10] = new java.awt.Point((int)(160 * SCALE), (int)(320 * SCALE));
    housesTrack[11] = new java.awt.Point((int)(120 * SCALE), (int)(320 * SCALE));
    housesTrack[12] = new java.awt.Point((int)(80 * SCALE),  (int)(320 * SCALE));
    housesTrack[13] = new java.awt.Point((int)(40* SCALE),   (int)(320 * SCALE));
    housesTrack[14] = new java.awt.Point((int)(0 * SCALE),    (int)(320 * SCALE));

    // Caminho do jogador 1 - subindo no braço esquerdo do tabuleiro
    housesTrack[15] = new java.awt.Point((int)(0 * SCALE),    (int)(280 * SCALE));
    housesTrack[16] = new java.awt.Point((int)(0 * SCALE),    (int)(240 * SCALE));
    
    // Caminho do jogador 1 - indo para direita em direção ao centro do tabuleiro
    housesTrack[17] = new java.awt.Point((int)(40 * SCALE),  (int)(240 * SCALE));
    housesTrack[18] = new java.awt.Point((int)(80 * SCALE),  (int)(240 * SCALE));
    housesTrack[19] = new java.awt.Point((int)(120 * SCALE), (int)(240 * SCALE));
    housesTrack[20] = new java.awt.Point((int)(160 * SCALE), (int)(240 * SCALE));
    housesTrack[21] = new java.awt.Point((int)(200 * SCALE), (int)(240 * SCALE));

    // Caminho do jogador 1 - subindo em direção a borda superior do tabuleiro
    housesTrack[22] = new java.awt.Point((int)(240 * SCALE), (int)(200 * SCALE));
    housesTrack[23] = new java.awt.Point((int)(240 * SCALE), (int)(160 * SCALE));
    housesTrack[24] = new java.awt.Point((int)(240 * SCALE), (int)(120 * SCALE));
    housesTrack[25] = new java.awt.Point((int)(240 * SCALE), (int)(80 * SCALE));
    housesTrack[26] = new java.awt.Point((int)(240 * SCALE), (int)(40 * SCALE));
    housesTrack[27] = new java.awt.Point((int)(240 * SCALE), (int)(0 * SCALE));
    
    // Caminho do jogador 1 - indo para direita no braço superior do tabuleiro
    housesTrack[28] = new java.awt.Point((int)(280 * SCALE), (int)(0 * SCALE));
    housesTrack[29] = new java.awt.Point((int)(320 * SCALE), (int)(0 * SCALE));

    // Caminho do jogador 1 - descendo para o centro do tabuleiro
    housesTrack[30] = new java.awt.Point((int)(320 * SCALE), (int)(40 * SCALE));
    housesTrack[31] = new java.awt.Point((int)(320 * SCALE), (int)(80 * SCALE));
    housesTrack[32] = new java.awt.Point((int)(320 * SCALE), (int)(120 * SCALE));
    housesTrack[33] = new java.awt.Point((int)(320 * SCALE), (int)(160 * SCALE));
    housesTrack[34] = new java.awt.Point((int)(320 * SCALE), (int)(200 * SCALE));
    
    // Caminho do jogador 1 - indo para direita em direção a borda do direita do tabuleiro
    housesTrack[35] = new java.awt.Point((int)(360 * SCALE), (int)(240 * SCALE));
    housesTrack[36] = new java.awt.Point((int)(400 * SCALE), (int)(240 * SCALE));
    housesTrack[37] = new java.awt.Point((int)(440 * SCALE), (int)(240 * SCALE));
    housesTrack[38] = new java.awt.Point((int)(480 * SCALE), (int)(240 * SCALE));
    housesTrack[39] = new java.awt.Point((int)(520 * SCALE), (int)(240 * SCALE));
    housesTrack[40] = new java.awt.Point((int)(560 * SCALE), (int)(240 * SCALE));
    
    // Caminho do jogador 1 - descendo no braço direito do tabuleiro
    housesTrack[41] = new java.awt.Point((int)(560 * SCALE), (int)(280 * SCALE));
    housesTrack[42] = new java.awt.Point((int)(560 * SCALE), (int)(320 * SCALE));
    
    // Caminho do jogador 1 - indo para esquerda em direção ao centro do tabuleiro
    housesTrack[43] = new java.awt.Point((int)(520 * SCALE), (int)(320 * SCALE));
    housesTrack[44] = new java.awt.Point((int)(480 * SCALE), (int)(320 * SCALE));
    housesTrack[45] = new java.awt.Point((int)(440 * SCALE), (int)(320 * SCALE));
    housesTrack[46] = new java.awt.Point((int)(400 * SCALE), (int)(320 * SCALE));
    housesTrack[47] = new java.awt.Point((int)(360 * SCALE), (int)(320 * SCALE));

    // Caminho do jogador 1 - descendo em direção a borda inferior do tabuleiro
    housesTrack[48] = new java.awt.Point((int)(320 * SCALE), (int)(360 * SCALE));
    housesTrack[49] = new java.awt.Point((int)(320 * SCALE), (int)(400 * SCALE));   
    housesTrack[50] = new java.awt.Point((int)(320 * SCALE), (int)(440 * SCALE));
    housesTrack[51] = new java.awt.Point((int)(320 * SCALE), (int)(480 * SCALE));
    housesTrack[52] = new java.awt.Point((int)(320 * SCALE), (int)(520 * SCALE));
    housesTrack[53] = new java.awt.Point((int)(320 * SCALE), (int)(560 * SCALE));
    
    // Caminho do jogador 1 - indo para esquerda no braço inferior do tabuleiro
    housesTrack[54] = new java.awt.Point((int)(280 * SCALE), (int)(560 * SCALE));
    
    // Caminho do jogador 1 - subindo em direção ao centro do tabuleiro (caminho final do jogador 1)
    housesTrack[55] = new java.awt.Point((int)(280 * SCALE), (int)(520 * SCALE));
    housesTrack[56] = new java.awt.Point((int)(280 * SCALE), (int)(480 * SCALE));
    housesTrack[57] = new java.awt.Point((int)(280 * SCALE), (int)(440 * SCALE));
    housesTrack[58] = new java.awt.Point((int)(280 * SCALE), (int)(400 * SCALE));
    housesTrack[59] = new java.awt.Point((int)(280 * SCALE), (int)(360 * SCALE));
    housesTrack[60] = new java.awt.Point((int)(280 * SCALE), (int)(320 * SCALE)); // Casa final do jogador 1 (centro do tabuleiro)

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

        if (this.player1Pawns != null) {
            for (PlayerPawn pawn : player1Pawns) {
                if (pawn != null) {
                    pawn.desenhar((Graphics2D) g);
                }
            }
        }

        // Libera os recursos do contexto gráfico 2D para evitar vazamentos de memória
        g2.dispose();
    }

    // Métodos getters
    public PlayerPawn getPlayer1Pawn() {
        return this.player1Pawns[0];
    }

    /**
     * Permite que o GameManager consulte o vetor de coordenadas das casas do circuito.
     */
    public java.awt.Point[] getCaminhoCasas() {
        return this.housesTrack;
    }
}
