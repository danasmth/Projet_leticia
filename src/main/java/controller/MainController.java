package controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import model.MetaDonnees;
import model.Transformation;
import service.MetaDonneesService;
import service.Rotate;
import service.filtres.Filtre;
import java.io.File;
import java.io.IOException; // Added missing import for IOException
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import service.filtres.ClassFiltres;
import javafx.scene.control.TextArea;

public class MainController {
    @FXML
    private ImageView imageView;
    
    @FXML
    private TextField tagTextField;
    
    @FXML
    private TextField searchTextField;

    private File currentImageFile;
    private Image originalImage; // Store the original image for reset functionality

    @FXML
    protected void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.gif")
        );
       File file = fileChooser.showOpenDialog(null);
       if(file != null) {
           currentImageFile = file;
           originalImage = Rotate.loadImageFromFile(file); // Store original image
           imageView.setImage(originalImage);
           String chemin = file.getAbsolutePath();
           //creer un objet Metadonnees pour cette image
           MetaDonnees meta = MetaDonneesService.trouver(chemin);
           
           if (meta == null) {
               meta = new MetaDonnees(chemin);
               //ajouter un tag
               meta.ajouterTag("choisi");
               // les ajouter a la sauvegarde
               MetaDonneesService.ajouter(meta);
           }
       }
    }
    
    @FXML
    protected void handleResetImage() {
        if (originalImage != null) {
            imageView.setImage(originalImage);
        }
    }
    
    @FXML
    protected void handleRotation() {
        if (imageView.getImage() != null) {
            Image rotated = Rotate.rotation(imageView.getImage());
            imageView.setImage(rotated);
            
            // Save transformation metadata
            if (currentImageFile != null) {
                String chemin = currentImageFile.getAbsolutePath();
                MetaDonnees meta = MetaDonneesService.trouver(chemin);
                
                if (meta == null) {
                    meta = new MetaDonnees(chemin);
                }
                meta.ajouterTransformation(new Transformation("rotation", "90"));
                MetaDonneesService.ajouter(meta);
            }
        }
    }
    
    @FXML
    protected void handleAddTag() {
        if (currentImageFile != null && tagTextField.getText() != null && !tagTextField.getText().isEmpty()) {
            String tag = tagTextField.getText().trim(); // Trim whitespace
            String chemin = currentImageFile.getAbsolutePath();
            
            System.out.println("Adding tag: " + tag + " to image: " + chemin);
            
            MetaDonnees meta = MetaDonneesService.trouver(chemin);
            
            if (meta == null) {
                System.out.println("Creating new metadata for image");
                meta = new MetaDonnees(chemin);
            } else {
                System.out.println("Found existing metadata with tags: " + meta.getTags());
            }
            
            meta.ajouterTag(tag);
            System.out.println("Tags after adding: " + meta.getTags());
            
            // Save immediately
            MetaDonneesService.ajouter(meta);
            System.out.println("Metadata saved");
            
            tagTextField.clear();
            
            // Show confirmation
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Tag ajouté");
            alert.setHeaderText(null);
            alert.setContentText("Le tag '" + tag + "' a été ajouté à l'image.");
            alert.showAndWait();
        }
    }
    
    @FXML
    protected void handleSaveTags() {
        if (currentImageFile != null) {
            String chemin = currentImageFile.getAbsolutePath();
            MetaDonnees meta = MetaDonneesService.trouver(chemin);
            
            if (meta != null) {
                MetaDonneesService.ajouter(meta);
                
                // Show confirmation
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Tags sauvegardés");
                alert.setHeaderText(null);
                alert.setContentText("Les tags ont été sauvegardés avec succès.");
                alert.showAndWait();
            }
        }
    }
    
    @FXML
    protected void handleSearchByTag() {
        if (searchTextField.getText() != null && !searchTextField.getText().isEmpty()) {
            String searchTag = searchTextField.getText().trim().toLowerCase();
            List<MetaDonnees> allMetadata = MetaDonneesService.charge();
            
            // Debug output
            System.out.println("Searching for tag: " + searchTag);
            System.out.println("Number of metadata entries: " + allMetadata.size());
            
            // Use the debug method from ClassFiltres
            ClassFiltres.debugTags(searchTag);
            
            boolean found = false;
            
            // Find images with matching tag (case-insensitive)
            for (MetaDonnees meta : allMetadata) {
                System.out.println("Checking image: " + meta.getChemin());
                System.out.println("Tags: " + meta.getTags());
                
                // Check if any tag matches (case-insensitive)
                boolean hasMatchingTag = false;
                for (String tag : meta.getTags()) {
                    if (tag.toLowerCase().equals(searchTag)) {
                        hasMatchingTag = true;
                        break;
                    }
                }
                
                if (hasMatchingTag) {
                    // Load and display the matching image
                    File file = new File(meta.getChemin());
                    System.out.println("Found matching tag in file: " + file.getAbsolutePath());
                    
                    if (file.exists()) {
                        currentImageFile = file;
                        originalImage = Rotate.loadImageFromFile(file);
                        imageView.setImage(originalImage);
                        
                        // Show confirmation
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Image trouvée");
                        alert.setHeaderText(null);
                        alert.setContentText("Une image avec le tag '" + searchTag + "' a été trouvée.");
                        alert.showAndWait();
                        
                        found = true;
                        break;
                    } else {
                        System.out.println("File does not exist: " + file.getAbsolutePath());
                    }
                }
            }
            
            // No matching image found
            if (!found) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Recherche terminée");
                alert.setHeaderText(null);
                alert.setContentText("Aucune image avec le tag '" + searchTag + "' n'a été trouvée.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    protected void handleGreyFilter() {
        if(imageView.getImage() != null){
              Filtre img= new ClassFiltres();
              Image grey=img.NoirEtBlanc(imageView.getImage());
              imageView.setImage(grey);

              //recuperer les metadonnées
            if (currentImageFile != null) {
                String chemin=currentImageFile.getAbsolutePath();
                MetaDonnees meta= MetaDonneesService.trouver(chemin);

                if(meta == null){
                    meta=new MetaDonnees(chemin);
                }
                meta.ajouterTransformation(new Transformation("noir et blanc","fort"));
                //sauvegarder
                MetaDonneesService.ajouter(meta);
            }
          }
    }

    @FXML
    protected void handleSepiaFilter() {
        if(imageView.getImage() != null){
            Filtre img= new ClassFiltres();
            Image sepia=img.sepia(imageView.getImage());
            imageView.setImage(sepia);
            //recuperer les metadonnées
            if (currentImageFile != null) {
                String chemin=currentImageFile.getAbsolutePath();
                MetaDonnees meta= MetaDonneesService.trouver(chemin);

                if(meta == null){
                    meta=new MetaDonnees(chemin);
                }
                meta.ajouterTransformation(new Transformation("SEPIA","fort"));
                //sauvegarder
                MetaDonneesService.ajouter(meta);
            }
        }
    }
    
    @FXML
    protected void handleSwapFilter() {
        if(imageView.getImage() != null){
          Filtre img= new ClassFiltres();
          Image swap=img.swapComposantes(imageView.getImage());
          imageView.setImage(swap);

            //recuperer les metadonnées
            if (currentImageFile != null) {
                String chemin= currentImageFile.getAbsolutePath();
                MetaDonnees meta= MetaDonneesService.trouver(chemin);

                if(meta == null){
                    meta=new MetaDonnees(chemin);
                }
                meta.ajouterTransformation(new Transformation("swap","fort"));
                //sauvegarder
                MetaDonneesService.ajouter(meta);
            }
        }
    }
    
    @FXML
    protected void handleSobelFilter() {
        if(imageView.getImage() != null){
            Filtre img= new ClassFiltres();
            Image sobel=img.sobel(imageView.getImage());
            imageView.setImage(sobel);

            //recuperer les metadonnées
            if (currentImageFile != null) {
                String chemin= currentImageFile.getAbsolutePath();
                MetaDonnees meta=MetaDonneesService.trouver(chemin);

                if(meta == null){
                    meta=new MetaDonnees(chemin);
                }
                meta.ajouterTransformation(new Transformation("sobel","fort"));
                //sauvegarder
                MetaDonneesService.ajouter(meta);
            }
        }
    }

    @FXML
    protected void handleShowMetadata() {
        File metadataFile = new File("metadonnees.json");
        if (metadataFile.exists()) {
            try {
                // Instead of using Desktop, read the file content and display it
                StringBuilder content = new StringBuilder();
                List<MetaDonnees> metadataList = MetaDonneesService.charge();
                
                content.append("Contenu du fichier métadonnées:\n\n");
                
                if (metadataList.isEmpty()) {
                    content.append("Aucune métadonnée trouvée.");
                } else {
                    content.append("Nombre total d'images: ").append(metadataList.size()).append("\n\n");
                    
                    for (MetaDonnees meta : metadataList) {
                        content.append("Image: ").append(meta.getChemin()).append("\n");
                        content.append("Tags: ").append(String.join(", ", meta.getTags())).append("\n");
                        
                        if (!meta.getTransformations().isEmpty()) {
                            content.append("Transformations:\n");
                            for (Transformation t : meta.getTransformations()) {
                                content.append("  - ").append(t.getType()).append(" (").append(t.getParametre()).append(")\n");
                            }
                        }
                        content.append("\n");
                    }
                }
                
                // Display the content in a dialog
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Métadonnées");
                alert.setHeaderText("Fichier: " + metadataFile.getAbsolutePath());
                
                // Create a scrollable text area for the content
                javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(content.toString());
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setPrefHeight(400);
                textArea.setPrefWidth(600);
                
                alert.getDialogPane().setContent(textArea);
                alert.getDialogPane().setPrefSize(650, 450);
                alert.showAndWait();
                
            } catch (Exception e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Could not read metadata file: " + e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("File Not Found");
            alert.setHeaderText(null);
            alert.setContentText("The metadata file does not exist yet.");
            alert.showAndWait();
        }
    }
    
    @FXML
    protected void handleEncryptImage() {
        if (imageView.getImage() != null) {
            // Create a dialog to get the password
            javafx.scene.control.TextInputDialog passwordDialog = new javafx.scene.control.TextInputDialog();
            passwordDialog.setTitle("Sécurisation d'image");
            passwordDialog.setHeaderText("Entrez un mot de passe pour sécuriser l'image");
            passwordDialog.setContentText("Mot de passe:");
            
            // Get the password
            java.util.Optional<String> result = passwordDialog.showAndWait();
            if (result.isPresent() && !result.get().isEmpty()) {
                String password = result.get();
                
                try {
                    // Apply the encryption filter
                    Image encrypted = encryptImage(imageView.getImage(), password);
                    imageView.setImage(encrypted);
                    
                    // Save transformation metadata (without the password)
                    if (currentImageFile != null) {
                        String chemin = currentImageFile.getAbsolutePath();
                        MetaDonnees meta = MetaDonneesService.trouver(chemin);
                        
                        if (meta == null) {
                            meta = new MetaDonnees(chemin);
                        }
                        meta.ajouterTransformation(new Transformation("securisation", "appliquée"));
                        MetaDonneesService.ajouter(meta);
                    }
                    
                    // Show confirmation
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Image sécurisée");
                    alert.setHeaderText(null);
                    alert.setContentText("L'image a été sécurisée avec succès. Conservez votre mot de passe pour pouvoir la déchiffrer ultérieurement.");
                    alert.showAndWait();
                    
                } catch (Exception e) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Erreur lors de la sécurisation de l'image: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        }
    }
    
    @FXML
    protected void handleDecryptImage() {
        if (imageView.getImage() != null) {
            // Create a dialog to get the password
            javafx.scene.control.TextInputDialog passwordDialog = new javafx.scene.control.TextInputDialog();
            passwordDialog.setTitle("Déchiffrement d'image");
            passwordDialog.setHeaderText("Entrez le mot de passe pour déchiffrer l'image");
            passwordDialog.setContentText("Mot de passe:");
            
            // Get the password
            java.util.Optional<String> result = passwordDialog.showAndWait();
            if (result.isPresent() && !result.get().isEmpty()) {
                String password = result.get();
                
                try {
                    // Apply the decryption filter (we need to use decryptImage instead of encryptImage)
                    Image decrypted = decryptImage(imageView.getImage(), password);
                    imageView.setImage(decrypted);
                    
                    // Save transformation metadata (without the password)
                    if (currentImageFile != null) {
                        String chemin = currentImageFile.getAbsolutePath();
                        MetaDonnees meta = MetaDonneesService.trouver(chemin);
                        
                        if (meta == null) {
                            meta = new MetaDonnees(chemin);
                        }
                        meta.ajouterTransformation(new Transformation("dechiffrement", "appliqué"));
                        MetaDonneesService.ajouter(meta);
                    }
                    
                    // Show confirmation
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Image déchiffrée");
                    alert.setHeaderText(null);
                    alert.setContentText("L'image a été déchiffrée avec succès.");
                    alert.showAndWait();
                    
                } catch (Exception e) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Erreur lors du déchiffrement de l'image: " + e.getMessage());
                    alert.showAndWait();
                }
            }
        }
    }
    
    private Image encryptImage(Image source, String password) {
        try {
            // Convert the password to a seed using SHA-256
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            
            // Create a secure random generator with the password hash as seed
            java.security.SecureRandom random = new java.security.SecureRandom(hashBytes);
            
            // Get the image data
            int width = (int) source.getWidth();
            int height = (int) source.getHeight();
            
            // Create a writable image to store the result
            javafx.scene.image.WritableImage result = new javafx.scene.image.WritableImage(width, height);
            javafx.scene.image.PixelReader reader = source.getPixelReader();
            javafx.scene.image.PixelWriter writer = result.getPixelWriter();
            
            // Create arrays to store the pixel mapping
            int[] pixelOrder = new int[width * height];
            for (int i = 0; i < pixelOrder.length; i++) {
                pixelOrder[i] = i;
            }
            
            // Shuffle the pixel order using the secure random generator
            for (int i = pixelOrder.length - 1; i > 0; i--) {
                int index = random.nextInt(i + 1);
                // Swap
                int temp = pixelOrder[index];
                pixelOrder[index] = pixelOrder[i];
                pixelOrder[i] = temp;
            }
            
            // Apply the pixel mapping to create the encrypted image
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int sourceIndex = y * width + x;
                    int targetIndex = pixelOrder[sourceIndex];
                    
                    int targetX = targetIndex % width;
                    int targetY = targetIndex / width;
                    
                    javafx.scene.paint.Color color = reader.getColor(x, y);
                    writer.setColor(targetX, targetY, color);
                }
            }
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chiffrement: " + e.getMessage());
        }
    }
    
    // Add the decryption method here
    private Image decryptImage(Image source, String password) {
        try {
            // Convert the password to a seed using SHA-256
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            
            // Create a secure random generator with the password hash as seed
            java.security.SecureRandom random = new java.security.SecureRandom(hashBytes);
            
            // Get the image data
            int width = (int) source.getWidth();
            int height = (int) source.getHeight();
            
            // Create a writable image to store the result
            javafx.scene.image.WritableImage result = new javafx.scene.image.WritableImage(width, height);
            javafx.scene.image.PixelReader reader = source.getPixelReader();
            javafx.scene.image.PixelWriter writer = result.getPixelWriter();
            
            // Create arrays to store the pixel mapping
            int[] pixelOrder = new int[width * height];
            for (int i = 0; i < pixelOrder.length; i++) {
                pixelOrder[i] = i;
            }
            
            // Shuffle the pixel order using the secure random generator
            for (int i = pixelOrder.length - 1; i > 0; i--) {
                int index = random.nextInt(i + 1);
                // Swap
                int temp = pixelOrder[index];
                pixelOrder[index] = pixelOrder[i];
                pixelOrder[i] = temp;
            }
            
            // Create the reverse mapping for decryption
            int[] reverseOrder = new int[pixelOrder.length];
            for (int i = 0; i < pixelOrder.length; i++) {
                reverseOrder[pixelOrder[i]] = i;
            }
            
            // Apply the reverse pixel mapping to create the decrypted image
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int sourceIndex = y * width + x;
                    int targetIndex = reverseOrder[sourceIndex];
                    
                    int targetX = targetIndex % width;
                    int targetY = targetIndex / width;
                    
                    javafx.scene.paint.Color color = reader.getColor(x, y);
                    writer.setColor(targetX, targetY, color);
                }
            }
            
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du déchiffrement: " + e.getMessage());
        }
    }
    
    @FXML
    protected void handleVerticalSymmetry() {
        if (imageView.getImage() != null) {
            Image image = imageView.getImage();
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            
            javafx.scene.image.WritableImage result = new javafx.scene.image.WritableImage(width, height);
            javafx.scene.image.PixelReader reader = image.getPixelReader();
            javafx.scene.image.PixelWriter writer = result.getPixelWriter();
            
            // Apply vertical symmetry (flip horizontally)
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    javafx.scene.paint.Color color = reader.getColor(x, y);
                    writer.setColor(width - 1 - x, y, color);
                }
            }
            
            imageView.setImage(result);
            
            // Save transformation metadata
            if (currentImageFile != null) {
                String chemin = currentImageFile.getAbsolutePath();
                MetaDonnees meta = MetaDonneesService.trouver(chemin);
                
                if (meta == null) {
                    meta = new MetaDonnees(chemin);
                }
                meta.ajouterTransformation(new Transformation("symétrie", "verticale"));
                MetaDonneesService.ajouter(meta);
            }
        }
    }
    
    @FXML
    protected void handleHorizontalSymmetry() {
        if (imageView.getImage() != null) {
            Image image = imageView.getImage();
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            
            javafx.scene.image.WritableImage result = new javafx.scene.image.WritableImage(width, height);
            javafx.scene.image.PixelReader reader = image.getPixelReader();
            javafx.scene.image.PixelWriter writer = result.getPixelWriter();
            
            // Apply horizontal symmetry (flip vertically)
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    javafx.scene.paint.Color color = reader.getColor(x, y);
                    writer.setColor(x, height - 1 - y, color);
                }
            }
            
            imageView.setImage(result);
            
            // Save transformation metadata
            if (currentImageFile != null) {
                String chemin = currentImageFile.getAbsolutePath();
                MetaDonnees meta = MetaDonneesService.trouver(chemin);
                
                if (meta == null) {
                    meta = new MetaDonnees(chemin);
                }
                meta.ajouterTransformation(new Transformation("symétrie", "horizontale"));
                MetaDonneesService.ajouter(meta);
            }
        }
    }
}
