package gui.animations;

import cards.CustomCards;
import gui.components.CardDeckBackground;
import gui.windows.CardsContainer;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CardFlipAnimation {
    private final CardsContainer container;
    private final CardDeckBackground cardBack;
    private final CustomCards cardFront;
    private final Timer timer;
    private final Runnable onComplete;
    private final boolean isDiscard;

    private double angleDeg = 0.0; 
    private final double speed = 9.0; 
    private boolean showingFront;

    private final int baseX, baseY, baseWidth, baseHeight;
    private final int backX, backY, backWidth, backHeight;

    public CardFlipAnimation(CardsContainer container, CardDeckBackground cardBack, CustomCards cardFront, double scaleFactor, boolean isDiscard, Runnable onComplete) {
        this.container = container;
        this.cardBack = cardBack;
        this.cardFront = cardFront;
        this.isDiscard = isDiscard;
        this.onComplete = onComplete;

        this.baseX = (int) (10 * scaleFactor) + 2;
        this.baseY = (int) (10 * scaleFactor) + 2;
        this.baseWidth = (int) (250 * scaleFactor);
        this.baseHeight = (int) (375 * scaleFactor);

        int glowMargin = 25;
        this.backX = this.baseX - glowMargin;
        this.backY = this.baseY - glowMargin;
        this.backWidth = this.baseWidth + (glowMargin * 2);
        this.backHeight = this.baseHeight + (glowMargin * 2);

        if (!isDiscard) {
            // MODO REVELAR: Começa mostrando as costas
            this.showingFront = false;
            this.cardFront.setVisible(false);
            this.cardFront.setBounds(baseX + baseWidth / 2, baseY, 0, baseHeight);
            this.container.add(cardFront);
        } else {
            // MODO DESCARTAR: Começa mostrando a frente
            this.showingFront = true;
            this.cardBack.setVisible(false);
        }

        this.timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animate();
            }
        });
    }

    public void start() {
        this.timer.start();
    }

    private void animate() {
        angleDeg += speed;

        if (angleDeg >= 180.0) {
            angleDeg = 180.0;
            timer.stop();
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        // Troca de lado na metade do giro (90°) usando ordenação segura
        if (angleDeg >= 90.0) {
            if (!isDiscard && !showingFront) {
                showingFront = true;
                cardBack.setVisible(false);
                cardFront.setVisible(true);
                safeSetComponentZOrder(cardFront, 0);
                safeSetComponentZOrder(cardBack, 1);
            } else if (isDiscard && showingFront) {
                showingFront = false;
                cardFront.setVisible(false);
                cardBack.setVisible(true);
                safeSetComponentZOrder(cardBack, 0);
                safeSetComponentZOrder(cardFront, 1);
            }
        }

        double angleRad = Math.toRadians(angleDeg);
        double widthScale = Math.abs(Math.cos(angleRad));
        double heightScale = 1.0 + (Math.sin(angleRad) * 0.06);

        if (!showingFront) {
            int currentWidth = (int) (backWidth * widthScale);
            int currentHeight = (int) (backHeight * heightScale);
            int currentX = backX + (backWidth - currentWidth) / 2;
            int currentY = backY - (currentHeight - backHeight) / 2; 
            cardBack.setBounds(currentX, currentY, currentWidth, currentHeight);
        } else {
            int currentWidth = (int) (baseWidth * widthScale);
            int currentHeight = (int) (baseHeight * heightScale);
            int currentX = baseX + (baseWidth - currentWidth) / 2;
            int currentY = baseY - (currentHeight - baseHeight) / 2;
            cardFront.setBounds(currentX, currentY, currentWidth, currentHeight);
        }

        container.revalidate();
        container.repaint();
    }

    // MÉTODO AUXILIAR: Evita o erro de "top-level window" garantindo a inclusão no painel antes do Z-Order
    private void safeSetComponentZOrder(java.awt.Component comp, int index) {
        if (comp.getParent() != container) {
            container.add(comp);
        }
        container.setComponentZOrder(comp, index);
    }
}