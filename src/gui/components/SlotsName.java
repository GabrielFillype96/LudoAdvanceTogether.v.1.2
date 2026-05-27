package gui.components;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.Font;

public class SlotsName extends JLabel {
    // Variável de instância para a cor dourada dos textos/bordas
    // A variável "GOLD_ACCENT" é final, pois seu valor não deve ser alterado após a inicialização
    private final static Color GOLD_ACCENT = new Color(222, 179, 102); // Dourado fosco dos textos/bordas
    private final static Color INPUT_BG = new Color(25, 14, 33); // Escuro das caixas de texto


    public static JTextField slotName(int x, int y, int w, int h, boolean ativo) {
        // Construtor vazio, pois esta classe é apenas um modelo para os slots de nome dos jogadores
        JTextField slotName = new JTextField();
        slotName.setBounds(x, y, w, h);
        slotName.setBackground(INPUT_BG);
        slotName.setForeground(ativo ? Color.WHITE : Color.GRAY);
        slotName.setCaretColor(GOLD_ACCENT);
        slotName.setFont(new Font("SansSerif", Font.PLAIN, 14));
        slotName.setBorder(new LineBorder(GOLD_ACCENT, 1));
        slotName.setEditable(ativo);
        slotName.setBorder(BorderFactory.createCompoundBorder(
            slotName.getBorder(), new EmptyBorder(0, 10, 0, 10)));
        return slotName;
    }
}
