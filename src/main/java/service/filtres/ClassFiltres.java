package service.filtres;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import java.util.List;
import model.MetaDonnees;
import service.MetaDonneesService;

public class ClassFiltres extends FiltreAbstrait implements Filtre {
    // implémentation de la methode d echange de composantes de couleurs
    @Override
    public Image swapComposantes(Image image) {
       initialise (image);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                Color swapColor = new Color(color.getBlue(), color.getGreen(), color.getRed(), color.getOpacity());
                writer.setColor(x, y, swapColor);
            }
        }
        return wImage;
    }

    // implementation de la methode noir et blanc
    @Override
    public Image NoirEtBlanc(Image image) {
       initialise (image);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                //calcule de la moyenne descomposantes red green blue
                double grey = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                Color greyColor = new Color(grey, grey, grey, color.getOpacity());
                //appliquer la couleur a l'image dest
                writer.setColor(x, y, greyColor);
            }
        }
        return wImage;
    }
    
    // implementatio de la methode sepia
    @Override
    public Image sepia(Image image) {
       initialise (image);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);

                double r=color.getRed();
                double g=color.getGreen();
                double b=color.getBlue();

                double newr=Math.min(1.0,0.393 *r + 0.769 *g +0.189 *b);
                double newg=Math.min(1.0, 0.349 * r +0.686 *g + 0.168 *b);
                double newb=Math.min(1.0, 0.272 * r +0.534 * g + 0.131 *b);

                Color sepiaColor= new Color(newr,newg,newb,color.getOpacity());
                writer.setColor(x, y, sepiaColor);
            }
        }
        return wImage;
    }

    //implementation de la methode sobel
    @Override
    public Image sobel(Image image) {
        initialise (image);
        // matrices pour la detection des bords vericales
        int[][]  sobel_x={
                {-1,0,1},
                {-2,0,2},
                {-1,0,1}
        };
        // pour les bords horizontales
        int[][] sobel_y={
                {-1,-2,-1},
                {0,-0, 0},
                {1, 2,1 }
        };

        //appliquez les convultion sur les pixels sauf les bords
        for(int y=1 ; y<height-1 ; y++){
            for(int x=1 ; x<width-1 ; x++){
                double gx=0;
                double gy=0;

                for(int dy=-1 ; dy<=1 ; dy++){
                    for(int dx=-1 ; dx<=1 ; dx++){
                        Color color = reader.getColor(x+dx, y+dy);
                        double intensity=(color.getRed()+color.getBlue()+color.getGreen())/3.0;
                        gx+=sobel_x[dy+1][dx+1]*intensity;
                        gy+=sobel_y[dy+1][dx+1]*intensity;
                    }
                }
                double magnitude=Math.sqrt(gx*gx + gy*gy);

                //limite la valeur de magnitude
                magnitude=Math.min(1.0,magnitude);
                Color bords=new Color(magnitude,magnitude,magnitude,1.0);

                //appliquer la couleur
                writer.setColor(x, y, bords);
            }
        }
        return wImage;
    }
    
    @Override
    public Image rotation(Image image) {
        initialise(image);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(x, height - y - 1, color);
            }
        }
        
        return wImage;
    }
    
    @Override
    public Image flipHorizontal(Image image) {
        initialise(image);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(width - x - 1, y, color);
            }
        }
        
        return wImage;
    }
    
    @Override
    public Image flipVertical(Image image) {
        initialise(image);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(x, height - y - 1, color);
            }
        }
        
        return wImage;
    }
    
    // Method to help debug tag issues
    public static void debugTags(String searchTag) {
        List<MetaDonnees> allMetadata = MetaDonneesService.charge();
        System.out.println("Debugging tag search for: " + searchTag);
        System.out.println("Total metadata entries: " + allMetadata.size());
        
        for (MetaDonnees meta : allMetadata) {
            System.out.println("Image path: " + meta.getChemin());
            System.out.println("Tags: " + meta.getTags());
        }
    }
}





