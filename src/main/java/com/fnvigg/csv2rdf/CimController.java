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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ResourceBundle;

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
    public Button btnCargarEsquema;
    public ImageView imageEsquema;
    public ImageView imageHelp;
    public ListView listviewRequerimientos;
    public TextField inputFieldRequerimientos;
    public Button btnEliminarRequerimiento;
    public Button btnAñadirRequerimiento;
    public Button btnSiguienteFase;
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

        //--------------- Panel 1 --------------------------------
        archivosIndicados = new LinkedList<String>();
        nombresArchivos = new LinkedList<String>();

        //Actualizacion listview e inicializacion de linkedlists
        nombreProyecto = AtributosSesion.getNombreProyecto();
        actualizarListview(nombreProyecto);

        //--------------- Panel 2 --------------------------------
        //Obtenemos el nombre de proyecto
        String proyecto = AtributosSesion.getNombreProyecto();
        //Creamos el fichero de destino
        String ruta = System.getProperty("user.dir");
        File f = new File(ruta + "src/main/resources/Proyectos/" + proyecto + "/esquema.png");
        if(f.exists() && !f.isDirectory()) {
            // do something
            Image img = new Image(f.getAbsolutePath());
            imageEsquema.setImage(img);
        }

        //--------------- Panel 3 --------------------------------
        ArrayList lista = new ArrayList<>();
        try {
            lista = proyectos.obtenerRequerimientos(proyecto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ObservableList listaRequerimientos = FXCollections.observableArrayList(lista);
        listviewRequerimientos.setItems(listaRequerimientos);
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
    private boolean mostrarAlertaTipoImagen(ActionEvent event){
        Alert alertaNombre = new Alert(Alert.AlertType.INFORMATION);
        alertaNombre.setTitle("Atencion");
        alertaNombre.setContentText("El fichero indicado no es formato png. Vuelva a indicar un archivo válido");
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

    @FXML
    private boolean mostrarAyuda(MouseEvent event){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Requerimientos de datos");
        String contenido = "Una serie de declaraciones tipo, en un conjunto de datos relativos al turismo en españa, serian las siguientes: \n"+
                            "R1: Los visitantes en España son aquellos que cruzan la frontera hacia el país. \n"+
                            "R2: El numero de visitantes se agrupa por mes.\n"+
                            "R3: El motivo del viaje puede ser: ocio, trabajo, u otras razones.\n"+
                            "R4: La región de destino se considera la region principal del viaje.\n"+
                            "R5: Los visitantes pueden acceder por aire, carretera, puerto, o tren.";
        alerta.setContentText(contenido);
        Optional<ButtonType> resultado = alerta.showAndWait();
        return false;
    }

    @FXML
    private boolean mostrarAlertaRequerimiento(Event event){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Requerimientos de datos");
        String contenido = "El campo está vacio.";
        alerta.setContentText(contenido);
        Optional<ButtonType> resultado = alerta.showAndWait();
        return false;
    }

    @FXML
    private boolean mostrarAlertaRequerimientoLista(Event event){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Requerimientos de datos");
        String contenido = "El elemento seleccionado no es válido.";
        alerta.setContentText(contenido);
        Optional<ButtonType> resultado = alerta.showAndWait();
        return false;
    }
    /*------------------------------------------------------------
    |                   LISTVIEW ARCHIVOS                        |
    ------------------------------------------------------------*/

    private void actualizarListview(String proyecto) {
        //Rellenar el listview de archivos en la init por si ya hubiera de antes
        String ruta = System.getProperty("user.dir");
        File ficheroDestino = new File( ruta + "/src/main/resources/Proyectos/" + proyecto + "/");

        //Filtro para no obtener los archivos de configuracion
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File f, String name)
            {
                return (!name.contentEquals("config.txt") && !name.contentEquals("esquema.png") && !name.contentEquals("requerimientos.txt")
                 && !name.contentEquals("MDO.png") && !name.contentEquals("esquemaOnt.png"));
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





    /*------------------------------------------------------------
    |                   PESTAÑA ESQUEMA GRAFICO                  |
    ------------------------------------------------------------*/

    public void btnCargarEsquemaAction(ActionEvent event) {
        //Funcion para abrir el explorador de archivos
        File ficheroSeleccionado = fileChooser.showOpenDialog(stage);
        //Obtenemos el nombre de proyecto
        String proyecto = AtributosSesion.getNombreProyecto();
        //Creamos el fichero de destino
        String ruta = System.getProperty("user.dir");
        File ficheroDestino = new File( "src/main/resources/Proyectos/" + proyecto + "/esquema.png");

        if(!ficheroSeleccionado.getName().endsWith(".png")){
            //Mostrar warning
            mostrarAlertaTipoImagen(event);
        }else {
            //Crear las copias de archivos para no trabajar sobre los originales
            FileChannel sourceChannel = null;
            FileChannel destChannel = null;
            try {

                sourceChannel = new FileInputStream(ficheroSeleccionado).getChannel();
                destChannel = new FileOutputStream(ficheroDestino).getChannel();
                destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

                Image img = new Image(ficheroDestino.getAbsolutePath());
                imageEsquema.setImage(img);

                sourceChannel.close();
                destChannel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*------------------------------------------------------------
    |                   PESTAÑA REQUERIMIENTOS                   |
    ------------------------------------------------------------*/

    //Accion que se realiza al clickear en la imagen
    public void imageHelpAction(MouseEvent mouseEvent) {
        mostrarAyuda(mouseEvent);
    }

    public void btnEliminarRequerimientoAction(ActionEvent event) {
        int indice = listviewRequerimientos.getSelectionModel().getSelectedIndex();

        if(indice == -1){
            //Mostrar alerta (La lista esta vacia)
            mostrarAlertaRequerimientoLista(event);
        }else{
            //Ya que la observableList no se puede modificar, hay que convertirla a arraylist, modificar, y luego revertir el tipo
            ArrayList<String> listLL = new ArrayList<>(listviewRequerimientos.getItems());
            listLL.remove(indice);
            ObservableList lista = FXCollections.observableArrayList(listLL);
            listviewRequerimientos.setItems(lista);

            //Borrar la linea igual al indice seleccionado
            String proyecto = AtributosSesion.getNombreProyecto();
            try {
                proyectos.borrarRequerimiento(indice, proyecto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void btnAñadirRequerimientoAction(ActionEvent event) {
        String requerimiento = inputFieldRequerimientos.getText();
        String proyecto = AtributosSesion.getNombreProyecto();
        if(requerimiento.isEmpty()){
            //mostrar alerta
            mostrarAlertaRequerimiento(event);
        }else{
            //Obtener la lista, añadirle el string, y actualizarla.
            ObservableList lista = listviewRequerimientos.getItems();
            lista.add(requerimiento);
            listviewRequerimientos.setItems(lista);
            inputFieldRequerimientos.setText("");

            try {
                //Actualizamos el archivo de requerimientos
                proyectos.addRequerimiento(requerimiento, proyecto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void btnSiguienteFaseAction(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("fasePim.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
