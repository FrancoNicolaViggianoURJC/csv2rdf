package com.fnvigg.csv2rdf;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistrarUsuarioController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private Gestor_usuarios usuarios = new Gestor_usuarios();

    public Label errContraseñas;
    public Button registrarBtn;
    public Button volverBtn;
    public TextField userFld;
    public TextField passFld1;
    public TextField passFld2;

    @FXML
    private ChoiceBox<String> rolCheckBox = new ChoiceBox<String>();

    @FXML
    private boolean mostrarAlerta(ActionEvent event){
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmacion de acción");
        alerta.setContentText("¿Estas seguro de querer volver?");
        Optional<ButtonType> resultado = alerta.showAndWait();

        if(resultado.isEmpty()){
            System.out.println("Confirmacion cerrada");
            return false;
        }else if(resultado.get() == ButtonType.OK){
            System.out.println("Volviendo a la pantalla anterior");
            return true;
        }else if(resultado.get() == ButtonType.CANCEL){
            System.out.println("No hay cambios");
            return false;
        }
        return false;
    }

    @FXML
    private boolean mostrarAlertaBlank(){
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Aviso");
        alerta.setContentText("Los campos no deben estar vacios");
        Optional<ButtonType> resultado = alerta.showAndWait();

        return false;
    }

    private final String[] roles = {"Ingeniero de datos","Ingeniero Ontológico"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.rolCheckBox.getItems().addAll(roles);
    }

    public void onClickRegistrarBtn(ActionEvent event) throws IOException {
        boolean exito = false;
        //Comprobar que ambas pass sean iguales, si error, poner el label errContraseñas visible
        String user = userFld.getText();
        String pass1 = passFld1.getText();
        String pass2 = passFld2.getText();
        String rol = rolCheckBox.getValue();
        if (!user.isBlank() && !pass1.isBlank() && rol.equals("")) {
            if(pass1.equals(pass2)){
                errContraseñas.setVisible(false);
                //llamar gestion usuarios
                exito = usuarios.registrarUsuario(user, pass1, rol);
                if(exito){
                    volver(event);
                }else{

                }
            }else{
                errContraseñas.setVisible(true);
            }
        }else{
            mostrarAlertaBlank();
        }

    }

    public void onClickVolverBtn(ActionEvent event) throws IOException {

        //pedir confirmacion
        if(mostrarAlerta(event)){
            //transicionar pantalla anterior
            volver(event);
        }
    }

    private void volver(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
