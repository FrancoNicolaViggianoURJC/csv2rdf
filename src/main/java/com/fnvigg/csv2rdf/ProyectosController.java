package com.fnvigg.csv2rdf;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProyectosController implements Initializable {
    public Label lblDatos;
    public Label lblOntologico;
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

    @FXML
    private boolean mostrarAlerta(ActionEvent event){
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmacion de acción");
        alerta.setContentText("¿Estas seguro de querer borrar el proyecto?");
        Optional<ButtonType> resultado = alerta.showAndWait();

        if(resultado.isEmpty()){
            System.out.println("Cerrada");
            return false;
        }else if(resultado.get() == ButtonType.OK){
            System.out.println("Borrar proyecto");
            return true;
        }else if(resultado.get() == ButtonType.CANCEL){
            System.out.println("Cancelado");
            return false;
        }
        return false;
    }

    private void actualizarLista(){
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
    public void onClickCargarProyecto(ActionEvent event) {

    }

    public void onClickBorrarProyecto(ActionEvent event) {
        if(mostrarAlerta(event)) {
            String proyecto = (String) listProyects.getSelectionModel().getSelectedItem();
            proyectos.borrarProyecto(proyecto);
            actualizarLista();
            lblDatos.setVisible(false);
            lblOntologico.setVisible(false);
        }else{

        }
    }

    public void onClickRegistrarProyecto(ActionEvent event) {

        String nombreProyecto = nameProyectFld.getText();
        String nombreOntologico = ontologicProyectFld.getText();
        String nombreDato = dataProyectFld.getText();
        boolean nombreDisponible = true;
        for (Object proyecto : listProyects.getItems()){
            String proyectoString = (String) proyecto;
            if(nombreProyecto.equals(proyectoString)){
                nombreDisponible = false;
                break;
            }
        }
        if(nombreDisponible) {
            proyectos.crearProyecto(nombreProyecto, nombreDato, nombreOntologico);
            actualizarLista();
            nameProyectFld.setText("");
            ontologicProyectFld.setText("");
            dataProyectFld.setText("");
        }else{
            //mostrar mensaje error...
        }
    }

    public void onClickDatosProyecto(MouseEvent mouseEvent) {
        //Recoger los nombres del ing datos, ontologico y cambiar las properties de las label de abajo
        String proyecto = (String) listProyects.getSelectionModel().getSelectedItem();
        File fichero = new File("src/main/resources/Proyectos/" + proyecto + "/config.txt");
        try {
            FileReader fr = new FileReader(fichero);
            BufferedReader br = new BufferedReader(fr);
            String linea = br.readLine();
            String ingDato, ingOnt;
            String[] tokens = linea.split(",");
            ingDato = tokens[0];
            ingOnt = tokens[1];

            lblDatos.setText(ingDato);
            lblOntologico.setText(ingOnt);
            lblDatos.setVisible(true);
            lblOntologico.setVisible(true);

            fr.close();
            br.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
