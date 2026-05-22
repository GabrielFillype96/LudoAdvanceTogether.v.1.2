/* Classe responsável por aplicar a arte ao botão "Configurações" */
// Packages
package gui.components.buttons;
// Imports
import javax.swing.ImageIcon;

// Cria a classe "ConfigButton" que irá herdar métodos da classe "CustomButton"
public class ConfigButton extends CustomButton {
    // Método para aplicar a arte ao botão
    public ConfigButton() {
        // Carrega a imagem e envia imediatamente para o construtor da classe mãe (CustomButton)
        /* ConfigButton.class --> diz para o método "olhar" na pasta onde a classe ConfigButton está
            getResource(path) --> procure o arquivo definido nesse path
        */
        super(
            new ImageIcon( ConfigButton.class.getResource("/assets/configurationMenu.png")), //Imagem padrão
            new ImageIcon( ConfigButton.class.getResource("/assets/configurationMenuSelected.png")) // Imagem selecionada
    );
    }
}
