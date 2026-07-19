package gui.components.buttons;

import javax.swing.JRadioButton;
import java.awt.Color;
import java.awt.Font;

public class DifficultyRadioButton {
    private static final Color GOLD_ACCENT = new Color(222, 179, 102); 
    
    // MODIFICADO: Agora o método recebe o parâmetro 'scale' no final
    public static JRadioButton goldenBtnRd(String texto, double scale) {
        JRadioButton rb = new JRadioButton(texto);
        
        // MODIFICADO: O tamanho da fonte agora acompanha o multiplicador de escala
        rb.setFont(new Font("SansSerif", Font.BOLD, (int) (13 * scale)));
        
        rb.setForeground(GOLD_ACCENT);
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        return rb;
    }
}