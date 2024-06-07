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
    public Button btnSiguienteFase;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private Gestor_proyectos proyectos = new Gestor_proyectos();
    private String idProyecto;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idProyecto = AtributosSesion.getIdProyecto();
        accordion.setExpandedPane(accordion.getPanes().get(0));

        //Update de la BBDD con la fase en la que estamos
        String fase = DatabaseH2.getProyectosFase(idProyecto);
        if(!fase.equals("PSM") && !fase.equals("DSL")){
            DatabaseH2.updateProyectosFase("PIM", idProyecto);
        }

        //Deshabilitar el boton hasta que el usuario suba el MDO
        btnSiguienteFase.setDisable(true);

        //Cargar el MDO si lo hubiera
        String ruta = "./Proyectos/" + idProyecto + "/MDO.png";
        File f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            Image img = new Image(f.getAbsolutePath());
            imageMDO.setImage(img);
            btnSiguienteFase.setDisable(false);
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
            root = FXMLLoader.load(HelloApplication.class.getResource("/views/faseCim.fxml"));
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
        String texto = " - Las clases y sus nombres se obtendrán de los contenedores representados en el esquema gráfico.";

        Image img = new Image(HelloApplication.class.getResource("/clases.png").toString());
        lblTexto.setText(texto);
        imageAyuda.setImage(img);
    }

    public void btnAsociacionesAction(ActionEvent event) {
        String texto = " - Las asociaciones entre clases se derivarán de las relaciones entre contenedores representados en el esquema gráfico y la información recolectada en \n" +
                "   los requerimientos de los datos.\n" +
                " - Cualquier relación en el esquema gráfico implicará la inclusión de un atributo en la clase referenciada cuyo tipo de dato será el nombre de la clase \n" +
                "   referenciada en el modelo de dominio objetivo.";

        Image img = new Image(HelloApplication.class.getResource("/asociaciones.png").toString());
        lblTexto.setText(texto);
        imageAyuda.setImage(img);
    }

    public void btnAtributosAction(ActionEvent event) {
        String texto = " - Los atributos de las clases se obtendrán a partir del análisis de los valores en su dominio de datos.\n" +
                " - Los atributos de una clase pueden ser definidos usando datatypes básicos como string, char, integer, float, boolean or date.";

        Image img = new Image(HelloApplication.class.getResource("/atributos.png").toString());
        lblTexto.setText(texto);
        imageAyuda.setImage(img);
    }

    public void btnEnumeradosAction(ActionEvent event) {
        String texto = " - Los atributos con un valor numérico usado para representar valores (1- ocio, 2- trabajo, 3-otros) se definirán usando el tipo enum.\n" +
                " - Los valores tendrán que ser anotados en el esquema por medio de comentarios.";

        Image img = new Image(HelloApplication.class.getResource("/enumerados.png").toString());
        lblTexto.setText(texto);
        imageAyuda.setImage(img);
    }
    public void btnRutasAction(ActionEvent event) {
        String texto = " - Se van a definir también rutas para aquellos atributos que sean de interés, pero que su contenedor no sea de interés.\n" +
                " - Estas rutas se definen por comentarios de la siguiente manera: el atributo de interés, en la misma caja, un atributo clave que se refiera a otro\n" +
                "   contenedor que será conectado por una linea";

        Image img = new Image(HelloApplication.class.getResource("/ruta.png").toString());
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
            if (ficheroSeleccionado.getName().endsWith(".png") || ficheroSeleccionado.getName().endsWith(".PNG")) {

                proyectos.guardarMDO(ficheroSeleccionado, idProyecto);

                String ruta = "./Proyectos/" + idProyecto + "/MDO.PNG";
                File f = new File(ruta);
                System.out.println(ruta);
                Image img = new Image(f.getAbsolutePath());

                DatabaseH2.insertPimMDO(ruta, idProyecto);

                imageMDO.setImage(img);
                btnSiguienteFase.setDisable(false);
            } else {
                //Mostrar alerta tipo
                mostrarAlertaImagen(event);
            }
        }
    }

    public void btnSiguienteFaseAction(ActionEvent event) {
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
