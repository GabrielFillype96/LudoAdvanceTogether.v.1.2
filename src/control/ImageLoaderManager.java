// Classe responsável por carregar e gerenciar as imagens do sistema de forma centralizada

// Packages
package control;

// Imports externos
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public class ImageLoaderManager {
    
    /**
     * Carrega um "ImageIcon" de forma segura
     * @param path Caminho do recurso (ex: "/assets/nome_do_arquivo.png")
     * @return O ImageIcon carregado, ou null se houver erro.
     */
    public static ImageIcon loadIcon(String path) {
        URL imgUrl = ImageLoaderManager.class.getResource(path);
        
        if (imgUrl != null) {
            return new ImageIcon(imgUrl);
        } else {
            System.out.println(
                "[ImageLoader] Erro: Arquivo não encontrado no caminho: " + path
            );
            return null;
        }
    }

    /**
     * Sobrecarga de método para carregar um "ImageIcon" de forma segura e redimensioná-lo.
     * @param path Caminho do recurso (ex: "/assets/nome_do_arquivo.png")
     * @param width Largura final
     * @param height Altura final
     * @return O ImageIcon redimensionado, ou null se houver erro
     */
    public static ImageIcon loadIcon(String path, int width, int height) {
        // Reutiliza o método original (de cima) para buscar o arquivo
        ImageIcon originalIcon = loadIcon(path);
        
        if (originalIcon != null) {
            // Se a imagem original não for nula realiza o redimensionamento
            Image originalImg = originalIcon.getImage();
            // Executa o redimensionamento suave
            Image rescaleImg = originalImg.getScaledInstance(
                width, 
                height, 
                Image.SCALE_SMOOTH
            );
            return new ImageIcon(rescaleImg);
        }
        
        return null;
    }
}