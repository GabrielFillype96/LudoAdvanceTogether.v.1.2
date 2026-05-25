package gui.windows;

public class WindowManager {
    
    private MainScreenInterface planoDeFundo;

    // Construtor que recebe a tela de fundo real para podermos adicionar coisas nela
    public WindowManager(MainScreenInterface planoDeFundo) {
        this.planoDeFundo = planoDeFundo;
    }

    public void abrirMenuOffline() {
        System.out.println("[WindowManager] Criando e centralizando o NewGameMenuInterface...");
        
        // Cria a instância do menu passando este gerenciador para ele
        NewGameMenuInterface miniMenu = new NewGameMenuInterface(this);
        
        // Centraliza o menu baseado nas dimensões dele (560x420) na tela (900x600)
        miniMenu.setBounds((900 - 560) / 2, (600 - 420) / 2, 560, 420);
        
        if (planoDeFundo != null) {
            // Adiciona o mini-menu ao plano de fundo
            planoDeFundo.add(miniMenu);
            
            // Força a placa roxa a ficar na camada mais alta da frente
            planoDeFundo.setComponentZOrder(miniMenu, 0); 
            
            // Atualiza o Swing para redesenhar a tela imediatamente com o novo menu visível
            planoDeFundo.revalidate();
            planoDeFundo.repaint();
        } else {
            System.err.println("[WindowManager Erro] O plano de fundo está nulo!");
        }
    }

    public void iniciarPartidaOffline(String nomeJogador, String dificuldade) {
        System.out.println("[WindowManager] Iniciando transição para a tela de jogo...");

        if (planoDeFundo != null) {
            // 1. Limpa absolutamente tudo o que estava desenhado na tela de fundo (botões e menu roxo)
            planoDeFundo.removeAll();

            // 2. Cria a nossa tela primitiva de jogo passando as strings coletadas
            GamePanel telaJogo = new GamePanel(nomeJogador, dificuldade);

            // 3. Adiciona a tela de jogo ao plano de fundo
            planoDeFundo.add(telaJogo);

            // 4. Força o Swing a revalidar a árvore de componentes e redesenhar o monitor
            planoDeFundo.revalidate();
            planoDeFundo.repaint();
        }
    }
}