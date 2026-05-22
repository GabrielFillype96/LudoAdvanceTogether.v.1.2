/* Classe responsável por aplicar a arte ao botão "Sobre" */
// Packages
package gui.components.buttons;
// Imports
import javax.swing.ImageIcon;

// Cria a classe "AboutButton" que irá herdar métodos da classe "CustomButton"
public class AboutButton extends CustomButton {
    // Método para aplicar a arte ao botão
    public AboutButton() {
        // Carrega a imagem e envia imediatamente para o construtor da classe mãe (CustomButton)
        /* AboutButton.class --> diz para o método "olhar" na pasta onde a classe AboutButton está
            getResource(path) --> procure o arquivo definido nesse path
        */
        super(
            new ImageIcon( AboutButton.class.getResource("/assets/aboutMenu.png")), // Imagem padrão
            new ImageIcon(AboutButton.class.getResource("/assets/aboutMenuSelected.png")) // Imagem quando selecionada
    );
    }
}
