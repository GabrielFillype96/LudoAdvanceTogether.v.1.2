/* Classe responsável por aplicar a arte ao botão "Novo Jogo" */
// Packages
package gui.components.buttons;
// Imports
import javax.swing.ImageIcon;

// Cria a classe "NewGameButton" que irá herdar métodos da classe "CustomButton"
public class NewGameButton extends CustomButton {
    // Método para aplicar a arte ao botão
    public NewGameButton() {
        // Carrega a imagem e envia imediatamente para o construtor da classe mãe (CustomButton)
        /* NewGameButton.class --> diz para o método "olhar" na pasta onde a classe NewGameButton está
            getResource(path) --> procure o arquivo definido nesse path
        */
        super(
            new ImageIcon( NewGameButton.class.getResource("/assets/newGameMenu.png")), // Imagem padrão
            new ImageIcon( NewGameButton.class.getResource("/assets/newGameMenuSelected.png")) // Imagem selecionada
        );
    }
}
