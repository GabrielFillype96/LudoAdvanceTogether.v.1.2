package gui.windows;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JPanel;
import cards.CardManager;
import cards.CustomCards;

/**
 * Painel base responsável por carregar, posicionar e gerenciar 
 * a exibição visual das novas cartas dinâmicas vindas do JSON.
 */
public class CardsPanel extends JPanel {

    private final Color MOLDURA_PRETA = new Color(0, 0, 0);
    private List<CustomCards> listaCartas;
    private CustomCards cartaAtual;

    /**
     * Construtor atualizado: agora ele mesmo carrega as cartas do tipo solicitado
     * @param filtroDificuldadeOuTipo Ex: "FÁCIL", "SORTE", "AZAR"
     */
    public CardsPanel(String filtroDificuldadeOuTipo) {
        // Mantém as dimensões de 220x340 definidas no layout
        Dimension tamanhoCard = new Dimension(220, 340);
        setPreferredSize(tamanhoCard);
        setSize(tamanhoCard);
        setOpaque(false);   // Mantém transparente para o fundo amadeirado aparecer atrás
        setLayout(null);    // Permite que a CustomCards ocupe o espaço absoluto interno

        // O próprio painel faz a chamada ao CardManager para buscar as cartas do JSON
        this.listaCartas = CardManager.carregarCartas(filtroDificuldadeOuTipo);

        // Se encontrou cartas no JSON, exibe a primeira da lista por padrão
        if (listaCartas != null && !listaCartas.isEmpty()) {
            mostrarCarta(0);
        }
    }

    /**
     * Método auxiliar para alternar a carta ativa dentro do painel
     */
    public void mostrarCarta(int indice) {
        if (listaCartas == null || indice < 0 || indice >= listaCartas.size()) return;

        // Se já existia uma carta visual na tela, remove-a antes de colocar a nova
        if (this.cartaAtual != null) {
            this.remove(this.cartaAtual);
        }

        // Pega a nova carta da lista
        this.cartaAtual = listaCartas.get(indice);

        // Garante que a carta comece posicionada no canto superior esquerdo do painel
        this.cartaAtual.setLocation(0, 0);

        // Adiciona o JPanel da carta dentro deste CardsPanel
        this.add(this.cartaAtual);

        // Atualiza a árvore de componentes do Swing para redesenhar na tela
        this.revalidate();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Linha preta fina opcional ao redor do bloco apenas para dar acabamento de contorno
        g2.setColor(MOLDURA_PRETA);
        g2.setStroke(new BasicStroke(2.0f));
        // g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18); // Se quiser uma borda externa extra, descomente esta linha

        g2.dispose();
    }

    // Getters úteis para a mecânica de jogo futura
    public CustomCards getCartaAtual() { return cartaAtual; }
    public List<CustomCards> getListaCartas() { return listaCartas; }
}