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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ResourceBundle;

public class PsmController implements Initializable {
    public ImageView imageEsquemaEjemplo;
    public Accordion accordion;
    public ImageView imageAyudasOnt;
    public Label labelAyudaOnt;
    public ImageView imageEsquema;
    public ListView listviewClases;
    public ListView listviewAtributos;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private String nombreProyecto = AtributosSesion.getNombreProyecto();
    private Gestor_proyectos proyectos = new Gestor_proyectos();
    private ArrayList<String> clasesUML = new ArrayList<>();
    private ArrayList<String> atributosUML = new ArrayList<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nombreProyecto = AtributosSesion.getNombreProyecto();
        accordion.setExpandedPane(accordion.getPanes().get(0));
        
        //Actualizar la fase en el setting
        String faseUltima = proyectos.obtenerFase(nombreProyecto);
        if(!faseUltima.equals("DSL")){
            proyectos.setFase(nombreProyecto, "PSM");
        }

        //Carga de la imagen por default
        String ruta = System.getProperty("user.dir") + "/src/main/resources/esquemaOntDef.png";
        File f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            Image img = new Image(ruta);
            imageEsquemaEjemplo.setImage(img);
        }

        //Cargar imagen esquema si la hubiera
        ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/esquemaOnt.png";
        f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            Image img = new Image(ruta);
            imageEsquema.setImage(img);
        }

        //Cargar listview de clases
        ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/clasesUML.txt";
        f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            clasesUML = proyectos.obtenerClases(f);
            ObservableList oll = FXCollections.observableArrayList(clasesUML);
            listviewClases.setItems(oll);
        }
    }

    @FXML
    private boolean mostrarAlertaImagen(Event event){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Requerimientos de datos");
        String contenido = "La imagen debe ser PNG.";
        alerta.setContentText(contenido);
        Optional<ButtonType> resultado = alerta.showAndWait();
        return false;
    }
    @FXML
    private boolean mostrarAlertaBlank(Event event){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Esquema ontológico");
        String contenido = "El campo no debe estar en blanco.";
        alerta.setContentText(contenido);
        Optional<ButtonType> resultado = alerta.showAndWait();
        return false;
    }
    @FXML
    private boolean mostrarAlertaDuplicado(Event event){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Esquema ontológico");
        String contenido = "La clase no debe ser idéntica a otra.";
        alerta.setContentText(contenido);
        Optional<ButtonType> resultado = alerta.showAndWait();
        return false;
    }

    @FXML
    private boolean mostrarAlertaSeleccion(Event event){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Esquema ontológico");
        String contenido = "Debe seleccionar una clase a borrar.";
        alerta.setContentText(contenido);
        Optional<ButtonType> resultado = alerta.showAndWait();
        return false;
    }
    //Pestaña introduccion
    public void btnFaseAnteriorAction(ActionEvent event) {
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

    //Pestaña set de reglas
    public void btnClasesOnt(ActionEvent event) {
        //Cargamos el texto y la imagen correspondiente
        String frase = "Una clase UML se transformará en una clase OWL.";
        labelAyudaOnt.setText(frase);
        String ruta = System.getProperty("user.dir") + "/src/main/resources/clasesOnt.png";

        //Comprobamos la existencia de la imagen
        File f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            Image img = new Image(ruta);
            imageAyudasOnt.setImage(img);
        }

    }

    public void btnAtributosAction(ActionEvent event) {
        //Cargamos el texto y la imagen correspondiente
        String frase = "Un atributo UML definido por un tipo básico de datos (string, char, integer, float, boolean or date) será\n" +
                "transformado en un OWL Datatype Property cuyo dominio será la correspondiente clase OWL y cuyo\n" +
                "rango será la clase RDFS Datatype asociada con el tipo de datos básico.";
        labelAyudaOnt.setText(frase);
        String ruta = System.getProperty("user.dir") + "/src/main/resources/atributosOnt.png";

        //Comprobamos la existencia de la imagen
        File f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            Image img = new Image(ruta);
            imageAyudasOnt.setImage(img);
        }else{
            System.out.println("");
        }
    }

    public void btnAtributosPropiosAction(ActionEvent event) {
        //Cargamos el texto y la imagen correspondiente
        String frase = "Un atributo UML definido por un tipo correspondiente a una clase será transformado a una OWL Object\n" +
                "Property cuyo dominio será la correspondiente clase OWL y cuyo rango será la clase OWL a la que se\n" +
                "refiere.";
        labelAyudaOnt.setText(frase);
        String ruta = System.getProperty("user.dir") + "/src/main/resources/AtributosPropiosOnt.png";

        //Comprobamos la existencia de la imagen
        File f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            Image img = new Image(ruta);
            imageAyudasOnt.setImage(img);
        }
    }

    public void btnEnumeradosAction(ActionEvent event) {
        //Cargamos el texto y la imagen correspondiente
        String frase = "Un atributo UML definido por un tipo enumerado sera transformado en una OWL Object Property cuyo\n" +
                "dominio será la correspondiente clase OWL y cuyo rango será la clase RDF Alt.";
        labelAyudaOnt.setText(frase);
        String ruta = System.getProperty("user.dir") + "/src/main/resources/enumeradosOnt.png";

        //Comprobamos la existencia de la imagen
        File f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            Image img = new Image(ruta);
            imageAyudasOnt.setImage(img);
        }
    }

    public void btnColeccionesAction(ActionEvent event) {
        //Cargamos el texto y la imagen correspondiente
        String frase = "Un atributo UML definido por un tipo colección sera transformado en una OWL Object Property cuyo\n" +
                "dominio será la correspondiente clase OWL y cuyo rango será la clase RDF Bag.";
        labelAyudaOnt.setText(frase);
        String ruta = System.getProperty("user.dir") + "/src/main/resources/coleccionesOnt.png";

        //Comprobamos la existencia de la imagen
        File f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            Image img = new Image(ruta);
            imageAyudasOnt.setImage(img);
        }
    }

    //Pestaña cargar esquema
    public void btnCargarEsquemaAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File ficheroSeleccionado = fc.showOpenDialog(stage);

        if(ficheroSeleccionado.exists() && !ficheroSeleccionado.isDirectory()) {
            if (ficheroSeleccionado.getName().endsWith(".png")) {
                proyectos.guardarEsquemaOnt(ficheroSeleccionado, nombreProyecto);

                String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/esquemaOnt.png";
                Image img = new Image(ruta);

                imageEsquema.setImage(img);
            } else {
                //Mostrar alerta tipo
                mostrarAlertaImagen(event);
            }
        }
    }

    //Pestaña introducir datos

    //Actualizar el listview izquierdo a partir de la clase derecha
    public void listviewClasesAction(MouseEvent mouseEvent) {
        int index = listviewClases.getSelectionModel().getSelectedIndex();
        if(index != -1){
            String clase = (String)listviewClases.getSelectionModel().getSelectedItem();
            if(!clase.isBlank()){
                //Iterar el archivo de atributos buscando los que tenga esta clase
                try {
                    LinkedList<String> atributos = proyectos.obtenerAtributos(clase, nombreProyecto);
                    ObservableList oll = FXCollections.observableArrayList(atributos);
                    listviewAtributos.setItems(oll);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void añadirTipoBasico(ActionEvent event) {
    }

    public void añadirTipoClase(ActionEvent event) {
    }

    public void añadirEnumerado(ActionEvent event) {
    }

    public void añadirColeccion(ActionEvent event) {
    }

    public void añadirColeccionOrd(ActionEvent event) {
    }

    public void btnEliminarAtributo(ActionEvent event) {
        String clase = (String)listviewClases.getSelectionModel().getSelectedItem();
        int index = listviewAtributos.getSelectionModel().getSelectedIndex();

        //Una vez obtenido el indice, hay que obtener la lista de atributos de cada clase
        LinkedList<String> atributos = new LinkedList<>();
        try {
            atributos = proyectos.obtenerAtributos(clase, nombreProyecto);
            atributos.remove(index);
            proyectos.guardarAtributos(clase, nombreProyecto, atributos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnEliminarClaseAction(ActionEvent event) {
        int index = listviewClases.getSelectionModel().getSelectedIndex();
        if(index == -1){
            //Mostrar alerta seleccion
            mostrarAlertaSeleccion(event);
        }else{
            //Eliminamos el objeto de la lista
            clasesUML.remove(index);

            //Eliminamos el objeto de la listview
            ArrayList<String> listLL = new ArrayList<>(listviewClases.getItems());
            listLL.remove(index);
            ObservableList obsList = FXCollections.observableArrayList(listLL);
            listviewClases.setItems(obsList);

            //Actualizamos la lista de clases
            try {
                proyectos.guardarClases(clasesUML, nombreProyecto);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Limpiamos el listview de atributos, al no haber ninguna clase seleccionada
            ArrayList<String> blankAL = new ArrayList<>();
            ObservableList blank = FXCollections.observableArrayList(blankAL);
            listviewAtributos.setItems(blank);
        }
    }

    public void btnAñadirClaseAction(ActionEvent event) {
        TextInputDialog td = new TextInputDialog();
        Optional<String> res = td.showAndWait();

        //Comprobacion cancelado
        if (!res.isPresent()) {
            //mostrar alerta
            mostrarAlertaBlank(event);
        } else {
            //Comprobacion campo en blanco
            String nombre_clase = res.get();
            if(nombre_clase.isBlank()){
                mostrarAlertaBlank(event);
            }else{
                //Comprobacion campo duplicado
                boolean duplicado = comprobarDuplicidad(nombre_clase);
                if(duplicado){
                    mostrarAlertaDuplicado(event);
                }else{
                    //Todo correcto

                    //Hay que mantener una lista con los nombres de las clases, y que se mantenga coherente.
                    clasesUML.add(nombre_clase);
                    ObservableList list = FXCollections.observableArrayList(clasesUML);
                    listviewClases.setItems(list);

                    //Guardar la clase en el archivo
                    try {
                        proyectos.guardarClases(clasesUML, nombreProyecto);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private boolean comprobarDuplicidad(String nombreClase) {
        boolean res = false;
        for(String clase : clasesUML){
            if(clase.equals(nombreClase)){
                res=true;
                break;
            }
        }
        return res;
    }
}
