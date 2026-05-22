/* Classe responsável por aplicar a arte ao botão "Jogar" */
// Packages
package gui.components.buttons;
// Imports
import javax.swing.ImageIcon;

// Cria uma classe "PlayButton" que irá herdar métodos da classe "CustomButton"
public class PlayButton extends CustomButton {
    // Método para aplicar a arte ao botão
    public PlayButton() {
        // Carrega a imagem e envia imediatamente para o construtor da classe mãe (CustomButton)
        /* PlayButton.class --> diz para o método "olhar" na pasta onde a classe PlayButton está
            getResource(path) --> procure o arquivo definido nesse path
        */
        super(
            new ImageIcon(PlayButton.class.getResource("/assets/play_button.png")),
            new ImageIcon(PlayButton.class.getResource("/assets/play_buttonSelected.png")),
            true
        );
    }
    
}
