/*Essa classe "MainScreenInterface" irá renderizar a imagem de fundo da tela inicial */
package gui.windows;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// Cria uma classe "MainScreenInterface" que irá herdar métodos da classe JPanel (nativa do Java Swing)
public class MainScreenInterface extends JPanel { 
    
    public MainScreenInterface() {
        setLayout(null); //Layout é nulo, pois apenas servirá como "tela" que preenche o fundo
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Chama um método do Java Swing para garantir que o painel seja limpo antes de inserir a imagem
        super.paintComponent(g); 

        // Usa-se a estrutura try/catch para tratamento de erros (uma espécie de if)
        try {
            // Busca a imagem dentro da pasta 'assets' que deve estar no seu diretório 'src'
            ImageIcon mainScreenImagePath = new ImageIcon(getClass().getResource("/assets/mainScreenBackground_900x600.jpeg"));
            Image mainScreenImage = mainScreenImagePath.getImage();

            // Desenha a imagem começando do canto superior esquerdo (0,0) 
            // e esticando até a largura (getWidth) e altura (getHeight) atuais do painel
            g.drawImage(mainScreenImage, 0, 0, getWidth(), getHeight(), this);
        } catch (Exception e) {
            // Caso a imagem não consiga ser carregada exibe essa msg de erro
            System.err.println("Erro ao carregar a imagem de fundo: " + e.getMessage());
        }
    }
}


