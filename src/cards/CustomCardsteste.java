package cards;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class CustomCardsteste extends JPanel {
    private ImageIcon cardImage; 
    private String cardType; 
    private String cardAnswer; 
    private String cardEffect; 
    private int cardID; 
    private int cardValue; 

    // Construtor 1: Cartas especiais (sorte, azar, "sacanear")
    public CustomCards(ImageIcon cardImage, String cardType, String cardEffect, int cardID, int cardValue) {
        this.cardImage = cardImage;
        this.cardType = cardType;
        this.cardEffect = cardEffect;
        this.cardID = cardID;
        this.cardValue = cardValue;

        // ADICIONE ESTA LINHA DE TESTE:
        if (cardImage == null || cardImage.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
            System.err.println("[ERRO] Imagem da carta ID " + cardID + " NÃO foi encontrada no diretório!");
        } else {
            System.out.println("[SUCESSO] Imagem da carta ID " + cardID + " carregada corretamente.");
        }

        configurarComponente();
    }

    // Construtor 2: Cartas de perguntas
    public CustomCards(ImageIcon cardImage, String cardType, String cardEffect, int cardID, int cardValue, String cardAnswer) {
        this.cardImage = cardImage;
        this.cardType = cardType;
        this.cardEffect = cardEffect;
        this.cardID = cardID;
        this.cardValue = cardValue;
        this.cardAnswer = cardAnswer;

        // ADICIONE ESTA LINHA DE TESTE:
        if (cardImage == null || cardImage.getImageLoadStatus() != java.awt.MediaTracker.COMPLETE) {
            System.err.println("[ERRO] Imagem da carta ID " + cardID + " NÃO foi encontrada no diretório!");
        } else {
            System.out.println("[SUCESSO] Imagem da carta ID " + cardID + " carregada corretamente.");
        }

        configurarComponente();
    }

    // Altere para bater exatamente com as dimensões novas do CardsPanel (220x340)
    private void configurarComponente() {
        setSize(220, 340);
        setBounds(0, 0, 220, 340);
        setOpaque(false);
    }

    // Métodos Getters para a lógica do jogo ler depois
    public String getCardType() { return cardType; }
    public int getCardValue() { return cardValue; }
    public String getCardAnswer() { return cardAnswer; }
    // Adicione este método na classe CustomCards.java para expor a imagem ao painel
    public java.awt.Image getCardImage() {
        if (this.cardImage != null) {
            return this.cardImage.getImage();
        }
        return null;
    }

    // ADICIONE ESTE CÓDIGO CORRIGIDO ABAIXO:
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Verifica se a imagem foi carregada e desenha-a esticada perfeitamente no tamanho total do painel
        if (cardImage != null) {
            java.awt.Image img = cardImage.getImage().getScaledInstance(getWidth(), getHeight(), java.awt.Image.SCALE_SMOOTH);
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
        }
    }
}