// Packages
package gui.components;
// Import externo
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;

public class SlotsIcon extends JLabel {
    private static String slotFontName = "Segoe UI Symbol"; 

    // MODIFICADO: Agora o método recebe o parâmetro 'scale' no final
    public static JLabel slotIconLabel(String unicodeString, int x, int y, double scale) {
        JLabel slotIconLabel = new JLabel(unicodeString, SwingConstants.CENTER);
        
        // MODIFICADO: Tamanho do componente e da fonte agora usam a escala
        int width = (int) (35 * scale);
        int height = (int) (35 * scale);
        int fontSize = (int) (18 * scale);

        slotIconLabel.setBounds(x, y, width, height); 
        slotIconLabel.setFont(new Font(slotFontName, Font.PLAIN, fontSize)); 
        slotIconLabel.setForeground(gui.theme.GameColors.GOLD_ACCENT); 

        return slotIconLabel;
    }
}