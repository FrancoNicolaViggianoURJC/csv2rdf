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
    public Button btnQuitarArchivo;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private Gestor_proyectos proyectos = new Gestor_proyectos();
    //Paths absolutos a los archivos
    private LinkedList<String> archivosIndicados;
    //Nombres archivos
    private LinkedList<String> nombresArchivos;
    private String nombreProyecto;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        accordion.setExpandedPane(panel1);
        archivosIndicados = new LinkedList<String>();
        nombresArchivos = new LinkedList<String>();

        //Actualizarcion listview e inicializacion de linkedlists
        nombreProyecto = AtributosSesion.getNombreProyecto();
        actualizarListview(nombreProyecto);

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

    @FXML
    private boolean mostrarConfirmacion(ActionEvent event){
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmacion de acción");
        alerta.setContentText("¿Estas seguro de querer borrar el archivo?");
        Optional<ButtonType> resultado = alerta.showAndWait();

        if(resultado.isEmpty()){
            System.out.println("Cerrada");
            return false;
        }else if(resultado.get() == ButtonType.OK){
            System.out.println("Borrar archivo");
            return true;
        }else if(resultado.get() == ButtonType.CANCEL){
            System.out.println("Cancelado");
            return false;
        }
        return false;
    }

    /*------------------------------------------------------------
    |                   LISTVIEW ARCHIVOS                        |
    ------------------------------------------------------------*/

    private void actualizarListview(String proyecto) {
        //Rellenar el listview de archivos en la init por si ya hubiera de antes
        String ruta = System.getProperty("user.dir");
        File ficheroDestino = new File( ruta + "/src/main/resources/Proyectos/" + proyecto + "/");

        //Filtro para no obtener el archivo de configuracion
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File f, String name)
            {
                return !name.contentEquals("config.txt");
            }
        };

        File[] ficheros = ficheroDestino.listFiles(filter);

        //Inicializacion linkedlists
        for(File f : ficheros){
            archivosIndicados.add(f.getAbsolutePath());
            nombresArchivos.add(f.getName());
        }

        //Popular lista
        ObservableList<String> listaObservable = FXCollections.observableArrayList();
        listaObservable.addAll(nombresArchivos);
        listViewArchivos.setItems(listaObservable);

    }

    public void btnIndicarArchivosAction(ActionEvent event) {
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

    public void archivoIndicado(MouseEvent mouseEvent) {
        //Las rutas de los archivos vienen indicadas en el linkedlist de la clase
        //Los indices deberian coincidir en todo momento, comprobar esto
        int indice = listViewArchivos.getSelectionModel().getSelectedIndex();
        String path = archivosIndicados.get(indice);
        String[] campos = proyectos.obtenerCampos(path);
        actualizarListviewCampos(campos);
    }

    public void btnQuitarArchivoAction(ActionEvent event) {
        int indice = listViewArchivos.getSelectionModel().getSelectedIndex();
        String path = archivosIndicados.get(indice);
        File archivo = new File(path);
        boolean accion = mostrarConfirmacion(event);
        if(accion){
            boolean resultado = archivo.delete();
            if(resultado){
                System.out.println("Archivo eliminado");
                //Actualizacion indices
                archivosIndicados.remove(indice);
                nombresArchivos.remove(indice);

                //Actualizacion indice archivos
                ObservableList<String> listaObservable = FXCollections.observableArrayList();
                listaObservable.addAll(nombresArchivos);
                //Popular el listview
                listViewArchivos.setItems(listaObservable);

                //Actualizacion indice campos
                listViewCampos.setItems(FXCollections.observableArrayList());
            }else{
                System.out.println("Error al eliminar el archivo");
            }
        }

    }

    /*------------------------------------------------------------
    |                   LISTVIEW CAMPOS                           |
     ------------------------------------------------------------*/
    private void actualizarListviewCampos(String[] campos) {
        //Actualiza el list view derecho
        ObservableList<String> listaObservable = FXCollections.observableArrayList();
        listaObservable.addAll(campos);
        //Añadimos la lista observable al listview
        listViewCampos.setItems(listaObservable);
    }
    public void btnQuitarCampoAction(ActionEvent event) {
        //Coger indice de la listview
        //Quitar header del archivo y sus respectivos valores
        //Al leerse las cabeceras en orden, el indice de la listview corresponde a la posicion en el csv
        int indiceCampo = listViewCampos.getSelectionModel().getSelectedIndex();
        int indiceArchivo = listViewArchivos.getSelectionModel().getSelectedIndex();

        String ruta = archivosIndicados.get(indiceArchivo);
        proyectos.removerCampo(ruta, indiceCampo);
        actualizarListviewCampos(proyectos.obtenerCampos(ruta));
    }

}
