package gui.animations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.JComponent;

/**
 * Orquestrador central de animações em tempo real para as cartas do jogo.
 * Controla os ciclos de pulsação de aura e deslocamento de brilho holográfico.
 */
public class CardAnimator {
    private Timer timer;
    private float alphaAura = 0.5f;
    private boolean aumentandoAlpha = true;
    private float deslocamentoBrilho = 0.0f;
    
    // Taxa de atualização fixa para ~60 frames por segundo (16ms)
    private static final int FPS_DELAY = 16; 
    
    private final JComponent componenteAlvo;
    private final double escala;

    public CardAnimator(JComponent componenteAlvo, double escala) {
        this.componenteAlvo = componenteAlvo;
        this.escala = escala;
        inicializarTimer();
    }

    private void inicializarTimer() {
        this.timer = new Timer(FPS_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atualizarAnimacoes();
                // Solicita que o componente se repinte para refletir as mudanças visuais
                if (componenteAlvo != null) {
                    componenteAlvo.repaint();
                }
            }
        });
    }

    private void atualizarAnimacoes() {
        // 1. Cálculo do efeito de respiração suave (Breathing/Fade)
        if (aumentandoAlpha) {
            alphaAura += 0.012f;
            if (alphaAura >= 1.0f) {
                alphaAura = 1.0f;
                aumentandoAlpha = false;
            }
        } else {
            alphaAura -= 0.012f;
            if (alphaAura <= 0.2f) { // Limite mínimo para não sumir totalmente
                alphaAura = 0.2f;
                aumentandoAlpha = true;
            }
        }

        // 2. Cálculo do deslocamento do brilho metálico correndo
        deslocamentoBrilho += (2.5f * escala);
        // Reseta o flash quando ele cruzar totalmente o dobro da largura estimada da carta
        float larguraMaximaEstimada = (float) (250 * escala * 2.2);
        if (deslocamentoBrilho > larguraMaximaEstimada) {
            deslocamentoBrilho = 0.0f;
        }
    }

    /** Inicia o ciclo de animação da carta */
    public void start() {
        if (timer != null && !timer.isRunning()) {
            timer.start();
        }
    }

    /** Interrompe o timer (útil para economizar memória quando a carta sai da tela) */
    public void stop() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    // Getters para que a classe da carta consulte os valores em tempo real no paintComponent
    public float getAlphaAura() { return alphaAura; }
    public float getDeslocamentoBrilho() { return deslocamentoBrilho; }
}