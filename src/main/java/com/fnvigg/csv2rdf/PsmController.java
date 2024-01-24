package com.fnvigg.csv2rdf;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PsmController implements Initializable {
    public ImageView imageEsquemaEjemplo;
    public Accordion accordion;
    public ImageView imageAyudasOnt;
    public Label labelAyudaOnt;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private String nombreProyecto = AtributosSesion.getNombreProyecto();
    private Gestor_proyectos proyectos = new Gestor_proyectos();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nombreProyecto = AtributosSesion.getNombreProyecto();
        accordion.setExpandedPane(accordion.getPanes().get(0));
        
        //Actualizar la fase en el setting

        String faseUltima = proyectos.obtenerFase(nombreProyecto);
        if(!faseUltima.equals("DSL")){
            proyectos.setFase(nombreProyecto, "PSM");
        }

        //Carga de la imagen
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/esquemaOnt.png";
        File f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            Image img = new Image(ruta);
            imageEsquemaEjemplo.setImage(img);
        }

    }
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

    public void btnClasesOnt(ActionEvent event) {
        //Cargamos el texto y la imagen correspondiente
        String frase = "Una clase UML se transformará en una clase OWL.";
        labelAyudaOnt.setText(frase);
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/clasesOnt.png";

        //Comprobamos la existencia de la imagen
        File f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            Image img = new Image(ruta);
            imageAyudasOnt.setImage(img);
        }

    }

    public void btnAtributosAction(ActionEvent event) {
        //Cargamos el texto y la imagen correspondiente
        String frase = "Un atributo UML definido por un tipo basico de datos (string, char, integer, float, boolean or date) será\n" +
                "transformado en un OWL Datatype Property cuyo dominio será la correspondiente clase OWL y cuyo\n" +
                "rango será la clase RDFS Datatype asociada con el tipo de datos básico.";
        labelAyudaOnt.setText(frase);
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/atributosOnt.png";

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
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/clasesOnt.png";

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
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/clasesOnt.png";

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
        String ruta = System.getProperty("user.dir") + "/src/main/resources/Proyectos/" + nombreProyecto + "/clasesOnt.png";

        //Comprobamos la existencia de la imagen
        File f = new File(ruta);
        if(f.exists() && !f.isDirectory()){
            Image img = new Image(ruta);
            imageAyudasOnt.setImage(img);
        }
    }
}
