/* Essa classe será responsável por padronizar a estilização dos botões, para que não seja preciso repetir todo esse processo sempre que um botão for ser criado */
// Packages
package gui.components.buttons;
// Imports
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/*
* Cria uma classe "CustomButton" que irá herdar métodos da classe JButton (nativa do Java Swing) 
*/ 
public class CustomButton extends JButton {
    // Variáveis
    private ImageIcon standardImage;
    private ImageIcon effectedImage;
    private static final int btnWidth = 200;
    private static final int btnHeight = 45;

    /*  
    *  Não é necessário atrelar um objeto aos métodos abaixo, pois a classe "CustomButton" é um JButton 
    *  e assim os métodos estariam sendo aplicados em si mesmo.
    */
    
    /*  
    * Construtor 1: inserir a arte do botão e aplicar as mudanças quando em estado hover.
    * @param standardImage O ImageIcon que representa a arte padrão do botão.
    * @param effectedImage O ImageIcon que representa a arte do botão com efeito.
    * Para diferenciar os construtores "CustomButton" e utilizar a sobrecarga de construtor, é utilizado parâmetro "hoverStatus".
    * Estrutura do construtor --> <visibilidade> <nomeConstrutor>(<tipoDado> <nomeVariável>).
    */
    public CustomButton(ImageIcon  standardImage, ImageIcon effectedImage, boolean hoverStatus) { // O tipo de dado "ImageIcon" é nativo do Java Swing
        // Envia a imagem para o construtor original do JButton
        super(standardImage != null ? new ImageIcon(standardImage.getImage().getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH)) : null);

        this.standardImage = resizedButton(standardImage); // Chama o método de redimensionamento e atribui a variável "resizedStandardImage" o resultado do método, passando como parâmetro a imagem original "standardImage".
        ImageIcon resizedEffectedImage = resizedButton(effectedImage); // Chama o método de redimensionamento e atribui a variável "resizedEffectedImage" o resultado do método, passando como parâmetro a imagem original "effectedImage".

        // Verifica se a imagem foi carregada ("effectedImage" é diferente de vazio).
        if (resizedEffectedImage != null) {
            setRolloverIcon(resizedEffectedImage); // Método nativo do Java Swing que permite inserir a imagem referente ao parâmetro "effectedImage".
            setRolloverEnabled(true); // Método nativo do Java Swing que habilita o recurso de hover.
        }

        initStyle(); // Chama o método de limpeza
    }

    /*
    * Construtor 2: atribui a imagem do parâmetro para a variável global
    * @param standardImage O ImageIcon que representa a arte padrão do botão.
    * @param effectedImage O ImageIcon que representa a arte do botão com efeito.
    */
    public CustomButton(ImageIcon standardImage, ImageIcon effectedImage) {
        super(standardImage != null ? new ImageIcon(standardImage.getImage().getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH)) : null); // Envia a imagem para o construtor original do JButton


        // O "this." permite fazer referência a variável global
        this.standardImage = resizedButton(standardImage); // Chama o método de redimensionamento e atribui a variável global "standardImage" o resultado do método, passando como parâmetro a imagem original "standardImage".
        this.effectedImage = resizedButton(effectedImage); // Chama o método de redimensionamento e atribui a variável global "effectedImage" o resultado do método, passando como parâmetro a imagem com efeito "effectedImage".

        initStyle(); // Chama o método de limpeza
    }

    /*
    * Método que realiza a troca das imagens quando selecionada
    */
    public void setSelected(boolean selected) {
        /*
        * Verifica se as imagens ("effectedImage" e "standardImage") não estão vazias.
        */
        if (effectedImage != null && standardImage != null) {
            /*
            * Espécie de if/else de forma compacta.
            * Estrutura --> <condição> ? <seForVerdadeiro> : <seForFalso>
            */
            setIcon(selected ? effectedImage : standardImage);
        }
    }

    // Método para redimensionar as imagens dos botões
    private ImageIcon resizedButton(ImageIcon originalImageBtn) {
        if (originalImageBtn == null) return null;

        Image catchImage = originalImageBtn.getImage();

        Image resizedImageBtn = catchImage.getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);

        return new ImageIcon(resizedImageBtn);
    }

    /* 
    * Método que realiza a limpeza visual dos botões nativos do Java Swing 
    * Visibilidade "private" --> permite ser visualizada/acionada dentro dessa classe "CustomButton"
    */
    private void initStyle() {
        setBorderPainted(false);     // Remove a borda quadrada cinza padrão
        setContentAreaFilled(false); // Remove o preenchimento de fundo padrão
        setFocusPainted(false);      // Remove a linha pontilhada quando o botão é selecionado
        setOpaque(false);     // Garante que o fundo do botão seja transparente (respeitando o PNG)
        setMargin(new Insets(0, 0, 0, 0));
        setIconTextGap(0);  

        Dimension tamanhoFixo = new Dimension(btnWidth, btnHeight);
        setPreferredSize(tamanhoFixo);
        setMinimumSize(tamanhoFixo);
        setMaximumSize(tamanhoFixo);
        setSize(tamanhoFixo);
        
        
        
        // if (image != null) {
        //     setPreferredSize(new Dimension(image.getIconWidth(), image.getIconHeight()));
        // }
        
    }

}