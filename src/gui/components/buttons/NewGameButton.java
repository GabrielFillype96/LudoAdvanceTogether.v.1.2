package gui.components.buttons;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NewGameButton extends CustomButton {

    public NewGameButton() {
        // Envia o texto específico que este botão deve carregar para a classe mãe tratar
        super("NOVO JOGO");

        // Ouvinte específico para capturar o clique e ativar o estado fixado dourado
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setSelecionado(true);
            }
        });
    }
}