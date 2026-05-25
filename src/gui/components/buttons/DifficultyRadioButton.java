package gui.components.buttons;

import javax.swing.JRadioButton;
import java.awt.Color;
import java.awt.Font;

public class DifficultyRadioButton {
    // Variável de instância para a cor dourada dos textos/bordas
    // A variável "GOLD_ACCENT" recebe um método static para que o método estático consiga enxergá-la
    private static final Color GOLD_ACCENT = new Color(222, 179, 102); // Dourado fosco dos textos/bordas
    
    /**
     * Método Estático para criar botões de rádio dourados customizados.
     * Pode ser chamado diretamente através de: DifficultyRadioButton.goldenBtnRd("Texto");
     */
    public static JRadioButton goldenBtnRd(String texto) {
        JRadioButton rb = new JRadioButton(texto);
        rb.setFont(new Font("SansSerif", Font.BOLD, 13));
        rb.setForeground(GOLD_ACCENT);
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        return rb;
    }
}