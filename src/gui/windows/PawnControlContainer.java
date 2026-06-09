// Classe responsável por construir o painel que irá conter o controle dos peões

// Packages
package gui.windows;

// Imports internos
import control.PawnControlManager;
import gui.components.ReferencePawn;

// Imports externos
import javax.swing.JPanel;
import java.awt.Rectangle;

public class PawnControlContainer extends JPanel {
    // VARIÁVEIS DE INSTÂNCIA
    private PawnControlManager pawnControlManager;
    private static final String STD_PAWN_IMG_PATH = "/assets/peaoAzul_90x90.png"; // Caminho da imagem padrão do peão
    private static final String DISABLED_PAWN_IMG_PATH = "/assets/peaoRosa_90x90.png"; // Caminho da imagem do peão desabilitado
    private static final String GOLDEN_PAWN_IMG_PATH = "/assets/peaoAmarelo_90x90.png"; // Caminho da imagem do peão desabilitado
    private ReferencePawn[] pawnLabels;
    private static final double SCALE = 1.5;
    private static final Rectangle PAWN_CONTROL_CONTAINER_BOUNDS = new Rectangle(
        (int) (0 * SCALE),
        (int) (0 * SCALE),
        (int) (220 * SCALE),
        (int) (100 * SCALE)
    );
    
    
    public PawnControlContainer(PawnControlManager pawnControlManager) {
        this.pawnControlManager = pawnControlManager;
        setBounds(PAWN_CONTROL_CONTAINER_BOUNDS); // Tamanho e posição do painel de controle dos peões (0, 0) (330x180)
        setOpaque(true);   // Mantém o painel transparente 
        setLayout(null);    // Permite que a PawnControl ocupe o espaço absoluto interno

        // Instancia o array para conter 4 elementos
        this.pawnLabels = new ReferencePawn[4];

        // Define o tamanho de cada JLabel (largura e altura)
        int labelWidth = (int) (30 * SCALE);
        int labelHeight = (int) (30 * SCALE);

        // Loop para instanciar os peões de referência passando como parâmetro o path das imagens dos peões e a escala desejada para eles dentro dos "JLabels"
        // Cria os 4 JLabels e os posiciona lado a lado
        for (int i = 0; i < 4; i++) {
            pawnLabels[i] = new ReferencePawn(
                STD_PAWN_IMG_PATH,
                DISABLED_PAWN_IMG_PATH,
                GOLDEN_PAWN_IMG_PATH,
                1.5
            );

            // Adiciona o JLabel ao PawnControlContainer
            add(pawnLabels[i]);

            final int PAWN_INDEX = i;
            
           
            pawnLabels[i].addMouseListener(new java.awt.event.MouseAdapter() {
                // Sobrescreve o método nativo de "MouseListener"
                @Override
                // Métodos para que o peão do tabuleiro posso ser "sensível" ao hover e ao clique
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    // Quando o mouse está sobre o peão do tabuleiro chama o método para fazer o peão do tabuleiro pular
                    if ("NORMAL".equals(pawnControlManager.getPawnState(PAWN_INDEX)) || 
                        "DESABILITADO".equals(pawnControlManager.getPawnState(PAWN_INDEX))) {
                        // Se o estado do peão do tabuleiro/referência é "NORMAL" OU "DESABILITADO" chama o método para fazer o peão do tabuleiro pular
                        System.out.println(
                            "Mouse ENTROU no peão de referência " + PAWN_INDEX + " - Iniciar tremor!"
                        );

                        /*
                        * Avisa a classe "PawnControlManager" para que peão do tabuleiro ("PlayerPawn") possa executar a funcionalidade de pulo através do método "startBoardPawnShake" que está na classe "PlayerPawn". O método "onReferencePawnHoverEntered" é uma espécie de telefone que escuta quando o mouse passa por cima do peão de referência
                        */
                        pawnControlManager.onReferencePawnHoverEntered(PAWN_INDEX);
                    }
                }
                @Override
                // Quando o mouse sai de cima do peão do tabuleiro chama o método para fazer o peão do tabuleiro pular
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if ("NORMAL".equals(pawnControlManager.getPawnState(PAWN_INDEX)) || 
                        "DESABILITADO".equals(pawnControlManager.getPawnState(PAWN_INDEX))) {
                        // Se o estado do peão do tabuleiro/referência é "NORMAL" OU "DESABILITADO" chama o método para fazer o peão do tabuleiro parar pular    
                        System.out.println(
                            "Mouse SAIU do peão de referência " + PAWN_INDEX + " - Parar tremor."
                        );

                        /*
                        * Avisa a classe "PawnControlManager" para que peão do tabuleiro ("PlayerPawn") possa executar a funcionalidade de parar pulo através do método "stopBoardPawnShake" que está na classe "PlayerPawn". O método "onReferencePawnHoverExited" é uma espécie de telefone que escuta quando o mouse sai de cima do peão de referência
                        */
                        pawnControlManager.onReferencePawnHoverExited(PAWN_INDEX);
                    }
                }
                @Override
                // Quando o peão de referência é clicado, chama o método para que o peão do tabuleiro seja selecionado para realizar sua ação
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if ("NORMAL".equals(pawnControlManager.getPawnState(PAWN_INDEX))) {
                        // Se o estado do peão do tabuleiro/referência é "NORMAL" chama o método para fazer o peão do tabuleiro executar sua ação (avançar, retroceder, etc)
                        System.out.println(
                            "Peão de referência " + PAWN_INDEX + " foi CLICADO! Avisar o GameManager!"
                        );

                        /*
                        * Avisa a classe "PawnControlManager" para que peão do tabuleiro ("PlayerPawn") possa executar sua ação de avanço ou retroação. O método "onReferencePawnClicked" é uma espécie de telefone que escuta quando o peão de referência é clicado
                        */
                        pawnControlManager.onReferencePawnClicked(PAWN_INDEX);
                    }
                }
            });
        }
        
        // Define as posições e o tamanho dos peões
        pawnLabels[0].setBounds((int) (50 * SCALE), (int) (10 * SCALE), labelWidth, labelHeight);
        pawnLabels[1].setBounds((int) (130 * SCALE), (int) (10 * SCALE), labelWidth, labelHeight);
        pawnLabels[2].setBounds((int) (50 * SCALE), (int) (60 * SCALE), labelWidth, labelHeight);
        pawnLabels[3].setBounds((int) (130 * SCALE), (int) (60 * SCALE), labelWidth, labelHeight);

        // Chama o método "pawnVisualState"
        pawnVisualState(0, "NORMAL");
        pawnVisualState(1, "DOURADO");
        pawnVisualState(2, "NORMAL");
        pawnVisualState(3, "DESABILITADO");

        
        this.pawnControlManager.setPawnControlContainer(this);
    }

    /**
     * Altera o visual de um peão específico no painel.
     * @param pawnIndex O índice do peão (0 a 3).
     * @param pawnState O estado visual: "NORMAL", "DESABILITADO" ou "DOURADO".
     */
    public void pawnVisualState(int pawnIndex, String pawnState) {
        // Trava de segurança para não quebrar o código se o índice for inválido
        if (pawnIndex < 0 || pawnIndex >= 4) return;
        
        pawnControlManager.setPawnState(pawnIndex, pawnState);

        pawnLabels[pawnIndex].setVisualState(pawnState);
        
        // Atualiza a tela para mostrar a nova imagem
        repaint();
    } 
    
    public ReferencePawn getReferencePawn(int index) {
    if (index >= 0 && index < 4) {
        return pawnLabels[index];
    }
    return null;
}

}