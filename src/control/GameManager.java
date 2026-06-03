package control;

import gui.windows.BoardScreen;
import gui.components.PlayerPawn;
import java.awt.Point;
import javax.swing.Timer;
import javax.swing.JOptionPane;

public class GameManager {
    
    private static BoardScreen tabuleiro;
    private static Timer animacaoTimer;
    private static boolean estaNaBase = true; // Controle se o peão ainda não saiu para o circuito

    public static void setTabuleiro(BoardScreen painelTabuleiro) {
        tabuleiro = painelTabuleiro;
    }

    public static void processarResultadoCarta(boolean acertou, String valorEfeito, String tipoEfeito) {
        if (tabuleiro == null) {
            System.err.println("[GameManager] Erro: O tabuleiro não foi registrado!");
            return;
        }

        // Se uma animação já estiver ocorrendo, ignora cliques repetidos por segurança
        if (animacaoTimer != null && animacaoTimer.isRunning()) {
            return;
        }

        PlayerPawn p1 = tabuleiro.getPlayer1Pawn();
        Point[] mapaCasas = tabuleiro.getCaminhoCasas();

        if (p1 == null || mapaCasas == null) {
            System.err.println("[GameManager] Erro: Componentes do tabuleiro não inicializados.");
            return;
        }

        // Se o jogador errou a carta
        if (!acertou) {
            System.out.println("[GameManager] Resposta incorreta. O peão " + p1.getPlayerName() + " permaneceu na casa " + p1.getPosicaoLogicaAtual());
            return;
        }

        // Se acertou, processa o avanço
        if ("AVANÇAR".equalsIgnoreCase(tipoEfeito)) {
            try {
                // Seu tratamento original e correto para o "6/6"
                String textoLimpo = valorEfeito.trim();
                if (textoLimpo.contains("/")) {
                    textoLimpo = textoLimpo.split("/")[0].trim();
                }

                int valorDado = Integer.parseInt(textoLimpo);
                int posicaoAtual = p1.getPosicaoLogicaAtual();

                // Caso especial: O peão está saindo da base neste turno
                if (estaNaBase) {
                    if (valorDado == 1 || valorDado == 6) {
                        estaNaBase = false;
                        System.out.println("[GameManager] " + p1.getPlayerName() + " tirou " + valorDado + " e SAIU DA BASE!");
                        
                        // tentar utilizar um switch case para as diferentes saídas de casas
                        int novaPosicaoLogica = 4; // Ex: se tirou 6, vai para a casa de índice 6
                        
                        if (novaPosicaoLogica >= mapaCasas.length) {
                            novaPosicaoLogica = mapaCasas.length - 1;
                        }
                        
                        p1.setPosicaoLogicaAtual(novaPosicaoLogica);
                        
                        // CORREÇÃO: Força o ponto inicial visual como -1 de forma abstrata 
                        // para que o primeiro passo intermediário seja obrigatoriamente a Casa [0]
                        iniciarAnimacaoSuave(p1, -1, novaPosicaoLogica, mapaCasas, valorDado == 6);
                        return;
                    } else {
                        System.out.println("[GameManager] " + p1.getPlayerName() + " tirou " + valorDado + ", mas precisa de 1 ou 6 para sair da base.");
                        return;
                    }
                }

                // Movimentação normal quando já está no circuito
                int novaPosicaoLogica = posicaoAtual + valorDado;

                if (novaPosicaoLogica >= mapaCasas.length) {
                    novaPosicaoLogica = mapaCasas.length - 1;
                }

                p1.setPosicaoLogicaAtual(novaPosicaoLogica);
                iniciarAnimacaoSuave(p1, posicaoAtual, novaPosicaoLogica, mapaCasas, valorDado == 6);

            } catch (NumberFormatException e) {
                System.err.println("[GameManager] Erro: O valor do efeito não pôde ser convertido: " + valorEfeito);
            }
        }
    }


    

    
    private static void iniciarAnimacaoSuave(PlayerPawn peao, int de, int para, Point[] mapaCasas, boolean ganhouTurnoExtra) {
        
        if (de == para) {
            verificarCondicoesFinais(peao, para, ganhouTurnoExtra);
            return;
        }

        final int VELOCIDADE = 8; 
        final int indiceDestino = para;
        
        // Calcula o próximo índice. Se veio da base (-1), o próximo obrigatoriamente será 0
        final int proximoIndiceIntermediario = (de < para) ? de + 1 : de - 1;
        
        Point destinoIntermediario = mapaCasas[proximoIndiceIntermediario];

        final double[] posVisualX = { peao.getCoordenadaVisual().getX() };
        final double[] posVisualY = { peao.getCoordenadaVisual().getY() };

        animacaoTimer = new Timer(15, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                double xDestino = destinoIntermediario.getX();
                double yDestino = destinoIntermediario.getY();

                double distX = xDestino - posVisualX[0];
                double distY = yDestino - posVisualY[0];
                double distanciaRestante = Math.hypot(distX, distY);

                if (distanciaRestante <= VELOCIDADE) {
                    if (animacaoTimer != null) {
                        animacaoTimer.stop();
                    }

                    peao.setCoordenadaVisual(destinoIntermediario);
                    tabuleiro.repaint();

                    // Próximo passo da recursão fluida
                    iniciarAnimacaoSuave(peao, proximoIndiceIntermediario, indiceDestino, mapaCasas, ganhouTurnoExtra);
                } else {
                    posVisualX[0] += (distX / distanciaRestante) * VELOCIDADE;
                    posVisualY[0] += (distY / distanciaRestante) * VELOCIDADE;
                    
                    peao.setCoordenadaVisual(new Point((int) posVisualX[0], (int) posVisualY[0]));
                    tabuleiro.repaint();
                }
            }
        });
            if (destinoIntermediario == null) {
            System.err.println("ERRO FATAL: A casa de destino " + proximoIndiceIntermediario + " é NULL no BoardScreen!");
            return;
            }
            System.out.println("O peão está a tentar ir para a coordenada: X=" + destinoIntermediario.getX() + " Y=" + destinoIntermediario.getY());
            
            animacaoTimer.start();
    }

    private static void verificarCondicoesFinais(PlayerPawn peao, int posicaoAlcancada, boolean ganhouTurnoExtra) {
        if (posicaoAlcancada >= tabuleiro.getCaminhoCasas().length - 1) {
            JOptionPane.showMessageDialog(tabuleiro, 
                "🏆 VITÓRIA! O peão de " + peao.getPlayerName() + " alcançou o Centro do Tabuleiro!\nVocê venceu o jogo!", 
                "Fim de Partida", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (ganhouTurnoExtra) {
            JOptionPane.showMessageDialog(tabuleiro, 
                "Incrível! Você tirou um efeito de valor '6'!\nVocê ganhou o direito de jogar novamente.", 
                "Turno Bônus", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}


