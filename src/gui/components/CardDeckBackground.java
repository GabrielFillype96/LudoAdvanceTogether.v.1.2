
// Packages
package gui.components; 

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import control.ImageLoaderManager;

public class CardDeckBackground extends JPanel {
    // VARIÁVEL DE INSTÂNCIA
    private Image backgroundDeckImg;

    /**
     ** Construtor da classe CardDeckBackground que passa como parâmetro o path da imagem do deck das cartas
     * @param backgroundDeckPath Path da imagem do deck das cartas
    */
    public CardDeckBackground(String backgroundDeckPath) {
        setOpaque(false); // Retira o fundo cinza do java

        // Carrega a imagem do deck das cartas
        ImageIcon backgroundDeckIcon = ImageLoaderManager.loadIcon(backgroundDeckPath);

        /*
         * A "CardDeckBackground" é um "JPanel" e como não existe um método automático para botar fundo nele, nós usamos o "ImageLoaderManager" que devolve um "ImageIcon"
         * O "Graphics2D" só aceita o formato "Image" e por isso precisamos de uma trava de segurança caso o "backgroundDeckIcon.getImage()" retorne "null"
         * Trava de segurança para impedir que se caso a variável "backgroundDeckIcon" não dá crash no jogo
        */
        if (backgroundDeckIcon != null) {
            this.backgroundDeckImg = backgroundDeckIcon.getImage();
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundDeckImg != null) {
            // Desenha a imagem preenchendo o painel inteiro
            g2.drawImage(backgroundDeckImg, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback (plano B) caso a imagem dê erro: pinta o fundo de roxo
            g2.setColor(gui.theme.GameColors.PURPLE_BG);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        g2.dispose();
    }
}