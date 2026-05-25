package Cards;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

public class CardManager {

    /**
     * Método que cria e retorna todas as cartas fáceis do jogo de uma vez só.
     * Aqui você pode criar quantas cartas quiser!
     */
    public static List<CustomCards> criarCartasFaceis() {
        List<CustomCards> cartasFaceis = new ArrayList<>();

        // CARTA 1
        cartasFaceis.add(new CustomCards(
            new ImageIcon(CardManager.class.getResource("/assets/easyCard_1_180x2401.png")),
            "Pergunta", 
            "Avançar", 
            1, 
            2, 
            "Sim"
        ));

        // CARTA 2 (Basta mudar o caminho da imagem e as propriedades)
        cartasFaceis.add(new CustomCards(
            new ImageIcon(CardManager.class.getResource("/assets/mediumCard_1_180x240.png")),
            "Pergunta", 
            "Avançar", 
            2, 
            1, 
            "A"
        ));

        // CARTA 3... adicione quantas quiser aqui dentro!
        
        return cartasFaceis;
    }

    /**
     * Método para criar as cartas de Azar / Barreiras
     */
    public static List<CustomCards> criarCartasAzar() {
        List<CustomCards> cartasAzar = new ArrayList<>();
        
        // Usa o Construtor 1 (Sem resposta correta)
        cartasAzar.add(new CustomCards(
            new ImageIcon(CardManager.class.getResource("/assets/badLuckCard_1_180x240.png")),
            "Azar", 
            "Retroceder", 
            101, 
            1
        ));

        return cartasAzar;
    }
}