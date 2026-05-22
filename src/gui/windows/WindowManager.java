package gui.windows;
import javax.swing.*; 
import java.awt.*; 

public class WindowManager extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainScreen;
    private int mainScreenWidth = 900;
    private int mainScreenHeight = 600;

    // --- Construtor 
    public WindowManager() {
        
        // --- Configurações da Tela --- //
        setTitle("Ludo Advance Together - Tela Inicial"); 
        setSize(mainScreenWidth, mainScreenHeight);
        setLocationRelativeTo(null); 
        
        // --- Instancia o painel que irá receber as telas --- //
        cardLayout = new CardLayout();
        mainScreen = new JPanel(cardLayout);

        
        // --- Instancia as telas passando o layout e o painel como argumentos --- //
        JPanel mainMenuScreen = MenuInterface.mainMenu(cardLayout, mainScreen);



        // --- Adiciona o painel principal dentro do JFrame --- //
        add(mainScreen);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        setVisible(true); 
    }
}