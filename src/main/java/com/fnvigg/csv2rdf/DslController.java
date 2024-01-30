package com.fnvigg.csv2rdf;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;
import javafx.scene.layout.GridPane;
import java.io.*;
import java.net.URL;
import java.util.*;

public class DslController implements Initializable {

    public ListView listviewAtributos;
    public ListView listviewClases;
    public Button btnEnumerado;
    public Button btnAtributoPrimario;
    public Accordion accordion;
    private String nombreProyecto;
    private Gestor_proyectos proyectos = new Gestor_proyectos();
    private Map<String, String> keyFlds;
    private ArrayList<Map<String, String>> enumerados;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.accordion.setExpandedPane(accordion.getPanes().get(0));
        this.nombreProyecto = AtributosSesion.getNombreProyecto();
        this.keyFlds = new HashMap<>();
        this.enumerados = new ArrayList<>();

        //Init listview clases
        cargarClases();

        //Init de los mapas
        int numeroClases = listviewClases.getItems().size();
        for(int i = 0; i < numeroClases; i++){
            enumerados.add(new HashMap<String, String>());
        }

    }

    private void cargarClases() {

        //Las clases se obtienen a partir de los nombres de archivos

        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/";
        File ficheroDestino = new File( ruta);

        //Filtro para no obtener los archivos de configuracion
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File f, String name)
            {
                return (name.endsWith(".csv"));
            }

        };

        File[] ficheros = ficheroDestino.listFiles(filter);
        ArrayList<String> nombres = new ArrayList<>();

        if(ficheros != null) {
            for (File f : ficheros) {
                nombres.add(f.getName());
            }
        }
        ObservableList oll = FXCollections.observableArrayList(nombres);
        listviewClases.setItems(oll);
    }
    public void listviewClasesAction(MouseEvent mouseEvent) {
        try {
            actualizarListViewAtributos();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void actualizarListViewAtributos() throws IOException {
        int index = listviewClases.getSelectionModel().getSelectedIndex();
        if(index != -1){
            String clase = (String)listviewClases.getSelectionModel().getSelectedItem();
            if(!clase.isBlank()){
                String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/" + clase;
                //Campos del archivo csv (atributos)
                ArrayList<String> campos = proyectos.obtenerCamposList(ruta);

                //Acceso al archivo de atributos correspondiente
                String rutaAtr = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/f_atributos" + clase.replaceAll(".csv",".txt");
                File atributosFile = new File(rutaAtr);
                if(atributosFile.exists() && !atributosFile.isDirectory()) {
                    FileReader fr = new FileReader(atributosFile);
                    BufferedReader br = new BufferedReader(fr);

                    //Lectura archivo atributos
                    String linea = br.readLine();
                    String[] tokens = {};
                    if (linea != null) {
                        //formato: nombreAtr;tipoAtr,nombreAtr;tipoAtr,...
                        tokens = linea.split(",");
                    }
                    List<String> tipos = Arrays.asList(tokens);

                    //Si hay un tipo distinto de "_" guardado en la posicion, indica que el atributo tiene un tipo
                    for (int i = 0; i < campos.size(); i++) {
                        String tipo = tipos.get(i);
                        if (!tipo.equals("_")) {
                            String nombre = campos.get(i);
                            campos.set(i, nombre + ";" + tipo);
                        }
                    }
                    fr.close();
                    br.close();
                }
                ObservableList oll = FXCollections.observableArrayList(campos);
                listviewAtributos.setItems(oll);
            }
        }

    }
    public void listviewAtributosAction(MouseEvent mouseEvent) {
        //Analizar si es tipo enum o cualquier otro tipo.
        //En funcion de eso, habilitar un boton u otro

        String atributo = (String) listviewAtributos.getSelectionModel().getSelectedItem();
        String[] tokens = atributo.split(";");
        // nombreAtributo ; tipoAtributo
        //String nombreAtributo = tokens[0];
        String tipoAtributo = tokens[1];

        if(tipoAtributo.equals("alt")){
            //Tipo atributo, disable key
            btnAtributoPrimario.setDisable(true);
            btnEnumerado.setDisable(false);
        }else{
            //Otro tipo, disable enum
            btnAtributoPrimario.setDisable(false);
            btnEnumerado.setDisable(true);
        }
    }

    public void btnAtributoPrimarioAction(ActionEvent event) {

        //Obtenemos valores de las clases para formar el par
        String clase = (String)listviewClases.getSelectionModel().getSelectedItem();
        clase = clase.replaceAll(".csv", "");
        String atributo = (String)listviewAtributos.getSelectionModel().getSelectedItem();
        String[] tokens = atributo.split(";");
        String nombreAtributo = tokens[0];

        //Metemos el par clave-valor [clase - atributo]
        keyFlds.put(clase, nombreAtributo);

    }

    public void btnEnumeradoAction(ActionEvent event) {
        int index = listviewClases.getSelectionModel().getSelectedIndex();
        //Datos del atributo
        String clase = (String)listviewClases.getSelectionModel().getSelectedItem();
        String atributo = (String)listviewAtributos.getSelectionModel().getSelectedItem();
        String[] tokens = atributo.split(";");
        String nombreAtributo = tokens[0];

        //Formatear el input del usuario
        Pair<String, String> resultado = preguntarEnum();
        String values = resultado.getKey();
        String literales = resultado.getValue();
        String format = "VALUES(" + values+"),ENUMS(" + literales+")))";

        //Obtenemso el mapa asociado a esa clase
        Map<String, String> mapa = this.enumerados.get(index);
        mapa.put(nombreAtributo, format);

    }
    public void generarDSL(ActionEvent event) {
        //Comprobar que todos los keyfields fueron introducidos
        if(keyFlds.size() == listviewClases.getItems().size()){
            DslGenerator dslGen = new DslGenerator(keyFlds, enumerados);
        }else{
            //mostrar alerta
            mostrarAlertaClave(event);
        }

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
}
