package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.MetaDonnees;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MetaDonneesService {
    // Chemin de sauvegarde
    private static final String FILE_PATH = "metadonnees.json";
    private static final Gson gson = new Gson();

    // Méthode pour lire les fichiers et convertir le JSON en une liste d'objets MetaDonnees
    public static List<MetaDonnees> charge() {
        File file = new File(FILE_PATH);
        System.out.println("Loading metadata from: " + file.getAbsolutePath());

        // Si le fichier n'existe pas, créer un fichier vide
        if (!file.exists() || file.length() == 0) {
            System.out.println("Metadata file does not exist or is empty");
            try {
                file.getParentFile().mkdirs(); // Create parent directories if they don't exist
                file.createNewFile(); // Create the file if it doesn't exist
                sauvegarde(new ArrayList<>()); // Save an empty list to initialize the file
            } catch (IOException e) {
                System.err.println("Error creating metadata file: " + e.getMessage());
                e.printStackTrace();
            }
            return new ArrayList<>(); // Si le fichier est vide ou inexistant, retourner une liste vide
        }

        // Lire le fichier JSON et le convertir en liste d'objets
        try (FileReader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<MetaDonnees>>(){}.getType();
            List<MetaDonnees> result = gson.fromJson(reader, listType);
            System.out.println("Loaded " + (result != null ? result.size() : 0) + " metadata entries");
            return result != null ? result : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error loading metadata: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // En cas d'erreur, retourner une liste vide
        }
    }

    // Sauvegarde la liste de MetaDonnees dans le fichier JSON
    public static void sauvegarde(List<MetaDonnees> liste) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            System.out.println("Saving " + liste.size() + " metadata entries");
            gson.toJson(liste, writer);
            System.out.println("Metadata saved successfully");
        } catch (IOException e) {
            System.err.println("Error saving metadata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Ajoute une nouvelle MetaDonnees (en enlevant une image déjà présente)
    public static void ajouter(MetaDonnees nouvelle) {
        List<MetaDonnees> liste = charge();

        // Retirer l'ancienne entrée si le chemin est le même
        liste.removeIf(m -> m.getChemin().equals(nouvelle.getChemin()));

        // Ajouter la nouvelle entrée
        liste.add(nouvelle);

        // Sauvegarder les modifications
        sauvegarde(liste);
    }

    // Trouver une MetaDonnees à partir de son chemin
    public static MetaDonnees trouver(String chemin) {
        return charge().stream()
                .filter(m -> m.getChemin().equals(chemin))
                .findFirst()
                .orElse(null);
    }
}
