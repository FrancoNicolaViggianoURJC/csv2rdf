package com.fnvigg.csv2rdf;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.net.URL;
import java.util.*;

public class PsmController implements Initializable {
    public ImageView imageEsquemaEjemplo;
    public Accordion accordion;
    public ImageView imageAyudasOnt;
    public Label labelAyudaOnt;
    public ImageView imageEsquema;
    public ListView listviewClases;
    public ListView listviewAtributos;
    public TextField publisherInput;
    public TextArea descripcionSPInput;
    public TextArea descripcionENGInput;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private String nombreProyecto = AtributosSesion.getNombreProyecto();
    private Gestor_proyectos proyectos = new Gestor_proyectos();
    private List<String> choices = new ArrayList<>();

    private String publisher;
    private String descripcion;
    private String description;
    private String idProyecto;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idProyecto = AtributosSesion.getIdProyecto();
        nombreProyecto = AtributosSesion.getNombreProyecto();
        accordion.setExpandedPane(accordion.getPanes().get(0));
        
        //Actualizar la fase en el setting
        String fase = DatabaseH2.getProyectosFase(nombreProyecto);
        if(!fase.equals("DSL")){
            DatabaseH2.updateProyectosFase("PSM", idProyecto);
        }

        //Init de las imagenes
        cargarImagenes();

        //Cargar listview de clases
        cargarClases();

        //Init de la lista de tipos
        choices.add("xsd:integer");
        choices.add("xsd:string");
        choices.add("xsd:float");
        choices.add("xsd:dateTime");
        choices.add("xsd:boolean");
        choices.add("xsd:decimal");

        //Init inputs de metadatos
        try {
            cargarMetadatos();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*----------------------------------------------------------------------------
||                      Inyeccion FXML                                        ||
||                                                                            ||
----------------------------------------------------------------------------*/
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
    private boolean mostrarAlertaSeleccion(Event event){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Esquema ontológico");
        String contenido = "Debe seleccionar una clase a borrar.";
        alerta.setContentText(contenido);
        Optional<ButtonType> resultado = alerta.showAndWait();
        return false;
    }


    @FXML
    private boolean mostrarAlertaOntologia(Event event){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Atributos");
        String contenido = "Aun faltan atributos por especificar.";
        alerta.setContentText(contenido);
        Optional<ButtonType> resultado = alerta.showAndWait();
        return false;
    }
    /*----------------------------------------------------------------------------
||                      Pestaña introduccion                                 ||
||                                                                            ||
 ----------------------------------------------------------------------------*/
    private void cargarImagenes() {
        //Carga de la imagen por default
        String ruta = System.getProperty("user.dir") + "/src/main/resources/esquemaOntDef.png";
        File f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            Image img = new Image(ruta);
            imageEsquemaEjemplo.setImage(img);
        }

        //Cargar imagen esquema si la hubiera
        ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + idProyecto + "/esquemaOnt.png";
        f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            Image img = new Image(ruta);
            imageEsquema.setImage(img);
        }
    }
    public void btnFaseAnteriorAction(ActionEvent event) {
        try {

            root = FXMLLoader.load(HelloApplication.class.getResource("/views/fasePim.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /*----------------------------------------------------------------------------
||                      Pestaña set de reglas                                 ||
||                                                                            ||
 ----------------------------------------------------------------------------*/
    public void btnClasesOnt(ActionEvent event) {
        //Cargamos el texto y la imagen correspondiente
        String frase = "Una clase UML se transformará en una clase OWL.";
        labelAyudaOnt.setText(frase);

        Image img = new Image(HelloApplication.class.getResource("/clasesOnt.png").toString());
        imageAyudasOnt.setImage(img);


    }

    public void btnAtributosAction(ActionEvent event) {
        //Cargamos el texto y la imagen correspondiente
        String frase = "Un atributo UML definido por un tipo básico de datos (string, char, integer, float, boolean or date) será\n" +
                "transformado en un OWL Datatype Property cuyo dominio será la correspondiente clase OWL y cuyo\n" +
                "rango será la clase RDFS Datatype asociada con el tipo de datos básico.";
        labelAyudaOnt.setText(frase);

        Image img = new Image(HelloApplication.class.getResource("/atributosOnt.png").toString());
        imageAyudasOnt.setImage(img);
    }

    public void btnAtributosPropiosAction(ActionEvent event) {
        //Cargamos el texto y la imagen correspondiente
        String frase = "Un atributo UML definido por un tipo correspondiente a una clase será transformado a una OWL Object\n" +
                "Property cuyo dominio será la correspondiente clase OWL y cuyo rango será la clase OWL a la que se\n" +
                "refiere.";
        labelAyudaOnt.setText(frase);

        Image img = new Image(HelloApplication.class.getResource("/AtributosPropiosOnt.png").toString());
        imageAyudasOnt.setImage(img);
    }

    public void btnEnumeradosAction(ActionEvent event) {
        //Cargamos el texto y la imagen correspondiente
        String frase = "Un atributo UML definido por un tipo enumerado sera transformado en una OWL Object Property cuyo\n" +
                "dominio será la correspondiente clase OWL y cuyo rango será la clase RDF Alt.";
        labelAyudaOnt.setText(frase);

        Image img = new Image(HelloApplication.class.getResource("/enumeradosOnt.png").toString());
        imageAyudasOnt.setImage(img);
    }

    public void btnColeccionesAction(ActionEvent event) {
        //Cargamos el texto y la imagen correspondiente
        String frase = "Un atributo UML definido por un tipo colección sera transformado en una OWL Object Property cuyo\n" +
                "dominio será la correspondiente clase OWL y cuyo rango será la clase RDF Bag.";
        labelAyudaOnt.setText(frase);

        Image img = new Image(HelloApplication.class.getResource("/coleccionesOnt.png").toString());
        imageAyudasOnt.setImage(img);
    }

    /*----------------------------------------------------------------------------
||                      Pestaña cargar esquema                                   ||
||                                                                               ||
 ----------------------------------------------------------------------------*/
    public void btnCargarEsquemaAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File ficheroSeleccionado = fc.showOpenDialog(stage);

        if(ficheroSeleccionado.exists() && !ficheroSeleccionado.isDirectory()) {
            if (ficheroSeleccionado.getName().endsWith(".png") || ficheroSeleccionado.getName().endsWith(".PNG")) {
                proyectos.guardarEsquemaOnt(ficheroSeleccionado, idProyecto);

                String ruta = "./Proyectos/" + idProyecto + "/esquemaOnt.png";
                File f = new File(ruta);
                Image img = new Image(f.getAbsolutePath());

                //Carga en la bbdd
                DatabaseH2.insertPsmEsquemaOntologico(ruta, idProyecto);

                imageEsquema.setImage(img);
            } else {
                //Mostrar alerta tipo
                mostrarAlertaImagen(event);
            }
        }
    }

    /*----------------------------------------------------------------------------
    ||                      Pestaña introducir datos                              ||
    ||                                                                            ||
     ----------------------------------------------------------------------------*/
    //private List<String> nombres = new LinkedList<>();
    private LinkedHashMap<String, List<String>> atributos = new LinkedHashMap<>(); //Nombre , lista atributos
    int contador = 0;
    int numAtributos = 0;

    private List<String> nombres = new LinkedList<>();
    //private List<List<String>> atributos = new LinkedList<>();

    //Init listview
    private void cargarClases() {
        //Cada clase es un archivo indicado
        List<Pair<String, String>> clases = DatabaseH2.getPsmArchivos(idProyecto); //Pair(idArchivo, nombreArchivo)

        for(Pair<String, String> p : clases){
            nombres.add(p.getValue());
            LinkedList<String> atributosAux = DatabaseH2.getPSMAtributos(p.getKey());
            atributos.put(p.getValue(), atributosAux);

            contador += atributosAux.size();    //Numero total de atributos
        }

        ObservableList oll = FXCollections.observableArrayList(nombres);
        listviewClases.setItems(oll);

    }

    //Al escoger una clase, listar sus campos
    public void listviewClasesAction(MouseEvent mouseEvent) {
        try {
            actualizarListViewAtributos();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void actualizarListViewAtributos() throws IOException {
        String clase = (String) listviewClases.getSelectionModel().getSelectedItem();
        //int index = listviewClases.getSelectionModel().getSelectedIndex();
        //if(index != -1){
        List atributo = atributos.get(clase);
        ObservableList oll = FXCollections.observableArrayList(atributo);
        listviewAtributos.setItems(oll);
            //String clase = (String)listviewClases.getSelectionModel().getSelectedItem();
            //if(!clase.isBlank()){
            //    String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/" + clase;
            //    //Campos del archivo csv (atributos)
            //    ArrayList<String> campos = proyectos.obtenerCamposList(ruta);
//
            //    //Acceso al archivo de atributos correspondiente
            //    String rutaAtr = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/f_atributos" + clase.replaceAll(".csv",".txt");
            //    File atributosFile = new File(rutaAtr);
            //    if(atributosFile.exists() && !atributosFile.isDirectory()) {
            //        FileReader fr = new FileReader(atributosFile);
            //        BufferedReader br = new BufferedReader(fr);
//
            //        //Lectura archivo atributos
            //        String linea = br.readLine();
            //        String[] tokens = {};
            //        if (linea != null) {
            //            //formato: nombreAtr;tipoAtr,nombreAtr;tipoAtr,...
            //            tokens = linea.split(",");
            //        }
            //        List<String> tipos = Arrays.asList(tokens);
//
            //        //Si hay un tipo distinto de "_" guardado en la posicion, indica que el atributo tiene un tipo
            //        for (int i = 0; i < campos.size(); i++) {
            //            String tipo = tipos.get(i);
            //            if (!tipo.equals("_")) {
            //                String nombre = campos.get(i);
            //                campos.set(i, nombre + ";" + tipo);
            //            }
            //        }
            //        fr.close();
            //        br.close();
            //    }
            //    ObservableList oll = FXCollections.observableArrayList(campos);
            //    listviewAtributos.setItems(oll);
            //}
        //}

    }
    public void añadirTipoBasico(ActionEvent event) {
        String atributo_aux = (String) listviewAtributos.getSelectionModel().getSelectedItem();
        String atributo = "";
        if(atributo_aux.contains(";")){
            atributo = atributo_aux.split(";")[0].trim();
        }
        //Obtencion tipo via dialog
        String tipo = mostrarInputBasico();

        if(tipo.isEmpty()){
            mostrarAlertaSeleccion(event);
        }else {
            //actualizarArchivoAtributos(clase, index, tipo);
            String archivo = (String) listviewClases.getSelectionModel().getSelectedItem();
            String idArchivo = DatabaseH2.getIdArchivo_nombre(archivo);
            List<String> lista = listviewAtributos.getItems();
            if(!atributo.equals("")){
                DatabaseH2.updateAtributo(atributo, tipo, idArchivo);
                int index = listviewAtributos.getSelectionModel().getSelectedIndex();
                lista.set(index, atributo + " ; " + tipo);
            }else{
                DatabaseH2.updateAtributo(atributo_aux, tipo, idArchivo);
                int index = listviewAtributos.getSelectionModel().getSelectedIndex();
                lista.set(index, atributo_aux + " ; " + tipo);
            }

            ObservableList oll = FXCollections.observableArrayList(lista);
            listviewAtributos.setItems(oll);

            numAtributos += 1;
        }
    }

    public void añadirTipoClase(ActionEvent event) {
        String atributo_aux = (String) listviewAtributos.getSelectionModel().getSelectedItem();
        String atributo = "";
        if(atributo_aux.contains(";")){
            atributo = atributo_aux.split(";")[0].trim();
        }
        //int index = listviewAtributos.getSelectionModel().getSelectedIndex();

        //Obtencion tipo via dialog
        String tipo = mostrarInputClase();

        if(tipo.isEmpty()){
            mostrarAlertaSeleccion(event);
        }else {
            //actualizarArchivoAtributos(clase, index, tipo);
            String archivo = (String) listviewClases.getSelectionModel().getSelectedItem();
            String idArchivo = DatabaseH2.getIdArchivo_nombre(archivo);
            List<String> lista = listviewAtributos.getItems();
            if(!atributo.equals("")){
                DatabaseH2.updateAtributo(atributo, tipo, idArchivo);
                int index = listviewAtributos.getSelectionModel().getSelectedIndex();
                lista.set(index, atributo + " ; " + tipo);
            }else{
                DatabaseH2.updateAtributo(atributo_aux, tipo, idArchivo);
                int index = listviewAtributos.getSelectionModel().getSelectedIndex();
                lista.set(index, atributo_aux + " ; " + tipo);
            }

            ObservableList oll = FXCollections.observableArrayList(lista);
            listviewAtributos.setItems(oll);
            numAtributos += 1;
        }

    }

    public void añadirEnumerado(ActionEvent event){
        String atributo_aux = (String) listviewAtributos.getSelectionModel().getSelectedItem();
        String atributo = "";
        if(atributo_aux.contains(";")){
            atributo = atributo_aux.split(";")[0].trim();
        }

        //int index = listviewAtributos.getSelectionModel().getSelectedIndex();
        String archivo = (String) listviewClases.getSelectionModel().getSelectedItem();
        String idArchivo = DatabaseH2.getIdArchivo_nombre(archivo);

        List<String> lista = listviewAtributos.getItems();
        if(!atributo.equals("")){
            DatabaseH2.updateAtributo(atributo, "alt", idArchivo);
            int index = listviewAtributos.getSelectionModel().getSelectedIndex();
            lista.set(index, atributo + " ; " + "alt");
        }else{
            DatabaseH2.updateAtributo(atributo_aux, "alt", idArchivo);
            int index = listviewAtributos.getSelectionModel().getSelectedIndex();
            lista.set(index, atributo_aux + " ; " + "alt");
        }
        ObservableList oll = FXCollections.observableArrayList(lista);
        listviewAtributos.setItems(oll);
        numAtributos += 1;
    }


    public void añadirColeccion(ActionEvent event) {
        String atributo_aux = (String) listviewAtributos.getSelectionModel().getSelectedItem();
        String atributo = "";
        if(atributo_aux.contains(";")){
            atributo = atributo_aux.split(";")[0].trim();
        }
        //int index = listviewAtributos.getSelectionModel().getSelectedIndex();
        //actualizarArchivoAtributos(clase, index, "bag");
        String archivo = (String) listviewClases.getSelectionModel().getSelectedItem();
        String idArchivo = DatabaseH2.getIdArchivo_nombre(archivo);
        List<String> lista = listviewAtributos.getItems();
        if(!atributo.equals("")){
            DatabaseH2.updateAtributo(atributo, "bag", idArchivo);
            int index = listviewAtributos.getSelectionModel().getSelectedIndex();
            lista.set(index, atributo + " ; " + "bag");
        }else{
            DatabaseH2.updateAtributo(atributo_aux, "bag", idArchivo);
            int index = listviewAtributos.getSelectionModel().getSelectedIndex();
            lista.set(index, atributo_aux + " ; " + "bag");
        }
        ObservableList oll = FXCollections.observableArrayList(lista);
        listviewAtributos.setItems(oll);
        numAtributos += 1;
    }

    public void añadirColeccionOrd(ActionEvent event) {
        String atributo_aux = (String) listviewAtributos.getSelectionModel().getSelectedItem();
        String atributo = "";
        if(atributo_aux.contains(";")){
            atributo = atributo_aux.split(";")[0].trim();
        }


        String archivo = (String) listviewClases.getSelectionModel().getSelectedItem();
        String idArchivo = DatabaseH2.getIdArchivo_nombre(archivo);
        List<String> lista = listviewAtributos.getItems();
        if(!atributo.equals("")){
            DatabaseH2.updateAtributo(atributo, "seq", idArchivo);
            int index = listviewAtributos.getSelectionModel().getSelectedIndex();
            lista.set(index, atributo + " ; " + "seq");
        }else{
            DatabaseH2.updateAtributo(atributo_aux, "seq", idArchivo);
            int index = listviewAtributos.getSelectionModel().getSelectedIndex();
            lista.set(index, atributo_aux + " ; " + "seq");
        }
        ObservableList oll = FXCollections.observableArrayList(lista);
        listviewAtributos.setItems(oll);
        numAtributos += 1;
    }


    private void actualizarArchivoAtributos(String clase, int index, String tipo) {
        String rutaAtr = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/f_atributos" + clase.replaceAll(".csv", ".txt");
        File atributos = new File(rutaAtr);

        try {

            if (!atributos.exists() && !atributos.isDirectory()) {
                //El archivo no está inicializado, llenarlo
                FileWriter fw = new FileWriter(atributos);
                BufferedWriter bw = new BufferedWriter(fw);
                String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/" + clase;
                ArrayList<String> campos = proyectos.obtenerCamposList(ruta);
                for (int i = 0; i <= campos.size()-1; i++) {
                    bw.write("_,");
                }
                bw.close();
                fw.close();
            }

            //Escritura atributo
            FileReader fr = new FileReader(atributos);
            BufferedReader br = new BufferedReader(fr);
            List<String> tokens = new LinkedList<>();
            String linea = br.readLine();
            if (linea != null) {
                tokens = Arrays.asList(linea.split(","));
                br.close();
                fr.close();
                tokens.set(index, tipo);
                //Reescribir el archivo con el nuevo atributo
                FileWriter fw = new FileWriter(atributos);
                BufferedWriter bw = new BufferedWriter(fw);
                for (String t : tokens) {
                    bw.write(t + ",");
                }
                bw.close();
                fw.close();
                actualizarListViewAtributos();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private String mostrarInputBasico(){

        ChoiceDialog<String> dialog = new ChoiceDialog<>("xsd:integer", choices);
        dialog.setTitle("Tipo básico");
        dialog.setHeaderText("Eleccion");
        dialog.setContentText("Elige el tipo básico:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            return result.get();
        }else{
            return "";
        }
    }

    @FXML
    private String mostrarInputClase(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tipo clase");
        dialog.setHeaderText("Introduzca el nombre de la clase a la que referencia");
        dialog.setContentText("Clase:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            return result.get();
        }else{
            return "";
        }
    }

    private boolean comprobarAtributos() throws IOException {
        return true;
        //if(numAtributos>= contador){
        //    return true;
        //}else {
        //    return false;
        //}
        /*
        //Iterar sobre los archivos de atributos y buscar "_", si hay alguno, devolver false
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/";
        File ficheroDestino = new File( ruta);

        //Filtro para no obtener los archivos de configuracion
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File f, String name)
            {
                return (name.startsWith("f_atributos"));
            }

        };

        File[] ficheros = ficheroDestino.listFiles(filter);
        if(ficheros != null && ficheros.length>0) {
            //Iterar por los ficheros de atributos buscando "_"
            for (File f : ficheros) {
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String linea = br.readLine();

                if(linea != null){
                    //formato: tipo1,tipo2,tipoN
                    String[] tokens = linea.split(",");
                    for(String t : tokens){
                        if(t.equals("_")){
                            return false;
                        }
                    }
                }
            }
        }else{
            return false;
        }
        //Si llega aqui, es porque todos los atributos estan configurados
        return true;
         */
    }

    private void limpiarArchivosUML() {
        //String clasesUML = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/clasesUML.txt";
        //String atributosUML = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/atributosUML.txt";
//
        //File clases = new File(clasesUML);
        //if(clases.exists() && !clases.isDirectory()){
        //    clases.delete();
        //}
        //File atts = new File(atributosUML);
        //if(atts.exists() && !atts.isDirectory()){
        //    atts.delete();
        //}
    }

    private void crearClasesUml() throws IOException {
        //List<String> ll = listviewClases.getItems();
        ////LinkedList<String> ll = (LinkedList) listviewClases.getItems();
//
        //String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/clasesUML.txt";
        //File clasesUML = new File(ruta);
        ////Borrado si existe el archivo
        //if(clasesUML.exists() && !clasesUML.isDirectory()){
        //    clasesUML.delete();
        //}
//
        //FileWriter fw = new FileWriter(clasesUML, true);
        //BufferedWriter bw = new BufferedWriter(fw);
//
        //for(String clase : ll){
        //    String format = clase.replace(".csv","");
        //    bw.write(format+",");
        //}
        //bw.close();
        //fw.close();
    }

    private void crearAtributosUML() throws IOException {
        //List<String> clases = listviewClases.getItems();
//
        //for(String clase : clases){
//
        //    String claseFormateada = clase.replace(".csv", ".txt");
        //    String claseAux = clase.replace(".csv", "");
//
        //    String rutaAtr = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/f_atributos" + claseFormateada;
        //    File atributos = new File(rutaAtr);
        //    if(atributos.exists()){
        //        //Crear un string: nombreAtr;tipoAtr;clase
        //        FileReader fr = new FileReader(rutaAtr);
        //        BufferedReader br = new BufferedReader(fr);
        //        String linea = br.readLine();
//
        //        //Obtencion tipos de cada atributo
        //        if(linea != null){
        //            String[] tokens = linea.split(",");
        //            br.close();
        //            fr.close();
//
        //            String rutaOutput = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/atributosUML.txt";
        //            File atributosUML = new File(rutaOutput);
        //            FileWriter fw = new FileWriter(atributosUML, true);
        //            BufferedWriter bw = new BufferedWriter(fw);
//
        //            //Obtencion campos de esa clase
        //            String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/" + clase;
        //            ArrayList<String> campos = proyectos.obtenerCamposList(ruta);
//
        //            int ind = 0;
        //            for(String campo : campos){
        //                bw.write( campo+";"+tokens[ind]+";"+claseAux+",");
        //                ind += 1;
        //            }
        //            bw.close();
        //            fw.close();
        //        }
//
        //    }
        //}
    }
    public void transicionarPantalla(ActionEvent event) {
        try {
            root = FXMLLoader.load(HelloApplication.class.getResource("/views/faseDsl.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
                Panel metadatos
     */

    public void btnProcesarAction(ActionEvent event) {

        try {
            //Transformar las estructuras de datos para que las acepte el generador de ontologias
            if(comprobarAtributos()) {
                guardarMetadatos();
                //limpiarArchivosUML();
                //crearClasesUml();
                //crearAtributosUML();
                OntologyGenerator og = new OntologyGenerator(publisher, descripcion, description);
                //transicionar pantalla
                transicionarPantalla(event);
            }else{
                //Mostrar alerta falta completar atributos
                mostrarAlertaOntologia(event);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void guardarMetadatos() throws IOException {
        this.publisher = publisherInput.getText();
        this.descripcion = descripcionSPInput.getText();
        this.description = descripcionENGInput.getText();
        DatabaseH2.updateMetadatos(publisher, descripcion, description, idProyecto);
        //String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/metadatos.txt";
        //File metadatos = new File(ruta);
        //FileWriter fw = new FileWriter(metadatos);
        //BufferedWriter bw = new BufferedWriter(fw);
//
        //bw.write(publisher + "," + descripcion + "," + description);
//
        //bw.close();
        //fw.close();
    }

    private void cargarMetadatos() throws IOException {
        List<String> metadatos = DatabaseH2.getMetadatos(idProyecto);
        if(metadatos.size() > 0){
            String publisher = metadatos.get(0);
            String descripcionES = metadatos.get(1);
            String descripcionEN = metadatos.get(2);
            publisherInput.setText(publisher);
            descripcionSPInput.setText(descripcionES);
            descripcionENGInput.setText(descripcionEN);
        }
        //String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/metadatos.txt";
        //File metadatos = new File(ruta);
        //if(metadatos.exists() && !metadatos.isDirectory()) {
        //    FileReader fr = new FileReader(metadatos);
        //    BufferedReader br = new BufferedReader(fr);
//
        //    String linea = br.readLine();
        //    if(linea != null) {
        //        String[] tokens = linea.split(",");
        //        //Comprobamos que los metadatos han sido introducidos
        //        if (tokens.length > 0) {
        //            publisherInput.setText(tokens[0]);
        //            descripcionSPInput.setText(tokens[1]);
        //            descripcionENGInput.setText(tokens[2]);
        //        }
        //    }
        //    br.close();
        //    fr.close();
        //}
    }
}
