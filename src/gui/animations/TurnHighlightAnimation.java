package gui.animations;

import javax.swing.JPanel;
import javax.swing.Timer;

public class TurnHighlightAnimation {

    private Timer timer;
    private JPanel targetPanel;
    
    // Variáveis que serão lidas pelo CardDeckBackground para desenhar o frame atual
    private int yOffset = 0;
    private int glowOpacity = 0;
    
    // Variável interna para controlar a onda do seno (movimento contínuo)
    private double angle = 0.0;

    // <-- NOVO: Controla a rotação dos raios de luz
    private double rayRotation = 0.0;

    public TurnHighlightAnimation(JPanel targetPanel) {
        this.targetPanel = targetPanel;
        
        // Configura o timer para rodar a ~60 FPS (16ms)
        this.timer = new Timer(16, e -> updateAnimation());
    }

    public void start() {
        if (!timer.isRunning()) {
            this.angle = 0.0; // Reseta a animação ao iniciar
            this.timer.start();
        }
    }

    public void stop() {
        if (timer.isRunning()) {
            this.timer.stop();
            // Reseta os valores para a carta voltar ao estado normal
            this.yOffset = 0;
            this.glowOpacity = 0;
            this.targetPanel.repaint(); // Força um último desenho sem animação
        }
    }

    private void updateAnimation() {
        // Incrementa o ângulo para a onda matemática avançar
        // Quanto maior esse valor, mais rápida será a animação
        angle += 0.05; 
        
        // 1. CALCULA A FLUTUAÇÃO (yOffset)
        // Math.sin() retorna de -1 a 1. 
        // Multiplicamos por 5 e subtraímos 5 para a carta subir de 0 a -10 pixels e voltar a 0.
        yOffset = (int) ((Math.sin(angle) * 5) - 5);

        // 2. CALCULA A OPACIDADE DO BRILHO (glowOpacity)
        // Queremos que a opacidade vá de 0 até um limite (ex: 180) para não ficar branca demais.
        // Convertendo o seno para ir de 0 a 1, e multiplicando pelo máximo de opacidade.
        double normalizedSin = (Math.sin(angle) + 1.0) / 2.0; // Vai de 0.0 a 1.0
        glowOpacity = (int) (normalizedSin * 180);

        // <-- NOVO: Incrementa a rotação da luz em 0.5 graus por frame
        rayRotation += 0.5; 
        if (rayRotation >= 360.0) {
            rayRotation = 0.0;
        }

        if (targetPanel != null) {
            targetPanel.repaint();
        }
    }

    // <-- NOVO: Getter para a rotação
    public double getRayRotation() {
        return rayRotation;
    }

    // Getters para a classe CardDeckBackground saber onde desenhar a carta e o brilho
    public int getYOffset() {
        return yOffset;
    }

    public int getGlowOpacity() {
        return glowOpacity;
    }
    
    public boolean isAnimating() {
        return timer.isRunning();
    }
}