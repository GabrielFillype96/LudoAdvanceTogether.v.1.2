package gui.components;

import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class PlayerIdentifier {
    
    private static final Color GOLD_ACCENT = new Color(222, 179, 102);

    /**
     * Método Estático para criar o identificador (número) do jogador.
     * Tenta carregar a arte customizada; se falhar, renderiza o fallback estruturado.
     */
    public static JLabel squarePlayerIdentifier(String text, int x, int y) {
        JLabel squarePlayerId = new JLabel(text, SwingConstants.CENTER);
        
        // Regra de Ouro: O tamanho e a posição devem ser definidos independente de ser imagem ou vetor
        squarePlayerId.setBounds(x, y, 35, 35);

        try {
            // Tenta carregar a imagem a partir da sua pasta de assets
            ImageIcon icon = new ImageIcon(PlayerIdentifier.class.getResource("/assets/squarePlayerId_" + text + "_35x35.png"));
            
            // Se o arquivo não existir, o getResource retorna null e força o erro para ir ao catch
            if (icon.getImage() == null) {
                throw new Exception("Imagem não encontrada");
            }
            
            squarePlayerId.setIcon(icon);
            squarePlayerId.setText(""); // Apaga o texto para destacar a arte gráfica
            
        } catch (Exception e) {
            // --- BLOCO FALLBACK (Executa se a arte .png sumir ou falhar) ---
            squarePlayerId.setOpaque(true);
            squarePlayerId.setBackground(new Color(40, 26, 50)); 
            squarePlayerId.setForeground(GOLD_ACCENT);
            squarePlayerId.setFont(new Font("Arial", Font.BOLD, 16));
            squarePlayerId.setBorder(new LineBorder(GOLD_ACCENT, 1));
            squarePlayerId.setText(text); // Garante que o texto numérico apareça
        }

        return squarePlayerId;
    }
}