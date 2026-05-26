package cards;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class CustomCards extends JPanel {
    
    // --- ATRIBUTOS LÓGICOS (ESTRUTURA DE DADOS) ---
    private int cardID;
    private String cardType;       
    private String textoPrincipal;  
    private String cardEffect;     
    private String cardValue;      
    
    private String dificuldade;    
    private String tipoPergunta;   
    private String[] alternativas; 
    private String cardAnswer;     
    
    // --- ATRIBUTOS VISUAIS (RENDERIZAÇÃO) ---
    private Color corFundo;
    private Image imgPeao;
    private Image imgEfeito;
    
    // --- CONSTANTES DE CORES PERSONALIZADAS ---
    private final Color MOLDURA_PRETA = new Color(20, 20, 20);
    private final Color TEXTO_ESCURO = new Color(35, 35, 35); 
    
    // =========================================================================
    // MODIFICADO AQUI: Cor das Molduras Internas alteradas para Preto sólido
    // =========================================================================
    private final Color COR_MOLDURA_EXTERNA = MOLDURA_PRETA;
    private final Color COR_MOLDURA_INTERNA = MOLDURA_PRETA; 
    
    // Cores de fundo (Categorias)
    private final Color COR_FACIL = Color.decode("#546B41");     
    private final Color COR_MEDIO = new Color(222, 179, 102);    
    private final Color COR_DIFICIL = new Color(178, 34, 34);    
    
    // Tons Metálicos para as Cartas Especiais
    private final Color COR_SORTE = new Color(212, 175, 55);     
    private final Color COR_SACANEAR = new Color(150, 165, 175); 
    private final Color COR_AZAR = new Color(165, 90, 45);      

    // =========================================================================
    // CONSTRUTORES
    // =========================================================================
    public CustomCards(int cardID, String cardType, String textoPrincipal, String cardEffect, String cardValue, String nomeIconePeao) {
        this.cardID = cardID;
        this.cardType = cardType.toUpperCase();
        this.textoPrincipal = textoPrincipal;
        this.cardEffect = cardEffect.toUpperCase();
        this.cardValue = cardValue;
        this.dificuldade = "";
        this.tipoPergunta = "";
        this.alternativas = null;
        this.cardAnswer = "";
        definirCorFundo();
        carregarIcones(nomeIconePeao);
        configurarComponente();
    }

    public CustomCards(int cardID, String cardType, String textoPrincipal, String cardEffect, String cardValue, String nomeIconePeao, String dificuldade, String cardAnswer) {
        this.cardID = cardID;
        this.cardType = cardType.toUpperCase();
        this.textoPrincipal = textoPrincipal;
        this.cardEffect = cardEffect.toUpperCase();
        this.cardValue = cardValue;
        this.dificuldade = dificuldade.toUpperCase();
        this.tipoPergunta = "SIM_NAO";
        this.cardAnswer = cardAnswer;
        this.alternativas = null;
        definirCorFundo();
        carregarIcones(nomeIconePeao);
        configurarComponente();
    }

    public CustomCards(int cardID, String cardType, String textoPrincipal, String cardEffect, String cardValue, String nomeIconePeao, String dificuldade, String[] alternativas, String cardAnswer) {
        this.cardID = cardID;
        this.cardType = cardType.toUpperCase();
        this.textoPrincipal = textoPrincipal;
        this.cardEffect = cardEffect.toUpperCase();
        this.cardValue = cardValue;
        this.dificuldade = dificuldade.toUpperCase();
        this.tipoPergunta = "MULTIPLA_ESCHLE";
        this.alternativas = alternativas;
        this.cardAnswer = cardAnswer;
        definirCorFundo();
        carregarIcones(nomeIconePeao);
        configurarComponente();
    }

    private void configurarComponente() {
        setSize(220, 340);
        setBounds(0, 0, 220, 340);
        setOpaque(false);
    }

    private void definirCorFundo() {
        if (this.cardType.equals("PERGUNTA")) {
            switch (this.dificuldade) {
                case "FÁCIL" -> this.corFundo = COR_FACIL;
                case "MÉDIO" -> this.corFundo = COR_MEDIO;
                case "DIFÍCIL" -> this.corFundo = COR_DIFICIL;
                default -> this.corFundo = COR_MEDIO;
            }
        } else {
            switch (this.cardType) {
                case "SORTE" -> this.corFundo = COR_SORTE;
                case "SACANEAR" -> this.corFundo = COR_SACANEAR;
                case "AZAR" -> this.corFundo = COR_AZAR;
                default -> this.corFundo = COR_SORTE;
            }
        }
    }

    private void carregarIcones(String nomeIconePeao) {
        try {
            if (nomeIconePeao != null && !nomeIconePeao.isEmpty()) {
                java.net.URL urlPeao = getClass().getResource("/assets/" + nomeIconePeao);
                if (urlPeao == null) urlPeao = getClass().getResource("/assets/icons/" + nomeIconePeao);
                if (urlPeao != null) this.imgPeao = new ImageIcon(urlPeao).getImage();
            }
            
            String iconeEfeito = this.cardEffect.equals("AVANÇAR") ? "setA_avancar.png" : "setA_retroceder.png";
            java.net.URL urlEfeito = getClass().getResource("/assets/" + iconeEfeito);
            if (urlEfeito == null) urlEfeito = getClass().getResource("/assets/icons/" + iconeEfeito);
            if (urlEfeito != null) this.imgEfeito = new ImageIcon(urlEfeito).getImage();
        } catch (Exception e) {
            System.err.println("[CustomCards] Aviso ao carregar ícones: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int raioCarta = 24; 

        // 1. FUNDO PRINCIPAL ESCURO (#546B41)
        g2.setColor(corFundo);
        g2.fillRoundRect(0, 0, w, h, raioCarta, raioCarta);

        // 2. MOLDURA EXTERNA DE CONTENÇÃO PRETA
        g2.setColor(MOLDURA_PRETA);
        g2.setStroke(new BasicStroke(3.0f));
        g2.drawRoundRect(2, 2, w - 4, h - 4, raioCarta, raioCarta);

        int offsetExt = 22;      
        int cruzExt = 14;        
        int pequenoOffset = 6;   
        int offsetInt = offsetExt + pequenoOffset; 
        int cruzInt = cruzExt - 2;

        // 3. MAPEAMENTO DO POLÍGONO DE RECORTE DAS QUINAS INVERTIDAS
        Polygon areaInternaClara = new Polygon();
        areaInternaClara.addPoint(offsetInt + cruzInt, offsetInt);
        areaInternaClara.addPoint(w - offsetInt - cruzInt, offsetInt);
        areaInternaClara.addPoint(w - offsetInt - cruzInt, offsetInt + cruzInt);
        areaInternaClara.addPoint(w - offsetInt, offsetInt + cruzInt);
        areaInternaClara.addPoint(w - offsetInt, h - offsetInt - cruzInt);
        areaInternaClara.addPoint(w - offsetInt - cruzInt, h - offsetInt - cruzInt);
        areaInternaClara.addPoint(w - offsetInt - cruzInt, h - offsetInt);
        areaInternaClara.addPoint(offsetInt + cruzInt, h - offsetInt);
        areaInternaClara.addPoint(offsetInt + cruzInt, h - offsetInt - cruzInt);
        areaInternaClara.addPoint(offsetInt, h - offsetInt - cruzInt);
        areaInternaClara.addPoint(offsetInt, offsetInt + cruzInt);
        areaInternaClara.addPoint(offsetInt + cruzInt, offsetInt + cruzInt);

        // Preenchimento do centro claro (#99AD7A)
        if (this.cardType.equals("PERGUNTA") && this.dificuldade.equals("FÁCIL")) {
            g2.setColor(Color.decode("#99AD7A"));
        } else {
            g2.setColor(new Color(255, 255, 255, 195)); 
        }
        g2.fillPolygon(areaInternaClara);

        // 4. DESENHO DAS MOLDURAS INTERNAS (Agora pretas)
        g2.setColor(COR_MOLDURA_EXTERNA);
        g2.setStroke(new BasicStroke(2.0f));
        desenharMolduraCruzInvertida(g2, offsetExt, cruzExt, w, h);

        g2.setColor(COR_MOLDURA_INTERNA);
        g2.setStroke(new BasicStroke(1.2f)); 
        desenharMolduraCruzInvertida(g2, offsetInt, cruzInt, w, h);

        // 5. TEXTOS SUPERIORES
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(Color.WHITE);
        String cabecalho = cardType;
        if (cardType.equals("PERGUNTA")) {
            cabecalho += " - " + dificuldade;
        }
        g2.drawString(cabecalho, 34, 42); 

        if (imgPeao != null) {
            g2.drawImage(imgPeao, w - 46, 24, 24, 24, this);
        }

        // 6. ENUNCIADO DO TEXTO
        g2.setColor(TEXTO_ESCURO);
        g2.setFont(new Font("Tahoma", Font.BOLD, 13)); 
        int xTexto = offsetInt + 20;
        int yTexto = offsetInt + 28;
        int larguraMaxTexto = w - (xTexto * 2);
        desenharTextoComQuebra(g2, textoPrincipal, xTexto, yTexto, larguraMaxTexto, "CENTRO");

        // 7. RODAPÉ DA CARTA
        if (imgEfeito != null) {
            g2.drawImage(imgEfeito, 34, h - 72, 42, 42, this);
        }

        g2.setFont(new Font("Arial", Font.BOLD, 32));
        g2.setColor(Color.WHITE);
        g2.drawString(cardValue, 88, h - 38);
        
        g2.setFont(new Font("Arial", Font.ITALIC, 10));
        g2.setColor(new Color(230, 230, 230));
        g2.drawString("#" + cardID, w - 54, h - 32);

        g2.dispose();
    }

    private void desenharMolduraCruzInvertida(Graphics2D g2, int offset, int tamCruz, int w, int h) {
        g2.drawLine(offset + tamCruz, offset, w - offset - tamCruz, offset);
        g2.drawLine(offset + tamCruz, h - offset, w - offset - tamCruz, h - offset);
        g2.drawLine(offset, offset + tamCruz, offset, h - offset - tamCruz);
        g2.drawLine(w - offset, offset + tamCruz, w - offset, h - offset - tamCruz);
        
        g2.drawLine(offset, offset + tamCruz, offset + tamCruz, offset + tamCruz);
        g2.drawLine(offset + tamCruz, offset, offset + tamCruz, offset + tamCruz);
        g2.drawLine(w - offset, offset + tamCruz, w - offset - tamCruz, offset + tamCruz);
        g2.drawLine(w - offset - tamCruz, offset, w - offset - tamCruz, offset + tamCruz);
        g2.drawLine(offset, h - offset - tamCruz, offset + tamCruz, h - offset - tamCruz);
        g2.drawLine(offset + tamCruz, h - offset, offset + tamCruz, h - offset - tamCruz);
        g2.drawLine(w - offset, h - offset - tamCruz, w - offset - tamCruz, h - offset - tamCruz);
        g2.drawLine(w - offset - tamCruz, h - offset, w - offset - tamCruz, h - offset - tamCruz);
    }

    /**
 * Quebra de linha automática baseada no limite da área útil,
 * com suporte a alinhamento: "ESQUERDA", "CENTRO" ou "DIREITA".
 */
private void desenharTextoComQuebra(Graphics2D g2, String texto, int x, int y, int larguraMax, String alinhamento) {
    if (texto == null || texto.isEmpty()) return;

    FontRenderContext frc = g2.getFontRenderContext();
    AttributedString attrString = new AttributedString(texto);
    attrString.addAttribute(TextAttribute.FONT, g2.getFont());
    attrString.addAttribute(TextAttribute.FOREGROUND, g2.getColor());
    
    LineBreakMeasurer measurer = new LineBreakMeasurer(attrString.getIterator(), frc);
    int endIndex = texto.length();

    while (measurer.getPosition() < endIndex) {
        TextLayout layout = measurer.nextLayout(larguraMax);
        y += layout.getAscent();
        
        // --- CÁLCULO DINÂMICO DO ALINHAMENTO ---
        int xAlinhado = x; // Padrão: ESQUERDA
        
        if (alinhamento.equalsIgnoreCase("CENTRO")) {
            // Calcula a sobra de espaço da linha e divide por 2 para centralizar
            float larguraLinha = layout.getAdvance();
            xAlinhado = x + (int)((larguraMax - larguraLinha) / 2);
            
        } else if (alinhamento.equalsIgnoreCase("DIREITA")) {
            // Empurra a linha até o limite direito
            float larguraLinha = layout.getAdvance();
            xAlinhado = x + (int)(larguraMax - larguraLinha);
        }
        
        // Desenha a linha na posição X corrigida
        layout.draw(g2, xAlinhado, y);
        y += layout.getDescent() + layout.getLeading();
    }
}

    // --- GETTERS ---
    public int getCardID() { return cardID; }
    public String getCardType() { return cardType; }
    public String getCardEffect() { return cardEffect; }
    public String getCardValueText() { return cardValue; }
    public String getDificuldade() { return dificuldade; }
    public String getTipoPergunta() { return tipoPergunta; }
    public String[] getAlternativas() { return alternativas; }
    public String getCardAnswer() { return cardAnswer; }
}