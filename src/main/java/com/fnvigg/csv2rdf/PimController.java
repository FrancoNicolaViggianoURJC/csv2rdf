package com.fnvigg.csv2rdf;

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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PimController implements Initializable {

    public Button btnVolver;
    public Accordion accordion;
    public Label lblTexto;
    public ImageView imageAyuda;
    public ImageView imageMDO;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private Gestor_proyectos proyectos = new Gestor_proyectos();
    private String nombreProyecto;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nombreProyecto = AtributosSesion.getNombreProyecto();
        accordion.setExpandedPane(accordion.getPanes().get(0));

        //Hay que escribir en el archivo de config la fase en la que estamos
        String faseUltima = proyectos.obtenerFase(nombreProyecto);
        if(!faseUltima.equals("PSM") && !faseUltima.equals("DSL")){
            proyectos.setFase(nombreProyecto, "PIM");
        }

        //Cargar el MDO si lo hubiera
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/MDO.png";
        File f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            Image img = new Image(ruta);
            imageMDO.setImage(img);
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

    // 1 Panel
    public void btnVolverAction(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("faseCim.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 2 Panel
    public void btnClasesAction(ActionEvent event) {
        String texto = "- Las clases y sus nombres se obtendrán de los contenedores representados en el esquema gráfico.";
        String ruta = System.getProperty("user.dir");
        Image img = new Image(ruta + "/src/main/resources/clases.png");

        lblTexto.setText(texto);
        imageAyuda.setImage(img);
    }

    public void btnAsociacionesAction(ActionEvent event) {
        String texto = "- Las asociaciones entre clases se derivarán de las relaciones entre contenedores representados en\n" +
                "   el esquema gráfico y la información recolectada en los requerimientos de los datos.\n" +
                "- Cualquier relación en el esquema gráfico implicará la inclusión de un atributo en la clase referenciada\n" +
                "   cuyo tipo de dato será el nombre de la clase referenciada en el modelo de dominio objetivo.";
        String ruta = System.getProperty("user.dir");
        Image img = new Image(ruta + "/src/main/resources/asociaciones.png");

        lblTexto.setText(texto);
        imageAyuda.setImage(img);
    }

    public void btnAtributosAction(ActionEvent event) {
        String texto = "- Los atributos de las clases se obtendrán a partir del análisis de los valores en su dominio de datos.\n" +
                "    Los atributos de una clase pueden ser definidos usando datatypes básicos como string, char, integer, float, boolean or date.";
        String ruta = System.getProperty("user.dir");
        Image img = new Image(ruta + "/src/main/resources/atributos.png");

        lblTexto.setText(texto);
        imageAyuda.setImage(img);
    }

    public void btnEnumeradosAction(ActionEvent event) {
        String texto = "- Los atributos con un valor numérico usado para representar valores (1- ocio, 2- trabajo, 3-otros) se\n" +
                "   definirán usando el tipo enum. Los valores tendrán que ser anotados en el esquema por medio de\n" +
                "   comentarios.";
        String ruta = System.getProperty("user.dir");
        Image img = new Image(ruta + "/src/main/resources/enumerados.png");

        lblTexto.setText(texto);
        imageAyuda.setImage(img);
    }
    public void btnRutasAction(ActionEvent event) {
        String texto = "- Se van a definir también rutas para aquellos atributos que sean de interés, pero que su contenedor\n" +
                "   no sea de interés. Estas rutas se definen por comentarios de la siguiente manera: el atributo de inte-\n" +
                "   rés, en la misma caja, un atributo clave que se refiera a otro contenedor que será conectado por una\n" +
                "   linea";
        String ruta = System.getProperty("user.dir");
        Image img = new Image(ruta + "/src/main/resources/ruta.png");

        lblTexto.setText(texto);
        imageAyuda.setImage(img);
    }

    //3 Panel

    @FXML
    private FileChooser fileChooser = new FileChooser();

    public void btnCargarImagenAction(ActionEvent event) {
        //Funcion para abrir el explorador de archivos
        File ficheroSeleccionado = fileChooser.showOpenDialog(stage);

        if(ficheroSeleccionado != null) {
            if (ficheroSeleccionado.getName().endsWith(".png")) {
                proyectos.guardarMDO(ficheroSeleccionado, nombreProyecto);

                String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/MDO.png";
                Image img = new Image(ruta);

                imageMDO.setImage(img);
            } else {
                //Mostrar alerta tipo
                mostrarAlertaImagen(event);
            }
        }
    }

    public void btnSiguienteFaseAction(ActionEvent event) {
        try {
            root = FXMLLoader.load(getClass().getResource("fasePsm.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
