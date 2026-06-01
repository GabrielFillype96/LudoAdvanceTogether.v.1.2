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

public class BoardScreen extends JPanel {

    // VARIÁVEIS DE INSTÂNCIA
    private String player1Name, player2Name, player3Name, player4Name;
    private String player1Color, player2Color, player3Color, player4Color;
    private final static Rectangle BOARD_SCREEN_BOUNDS = new Rectangle(0, 0, 600, 600);
    private PlayerPawn player1Pawn;
    private java.awt.Point[] caminhoCasas;
    
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

        setOpaque(true);  // Mantém o fundo transparente para a imagem de fundo do jogo aparecer
        setLayout(null); // Layout nulo torna o painel absoluto 
        setBounds(BOARD_SCREEN_BOUNDS);

        JLabel player1Label = new JLabel(
            "JOGADOR 1: " + player1Name, 
            SwingConstants.CENTER
        );
         player1Label.setFont( new Font(
            "SansSerif",
             Font.BOLD,
             22
        ));

        String arquivoImagemP1 = "peaoAzul_90x90.png"; // Substitua pelo nome real dos seus arquivos PNG

        this.player1Pawn = new PlayerPawn(
            player1Name, 
            arquivoImagemP1
        );

        // 2. Mapeia as coordenadas das casas
        inicializarCaminhoTabuleiro();

        // 3. Coloca o peão na casa inicial (casa 0)
        if (caminhoCasas != null && caminhoCasas.length > 0) {
            this.player1Pawn.setCoordenadaVisual(caminhoCasas[0]);
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
    caminhoCasas = new java.awt.Point[80];

    // --- TRACKING DO CIRCUITO (A partir do canto inferior esquerdo) ---
    // Cada ponto representa o canto superior esquerdo (X, Y) do quadrado de 40x40.
    
    // SUBINDO pela coluna da esquerda
    caminhoCasas[0]  = new java.awt.Point(40, 480); // Casa Inicial (Saída do Azul)
    caminhoCasas[1]  = new java.awt.Point(240, 480);
    caminhoCasas[2]  = new java.awt.Point(240, 440);
    caminhoCasas[3]  = new java.awt.Point(240, 400);
    caminhoCasas[4]  = new java.awt.Point(240, 360);
    
    // CURVA: Avançando para o centro/topo esquerdo
    caminhoCasas[5]  = new java.awt.Point(200, 320);
    caminhoCasas[6]  = new java.awt.Point(160, 320);
    caminhoCasas[7]  = new java.awt.Point(120, 320);
    
    // VIRANDO e subindo em direção ao topo
    caminhoCasas[8]  = new java.awt.Point(80, 320);
    caminhoCasas[9]  = new java.awt.Point(40, 320);
    caminhoCasas[10] = new java.awt.Point(0, 320);
    
    // AVANÇANDO pela parte superior
    caminhoCasas[11] = new java.awt.Point(0, 280);
    caminhoCasas[12] = new java.awt.Point(0, 240);
    caminhoCasas[13] = new java.awt.Point(40, 240);

    caminhoCasas[14] = new java.awt.Point(80, 240);
    caminhoCasas[15] = new java.awt.Point(120, 240);
    caminhoCasas[16] = new java.awt.Point(160, 240);

    caminhoCasas[17] = new java.awt.Point(200, 240);
    caminhoCasas[18] = new java.awt.Point(240, 200);
    caminhoCasas[19] = new java.awt.Point(240, 120);


    
    
    // ... Continue adicionando os Points (X, Y) seguindo o fluxo do desenho ...
    // Dica: Vá somando ou subtraindo de 40 em 40 pixels para sincronizar perfeitamente 
    // com os loops for que você criou no seu paintComponent!
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
            getWidth(), 
            getHeight()
        );

        // Borda preta pequena para demarcar o tabuleiro
        g2.setColor(Color.BLACK);
        g2.drawRect(
            0, 
            0,
            getWidth() - 1, 
            getHeight() - 1
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
                    x, 
                    y, 
                    240, 
                    240
                );

                // Desenha uma borda preta ao redor de cada base e sua espessura
                g2.setColor(Color.BLACK);
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawRect(
                    x, 
                    y, 
                    240, 
                    240
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
                    x, 
                    y, 
                    40,
                    40
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
                    x, 
                    y, 
                    40,
                    40
                );
            }
        }

       // Desenho do quadrado central
       // Desenha o triângulo debaixo do jogador 1
        Polygon downTrianglePolygon = new Polygon();
        downTrianglePolygon.addPoint(240, 360); // Canto inferior esquerdo do miolo
        downTrianglePolygon.addPoint(360, 360); // Canto inferior direito do miolo
        downTrianglePolygon.addPoint(300, 300); // Centro perfeito do tabuleiro

        // Desenha o triângulo da esquerda do jogador 2
        Polygon leftTrianglePolygon = new Polygon();
        leftTrianglePolygon.addPoint(240, 240); // Canto superior esquerdo do miolo
        leftTrianglePolygon.addPoint(240, 360); // Canto inferior esquerdo do miolo
        leftTrianglePolygon.addPoint(300, 300); // Centro perfeito do tabuleiro
        
        // Desenha o triângulo de cima do jogador 3
        Polygon upTrianglePolygon = new Polygon();
        upTrianglePolygon.addPoint(240, 240); // Canto superior esquerdo do miolo
        upTrianglePolygon.addPoint(360, 240); // Canto superior direito do miolo
        upTrianglePolygon.addPoint(300, 300); // Centro perfeito do tabuleiro

        // Desenha o triângulo da direita jogador 4
        Polygon rightTrianglePolygon = new Polygon();
        rightTrianglePolygon.addPoint(360, 360); // Canto superior direito do miolo
        rightTrianglePolygon.addPoint(360, 240); // Canto inferior direito do miolo
        rightTrianglePolygon.addPoint(300, 300); // Centro perfeito do tabuleiro

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
        
        // Loop para pintar a trilha do jogador 2 e 4 (esquerda para direita)
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
                x, 
                280, 
                40, 
                40
            );
        
            // Desenha a borda
            g2.setColor(Color.BLACK);
            g2.setStroke(new java.awt.BasicStroke(1));
            g2.drawRect(
                x, 
                280, 
                40, 
                40
            );
        }

        // Loop para pintar a trilha do jogador 1 e 3
        for (int y = 40; y <= 480; y += 40) {
            if (y >= 240 && y <= 320) continue; // Salta uma etapa da repetição não preencher o centro do tabuleiro
            
            // Se a coord. "y" for menor que 240 utilizara a cor do jogador 1. Se for maior usa a cor do jogador 3
            if (y < 240) {
                g2.setColor(colorP3); // Cor do Jogador 1
            } else {
                g2.setColor(colorP1); // Cor do Jogador 3
            }
        
            // Desenha o quadrado uma única vez no código
            g2.fillRect(
                280, 
                y, 
                40, 
                40
            );
        
            // Desenha a borda
            g2.setColor(Color.BLACK);
            g2.setStroke(new java.awt.BasicStroke(1));
            g2.drawRect(
                280, 
                y, 
                40, 
                40
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
                        x, 
                        y, 
                        40, 
                        40
                    );

                    // Desenha a borda preta ao redor dela
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new java.awt.BasicStroke(1));
                    g2.drawRect(x, y, 40, 40);
                }
            }        
        }

        // Loop para construir e pintar as casas de saída verticais dos jogadores 1 e 3 (cima e baixo)
        for (int x = 240; x <= 320; x += 80) { // x assume 240 e 320
            for (int y = 40; y <= 480; y += 440) { // y assume 40 e 520
                
                // Se for a combinação de cima (320, 40) OU a combinação de baixo (240, 320)
                if ((x == 320 && y == 40) || (x == 240 && y == 480)) {
                    
                    // Se o x for 40, é o lado esquerdo (Jogador 3)
                    if (y == 40) {
                        g2.setColor(colorP3); // Cor do jogador 3
                    } else {
                        g2.setColor(colorP1); // Cor do jogador 1
                    }

                    // Desenha o preenchimento da casa de saída
                    g2.fillRect(x, y, 40, 40);

                    // Desenha a borda preta
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new java.awt.BasicStroke(1));
                    g2.drawRect(x, y, 40, 40);
                }
            }
        }

        if (this.player1Pawn != null) {
        this.player1Pawn.desenhar((Graphics2D) g);
    }

        // Libera os recursos do contexto gráfico 2D para evitar vazamentos de memória
        g2.dispose();
    }

    // Métodos getters
    public PlayerPawn getPlayer1Pawn() {
    return this.player1Pawn;
    }

    /**
     * Permite que o GameManager consulte o vetor de coordenadas das casas do circuito.
     */
    public java.awt.Point[] getCaminhoCasas() {
        return this.caminhoCasas;
    }
}
