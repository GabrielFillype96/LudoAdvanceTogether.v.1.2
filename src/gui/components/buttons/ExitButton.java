/* Classe responsável por aplicar a arte ao botão "Sair" */
// Packages
package gui.components.buttons;
// Imports
import javax.swing.ImageIcon;

// Cria a classe "ExitButton" que irá herdar métodos da classe "CustomButton"
public class ExitButton extends CustomButton {
    // Método para aplicar a arte ao botão
    public ExitButton() {
        // Carrega a imagem e envia imediatamente para o construtor da classe mãe (CustomButton)
        /* ExitButton.class --> diz para o método "olhar" na pasta onde a classe ExitButton está
            getResource(path) --> procure o arquivo definido nesse path
        */
        super(
            new ImageIcon( ExitButton.class.getResource("/assets/exitMenu.png")), // Imagem padrão
            new ImageIcon(ExitButton.class.getResource("/assets/exitMenuSelected.png")), // Imagem quando em hover
            true // Ativa o construtor 1 (modo hover)
            );
    }
}
