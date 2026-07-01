// Classe responsável por gerenciar as regras de movimentação dos peões de acordo com as cartas

// Packages
package control;

// Imports interno
import gui.windows.BoardScreen;
import gui.components.PlayerPawn;

// Imports externos
import java.awt.Point;
import javax.swing.Timer;
import javax.swing.JOptionPane;

public class GameManager {
    // VARIÁVEIS DE INSTÂNCIA
    private BoardScreen boardScreen;
    private Timer timerAnimation; // Variável para armazenar o método Timer
    //private boolean isAtBase = true; // Controle se o peão ainda não saiu para o circuito
    private PawnControlManager pawnControlManager;

    /** 
    ** Construtor da classe "GameManager" que passa como parâmetro o tabuleiro do jogo
    * @param boardScreen Tabuleiro do jogo
    */
    public GameManager(BoardScreen boardScreen) {
        this.boardScreen = boardScreen;
    }

    /** 
    ** Método setter para que a classe "GameManager" conheça a classe "PawnControlManager"
    * @param pawnControlManager Gerenciador de controle dos peões
    */
    public void setPawnControlManager(PawnControlManager pawnControlManager) {
        this.pawnControlManager = pawnControlManager;
    }

    // Método para processar o resultado das cartas
    public void cardResultVerification(boolean correct, String cardValue, String cardEffect) {
        
        if (boardScreen == null) {
        // Se o tabuleiro for nulo encerra a aplicação
            System.err.println(
                "[GameManager] Erro: O tabuleiro não foi registrado!"
            );
            return;
        }

        // Se uma animação já estiver ocorrendo, ignora cliques repetidos por segurança 
        if (timerAnimation != null && timerAnimation.isRunning()) {
            return;
        }
 
        // Instância o player 1 passando o index do peão (para testes)
        PlayerPawn p1 = boardScreen.getPlayer1Pawn(0);

        // Array que armazena as coordenadas do path das casas dos peões
        Point[] mapaCasas = boardScreen.getCaminhoCasas();

        // Se o peão do jogador ou o track do seu caminho for nulo, encerra a aplicação 
        if (p1 == null || mapaCasas == null) {
            System.err.println(
                "[GameManager] Erro: Componentes do tabuleiro não inicializados."
            );
            return;
        }

        // Se o jogador errou a carta (diferente de "correct"), não acontece nada
        if (!correct) {
            System.out.println(
                "[GameManager] Resposta incorreta. O peão " + p1.getPlayerName() + " permaneceu na casa " + p1.getPawnCurrentPos()
            );
            return;
        }

        if ("AVANÇAR".equalsIgnoreCase(cardEffect)) {

            // Se acertou e o efeito da carta for avançar, processa esse avanço
            try {
                // Realiza um tratamento no valor do carta para pegar o número de casas que ele precisa andar
                String cardValueTreated = cardValue.trim(); // Remove os espaços em branco no início e fim da string
                
                if (cardValueTreated.contains("/")) {
                    // Se a string do valor da carta possuí o caractere "/", retira da string e elimina os espaços em branco
                    cardValueTreated = cardValueTreated.split("/")[0].trim();
                }

                int valorDado = Integer.parseInt(cardValueTreated);

                // ETAPA 2: Interceptando o movimento!
                // Em vez de andar o peão 0 automaticamente, guardamos o valor na memória.
                if (this.pawnControlManager != null) {
                    System.out.println(
                        "[GameManager] Interceptado! Mandando " + valorDado + " passos para a memória..."
                    );
                    this.pawnControlManager.preparePendingMovement(valorDado, cardEffect);
                } else {
                    System.err.println(
                        "[GameManager] Erro: PawnControlManager não foi injetado no GameManager!"
                    );
                }
                

            } catch (NumberFormatException e) {
                System.err.println("[GameManager] Erro: O valor do efeito não pôde ser convertido: " + cardValue);
            }
        }
    }
    

    /**
    * Método responsável por realizar movimentação do peão pelo tabuleiro, de forma que seja realizada progressivamente
    * @param mapaCasas Vetor que guarda cada posição (x, y) do tabuleiro.
    * @param fromWhere Índice da casa onde o peão se encontra
    * @param toWhere índice da casa para onde o peão vai
    * 
    */
    private void pawnMovement(PlayerPawn playerPawn, int fromWhere, int toWhere, Point[] mapaCasas, boolean ganhouTurnoExtra, boolean baseExit) {
        java.util.List<Point> pawnPathList = new java.util.ArrayList<>(); // Lista que armazena a casa que o peão irá avançar/retroceder até o seu destino final. Inclui a casa que ele se contra atualmente. Evita que o método tenha que olha para o path inteiro do jogador
        
        // Condicional para evitar que o peão percorra os caminhos intermediários enquanto estiver na base
        if (baseExit) {
            // Se está saindo da base, ignora o loop sequencial e vai direito para casa de saída [4]
            pawnPathList.add(mapaCasas[fromWhere]); // Adiciona a lista a casa onde peão estão ([0], [1], [2], [3])
            
            // Loop que adiciona as casas sequenciais a partir do índice [4] até o destino final. Assim evita que ele tenha que passar pelos outros índices até chegar na sua casa de destino
            for (int i = 4; i <= toWhere; i++) {
                pawnPathList.add(mapaCasas[i]);
            }
        } else {
            // Loop de movimentação normal pelo tabuleiro (casa por casa)
            for (int i = fromWhere; i <= toWhere; i++) {
                pawnPathList.add(mapaCasas[i]); // Pega o índice ("i") do path (vetor que corresponde a casa) e vai adicionando ao lista "pontosDoCaminho" que servirá de guia para a movimentação do peão
            }
        }

        /*
        * Classes internas como o "Timer" só conseguem acessar variáveis fora dela se essas variáveis forem do tipo "final", ou seja, que não pode ser alterada, mas a função "Timer" precisa somar a velocidade definida para movimentação ocorrer. Assim ao criar um lista "final" seu endereço na memória permanece fixa, mas é possível alterar o seu conteúdo interno (itens da lista)
        */

        final int[] STEP_INDEX = {0}; // Lista que armazena o número de casas que o peão já percorreu.

        // Capturam a posição real em pixels onde o peão está no exato momento.
        final double[] VISUAL_POS_X = {playerPawn.getX()}; // Pega a coordenada "x" do peão no momento exato que ele está desenhado na tela e adiciona a lista "VISUAL_POS_X"
        final double[] VISUAL_POS_Y = {playerPawn.getY()};// Pega a coordenada "y" do peão no momento exato que ele está desenhado na tela a lista "VISUAL_POS_Y"
        final int SPEED = 8; // Quantos pixels o peão irá andar em 0.015seg

        // Ativa a trava na classe "PlayerPawn" para impedir que o peão bug ao "pular" enquanto se movimenta
        playerPawn.setMoving(true);

        // Força o peão a parar de pular se o mouse estiver em cima dele
        playerPawn.stopBoardPawnShake();

        // Cria uma classe anônima
        timerAnimation = new Timer(15, new java.awt.event.ActionListener() {
            @Override
            // Método para executar uma ação repetidamente a cada intervalo de tempo determinado (0.015seg)
            public void actionPerformed(java.awt.event.ActionEvent e) {
                
                // Verifica se o caminho já chegou ao fim
                if (STEP_INDEX[0] >= pawnPathList.size() - 1) {
                    // Se o índice da etapa que ele está agora for maior ou igual a quantidade de paradas (tamanho da lista menos 1)  que o peão precisa fazer, irá parar a animação de movimentação
                    timerAnimation.stop();
                    System.out.println("[Animação] Movimento concluído suavemente.");

                    // Verifica as condições finais que o peão o jogador está submetido (final do tabuleiro, turno extra, etc)
                    verificarCondicoesFinais(playerPawn, toWhere, ganhouTurnoExtra);
                    return;
                }

                // Caso o caminho que o peão precisa percorrer ainda não chegou ao fim, ele precisará descobrir qual a casa ele precisa ir para conseguir chegar ao destino.
                // Quando o peão anda uma casa, soma 1 ao índice. A variável "pontosDoCaminho" pega a coordenada da casa referente ao índice e o armazena na variável "destinoIntermediário". Assim o peão se move até essa coordenada. 
                Point intermediateSteps = pawnPathList.get(STEP_INDEX[0] + 1); // Contador se mantém o mesmo após toda a operação("0")
                
                // Realiza uma subtração entre a coordenada onde o peão quer ir ("destinoIntermediario") e a coordenada que ele está ("posVisual")
                double dx = intermediateSteps.getX() - VISUAL_POS_X[0];
                double dy = intermediateSteps.getY() - VISUAL_POS_Y[0];

                // Calcula por Pitágoras a distância restante que o peão precisa percorrer em linha reta
                // O calculo é feito por Pitágoras para quando o peão precisar se mover na diagonal, ele não anadar "x" e "y" ao mesmo tempo. Isso faria o peão se mover mais rápido (a diagonal do triângulo)
                double remainingDistance = Math.sqrt(dx * dx + dy * dy);

                // Espécie de trava para impedir que o peão acabe passando do centro da casa que ele precisa percorrer
                if (remainingDistance <= SPEED) {
                    // Se a distância que falta para o peão chegar a próxima casa for menor ou igual que a velocidade, ele ignora a quantidade de pixels que deveria andar por frame (velocidade) e vai direto para o centro da casa
                    VISUAL_POS_X[0] = intermediateSteps.getX();
                    VISUAL_POS_Y[0] = intermediateSteps.getY();

                    // "Seta" o peão para a coordenada do destino
                    playerPawn.setPawnVisualCoordinates(intermediateSteps);
                    
                    // Permite que o peão possa "pular" novamente
                    playerPawn.setMoving(false);

                    // Redesenha o tabuleiro
                    boardScreen.repaint();

                    // Ao somar mais 1 o peão sabe que conclui essa etapa. Assim quando passar pelo "indiceEtapa[0] + 1" ele sabe para qual casa irá em direção
                    STEP_INDEX[0]++;  // Contador é permanentemente somado +1
                } else {
                    // Soma a velocidade de pixels que o peão precisa andar a posição que ele se encontra
                    // Realiza uma divisão entre a distância que ele precisa percorrer pela distância total restante. Assim será possível definir em qual direção ele irá se movimentar de acordo com o sinal (cima, baixo, direita, esquerda)
                    // Multiplica a proporção, encontrada com a divisão, pela velocidade e soma da posição o peão se encontra.
                    VISUAL_POS_X[0] += (dx / remainingDistance) * SPEED; 
                    VISUAL_POS_Y[0] += (dy / remainingDistance) * SPEED;
                    
                    // A cada frame é inserida essa nova posição somada e o paintComponent "repinta" o peão nela
                    playerPawn.setPawnVisualCoordinates(new Point((int) VISUAL_POS_X[0], (int) VISUAL_POS_Y[0]));
                }
            }
        });

        timerAnimation.start();
    }

    /**
     ** Método acionado pelo PawnControlManager quando o jogador clica em um peão de referência, calculando a lógica de movimentação
    */
    public boolean moveChosenPawn(int pawnIndex, int cardValue, String cardEffect) {
        // Pega o peão exato que o jogador escolheu
        PlayerPawn chosenPawn = boardScreen.getPlayer1Pawn(pawnIndex);
        
        // Trava de segurança caso o peão seja nulo
        if (chosenPawn == null) {
            // Se o peão escolhido for nulo, interrompe a execução e avisa que falhou
            System.err.println(
                "[GameManager] Erro: Peão índice " + pawnIndex + " não encontrado no tabuleiro!"
            );
            return false;
        }

        Point[] pawnPath = boardScreen.getCaminhoCasas();
        int pawnActualPosition = chosenPawn.getPawnCurrentPos();
        
        System.out.println(
            "[GameManager] Iniciando cálculo para o Peão " + pawnIndex + ". Posição Atual Lógica: " + pawnActualPosition
        );

        // 2. Trata os efeitos da carta
        boolean isBackwards = "VOLTAR".equalsIgnoreCase(cardEffect) || "RETRÓGRADO".equalsIgnoreCase(cardEffect);
        if (isBackwards && cardValue > 0) {
            cardValue = -cardValue; // Garante que o valor fique negativo se for para trás
        }
        
        boolean ganhouTurnoExtra = (Math.abs(cardValue) == 6);
        boolean exitBase = false;

        // 3. Regra de Saída da Base INDIVIDUAL:
        // Se a posição atual for menor que 4, ele AINDA está no quadrante da base (índices 0, 1, 2 ou 3)
        if (pawnActualPosition < 4) {
            if (Math.abs(cardValue) == 1 || Math.abs(cardValue) == 6) {
                // Índice 4 é a primeira casa real do circuito externo
                int pawnStarterPath = 4; 
                
                chosenPawn.setPawnCurrentPos(pawnStarterPath);
                exitBase = true; // Saiu da base!
                
                System.out.println("[GameManager] Peão " + pawnIndex + " AUTORIZADO a sair da base. Indo para a casa " + pawnStarterPath);
                
                // Chama a sua animação
                pawnMovement(chosenPawn, pawnActualPosition, pawnStarterPath, pawnPath, ganhouTurnoExtra, exitBase);
                return true; // SUCESSO! O peão vai andar.
            } else {
                System.out.println(
                    "[GameManager] Peão " + pawnIndex + " não pode sair da base (tirou " + Math.abs(cardValue) + ")."
                );
                JOptionPane.showMessageDialog(boardScreen, 
                    "Este peão específico precisa de um 1 ou 6 para sair da base!", 
                    "Movimento Inválido", JOptionPane.WARNING_MESSAGE);
                return false; // FALHOU! A regra impediu. O PawnControlManager NÃO vai limpar a memória.
            }
        }

        // 4. Movimentação normal pelo tabuleiro (para peões que já estão da casa 4 em diante)
        
        // Soma a posição atual com o valor da carta
        int pawnStarterPath = pawnActualPosition + cardValue; 

        // Trava de segurança para não ultrapassar a chegada (final do array)
        if (pawnStarterPath >= pawnPath.length) {
            pawnStarterPath = pawnPath.length - 1;
        }
        
        // Trava de segurança para não voltar para dentro da base (antes da casa 4) se o efeito for negativo
        if (pawnStarterPath < 4) {
            pawnStarterPath = 4;
        }

        System.out.println("[GameManager] Peão " + pawnIndex + " movendo no circuito. Indo de " + pawnActualPosition + " para " + pawnStarterPath);

        // Atualiza a posição lógica e chama a animação
        chosenPawn.setPawnCurrentPos(pawnStarterPath);
        pawnMovement(chosenPawn, pawnActualPosition, pawnStarterPath, pawnPath, ganhouTurnoExtra, exitBase);
        
        return true; // SUCESSO! O peão vai andar.
    }

    private void verificarCondicoesFinais(PlayerPawn peao, int posicaoAlcancada, boolean ganhouTurnoExtra) {
        if (posicaoAlcancada >= boardScreen.getCaminhoCasas().length - 1) {
            JOptionPane.showMessageDialog(boardScreen, 
                "🏆 VITÓRIA! O peão de " + peao.getPlayerName() + " alcançou o Centro do Tabuleiro!\nVocê venceu o jogo!", 
                "Fim de Partida", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (ganhouTurnoExtra) {
            JOptionPane.showMessageDialog(boardScreen, 
                "Incrível! Você tirou um efeito de valor '6'!\nVocê ganhou o direito de jogar novamente.", 
                "Turno Bônus", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}


