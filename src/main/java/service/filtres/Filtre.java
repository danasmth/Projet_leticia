package service.filtres;

import javafx.scene.image.Image;
public interface Filtre {
    Image swapComposantes(Image image);
    Image NoirEtBlanc(Image image);
    Image sepia(Image image);
    Image sobel(Image image);
    Image flipHorizontal(Image image);
    Image flipVertical(Image image);
    Image rotation(Image image);
}
