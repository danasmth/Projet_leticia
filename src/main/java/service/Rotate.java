package service;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;


import java.io.File;

public class Rotate {
public static Image loadImageFromFile(File file){

    if(file!=null && file.exists()){
       return new Image(file.toURI().toString());
    }
    return null;

}
public static Image rotation(Image image){
    // recuprer la largeur et hauteur de l'image d'origine
    int width= (int) image.getWidth();
    int height= (int) image.getHeight();
    // creation d'une nouvelle image vide pour appliquer les modifs
     WritableImage wImage= new WritableImage(width,height);
     //methode pixelReader qui lit les pixels de l image dorigine
    PixelReader reader=image.getPixelReader();
    // methode pour ecrire les pixels dans la nouvelle page
    PixelWriter writer= wImage.getPixelWriter();
    //parcour des pixels
    for(int y=0; y<height ; y++){
        for(int x=0; x<width ; x++){
            Color color= reader.getColor(x, y);
            writer.setColor(x, height - y - 1, color);


        }
    }
    return wImage;

}

}
