package service.filtres;

import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.Image;

public abstract class FiltreAbstrait implements Filtre {
    protected int width,height;
    protected PixelReader reader;
    protected PixelWriter writer;
    protected WritableImage wImage;

    protected void initialise(Image inputImage){
        width =(int) inputImage.getWidth();
        height =(int) inputImage.getHeight();
        reader=inputImage.getPixelReader();
        wImage=new WritableImage(width,height);
        writer=wImage.getPixelWriter();
    }
    
    // Method to get the processed image
    protected Image getProcessedImage() {
        return wImage;
    }
    
    // Method to implement image symmetry (horizontal flip)
    public Image flipHorizontal(Image image) {
        initialise(image);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setColor(width - x - 1, y, reader.getColor(x, y));
            }
        }
        
        return wImage;
    }
    
    // Method to implement image symmetry (vertical flip)
    public Image flipVertical(Image image) {
        initialise(image);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setColor(x, height - y - 1, reader.getColor(x, y));
            }
        }
        
        return wImage;
    }
}
