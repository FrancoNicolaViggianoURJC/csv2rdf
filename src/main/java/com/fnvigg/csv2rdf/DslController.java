package com.fnvigg.csv2rdf;

import java.awt.Desktop;
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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.layout.GridPane;
import java.io.*;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.*;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

public class DslController implements Initializable {

    public ListView listviewAtributos;
    public ListView listviewClases;
    public Button btnEnumerado;
    public Button btnAtributoPrimario;
    public Accordion accordion;
    public Label atributoPrimarioLabel;
    public ListView listviewRutas;
    public ListView listviewArchivosRelevantes;
    public ListView listviewAtributoClave;
    public Button btnAñadirRuta;
    public Button btnAñadirArchivo;
    public Button btnSeleccionarAtributo;
    public Label atributoClaveLbl;
    public ChoiceBox classChoice;
    public Label clasePerteneceLbl;
    private String nombreProyecto;
    private Gestor_proyectos proyectos = new Gestor_proyectos();
    private Map<String, String> keyFlds;
    private ArrayList<Map<String, String>> enumerados;
    private List<String> choices = new ArrayList<>();
    private Stage stage;
    private Scene scene;
    private Parent root;
    private String idProyecto;
    private List<Pair<String,String>> clases;   // [IdArchivo, NombreArchivo]
    private List<Pair<String,List<Pair<String, String>>>> atributos = new LinkedList<>(); // [Clase, ListaAtributos[nombreAtributo, valorAtributo]]
    private Map<String, List<Pair<String, String>>> atributosPorClase = new HashMap<>();
    private Map<String, String> atributosPrimariosPorClase = new HashMap<>();
    private Map<String, String> enumeradosPorClase = new HashMap<>();
    private List<Pair<String,String>> rutasAux = new ArrayList<>();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idProyecto = AtributosSesion.getIdProyecto();
        this.accordion.setExpandedPane(accordion.getPanes().get(0));
        this.nombreProyecto = AtributosSesion.getNombreProyecto();
        this.keyFlds = new HashMap<>();
        this.enumerados = new ArrayList<>();
        choices.add("xsd:integer");
        choices.add("xsd:string");
        choices.add("xsd:float");
        choices.add("xsd:dateTime");
        choices.add("xsd:boolean");
        choices.add("xsd:decimal");

        //Init Estructuras de datos
        this.clases = AtributosSesion.getClasesStatic();
        this.atributos = AtributosSesion.getAtributosStatic();
        for(Pair<String,List<Pair<String, String>>> claseAtributo : atributos){
            atributosPorClase.put(claseAtributo.getKey().replace(".csv", ""), claseAtributo.getValue());
        }
        //Init listview clases
        cargarClases();

        //TODO
        //Init de los atributos primarios
        //obtener todos los atributos primarios y meterlos en atributosPrimariosPorClase

        //Botones panel 1
        btnEnumerado.setDisable(true);
        btnAtributoPrimario.setDisable(true);

        //Panel 2
        cargarChoicebox();

        //Actualizar la fase en el setting
        DatabaseH2.updateProyectosFase("PSM", idProyecto);
    }


    /*
        --------------------------------- PANEL KEYFIELDS Y ENUM --------------------------------
     */
    private void cargarClases() {

        //String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/";
        //File ficheroDestino = new File( ruta);

        //Filtro para no obtener los archivos de configuracion
        //FilenameFilter filter = new FilenameFilter() {
        //    public boolean accept(File f, String name)
        //    {
        //        return (name.endsWith(".csv") && !name.startsWith("nonRelevant_"));
        //    }
//
        //};
//
        //File[] ficheros = ficheroDestino.listFiles(filter);
        //ArrayList<String> nombres = new ArrayList<>();
//
        //if(ficheros != null) {
        //    for (File f : ficheros) {
        //        nombres.add(f.getName());
        //    }
        //}
        ArrayList<String> claseList = new ArrayList<>();
        for(Pair<String,String> clase : clases){
            claseList.add(clase.getValue().replace(".csv", ""));    //Se crea la lista con solo los nombres de la clase
        }
        ObservableList oll = FXCollections.observableArrayList(claseList);
        listviewClases.setItems(oll);
    }

    public void listviewClasesAction(MouseEvent mouseEvent) {
        try {
            //Cargar solo los atributos relativos a esa clase
            //Diccionario clase -> listaAtributos
            actualizarListViewAtributos();
            String clase = listviewClases.getSelectionModel().getSelectedItem()+".csv";
            String pk = atributosPrimariosPorClase.get(clase);
            if(pk!=null){
                atributoPrimarioLabel.setText("Atributo Primario: "+pk);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void actualizarListViewAtributos() throws IOException {
        int index = listviewClases.getSelectionModel().getSelectedIndex();
        if(index != -1){
            String clase = (String)listviewClases.getSelectionModel().getSelectedItem();
            if(!clase.isBlank()){
                List<Pair<String, String>> atributos = atributosPorClase.get(clase.replace(".csv", ""));
                List<String> nombresAtributos = new ArrayList<>();
                for(Pair<String, String> atributo : atributos){
                    nombresAtributos.add(atributo.getKey() + " : " + atributo.getValue());
                }

                ObservableList oll = FXCollections.observableArrayList(nombresAtributos);
                listviewAtributos.setItems(oll);
               //String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/" + clase;
               ////Campos del archivo csv (atributos)
               //ArrayList<String> campos = proyectos.obtenerCamposList(ruta);

               ////Acceso al archivo de atributos correspondiente
               //String rutaAtr = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/f_atributos" + clase.replaceAll(".csv",".txt");
               //File atributosFile = new File(rutaAtr);
               //if(atributosFile.exists() && !atributosFile.isDirectory()) {
               //    FileReader fr = new FileReader(atributosFile);
               //    BufferedReader br = new BufferedReader(fr);

               //    //Lectura archivo atributos
               //    String linea = br.readLine();
               //    String[] tokens = {};
               //    if (linea != null) {
               //        //formato: nombreAtr;tipoAtr,nombreAtr;tipoAtr,...
               //        tokens = linea.split(",");
               //    }
               //    List<String> tipos = Arrays.asList(tokens);

               //    //Si hay un tipo distinto de "_" guardado en la posicion, indica que el atributo tiene un tipo
               //    for (int i = 0; i < campos.size(); i++) {
               //        String tipo = tipos.get(i);
               //        if (!tipo.equals("_")) {
               //            String nombre = campos.get(i);
               //            campos.set(i, nombre + ";" + tipo);
               //        }
               //    }
               //    fr.close();
               //    br.close();
               //}
               //ObservableList oll = FXCollections.observableArrayList(campos);
               //listviewAtributos.setItems(oll);

               //Actualizacion label
               String field = DatabaseH2.getAtributoPrimario(clase);
               if(field != null && !field.isBlank()){
                   atributoPrimarioLabel.setText("Atributo Primario: " + field);
               }else{
                   atributoPrimarioLabel.setText("Atributo Primario: ");
               }

            }

        }

    }
    public void listviewAtributosAction(MouseEvent mouseEvent) {
        //Analizar si es tipo enum o cualquier otro tipo.
        //En funcion de eso, habilitar un boton u otro

        String atributo = (String) listviewAtributos.getSelectionModel().getSelectedItem();
        String[] tokens = atributo.split(":"); // atributo = nombreAtributo ; tipoAtributo

        //String nombreAtributo = tokens[0];
        String tipoAtributo = tokens[1].replace(" ", "");

        if(tipoAtributo.equals("alt")){
            //Tipo atributo, disable keyAttribute
            btnAtributoPrimario.setDisable(true);
            btnEnumerado.setDisable(false);
        }else{
            //Otro tipo, disable enum
            btnAtributoPrimario.setDisable(false);
            btnEnumerado.setDisable(true);
        }
    }

    public void btnAtributoPrimarioAction(ActionEvent event) {
        //Update la tabla Archivo con el nuevo atributo primario
        //Obtener primero el idAtributo seleccionado
        String clase = listviewClases.getSelectionModel().getSelectedItem()+".csv";
        String idArchivo = DatabaseH2.getIdArchivo_nombre(clase);
        String atributo = (String) listviewAtributos.getSelectionModel().getSelectedItem();
        String[] tokens = atributo.split(":"); // atributo = nombreAtributo ; tipoAtributo
        String nombreAtributo = tokens[0];

        String idAtributo = DatabaseH2.getIdAtributo(idArchivo, nombreAtributo.replace(" ", ""));
        //DatabaseH2.getAtributosTable();
        if(! idAtributo.isBlank()){
            //Update tabla archivo

            DatabaseH2.updateAtributoPrimario(idAtributo, idArchivo);
            atributosPrimariosPorClase.put(clase, nombreAtributo);
            atributoPrimarioLabel.setText("Atributo Primario: "+nombreAtributo);
        }
        //Obtenemos valores de las clases para formar el par
        //String clase = (String)listviewClases.getSelectionModel().getSelectedItem();
        //clase = clase.replaceAll(".csv", "");
        //String atributo = (String)listviewAtributos.getSelectionModel().getSelectedItem();
        //String[] tokens = atributo.split(";");
        //String nombreAtributo = tokens[0];
//
        ////Metemos el par clave-valor [clase - atributo]
        //keyFlds.put(clase, nombreAtributo);
//
        //atributoPrimarioLabel.setText("Atributo Primario: "+nombreAtributo);

    }

    public void btnEnumeradoAction(ActionEvent event) {
        //Guardar asociacion entre clase : nombreAtributo : valoresLiterales
        String clase = (String)listviewClases.getSelectionModel().getSelectedItem();
        String claseFormatted = clase.replace(".csv", "").replace(" ", "");
        String atributo = (String)listviewAtributos.getSelectionModel().getSelectedItem();
        String[] tokens = atributo.split(":");
        String nombreAtributo = tokens[0].replace(" ", "");

        //int index = listviewClases.getSelectionModel().getSelectedIndex();
        ////Datos del atributo
        //String clase = (String)listviewClases.getSelectionModel().getSelectedItem();
        //String atributo = (String)listviewAtributos.getSelectionModel().getSelectedItem();
        //String[] tokens = atributo.split(":");
        //String nombreAtributo = tokens[0];
//
        //Formatear el input del usuario
        Pair<String, String> resultado = preguntarEnum();
        String values = resultado.getKey();
        String literales = resultado.getValue();
        String format = "VALUES(" + values+"),ENUMS(" + literales+")))";

        enumeradosPorClase.put(claseFormatted+nombreAtributo, format);
        ////Obtenemso el mapa asociado a esa clase
        //Map<String, String> mapa = this.enumerados.get(index);
        //mapa.put(nombreAtributo, format);
        //System.out.println("Mapa");
    }
    public void generarDSL(ActionEvent event) {
        //Comprobar que todos los keyfields fueron introducidos
        if(atributosPrimariosPorClase.size() == listviewClases.getItems().size()){
            //Obtener todas las clases y las rutas a sus ficheros fuente

            //FILE STATEMENTS. One for each Pair
            List<Pair<String, String>> clases_rutas = DatabaseH2.getDslClases(idProyecto);  // (Clase, Ruta)

            //SUBJECT STATEMENTS. Gather class and its primary att
            Map<String, String> clases_pk = DatabaseH2.getDslPks(idProyecto); // (Clase, PK)

            //PREDICATE-OBJECTS
            //Obtener todos los atributos, junto con la clase que pertenecen y el valor que tienen (atributo, clase, valor)
            List<Triplet<String, String, String>> atributos_clases = DatabaseH2.getDslAtributos(idProyecto);

            //TODO
            //bag,seq


            try {
                DslGenerator dslGen = new DslGenerator(idProyecto, clases_rutas, clases_pk, atributos_clases, enumeradosPorClase, rutas, rutasAux);
                //limpiarCarpeta();
                ejecutarJar();
                abrirDirectorio();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            //mostrar alerta
            mostrarAlertaClave(event);
        }

    }

    private void abrirDirectorio() throws IOException {
        String directorio = "./Proyectos/" + idProyecto + "/";
        File rdf = new File(directorio);
        Desktop.getDesktop().open(rdf);
    }


    private void ejecutarJar() throws IOException {
        //direccion donde está el motor, y la direccion temporal donde ejecutarlo en el proyecto

        InputStream inputStream = HelloApplication.class.getResourceAsStream("/DSLengine2RDF.jar");
        File tempFile = File.createTempFile("DSLengine2RDF", ".jar");
        //tempFile.deleteOnExit(); // Eliminar el archivo al salir

        // Copiar el contenido del InputStream al archivo temporal
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        //String rutaJar = HelloApplication.class.getResource("/DSLengine2RDF.jar");
        //String rutaJarDest = "./Proyectos/" + idProyecto + "/DSLengine2RDF.jar";
        //File jar = new File(HelloApplication.class.getResource("/DSLengine2RDF.jar").getPath().replace("jar:file:\\ ", ""));
       // File jarDest = new File(rutaJarDest);
        //System.out.println(jar);
        //System.out.println(jar.getAbsolutePath());

        //System.out.println(jarDest.getAbsolutePath());
        //crearCopias(jar, jarDest); //copia del motor

        //ruta donde se encuentra el codigo DSL, asi como archivos de salida/error de la ejecucion
        String rutaDSL = "./Proyectos/" + idProyecto + "/DSLCode.txt";
        String rutaOut = "./Proyectos/" + idProyecto + "/out.txt";
        String rutaLog = "./Proyectos/" + idProyecto + "/log.txt";
        File log = new File(rutaLog);
        File out = new File(rutaOut);
        File dslFile = new File(rutaDSL);
        System.out.println("RUTA DSL:");
        System.out.println(dslFile.getAbsolutePath());

        //Configurar el env del jar
        String directorio = "./Proyectos/" + idProyecto + "/";
        File dirFile = new File(directorio);

        //Ejecucion del jar
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar" , tempFile.getAbsolutePath() , dslFile.getAbsolutePath());
        processBuilder.directory(dirFile);
        processBuilder.redirectError(log);
        processBuilder.redirectOutput(out);
        Process process = processBuilder.start();

        //jarDest.delete();

    }

    private void añadirRutas() throws IOException {
        String ruta = "./Proyectos/" + nombreProyecto + "/clasesUML.txt";
        File clasesUML = new File(ruta);
        FileWriter fw = new FileWriter(clasesUML, true);
        BufferedWriter bw = new BufferedWriter(fw);

        for(Quartet<String, String, ArrayList<String>, ArrayList<String>> paths : rutas){
            ArrayList<String> archivos = paths.getValue2();
            for(String archivo : archivos){
                String nombreClase = archivo.replace(".csv", "")+",";
                bw.write(nombreClase);
            }
        }
        bw.close();
        fw.close();
    }

    private void limpiarCarpeta() {
        String rutaClase = "./Proyectos/" + nombreProyecto + "/";
        File directorio = new File( rutaClase);

        //Filtro para no obtener los archivos de configuracion
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File f, String name)
            {
                return (!name.contentEquals("ontology.txt") && !name.contentEquals("DSLCode.txt") &&
                        !name.contentEquals("config.txt") && !name.contentEquals("metadatos.txt") &&
                        !name.endsWith(".csv") && !name.endsWith(".png"));
            }
        };

        File[] ficheros = directorio.listFiles(filter);


        //Inicializacion linkedlists
        for(File f : ficheros){
            f.delete();
        }
    }

    //Preparar los datos para la clase DSLGenerator
    private List<String> preprocesarDatos() throws IOException {
        //Preprocesamiento -> separar cada cosa en su clase
        List<String> clases = listviewClases.getItems();

        //Formateo lista de clases
        List<String> clasesFormateadas = new ArrayList<>();
        for(String clase : clases){
            clasesFormateadas.add(clase.replaceAll(".csv", ""));
        }

        int indexClass = 0;
        //Generar por cada clase el archivo DSL correspondiente
        for(String nombreClase : clasesFormateadas){
            //Archivo de la clase especifica
            String rutaClase = "./Proyectos/" + nombreProyecto + "/" + nombreClase + "DSL.txt";
            File claseFile = new File(rutaClase);
            if(claseFile.exists()){
                claseFile.delete();
            }
            FileWriter fw = new FileWriter(claseFile, true);
            BufferedWriter bw = new BufferedWriter(fw);

            // SUBJECT
            String clave = keyFlds.get(nombreClase);
            bw.write("SUBJECT(#"+nombreClase+"."+clave+","+nombreProyecto+":"+nombreClase+")\n");

            //DATATYPES & OBJECTS

            // I/O
            String rutaAtributos = "./Proyectos/" + nombreProyecto + "/atributosUML.txt";
            File atributosUML = new File(rutaAtributos);
            FileReader fr = new FileReader(atributosUML);
            BufferedReader br = new BufferedReader(fr);

            //Lectura atributos
            String linea = br.readLine();
            if(linea != null){
                String[] tokens = linea.split(",");
                // [nombreAtr; tipoAtr;claseAtr],[],[],...
                for(String atributo : tokens){
                    // [nombreAtr;tipoAtr;claseAtr]
                    String[] campos = atributo.split(";");
                    String nombreAtr = campos[0];
                    String tipo = campos[1];
                    String clasePertenece = campos[2];
                    //Si pertenece a la clase que estamos analizando ahora
                    if(nombreClase.equals(clasePertenece)){
                        if(choices.contains(tipo)){
                            //Datatype
                            bw.write("PREDICATE-OBJECT(PREDICATE("+nombreProyecto+":"+nombreAtr+"),OBJECT("+nombreProyecto+":#"+nombreClase+"."+nombreAtr+"))\n");
                        }else{
                            //Object
                            if(tipo.equals("bag")){
                                //escribir collection
                                bw.write("PREDICATE-OBJECT(PREDICATE("+nombreProyecto+":"+nombreAtr+"),OBJECT(CONTAINER(BAG,#"+nombreClase+"."+nombreAtr+")))\n");
                            }else if(tipo.equals("seq")){
                                //Collection ord
                                bw.write("PREDICATE-OBJECT(PREDICATE("+nombreProyecto+":"+nombreAtr+"),OBJECT(CONTAINER(SEQ,#"+nombreClase+"."+nombreAtr+")))\n");
                            }
                        }
                    }
                }
            }

            //ENUMS
            Map<String, String> enumeradosClase = enumerados.get(indexClass);
            indexClass++;
            for(String key : enumeradosClase.keySet()){
                // key -> nombre atributo
                // valor -> VALUES(..),ENUM(...)))
                String valor = enumeradosClase.get(key);
                bw.write("PREDICATE-OBJECT(PREDICATE("+nombreProyecto+":"+key+"),OBJECT(#"+nombreClase+"."+key+","+valor+"\n");
            }

            //RUTAS
            for(Quartet<String, String, ArrayList<String>, ArrayList<String>> paths : rutas){
                String clase = paths.getValue0();
                String nombreAtrFinal = paths.getValue1();
                ArrayList<String> archivos = paths.getValue2();
                ArrayList<String> intermedios = paths.getValue3();
                StringBuilder cadena = new StringBuilder();
                String ultimoArchivo = "";
                for(int i = 0; i <= archivos.size()-1; i++){
                    String archivo = archivos.get(i);
                    String atributoIntermedio = intermedios.get(i);

                    if(i== archivos.size()-1){
                        //Ultimo archivo de la ruta
                        cadena.append("#" + archivo.replace(".csv", "") + "."+atributoIntermedio+"),");
                        ultimoArchivo = archivo.replace(".csv", "");
                    }else{
                        cadena.append("#" + archivo.replace(".csv", "") + "."+atributoIntermedio+",");
                    }
                }
                cadena.append(nombreProyecto+":#"+ultimoArchivo+"."+nombreAtrFinal+"))");
                if(nombreClase.equals(clase)){
                    //Es un path perteneciente a esa clase
                    bw.write("PREDICATE-OBJECT(PREDICATE("+nombreProyecto+":"+nombreAtrFinal+"),OBJECT" +
                            "(QUERY(MATCH("+cadena+")\n");
                }
            }

            bw.close();
            fw.close();
            fr.close();
            br.close();
        }
        return clasesFormateadas;
    }

    @FXML
    private Pair<String, String> preguntarEnum(){
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Especificar enumerado");
        dialog.setHeaderText("Especifique los valores y sus equivalencias, separados por comas");

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField valores = new TextField();
        valores.setPromptText("Valores");
        TextField literales = new TextField();
        literales.setPromptText("Literales");

        grid.add(new Label("valor0,valor1,valor2,..:"), 0, 0);
        grid.add(valores, 1, 0);
        grid.add(new Label("literal0,literal1,literal2,..:"), 0, 1);
        grid.add(literales, 1, 1);


        dialog.getDialogPane().setContent(grid);

        Optional<Pair<String, String>> result = dialog.showAndWait();

        if(result.isPresent()){
            return new Pair<>(valores.getText(), literales.getText());
        }else{
            return new Pair<>("","");
        }

    }

    @FXML
    private boolean mostrarAlertaClave(Event event){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Campos clave");
        String contenido = "Cada clase debe tener su campo clave.";
        alerta.setContentText(contenido);
        Optional<ButtonType> resultado = alerta.showAndWait();
        return false;
    }

    @FXML
    private boolean mostrarAlertaTipoFichero(Event event){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Ficheros csv");
        String contenido = "El fichero debe ser de tipo csv.";
        alerta.setContentText(contenido);
        Optional<ButtonType> resultado = alerta.showAndWait();
        return false;
    }

    @FXML
    private boolean mostrarAlertaSeleccionRuta() {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Ruta");
        String contenido = "Debe seleccionar una ruta primero.";
        alerta.setContentText(contenido);
        Optional<ButtonType> resultado = alerta.showAndWait();
        return false;
    }
    /*
    ----------------------- PANEL RUTAS ---------------------------------------
     */
    // nombreRuta , Archivos[], Atributos[]
    // Para el archivo[0], su atributo relevante es el atributos[0]
    List<Quartet<String, String, ArrayList<String>, ArrayList<String>>> rutas = new ArrayList<>();

    private void cargarChoicebox() {
        //String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/clasesUML.txt";
        //File clasesUML = new File(ruta);
        //ArrayList<String> clases = proyectos.obtenerClases(clasesUML);
        ArrayList<String> claseList = new ArrayList<>();
        for(Pair<String,String> clase : clases){
            claseList.add(clase.getValue().replace(".csv", ""));
        }
        ObservableList oll = FXCollections.observableArrayList(claseList);
        classChoice.setItems(oll);
    }

    public void listviewRutasAction(MouseEvent mouseEvent) {
        int index = listviewRutas.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            //Obtenemos la tripleta asociada a esa ruta
            Quartet<String, String, ArrayList<String>, ArrayList<String>> ruta = rutas.get(index);

            //Actualizacion listview archivos
            ArrayList<String> archivos = ruta.getValue2();
            ObservableList ollArchivos = FXCollections.observableArrayList(archivos);
            listviewArchivosRelevantes.setItems(ollArchivos);

            //Limpiar el lbl
            atributoClaveLbl.setText("Atributo primario: ");

            //Limpiar el listview atributos
            ArrayList<String> blank = new ArrayList<>();
            ObservableList ollAtributos = FXCollections.observableArrayList(blank);
            listviewAtributoClave.setItems(ollAtributos);

            //Actualizar el lbl de clases
            String clasePertenece = ruta.getValue0();
            clasePerteneceLbl.setText("Clase: "+clasePertenece);
        }
    }
    public void listviewArchivosRelevantesAction(MouseEvent mouseEvent) {
        int indexRuta = listviewRutas.getSelectionModel().getSelectedIndex();
        int indexArchivo = listviewArchivosRelevantes.getSelectionModel().getSelectedIndex();
        if (indexRuta != -1) {
            Quartet<String, String, ArrayList<String>, ArrayList<String>> ruta = rutas.get(indexRuta);
            ArrayList<String> archivos = ruta.getValue2();
            ArrayList<String> atributos = ruta.getValue3();

            //Mostrar campos de ese archivo
            String nombreArchivo = archivos.get(indexArchivo);
            String rutaArchivo = "./Proyectos/" + idProyecto + "/" + nombreArchivo;
            ArrayList<String> campos = proyectos.obtenerCamposList(rutaArchivo);
            ObservableList oll = FXCollections.observableArrayList(campos);
            listviewAtributoClave.setItems(oll);

            //Actualizacion label
            boolean b = indexArchivo <= atributos.size()-1;
            if(indexArchivo != -1 && !atributos.isEmpty() && b)  {
                String atributo = atributos.get(indexArchivo);
                if (atributo != null) {
                    atributoClaveLbl.setText("Atributo primario: " + atributo);
                }
            }else{
                atributoClaveLbl.setText("Atributo primario: ");
            }

            //Bloqueo boton añadir archivos si aun no se ha seleccionado un atributo clave
            if(atributoClaveLbl.getText().equals("Atributo primario: ")){
                btnAñadirArchivo.setDisable(true);
            }
        }

    }

    public void listviewAtributoClaveAction(MouseEvent mouseEvent) {
    }

    //Función para añadir una ruta al codigo dsl
    public void btnAñadirRutaAction(ActionEvent event) {
        //El nombre de la ruta será el nombre del atributo
        String nombreRuta = pedirNombreRuta();
        if(!nombreRuta.isBlank()) {
            //Se inicializan el resto de componentes de esa ruta (clase, archivos, atributos primarios)
            String clase = (String) classChoice.getSelectionModel().getSelectedItem();
            ArrayList<String> archivos = new ArrayList<>();
            ArrayList<String> atributos = new ArrayList<>();
            Quartet<String, String, ArrayList<String>, ArrayList<String>> ruta = new Quartet<>(clase, nombreRuta, archivos, atributos);
            //Se añade a la lista de rutas
            rutas.add(ruta);

            actualizarlistViewRutas();
        }
    }

    @FXML
    private FileChooser fileChooser = new FileChooser();
    public void btnAñadirArchivoAction(ActionEvent event) {
        int index = listviewRutas.getSelectionModel().getSelectedIndex();
        if(index != -1){
            Quartet<String, String, ArrayList<String>, ArrayList<String>> ruta = rutas.get(index);

            //Funcion para abrir el explorador de archivos
            File ficheroSeleccionado = fileChooser.showOpenDialog(stage);
            String archivoRuta = "./Proyectos/" + idProyecto + "/nonRelevant_" + ficheroSeleccionado.getName();
            File ficheroDestino = new File(archivoRuta);
            if(!ficheroSeleccionado.getName().endsWith(".csv")){
            //Mostrar warning
            mostrarAlertaTipoFichero(event);
            }else {
                crearCopias(ficheroSeleccionado, ficheroDestino);

                //Actualizar las listas
                ArrayList<String> ficheros = ruta.getValue2();
                ficheros.add(ficheroDestino.getName());

                //Actualizamos la lista de ficheros
                ruta.setAt2(ficheros);
                //Actualizamos la lista de rutas general
                rutas.set(index, ruta);
                rutasAux.add(new Pair("nonRelevant_"+ficheroSeleccionado.getName(),archivoRuta));
                //Actualizar listview de archivos
                ObservableList<String> oll = FXCollections.observableArrayList(ficheros);
                listviewArchivosRelevantes.setItems(oll);
            }
        }else{
            mostrarAlertaSeleccionRuta();
        }
    }



    private void crearCopias(File ficheroSeleccionado, File ficheroDestino) {
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

    public void btnSeleccionarAtributoAction(ActionEvent event) {
        int indexRuta = listviewRutas.getSelectionModel().getSelectedIndex();
        int indexArchivo = listviewArchivosRelevantes.getSelectionModel().getSelectedIndex();
        String nombreAtributo = (String)listviewAtributoClave.getSelectionModel().getSelectedItem();
        Quartet<String, String, ArrayList<String>, ArrayList<String>> ruta = rutas.get(indexRuta);
        ArrayList<String> atributos = ruta.getValue3();
        if(atributos.isEmpty()){
            atributos.add(nombreAtributo);
            ruta.setAt3(atributos);
        }else{
            if(indexArchivo <= atributos.size()-1){
                atributos.set(indexArchivo, nombreAtributo);
                ruta.setAt3(atributos);
            }else{
                atributos.add(nombreAtributo);
                ruta.setAt3(atributos);
            }
        }

        //Actualizacion label
        if(indexArchivo != -1 && !atributos.isEmpty()) {
            String atributo = atributos.get(indexArchivo);
            if (atributo != null) {
                atributoClaveLbl.setText("Atributo primario: " + atributo);
            }
        }

        //Bloqueo boton añadir archivos si aun no se ha seleccionado un atributo clave
        if(atributoClaveLbl.getText().equals("Atributo primario: ")){
            btnAñadirArchivo.setDisable(true);
        }else{
            btnAñadirArchivo.setDisable(false);
        }
    }

    private String pedirNombreRuta() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nombre del atributo");
        dialog.setHeaderText("");
        dialog.setContentText("Introduzca el nombre del atributo a guardar a partir de paths:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            return result.get();
        }else{
            return "";
        }
    }

    private void actualizarlistViewRutas(){

        ArrayList<String> nombres = new ArrayList<>();
        for(Quartet<String, String, ArrayList<String>, ArrayList<String>> t : rutas){
            String nombreRuta = t.getValue1();
            nombres.add(nombreRuta);
        }
        ObservableList oll = FXCollections.observableArrayList(nombres);
        listviewRutas.setItems(oll);
    }


    public void faseAnteriorAction(ActionEvent event) {
        try {
            root = FXMLLoader.load(HelloApplication.class.getResource("/views/fasePsm.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
