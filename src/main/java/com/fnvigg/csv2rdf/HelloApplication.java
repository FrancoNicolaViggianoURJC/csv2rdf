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
        //Crear el gestor de escenas, e indicarle la vista que queremos cargar
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        //Dimensiones de la ventana
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setResizable(false);
        stage.getIcons().add(new Image("/rdf graph.png"));
        stage.setTitle("CSV@RDF Helptool");
        //Activar la escena
        stage.setScene(scene);
        stage.show();
        stage.sizeToScene();
    }
    //Launch
    public static void main(String[] args) {
        launch();
    }
}

