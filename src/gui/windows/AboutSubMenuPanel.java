package gui.windows;

import gui.theme.GameColors;

import java.awt.*;
import java.net.URL;
import javax.swing.*;

public class AboutSubMenuPanel extends JPanel {

    private static final double SCALE = 1.5;
    private static final Dimension MENU_DIMENSION = new Dimension(
        (int) (560 * SCALE),
        (int) (460 * SCALE)
    );

    // Caminho da imagem da logo (Ajuste a pasta de assets se necessário)
    private static final String LOGO_PATH = "/assets/img/gameIcon.png";

    public AboutSubMenuPanel() {
        setPreferredSize(MENU_DIMENSION);
        setMinimumSize(MENU_DIMENSION);
        setMaximumSize(MENU_DIMENSION);

        setOpaque(false);
        setLayout(null);

        // --- TÍTULO ---
        JLabel title = new JLabel("SOBRE O JOGO", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, (int) (22 * SCALE)));
        title.setForeground(GameColors.GOLD_ACCENT);
        title.setBounds((int) (0 * SCALE), (int) (20 * SCALE), (int) (560 * SCALE), (int) (30 * SCALE));
        add(title);

        // --- LOGO ---
        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setBounds((int) (220 * SCALE), (int) (55 * SCALE), (int) (120 * SCALE), (int) (120 * SCALE));

        URL logoUrl = getClass().getResource(LOGO_PATH);
        if (logoUrl != null) {
            ImageIcon icon = new ImageIcon(logoUrl);
            Image img = icon.getImage().getScaledInstance((int) (110 * SCALE), (int) (110 * SCALE), Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
        } else {
            lblLogo.setText("<html><center><b>LUDO</b><br/>ADVANCE TOGETHER</center></html>");
            lblLogo.setFont(new Font("SansSerif", Font.BOLD, (int) (11 * SCALE)));
            lblLogo.setForeground(GameColors.GOLD_ACCENT);
        }
        add(lblLogo);

        // --- ÁREA DAS REGRAS (TEXTO COM ROLAGEM) ---
        JEditorPane txtRules = new JEditorPane();
        txtRules.setContentType("text/html");
        txtRules.setEditable(false);
        txtRules.setOpaque(false);
        txtRules.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

        String htmlContent = "<html>"
                + "<body style='font-family: SansSerif; color: #FFFFFF; font-size: 11px; margin: 5px;'>"
                + "<h3 style='color: #F5C672; text-align: center; margin-bottom: 8px;'>REGRAS DO JOGO</h3>"
                
                + "<p><b>Bem-vindo(a)!</b> Este jogo é uma adaptação do clássico Ludo, projetado para divertir e informar sobre direitos, prevenção e canais de apoio no combate à violência contra a mulher. Você jogará contra 3 adversários controlados pelo computador. O objetivo é ser o primeiro a levar todos os seus peões até a base final.</p>"
                
                + "<h4 style='color: #F5C672; margin-top: 10px; margin-bottom: 4px;'>Como Funciona o Baralho?</h4>"
                + "<p>Em vez de dados, a sua movimentação é definida por um baralho especial com 6 tipos de cartas. Cada carta traz uma pergunta educativa e o número de casas a avançar.</p>"
                
                + "<p><b>Cartas de Perguntas:</b><br/>"
                + "• <b style='color: #55FF55;'>Verde (Fácil):</b> Perguntas simples de conscientização.<br/>"
                + "• <b style='color: #FFFF55;'>Amarela (Médio):</b> Nível intermediário de informação.<br/>"
                + "• <b style='color: #FF5555;'>Vermelha (Difícil):</b> Leis e recursos de apoio.<br/>"
                + "<span style='color: #FFC107;'><i>ATENÇÃO! Para mover o peão, você precisa acertar a pergunta!</i></span></p>"
                
                + "<p><b>Cartas Especiais:</b><br/>"
                + "• <b style='color: #F5C672;'>Dourada (Sorte):</b> Bônus ou vantagem.<br/>"
                + "• <b>Prata (Pegadinhas):</b> Desafios divertidos.<br/>"
                + "• <b>Bronze (Azar):</b> Obstáculos na pista.</p>"
                
                + "<h4 style='color: #F5C672; margin-top: 10px; margin-bottom: 4px;'>Saindo da Base</h4>"
                + "<p>Para tirar um peão da base e colocá-lo na pista, tire uma carta de <b>6 casas</b> e acerte a pergunta.</p>"
                
                + "<h4 style='color: #F5C672; margin-top: 10px; margin-bottom: 4px;'>Fim de Jogo</h4>"
                + "<p>A partida termina assim que você ou um dos computadores levar todos os peões ao centro do tabuleiro.</p>"
                
                + "<p style='text-align: center; color: #F5C672; margin-top: 12px;'><b>Dica:</b> Conhecimento é a nossa maior ferramenta de prevenção e proteção. Bom jogo!</p>"
                + "</body></html>";

        txtRules.setText(htmlContent);
        txtRules.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(txtRules);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.setBounds((int) (40 * SCALE), (int) (190 * SCALE), (int) (480 * SCALE), (int) (235 * SCALE));

        add(scrollPane);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fundo principal (Roxo padrão)
        g2.setColor(GameColors.PURPLE_BG);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), (int) (24 * SCALE), (int) (24 * SCALE));

        // Card do conteúdo das regras
        Color cardBg = new Color(42, 24, 54);
        Color cardBorder = new Color(222, 179, 102, 60);

        int cardX = (int) (30 * SCALE);
        int cardY = (int) (180 * SCALE);
        int cardW = (int) (500 * SCALE);
        int cardH = (int) (255 * SCALE);

        g2.setColor(cardBg);
        g2.fillRoundRect(cardX, cardY, cardW, cardH, (int) (12 * SCALE), (int) (12 * SCALE));

        g2.setColor(cardBorder);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, (int) (12 * SCALE), (int) (12 * SCALE));

        // Borda externa principal (Dourada)
        g2.setColor(GameColors.GOLD_ACCENT);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect((int) (2 * SCALE), (int) (2 * SCALE), getWidth() - (int) (5 * SCALE), getHeight() - (int) (5 * SCALE), (int) (24 * SCALE), (int) (24 * SCALE));
    }
}