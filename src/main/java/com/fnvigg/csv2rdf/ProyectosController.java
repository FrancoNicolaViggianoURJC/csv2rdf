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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProyectosController implements Initializable {
    public Label lblDatos;
    public Label lblOntologico;
    private Stage stage;
    private Scene scene;
    private Parent root;
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

        //Llenar el listview de proyectos
        List<String> listaProyectos = DatabaseH2.getProyectos();
        ObservableList<String> oll = FXCollections.observableArrayList(listaProyectos);
        listProyects.setItems(oll);

        //Rellenar los campos para registrar un proyecto
        initCampos();
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
    private boolean mostrarAlertaArchivos(ActionEvent event){
        Alert alertaNombre = new Alert(Alert.AlertType.INFORMATION);
        alertaNombre.setTitle("Atencion");
        alertaNombre.setContentText("Ha ocurrido un error con el sistema de archivos");
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

    @FXML
    private boolean mostrarAlertaBorrado(ActionEvent event){
        Alert alertaNombre = new Alert(Alert.AlertType.INFORMATION);
        alertaNombre.setTitle("Atencion");
        alertaNombre.setContentText("No se eliminó correctamente el proyecto");
        Optional<ButtonType> resultado = alertaNombre.showAndWait();
        return false;
    }

    private void actualizarLista(){
            List<String> listaProyectos = DatabaseH2.getProyectos();
            ObservableList<String> oll = FXCollections.observableArrayList(listaProyectos);
            listProyects.setItems(oll);
    }

    public void onClickCargarProyecto(ActionEvent event) {

        String nombreProyecto = (String) listProyects.getSelectionModel().getSelectedItem();
        String fase = DatabaseH2.getProyectosFase(nombreProyecto);
        AtributosSesion.setIdProyecto(DatabaseH2.getProyectosID(nombreProyecto));
        AtributosSesion.setNombreProyecto(nombreProyecto);
        //Cargar la escena en funcion de la fase
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
            root = FXMLLoader.load(HelloApplication.class.getResource("/views/"+s));
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
            String nombreProyecto = (String) listProyects.getSelectionModel().getSelectedItem();
            String idProyecto = DatabaseH2.getProyectosID(nombreProyecto);
            Boolean exito = DatabaseH2.deleteProyecto(nombreProyecto);
            if(exito){
                //Borrar tambien el directorio del proyecto
                String path = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + idProyecto + "/";
                File directorio = new File(path);
                if(directorio.exists()){
                    borrarDirectorio(directorio);
                    actualizarLista();
                    lblDatos.setVisible(false);
                    lblOntologico.setVisible(false);
                }
                actualizarLista();

            }else{
                mostrarAlertaBorrado(event);
            }
        }
    }

    private boolean borrarDirectorio(File directorio) {
        File[] allContents = directorio.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                borrarDirectorio(file);
            }
        }
        return directorio.delete();
    }

    public void onClickRegistrarProyecto(ActionEvent event) {
        //Obtencion de los campos

        //Crear el directorio donde se guardarán los proyectos
        Boolean bool = false;

        File f = new File("./Proyectos");
        if(!f.exists()){
            bool = f.mkdir();
        }else{
            bool = true;
        }

        if(bool){
            String nombreProyecto = nameProyectFld.getText();
            String nombreOntologico = ontologicProyectFld.getText();
            String nombreDato = dataProyectFld.getText();
            //Comprobacion que ninguno esté vacio
            if(nombreProyecto.equals("") || nombreDato.equals("") || nombreOntologico.equals("")){
                mostrarAlertaCampos(event);
            }else{
                boolean insertado = DatabaseH2.insertProyecto(nombreDato, nombreOntologico, nombreProyecto);
                String idProyecto = DatabaseH2.getProyectosID(nombreProyecto);
                if(insertado){
                    //Se crea el proyecto
                    crearDirectorio(idProyecto);
                    actualizarLista();
                    initCampos();
                }else{
                    //No se crea el proyecto
                    mostrarAlertaNombre(event);
                }
            }
        }else{
            mostrarAlertaArchivos(event);
        }


    }

    private void crearDirectorio(String id) {
        File f = new File("./Proyectos/"+id);
        if (!f.exists()){
            f.mkdirs();
        }
    }

    private void initCampos() {
        String rol = AtributosSesion.getRol();
        String nombre = AtributosSesion.getUser();

        nameProyectFld.setText("");
        if(rol.equals("Ingeniero de datos")){
            dataProyectFld.setText(nombre);
            dataProyectFld.setDisable(true);
            ontologicProyectFld.setText("");
        }else{
            ontologicProyectFld.setText(nombre);
            ontologicProyectFld.setDisable(true);
            dataProyectFld.setText("");
        }
    }

    public void onClickDatosProyecto(MouseEvent mouseEvent) {
        //Recoger datos del proyecto
        String proyecto = (String) listProyects.getSelectionModel().getSelectedItem();
        ArrayList<String> datosProyecto = DatabaseH2.getProyectosDatos(proyecto);
        String ingDatos = datosProyecto.get(0);
        String ingOntologico = datosProyecto.get(1);

        if(AtributosSesion.getUser().equals(ingDatos) || AtributosSesion.getUser().equals(ingOntologico)){
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
