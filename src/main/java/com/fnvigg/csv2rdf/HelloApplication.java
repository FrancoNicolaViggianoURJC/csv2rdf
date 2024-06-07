package com.fnvigg.csv2rdf;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //Creacion de la base de datos
        try {
            DatabaseH2 bbdd = new DatabaseH2();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //Crear el gestor de escenas, e indicarle la vista que queremos cargar
        System.out.println(getClass());
        System.out.println(this.getClass().getResource("/views/hello-view.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/views/hello-view.fxml"));
        //Dimensiones de la ventana
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setResizable(false);
        String url = HelloApplication.class.getResource("/rdfGraph.png").getPath();
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

