/*Essa classe "MainScreenInterface" irá renderizar a imagem de fundo da tela inicial */
// Packages
package gui.windows;
// Imports externos
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;


// Cria uma classe "MainScreenInterface" que irá herdar métodos da classe JPanel (nativa do Java Swing)
public class MainScreenInterface extends JPanel { 
    // Variáveis
    private Image mainScreenImage; // Variável para armazenar a imagem de fundo da tela principal
    private final String mainScreenImageURL = "/assets/mainScreenBackground_900x600.jpeg"; // Caminho da imagem de fundo da tela principal (dentro da pasta de recursos)

    public MainScreenInterface() {
        setLayout(null); //Layout é nulo, pois apenas servirá como "tela" que preenche o fundo
    }

    // Sobrescreve o método paintComponent do JPanel para desenhar a imagem de fundo personalizada
    @Override
    // Visibilidade "protected" para que apenas classes dentro do mesmo pacote ou subclasses possam acessar este método
    protected void paintComponent(Graphics g) {
        // Chama um método do Java Swing para garantir que o painel seja limpo antes de inserir a imagem
        super.paintComponent(g); 

        // Usa-se a estrutura try/catch para tratamento de erros (uma espécie de if)
        try {
            java.net.URL mainScreenImagePath = getClass().getResource(mainScreenImageURL);
            mainScreenImage = new ImageIcon(mainScreenImagePath).getImage();

            // Desenha a imagem começando do canto superior esquerdo (0,0) 
            // e esticando até a largura (getWidth) e altura (getHeight) atuais do painel
            g.drawImage(mainScreenImage, 0, 0, getWidth(), getHeight(), this);
        } catch (Exception e) {
            // Caso a imagem não consiga ser carregada exibe essa msg de erro
            System.err.println("Erro ao carregar a imagem de fundo: " + e.getMessage());
        }
    }
}


