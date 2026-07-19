// Classe responsável por criar o identificador do jogador (número) que aparece ao lado do nome do jogador na interface gráfica, tanto no menu de configuração quanto durante a partida.
// Packages
package gui.components;

// Imports internos
// Imports externos
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

public class PlayerIdentifier {

    /**
     * Método Estático para criar o identificador (número) do jogador.
     * Tenta carregar a arte customizada; se falhar, renderiza o fallback estruturado.
     */
    // MODIFICADO: Agora recebe o parâmetro 'scale' no final
    public static JLabel squarePlayerIdentifier(String text, int x, int y, double scale) {
        // Cria um JLabel para o identificador do jogador, centralizando o texto
        JLabel squarePlayerId = new JLabel(text, SwingConstants.CENTER);
        
        // MODIFICADO: Tamanho do quadrado recalculado com base na escala
        int size = (int) (35 * scale);
        squarePlayerId.setBounds(x, y, size, size);

        try {
            // Tenta carregar a imagem a partir da sua pasta de assets
            ImageIcon icon = new ImageIcon(PlayerIdentifier.class.getResource("/assets/squarePlayerId_" + text + "_35x35.png"));
            
            // Se o arquivo não existir, o getResource retorna null e força o erro para ir ao catch
            if (icon.getImage() == null) {
                throw new Exception("Imagem não encontrada");
            }
            
            // MODIFICADO: Redimensiona suavemente a imagem para o novo tamanho escalado
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            
            squarePlayerId.setIcon(new ImageIcon(scaledImg));
            squarePlayerId.setText(""); // Apaga o texto para destacar a arte gráfica
            
        } catch (Exception e) {
            // --- BLOCO FALLBACK (Executa se a arte .png sumir ou falhar) ---
            squarePlayerId.setOpaque(true);
            squarePlayerId.setBackground(new Color(40, 26, 50)); 
            squarePlayerId.setForeground(gui.theme.GameColors.GOLD_ACCENT);
            
            // MODIFICADO: Tamanho da fonte agora acompanha o multiplicador de escala
            squarePlayerId.setFont(new Font("Arial", Font.BOLD, (int) (16 * scale)));
            
            squarePlayerId.setBorder(new LineBorder(gui.theme.GameColors.GOLD_ACCENT, 1));
            squarePlayerId.setText(text); // Garante que o texto numérico apareça
        }

        return squarePlayerId;
    }
}