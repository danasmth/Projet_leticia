module com.example.projet {
    requires javafx.controls;
    requires javafx.fxml;
    //Autoriser a lire le module gson
    requires com.google.gson;

// permet a FXMLLoader d acceder au controleur
    opens controller to javafx.fxml;
    exports com.example.projet;
    opens model to com.google.gson;

}