package com.fnvigg.csv2rdf;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setResizable(false);
        stage.getIcons().add(new Image("/rdf graph.png"));
        stage.setTitle("CSV@RDF Helptool");
        stage.setScene(scene);
        stage.show();
        stage.sizeToScene();
    }

    public static void mostrarRegistro() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(HelloApplication.class.getResource("registrarUsuario.fxml"));

    }
    public static void main(String[] args) {
        launch();
    }
}

