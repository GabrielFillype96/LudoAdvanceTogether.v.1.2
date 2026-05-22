/* Classe responsável por aplicar a arte ao botão "Rede" */
// Packages
package gui.components.buttons;
// Imports
import javax.swing.ImageIcon;

// Cria a classe "ConnectionButton" que irá herdar métodos da classe "CustomButton"
public class ConnectionButton extends CustomButton {
    // Método para aplicar a arte ao botão
    public ConnectionButton() {
        // Carrega a imagem e envia imediatamente para o construtor da classe mãe (CustomButton)
        /* ConnectionButton.class --> diz para o método "olhar" na pasta onde a classe ConnectionButton está
            getResource(path) --> procure o arquivo definido nesse path
        */
        super(
            new ImageIcon(ConnectionButton.class.getResource("/assets/connectionMenu.png")), // Imagem padrão
            new ImageIcon(ConnectionButton.class.getResource("/assets/connectionMenuSelected.png")) // Imagem selecionada
    
    );
    }
}