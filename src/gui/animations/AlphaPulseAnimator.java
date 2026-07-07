package gui.animations;

import javax.swing.JPanel;
import javax.swing.Timer;

public class AlphaPulseAnimator {
    private Timer timer;
    private long startTime;

    public AlphaPulseAnimator(JPanel panel) {
        // Marca a hora que a animação nasceu
        startTime = System.currentTimeMillis();
        
        // O Timer agora só tem a função de mandar a tela repintar a cada 30ms
        timer = new Timer(30, e -> panel.repaint());
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    // NOVO: Calcula o brilho individual dependendo da posição (index) da bolinha
    public int getAlphaForIndex(int index) {
        long tempoDecorrido = System.currentTimeMillis() - startTime;
        
        // Cria uma onda senoidal matemática (vai de -1.0 a 1.0)
        // O valor 0.005 dita a velocidade da onda.
        // O valor 0.5 dita o quão diferente a bolinha atual é da próxima bolinha.
        double onda = Math.sin((tempoDecorrido * 0.005) - (index * 0.5));
        
        // Converte a onda para o limite do Alpha (que no Java vai de 0 a 255)
        // Mapeamos para oscilar suavemente entre 50 e 200
        int alpha = (int) (125 + (75 * onda));
        
        return alpha;
    }
}