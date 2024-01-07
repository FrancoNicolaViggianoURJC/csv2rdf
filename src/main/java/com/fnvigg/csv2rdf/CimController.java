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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

public class CimController implements Initializable {

    public TitledPane titledPanel;
    public Accordion accordion;
    public Button btnIndicarArchivos;
    public ListView listViewCampos;
    public ListView listViewArchivos;
    public Button btnRestablecerCampo;
    public Button btnQuitarCampo;
    public TitledPane panel1;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private Gestor_proyectos proyectos = new Gestor_proyectos();
    //Paths absolutos a los archivos
    private LinkedList<String> archivosIndicados;
    //Nombres archivos
    private LinkedList<String> nombresArchivos;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        accordion.setExpandedPane(panel1);
        archivosIndicados = new LinkedList<String>();
        nombresArchivos = new LinkedList<String>();
        //accordion.getPanes().add()
    }

    @FXML
    private FileChooser fileChooser = new FileChooser();

    @FXML
    private boolean mostrarAlertaTipoFichero(ActionEvent event){
        Alert alertaNombre = new Alert(Alert.AlertType.INFORMATION);
        alertaNombre.setTitle("Atencion");
        alertaNombre.setContentText("El fichero indicado no es formato csv. Vuelva a indicar un archivo válido");
        Optional<ButtonType> resultado = alertaNombre.showAndWait();
        return false;
    }

    public void indicarArchivos(ActionEvent event) {
        //Funcion para abrir el explorador de archivos
        File ficheroSeleccionado = fileChooser.showOpenDialog(stage);
        //Obtenemos el nombre de proyecto
        String proyecto = AtributosSesion.getNombreProyecto();
        //Creamos el fichero de destino
        String ruta = System.getProperty("user.dir");
        File ficheroDestino = new File( "src/main/resources/Proyectos/" + proyecto + "/" + ficheroSeleccionado.getName());

        if(!ficheroSeleccionado.getName().endsWith(".csv")){
            //Mostrar warning
            mostrarAlertaTipoFichero(event);
        }else {
            //Actualizar las listas
            archivosIndicados.add(ficheroDestino.getAbsolutePath());
            nombresArchivos.add(ficheroDestino.getName());

            ObservableList<String> listaObservable = FXCollections.observableArrayList();
            listaObservable.addAll(nombresArchivos);
            //Popular el listview
            listViewArchivos.setItems(listaObservable);

            //Crear las copias de archivos para no trabajar sobre los originales
            FileChannel sourceChannel = null;
            FileChannel destChannel = null;
            try {
                sourceChannel = new FileInputStream(ficheroSeleccionado).getChannel();
                destChannel = new FileOutputStream(ficheroDestino).getChannel();
                destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                sourceChannel.close();
                destChannel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void campoIndicado(MouseEvent mouseEvent) {
    }

    public void archivoIndicado(MouseEvent mouseEvent) {
        //Las rutas de los archivos vienen indicadas en el linkedlist de la clase
        //Los indices deberian coincidir en todo momento, comprobar esto
        int indice = listViewArchivos.getSelectionModel().getSelectedIndex();
        String path = archivosIndicados.get(indice);
        try {
            Reader input = new FileReader(path);
            Iterable<CSVRecord> campos = CSVFormat.EXCEL.parse(input);
            for(CSVRecord campo : campos){
                String[] columnas = campo.values();
                actualizarListviewCampos(columnas);
                break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void actualizarListviewCampos(String[] campos) {
        //Actualiza el list view derecho
        ObservableList<String> listaObservable = FXCollections.observableArrayList();
        listaObservable.addAll(campos);
        //Añadimos la lista observable al listview
        listViewCampos.setItems(listaObservable);
    }

    public void btnReestablecerCampoAction(ActionEvent event) {
    }

    public void btnQuitarCampoAction(ActionEvent event) {
        //Coger indice de la listview
        //Quitar header del archivo y sus respectivos valores

    }
}
