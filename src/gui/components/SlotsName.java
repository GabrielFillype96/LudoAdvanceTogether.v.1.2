package gui.components;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Font;

public class SlotsName extends JLabel {
    private final static Color GOLD_ACCENT = new Color(222, 179, 102); 
    private final static Color INPUT_BG = new Color(25, 14, 33); 

    // MODIFICADO: Agora o método recebe o parâmetro 'scale' no final
    public static JTextField slotName(int x, int y, int w, int h, boolean ativo, double scale) {
        JTextField slotName = new JTextField();
        slotName.setBounds(x, y, w, h);
        slotName.setBackground(INPUT_BG);
        slotName.setForeground(ativo ? Color.WHITE : Color.GRAY);
        slotName.setCaretColor(GOLD_ACCENT);
        
        // MODIFICADO: A fonte agora acompanha o multiplicador de escala
        slotName.setFont(new Font("SansSerif", Font.PLAIN, (int) (14 * scale)));
        
        slotName.setBorder(new LineBorder(GOLD_ACCENT, 1));
        slotName.setEditable(ativo);
        
        // MODIFICADO: O respiro interno (padding) também acompanha a escala
        int padding = (int) (10 * scale);
        slotName.setBorder(BorderFactory.createCompoundBorder(
            slotName.getBorder(), new EmptyBorder(0, padding, 0, padding)));
            
        return slotName;
    }
}