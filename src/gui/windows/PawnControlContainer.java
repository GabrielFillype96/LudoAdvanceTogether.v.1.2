// Classe responsável por construir o painel que irá conter o controle dos peões

// Packages
package gui.windows;

// Imports internos
import control.PawnControlManager;
import gui.components.ReferencePawn;
import gui.events.ReferencePawnMouseListener;

// Imports externos
import javax.swing.JPanel;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;

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
    
    /**
    * @param pawnControlManager Gerenciador das funcionalidades do peão (wobble, shake, seleção para andar)
    * Construtor da classe "PawnControlContainer" que recebe como parâmetro o gerenciador de movimento dos peões "PawnControlManager"
    */
    public PawnControlContainer(PawnControlManager pawnControlManager) {
        this.pawnControlManager = pawnControlManager;
        setBounds(PAWN_CONTROL_CONTAINER_BOUNDS); // Tamanho e posição do painel de controle dos peões (0, 0) (330x180)
        setOpaque(false);   // Mantém o painel transparente 
        setLayout(null);    // Pe   rmite que a PawnControl ocupe o espaço absoluto interno

        // Instancia o array para conter 4 elementos
        this.pawnLabels = new ReferencePawn[4];

        // Define o tamanho de cada JLabel (largura e altura)
        int labelWidth = (int) (30 * SCALE);
        int labelHeight = (int) (30 * SCALE);

        for (int i = 0; i < 4; i++) {
        pawnLabels[i] = new ReferencePawn(
        STD_PAWN_IMG_PATH, 
        DISABLED_PAWN_IMG_PATH, 
        GOLDEN_PAWN_IMG_PATH, 
        (i + 1), // <--- ADICIONE ESTA LINHA AQUI
        SCALE
    );

            // Adiciona o JLabel ao PawnControlContainer
            add(pawnLabels[i]);

            // Configura o ouvinte de cliques do mouse
            pawnLabels[i].addMouseListener(new ReferencePawnMouseListener(pawnControlManager, i));
        }
        
        // Define as posições e o tamanho dos peões
        pawnLabels[0].setBounds((int) (50 * SCALE), (int) (10 * SCALE), labelWidth, labelHeight);
        pawnLabels[1].setBounds((int) (130 * SCALE), (int) (10 * SCALE), labelWidth, labelHeight);
        pawnLabels[2].setBounds((int) (50 * SCALE), (int) (60 * SCALE), labelWidth, labelHeight);
        pawnLabels[3].setBounds((int) (130 * SCALE), (int) (60 * SCALE), labelWidth, labelHeight);

        // DINÂMICO: Agora todos os 4 peões nascem como "NORMAL" (cor padrão)
        pawnVisualState(0, "NORMAL");
        pawnVisualState(1, "NORMAL");
        pawnVisualState(2, "NORMAL");
        pawnVisualState(3, "NORMAL");

        // Chama o método setter da classe "PawnControlManager" e entrega o próprio (this) "PawnControlContainer"
        this.pawnControlManager.setPawnControlContainer(this);
    }

    @Override
protected void paintComponent(Graphics g) {
    // Garante a renderização correta do fundo do painel
    super.paintComponent(g); 
    
    // Ativa o Graphics2D para recursos visuais avançados
    Graphics2D g2 = (Graphics2D) g.create();
    
    // Ativa o Antialiasing (suavização de bordas) para os cantos arredondados ficarem perfeitos
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    // 1. DEFINIÇÃO DE TAMANHOS ADAPTATIVOS (Respeitando o seu SCALE)
    int w = getWidth();
    int h = getHeight();
    int espessura = (int) (4 * SCALE); // Largura das linhas da cruz
    int arredondamento = (int) (4 * SCALE); // O quão redondos serão os cantos
    int margem = (int) (8 * SCALE); // Afastamento sutil das bordas para não colidir com as laterais
    
    // 2. CONFIGURAÇÃO DA COR
    // Opção A: Usar uma cor customizada translúcida (Dourado sutil com Alpha/Opacidade em 90)
    g2.setColor(new Color(212, 175, 55, 90));
    
    // Opção B: Caso prefira usar a cor padrão do seu tema (descomente a linha abaixo se preferir):
    // g2.setColor(GameColors.GOLD_ACCENT);

    // 3. DESENHO DA CRUZ MODERNA
    // Linha Horizontal (centralizada verticalmente)
    g2.fillRoundRect(
        margem, 
        (h - espessura) / 2, 
        w - (margem * 2), 
        espessura, 
        arredondamento, 
        arredondamento
    );
    
    // Linha Vertical (centralizada horizontalmente)
    g2.fillRoundRect(
        (w - espessura) / 2, 
        margem, 
        espessura, 
        h - (margem * 2), 
        arredondamento, 
        arredondamento
    );
    
    // Libera os recursos gráficos para evitar lentidão
    g2.dispose();
}

    /**
     * Altera o visual de um peão específico no painel.
     * @param pawnIndex O índice do peão (0 a 3).
     * @param pawnState O estado visual: "NORMAL", "DESABILITADO" ou "DOURADO".
     */
    public void pawnVisualState(int pawnIndex, String pawnState) {
        // Trava de segurança para não quebrar o código se o índice for inválido
        if (pawnIndex < 0 || pawnIndex >= 4) return;
        
        // Método setter para modificar a variável "pawnStates" na classe "PawnControlManager".
        pawnControlManager.setPawnState(pawnIndex, pawnState);

        // Utiliza o método perfeito que você já tinha criado no ReferencePawn!
        pawnLabels[pawnIndex].setVisualState(pawnState);
        
        // Atualiza a tela para mostrar a nova imagem
        repaint();
    }
    
    // Método getter para retornar o índice do peão de referência
    public ReferencePawn getReferencePawn(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            return pawnLabels[pawnIndex];
        }
        return null;
    }

    public void startReferencePawnWobble(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            ReferencePawn referencePawn = pawnLabels[pawnIndex];
            if (referencePawn != null) {
                referencePawn.startReferencePawnWobble();
            }
        }
    }

    public void stopReferencePawnWobble(int pawnIndex) {
        if (pawnIndex >= 0 && pawnIndex < 4) {
            ReferencePawn referencePawn = pawnLabels[pawnIndex];
            if (referencePawn != null) {
                referencePawn.stopReferencePawnWobble();
            }
        }
    }
}