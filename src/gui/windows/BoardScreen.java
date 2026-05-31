// Classe responsável por criar o tabuleiro do jogo

// Packages
package gui.windows;

// Imports internos
import gui.windows.GameContainer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JLabel;
// Imports externos
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
    }

    private Color colorName(String colorName) {
        if (colorName == null) return Color.GRAY;

        switch(colorName.toLowerCase()) {
            case "azul": return new Color(80, 163, 213);
            case "roxo": return new Color(107, 86, 165);
            case "rosa": return new Color(218, 99, 127);
            case "amarelo": return new Color(243, 177, 28);
            default: return Color.GRAY;
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
        Color[] colors = {colorP1, colorP2, colorP3, colorP4};
        // Variável para cada índice da array
        int i = 0;
        
        
        // "For" para construir os quadrados da base e atribuir a cor
        for (int x = 0; x <= 360; x += 360) {
            for (int y = 0; y <= 360; y += 360) {
                // Atribui a cor para cada base
                g2.setColor(colors[i]);
                g2.fillRect(
                    x, 
                    y, 
                    240, 
                    240
                );
                // Desenha uma borda preta ao redor de cada base
                g2.setColor(Color.BLACK);
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawRect(x, y, 240, 240);
                
                i++; // Aumenta o índice para percorrer todas as cores
            }        
        }
        
        // Define a cor e a espessura das bordas das casas
        g2.setColor(Color.BLACK);
        g2.setStroke(new java.awt.BasicStroke(1));

        // "For" para construir os quadrados das casas horizontais
        for (int x = 0; x <= 600; x += 40) {
            for (int y = 240; y <= 320; y += 40) {
                if (x >= 240 && x <= 320) continue; // Salta uma etapa da repetição não preencher o centro do tabuleiro
                System.out.println("x: " + x + " | y: " + y);
                g2.drawRect(
                    x, 
                    y, 
                    40,
                    40
                );
            }
        }

        // "For" para construir os quadrados das casas verticais
        for (int x = 240; x <= 320; x += 40) {
            for (int y = 0; y <= 600; y += 40) {
                if (y >= 240 && y <= 320) continue; // Salta uma etapa da repetição não preencher o centro do tabuleiro
                System.out.println("x: " + x + " | y: " + y);
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
        Polygon[] trianglePolygon = {downTrianglePolygon, leftTrianglePolygon, upTrianglePolygon, rightTrianglePolygon};
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

        i = 0; // Como foi declarada anteriormente e utilizada no loop das cores das bases, seu valor estava como "4". Repassada novamente para 0 seu valor e ser utilizado no loop abaixo
        
        for (int x = 40; x <= 200; x += 40) {
            g2.setColor(colorP1);
            g2.fillRect(
                x, 
                280,
                40,
                40);
        }




       


        // Libera os recursos do contexto gráfico 2D para evitar vazamentos de memória
        g2.dispose();

    }
        

    
    
}
