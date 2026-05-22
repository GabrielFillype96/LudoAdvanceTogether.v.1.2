/* Essa classe será responsável por padronizar a estilização dos botões, para que não seja preciso repetir todo esse processo sempre que um botão for ser criado */
// Packages
package gui.components.buttons;
// Imports
import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/*
* Cria uma classe "CustomButton" que irá herdar métodos da classe JButton (nativa do Java Swing) 
*/ 
public class CustomButton extends JButton {
    // Variáveis
    private ImageIcon standardImage;
    private ImageIcon effectedImage;

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
        super(standardImage); /*  SuperClasse que irá usar um construtor nativo do JButton e passar para ele o parâmetro "standardImage" (que receberá o path da imagem) */

        // Verifica se a imagem foi carregada ("effectedImage é diferente de vazio")
        if (effectedImage != null) {
            setRolloverIcon(effectedImage); // Método nativo do Java Swing que permite inserir a imagem referente ao parâmetro "effectedImage".
            setRolloverEnabled(true); // Método nativo do Java Swing que habilita o recurso de hover.
        }

        initStyle(standardImage); // Chama o método de limpeza
    }

    /*
    * Construtor 2: atribui a imagem do parâmetro para a variável global
    * @param standardImage O ImageIcon que representa a arte padrão do botão.
    * @param effectedImage O ImageIcon que representa a arte do botão com efeito.
    */
    public CustomButton(ImageIcon standardImage, ImageIcon effectedImage) {
        super(standardImage); /*  SuperClasse que irá usar um construtor nativo do JButton e passar para ele o parâmetro "standardImage" (que receberá o path da imagem) */

        // O "this." permite fazer referência a variável global
        this.standardImage = standardImage;
        this.effectedImage = effectedImage;

        initStyle(standardImage);
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

    /* 
    * Método que realiza a limpeza visual dos botões nativos do Java Swing 
    * Visibilidade "private" --> permite ser visualizada/acionada dentro dessa classe "CustomButton"
    */
    private void initStyle(ImageIcon image) {
        setBorderPainted(false);     // Remove a borda quadrada cinza padrão
        setContentAreaFilled(false); // Remove o preenchimento de fundo padrão
        setFocusPainted(false);      // Remove a linha pontilhada quando o botão é selecionado
        setOpaque(false);     // Garante que o fundo do botão seja transparente (respeitando o PNG)

        if (image != null) {
            setPreferredSize(new Dimension(image.getIconWidth(), image.getIconHeight()));
        }
        
    }

}