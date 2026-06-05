// Classe responsável por construir o painel que irá conter o controle dos peões

// Packages
package gui.windows;

// Imports externos
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Rectangle;

public class PawnControlContainer extends JPanel {
    // VARIÁVEIS DE INSTÂNCIA
    private Image stdPawnImg; // Imagem padrão do peão para permitir
    private Image disabledPawnImg; // Imagem do peão desabilitados
    private Image goldenPawnImg; // Imagem do peão dourado ao chegar no centro do tabuleiro
    private ImageIcon stdIcon, disabledIcon, goldenIcon; // Ícones dos peões em seus estados
    private static final String STD_PAWN_URL = "/assets/peaoAzul_90x90.png"; // Caminho da imagem padrão do peão
    private static final String DISABLED_PAWN_URL = "/assets/peaoRosa_90x90.png"; // Caminho da imagem do peão desabilitado
    private static final String GOLDEN_PAWN_IMG = "/assets/peaoAmarelo_90x90.png"; // Caminho da imagem do peão desabilitado
    private javax.swing.JLabel[] pawnLabels;
    private static final double SCALE = 1.5;
    private static final Rectangle PAWN_CONTROL_CONTAINER_BOUNDS = new Rectangle(
        (int) (0 * SCALE),
        (int) (0 * SCALE),
        (int) (220 * SCALE),
        (int) (120 * SCALE)
    );
    
    public PawnControlContainer() {
        setBounds(PAWN_CONTROL_CONTAINER_BOUNDS); // Tamanho e posição do painel de controle dos peões (0, 0) (330x180)
        setOpaque(false);   // Mantém o painel transparente 
        setLayout(null);    // Permite que a PawnControl ocupe o espaço absoluto interno

        
        // Poderia ser utilizado o método "try/catch" para tratamento de erros, mas o if/else é mais sútil e simples
        // Carrega a imagem padrão do peão para indicar a possibilidade de ser acionado
        java.net.URL stdPawnImgPath = getClass().getResource(STD_PAWN_URL);
        if (stdPawnImgPath != null) {
            // Se encontrou a imagem, carrega e armazena na variável deckImg
            System.out.println(
                "[PawnControlContainer] Imagem do padrão do peão encontrada em: " + STD_PAWN_URL
            );
            this.stdPawnImg = new ImageIcon(stdPawnImgPath).getImage();
        } else {
            // Se não encontrou a imagem, imprime um erro no console
            System.err.println(
                "[PawnControlContainer] Erro: Imagem padrão do peão não encontrada em /assets/peaoAzul_90x90.png"
            );
        }

        // Carrega a imagem do peão desabilitado para indicar a impossibilidade de ser acionado
         java.net.URL disabledPawnImgPath = getClass().getResource(DISABLED_PAWN_URL);
         if (disabledPawnImgPath != null) {
            // Se encontrou a imagem, carrega e armazena na variável deckImg
            System.out.println(
                "[PawnControlContainer] Imagem do peão desabilitado encontrada em: " + DISABLED_PAWN_URL
            );
            this.disabledPawnImg = new ImageIcon(disabledPawnImgPath).getImage();
        } else {
            // Se não encontrou a imagem, imprime um erro no console
            System.err.println(
                "[PawnControlContainer] Erro: Imagem do peão desabilitado não encontrada em /assets/peaoRosa_90x90.png"
            );
        }

        // Carrega a imagem do peão dourado para indicar que está no centro do tabuleiro
         java.net.URL goldenPawnImgPath = getClass().getResource(DISABLED_PAWN_URL);
         if (goldenPawnImgPath != null) {
            // Se encontrou a imagem, carrega e armazena na variável deckImg
            System.out.println(
                "[PawnControlContainer] Imagem do peão desabilitado encontrada em: " + GOLDEN_PAWN_IMG
            );
            this.goldenPawnImg = new ImageIcon(goldenPawnImgPath).getImage();
        } else {
            // Se não encontrou a imagem, imprime um erro no console
            System.err.println(
                "[PawnControlContainer] Erro: Imagem do peão desabilitado não encontrada em /assets/peaoAmarelo_90x90.png"
            );
        }

        // Instancia os "labels" dos peões
        this.pawnLabels = new javax.swing.JLabel[4];
        
        // Define o tamanho visual de cada JLabel (largura e altura)
        int labelWidth = (int) (40 * SCALE);
        int labelHeight = (int) (40 * SCALE);
        
        // Espaçamento horizontal entre um peão e outro
        int pawnOffset = (int) (10 * SCALE); 
        
        // Posição X inicial (onde o primeiro peão vai nascer)
        int startX = (int) (15 * SCALE);
        int startY = (int) (35 * SCALE); // Centralizado verticalmente no container

        // Usamos a imagem padrão recém-carregada para criar o ícone que vai no JLabel
        ImageIcon stdIcon = null;
        ImageIcon disabledIcon = null;
        ImageIcon goldenPawnImg = null;
        
        if (this.stdPawnImg != null) {
            this.stdIcon = new ImageIcon(
                this.stdPawnImg.getScaledInstance(
                    labelWidth, 
                    labelHeight, 
                    Image.SCALE_SMOOTH)
                );
        }
        if (this.disabledPawnImg != null) {
            this.disabledIcon = new ImageIcon(
                this.disabledPawnImg.getScaledInstance(
                    labelWidth, 
                    labelHeight, 
                    Image.SCALE_SMOOTH)
                );
        }
        if (this.goldenPawnImg != null) {
            this.goldenIcon = new ImageIcon(
                this.goldenPawnImg.getScaledInstance(
                    labelWidth, 
                    labelHeight, 
                    Image.SCALE_SMOOTH)
                );
        }

        // Cria os 4 JLabels e os posiciona lado a lado
        for (int i = 0; i < 4; i++) {
            pawnLabels[i] = new javax.swing.JLabel();
            
            if (stdIcon != null) {
                pawnLabels[i].setIcon(stdIcon); // Aplica a imagem azul padrão
            }
            
            // Calcula a posição X de cada peão (O primeiro é startX, o segundo é startX + tamanho + espaco...)
            int xPos = startX + (i * (labelWidth + pawnOffset));
            
            pawnLabels[i].setBounds(xPos, startY, labelWidth, labelHeight);
            
            // Adiciona o JLabel ao PawnControlContainer
            add(pawnLabels[i]);

            // Chama o método "pawnVisualState"
            pawnVisualState(0, "NORMAL");
        }
    }

    /**
     * Altera o visual de um peão específico no painel.
     * @param pawnIndex O índice do peão (0 a 3).
     * @param pawnState O estado visual: "NORMAL", "DESABILITADO" ou "DOURADO".
     */
    public void pawnVisualState(int pawnIndex, String pawnState) {
        // Trava de segurança para não quebrar o código se o índice for inválido
        if (pawnIndex < 0 || pawnIndex >= 4) return;

        // Switch para controlar os estados que o peão pode assumir
        switch (pawnState) {
            case "NORMAL":
                pawnLabels[pawnIndex].setIcon(this.stdIcon);
                break;
            case "DESABILITADO":
                pawnLabels[pawnIndex].setIcon(this.disabledIcon);
                break;
            case "DOURADO":
                pawnLabels[pawnIndex].setIcon(this.goldenIcon);
                break;
            default:
                System.out.println(
                    "[PawnControl] Estado desconhecido: " + pawnState
                );
        }
        
        // Atualiza a tela para mostrar a nova imagem
        repaint();
    }
}