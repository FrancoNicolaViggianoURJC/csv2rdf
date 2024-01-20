package com.fnvigg.csv2rdf;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

        //Llenar y bloquear el rol asociado
        String rol = AtributosSesion.getRol();
        String nombre = AtributosSesion.getUser();
        if(rol.equals("Ingeniero de datos")){
            dataProyectFld.setDisable(true);
            dataProyectFld.setText(nombre);

        } else if (rol.equals("Ingeniero Ontológico")) {
            ontologicProyectFld.setDisable(true);
            ontologicProyectFld.setText(nombre);
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

    @FXML
    private boolean mostrarAlertaNombre(ActionEvent event){
        Alert alertaNombre = new Alert(Alert.AlertType.INFORMATION);
        alertaNombre.setTitle("Atencion");
        alertaNombre.setContentText("Ya existe un proyecto con ese nombre");
        Optional<ButtonType> resultado = alertaNombre.showAndWait();
        return false;
    }

    @FXML
    private boolean mostrarAlertaCampos(ActionEvent event){
        Alert alertaNombre = new Alert(Alert.AlertType.INFORMATION);
        alertaNombre.setTitle("Atencion");
        alertaNombre.setContentText("Faltan datos por introducir");
        Optional<ButtonType> resultado = alertaNombre.showAndWait();
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
        String nombreProyecto = (String) listProyects.getSelectionModel().getSelectedItem();
        String fase = proyectos.obtenerFase(nombreProyecto);

        if(fase.equals("PIM")){
            cargarEscena("fasePim.fxml", event);
        }else if(fase.equals("PSM")){
            cargarEscena("fasePsm.fxml", event);
        }else if(fase.equals("DSL")){
            cargarEscena("faseDsl.fxml", event);
        }else{
            cargarEscena("faseCim.fxml", event);
        }

    }

    private void cargarEscena(String s, ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource(s));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onClickBorrarProyecto(ActionEvent event) {
        if(mostrarAlerta(event)) {
            String proyecto = (String) listProyects.getSelectionModel().getSelectedItem();
            proyectos.borrarProyecto(proyecto);
            actualizarLista();
            lblDatos.setVisible(false);
            lblOntologico.setVisible(false);
        }
    }

    public void onClickRegistrarProyecto(ActionEvent event) {
        //Obtencion de los campos
        String nombreProyecto = nameProyectFld.getText();
        String nombreOntologico = ontologicProyectFld.getText();
        String nombreDato = dataProyectFld.getText();
        //Comprobacion que ninguno esté vacio
        if(nombreProyecto.equals("") || nombreDato.equals("") || nombreOntologico.equals("")){
            mostrarAlertaCampos(event);
        }else{
            //Comprobacion nombre disponible
            boolean nombreDisponible = true;
            for (Object proyecto : listProyects.getItems()){
                String proyectoString = (String) proyecto;
                if(nombreProyecto.equals(proyectoString)){
                    nombreDisponible = false;
                    break;
                }
            }

            //Creacion del proyecto
            if(nombreDisponible) {
                proyectos.crearProyecto(nombreProyecto, nombreDato, nombreOntologico);
                actualizarLista();
                nameProyectFld.setText("");
                ontologicProyectFld.setText("");
                dataProyectFld.setText("");
            }else{
                //mostrar mensaje error...
                mostrarAlertaNombre(event);
            }
        }

    }

    public void onClickDatosProyecto(MouseEvent mouseEvent) {
        //Recoger los nombres del ing datos, ontologico y cambiar las properties de las label de abajo
        String proyecto = (String) listProyects.getSelectionModel().getSelectedItem();
        String[] ingenieros = proyectos.obtenerPropiedades(proyecto);
        String ingDatos = ingenieros[0];
        String ingOntologico = ingenieros[1];
        String userSesion = AtributosSesion.getUser();

        if(userSesion.equals(ingDatos) || userSesion.equals(ingOntologico)){
            //No coincide con ningun miembro del proyecto
            deleteBtn.setDisable(false);
            loadBtn.setDisable(false);
        }else{
            deleteBtn.setDisable(true);
            loadBtn.setDisable(true);
        }

        lblDatos.setText(ingDatos);
        lblOntologico.setText(ingOntologico);
        lblDatos.setVisible(true);
        lblOntologico.setVisible(true);
    }


}
