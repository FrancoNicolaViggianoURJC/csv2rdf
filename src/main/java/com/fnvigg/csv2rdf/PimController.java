package com.fnvigg.csv2rdf;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PimController implements Initializable {

    public Button btnVolver;
    public Accordion accordion;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private Gestor_proyectos proyectos = new Gestor_proyectos();
    private String nombreProyecto;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nombreProyecto = AtributosSesion.getNombreProyecto();
        accordion.setExpandedPane(accordion.getPanes().get(0));
        //Hay que escribir en el archivo de config la fase en la que estamos
        String faseUltima = proyectos.obtenerFase(nombreProyecto);
        if(!faseUltima.equals("PSM") && !faseUltima.equals("DSL")){
            proyectos.setFase(nombreProyecto, "PIM");
        }

    }

    public void btnVolverAction(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("faseCim.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
