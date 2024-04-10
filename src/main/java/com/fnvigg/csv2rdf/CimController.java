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
import java.util.*;

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
    private String idProyecto;
    private String idArchivoSeleccionado;
    @Override
    public void initialize(URL location, ResourceBundle resources){
        accordion.setExpandedPane(panel1);

        idProyecto = AtributosSesion.getIdProyecto();
        //--------------- Panel 1 --------------------------------

        //Actualizacion listview
        actualizarListview();



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
        //Deshabilitar la opcion de pasar a la siguiente fase hasta que se seleccione un archivo

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

    private void actualizarListview() {

        List<String> listaFicheros = DatabaseH2.getCimNombresArchivos(idProyecto);
        ObservableList oll = FXCollections.observableArrayList(listaFicheros);
        listViewArchivos.setItems(oll);

        if(oll.isEmpty()) {
            btnSiguienteFase.setDisable(true);
        }else{
            btnSiguienteFase.setDisable(false);
        }
    }

    public void btnIndicarArchivosAction(ActionEvent event) {
        //Funcion para abrir el explorador de archivos
        File ficheroSeleccionado = fileChooser.showOpenDialog(stage);
        //Obtenemos el nombre de proyecto
        String proyecto = AtributosSesion.getNombreProyecto();
        //Creamos el fichero de destino
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + idProyecto + "/" + ficheroSeleccionado.getName();
        File ficheroDestino = new File(ruta);

        if(!ficheroSeleccionado.getName().endsWith(".csv")){
            //Mostrar warning
            mostrarAlertaTipoFichero(event);
        }else {
            actualizarListview();

            //Crear las copias de archivos para no trabajar sobre los originales
            crearCopias(ficheroSeleccionado, ficheroDestino);
            //Insercion de la ruta del archivo nuevo en la bbdd
            DatabaseH2.insertCimArchivo(ruta, ficheroDestino.getName(),idProyecto);
            String idArchivo = DatabaseH2.getCimIdArchivo(ruta);
            actualizarListview();

            //Insercion de los campos en la bbdd
            String[] camposArchivo = proyectos.obtenerCampos(ruta);
            DatabaseH2.insertCimAtributos(camposArchivo, idArchivo, idProyecto);

            //Habilitar la transicion a la siguiente fase
            btnSiguienteFase.setDisable(false);
        }
    }

    private void crearCopias(File ficheroSeleccionado, File ficheroDestino) {
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

    public void archivoIndicado(MouseEvent mouseEvent) {
        //Se ejecuta cada vez que se indica un archivo en la listview de archivos
        String nombreArchivo = (String) listViewArchivos.getSelectionModel().getSelectedItem();
        String ruta = DatabaseH2.getCimRutaIndividual(nombreArchivo);
        idArchivoSeleccionado = DatabaseH2.getCimIdArchivo(ruta);
        actualizarListviewCampos();
    }

    //Eliminar un archivo del proyecto
    public void btnQuitarArchivoAction(ActionEvent event) {
        //todo: obtener el nombre del archivo, su id, borrar tanto el archivo en disco como sus entrada en la bbdd, sus atributos tmb
        //Obtención del nombre del archivo
        int indice = listViewArchivos.getSelectionModel().getSelectedIndex();
        String path = archivosIndicados.get(indice);
        File archivo = new File(path);
        //Pedir confirmacion al usuario
        boolean accion = mostrarConfirmacion(event);
        if(accion){
            boolean resultado = archivo.delete();
            if(resultado){
                //Si se ha eliminado correctamente
                //System.out.println("Archivo eliminado");
                //Actualización indices donde se ubican los paths y los nombres
                archivosIndicados.remove(indice);
                nombresArchivos.remove(indice);

                //Actualizacion lista de archivos
                ObservableList<String> listaObservable = FXCollections.observableArrayList();
                listaObservable.addAll(nombresArchivos);
                listViewArchivos.setItems(listaObservable);

                //Actualizacion indice campos
                listViewCampos.setItems(FXCollections.observableArrayList());
            }else{
                //En caso de no poder borrar el archivo
                System.out.println("Error al eliminar el archivo");
            }
        }
        //Deshabilitar la siguiente fase si no quedan archivos indicados
        if(archivosIndicados.isEmpty()){
            btnSiguienteFase.setDisable(true);
        }
    }//Fin método

    /*------------------------------------------------------------
    |                   LISTVIEW CAMPOS                           |
     ------------------------------------------------------------*/
    private void actualizarListviewCampos() {
        List<String> campos = DatabaseH2.getCimAtributos(idArchivoSeleccionado);
        ObservableList oll = FXCollections.observableArrayList(campos);
        listViewCampos.setItems(oll);
    }
    public void btnQuitarCampoAction(ActionEvent event) {

        String nombreArchivo = (String) listViewArchivos.getSelectionModel().getSelectedItem();
        String rutaArchivo = DatabaseH2.getCimRutaIndividual(nombreArchivo);
        String idArchivo = DatabaseH2.getCimIdArchivo(rutaArchivo);
        String nombreAtributo = (String) listViewCampos.getSelectionModel().getSelectedItem();

        //Remover ese campo de la bbdd
        DatabaseH2.deleteCimAtributo(nombreAtributo, idArchivo);
        actualizarListviewCampos();

        //Remover ese campo en el archivo duplicado
        int indexAtributo = listViewCampos.getSelectionModel().getSelectedIndex();
        proyectos.removerCampo(rutaArchivo, indexAtributo);

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
        String ruta = System.getProperty("user.dir")+ "src/main/resources/Proyectos/" + proyecto + "/esquema.png";
        File ficheroDestino = new File( ruta );

        if(ficheroSeleccionado != null && !ficheroSeleccionado.getName().endsWith(".png")){
            //Mostrar warning
            mostrarAlertaTipoImagen(event);
        }else {
            //Crear las copias de archivos para no trabajar sobre los originales
            crearCopias(ficheroSeleccionado, ficheroDestino);
            Image img = new Image(ficheroDestino.getAbsolutePath());
            imageEsquema.setImage(img);

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
