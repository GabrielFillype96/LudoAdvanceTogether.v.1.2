// Classe responsável por criar o container principal que irá receber o menu principal e o container dos sub-menus e irá renderizar a imagem de fundo da tela inicial

// Packages
package gui.windows;

// Imports externos
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

// Cria uma classe "MainScreenContainer" que irá herdar métodos da classe JPanel (nativa do Java Swing)
public class MainScreenContainer extends JPanel {
    // VARIÁVEIS DE INSTÂNCIA
    private Image mainScreenImage; // Variável para armazenar a imagem de fundo da tela principal
    private static final double SCALE = 1.5; // Fator de escala para ajustar o tamanho dos componentes (pode ser ajustado conforme necessário)
    private final static String MAIN_SCREEN_IMAGE_PATH = "/assets/mainScreenBackground_900x600.jpeg"; // Caminho da imagem de fundo da tela principal
    private final static Rectangle MAIN_SCREEN_BOUNDS = new Rectangle( // Dimensões fixas para o painel da tela principal
        (int) (0 * SCALE),
        (int) (0 * SCALE), 
        (int) (900 * SCALE),
        (int) (600 * SCALE)
    ); 

    // Construtor da classe "MainScreenContainer"
    public MainScreenContainer() {
        setBounds(MAIN_SCREEN_BOUNDS); // Define o tamanho do painel para cobrir toda a área da janela

        // Procura o path da imagem de fundo da tela principal
        java.net.URL mainScreenImagePath = getClass().getResource(MAIN_SCREEN_IMAGE_PATH);
        if (mainScreenImagePath != null) {
            // Se encontrou a imagem, carrega e armazena na variável mainScreenImage
            System.out.println(
                "[MainScreenContainer] Imagem de fundo da tela principal encontrada em: " + MAIN_SCREEN_IMAGE_PATH
            );
            this.mainScreenImage = new ImageIcon(mainScreenImagePath).getImage();
        } else {
            // Se não encontrou a imagem, imprime um erro no console
            System.err.println(
                "[MainScreenContainer] Erro: Imagem de fundo da tela principal não encontrada em " + MAIN_SCREEN_IMAGE_PATH
            );
        }
    }

    // Sobrescreve o método paintComponent do JPanel para desenhar a imagem de fundo personalizada
    // @Override indica que o método "paintComponent" está sendo sobrescrito da classe pai (JPanel). Serve como uma espécie de "guarda-costas" para garantir que estamos realmente sobrescrevendo um método existente e não criando um novo método por engano.
    @Override
    // O método "paintComponent" é chamado sempre que o painel precisa ser redesenhado, permitindo que personalizemos a aparência do fundo do menu offline.
    // Visibilidade "protected" para que apenas classes dentro do mesmo pacote ou subclasses possam acessar este método
    protected void paintComponent(Graphics g) {
        
        // Estrutura padrão do "paintComponent" para garantir que o fundo seja desenhado corretamente
        super.paintComponent(g);

        // Cria um contexto gráfico 2D para aplicar renderizações avançadas (como anti-aliasing)
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Habilita o anti-aliasing para suavizar as bordas das imagens desenhadas
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON
        );

         // Se a imagem de fundo foi carregada com sucesso, desenha ela como plano de fundo do painel
        if (mainScreenImage != null) {
            // Desenha a imagem de fundo da tela principal no container do "MainScreenContainer", escalando para preencher todo o painel
            g2.drawImage(
                mainScreenImage, 
                (int) (0 * SCALE), 
                (int) (0 * SCALE), 
                this.getWidth(), 
                this.getHeight(), 
                this
            );
            System.out.println(
                "[MainScreenContainer] Imagem de fundo da tela principal desenhada com sucesso."
            );
        } else {
            // Fallback: fundo cinza caso a imagem de fundo da tela principal falhe, e imprime um erro no console
            g.setColor(java.awt.Color.GRAY);
            g.fillRect(
                (int) (0 * SCALE), 
                (int) (0 * SCALE), 
                this.getWidth(), 
                this.getHeight()
            );
            // Se a imagem de fundo da tela principal não foi carregada, imprime um erro no console
            System.err.println(
                "[MainScreenContainer] Erro: Imagem de fundo da tela principal não carregada, usando fallback cinza."
            );
        }

        // Libera os recursos do contexto gráfico 2D para evitar vazamentos de memória
        g2.dispose(); 
    }

    // Método getter para acessar as dimensões da tela principal
    public static Rectangle getMainScreenContainerBounds() {
        return MAIN_SCREEN_BOUNDS;
    }
}


