// Packages
package gui.components;
// Import externo
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;

public class SlotsIcon extends JLabel {
    // Variáveis
    private static int  slotIconWidth = 35; // Largura fixa para os ícones quadrados
    private static int slotIconHeight = 35; // Altura fixa para os ícones quadrados
    private static String slotFontName = "Segoe UI Symbol"; // Fonte que contém os símbolos Unicode para os ícones
    private static int slotFontSize = 18; // Tamanho da fonte para os ícones quadrados

    public static JLabel slotIconLabel(String unicodeString, int x, int y ) {
        JLabel slotIconLabel = new JLabel(unicodeString, SwingConstants.CENTER);
        slotIconLabel.setBounds(x, y, slotIconWidth, slotIconHeight); // Tamanho fixo para os ícones quadrados
        slotIconLabel.setFont(new Font(slotFontName, Font.PLAIN, slotFontSize)); // Define a fonte para os ícones quadrados
        slotIconLabel.setForeground(gui.theme.GameColors.GOLD_ACCENT); // Define a cor dourada para os ícones

        // Quando tiver os arquivos .png prontos, a lógica será:
        // if(unicodePadrao.equals("👤")) lbl.setIcon(new ImageIcon(getClass().getResource("/images/icone_humano.png")));
        // else lbl.setIcon(new ImageIcon(getClass().getResource("/images/icone_engrenagem.png")));
        // lbl.setText(""); 

        // Retorna o JLabel configurado como um ícone quadrado com o símbolo Unicode centralizado
        return slotIconLabel;
    }
}
