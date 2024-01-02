package com.fnvigg.csv2rdf;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProyectosController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    private Gestor_proyectos proyectos = new Gestor_proyectos();
    public TextField nameProyectFld;
    public Label nameLbl;
    public TextField ontologicProyectFld;
    public Label ontologicalLbl;
    public Label dataLbl;
    public TextField dataProyectFld;
    public Button registerNewBtn;
    public Button deleteBtn;
    public Button loadBtn;
    public ListView listProyects;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        //Llamar al gestor archivos para obtener la lista de proyectos
        try {
            List<String> listaProyectos = proyectos.getProyectos();
            ObservableList<String> listaObservable = FXCollections.observableArrayList();
            listaObservable.addAll(listaProyectos);
            //Popular el listview
            listProyects.setItems(listaObservable);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    
}
