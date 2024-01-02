package com.fnvigg.csv2rdf;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    public Button loginBtn;
    public TextField userFld;

    public Button newUserBtn;
    public Label prueba;
    public PasswordField passFld;
    public Label errLbl;
    //Controller para la pantalla de bienvenida
    private Gestor_usuarios users = new Gestor_usuarios();

    @FXML
    protected void loginBtnButtonClick(ActionEvent event) throws IOException {
        String user = userFld.getText();
        String pass = passFld.getText();
        boolean validado = false;
        //Crear la java class gestor_usuarios pasandole como argumento el user y el pass
        validado = users.validar_Usuario(user, pass);
        //if validado ... transicionar a la siguiente
        //else repeat
        if (validado){
            errLbl.setVisible(false);
            //transicionar a pantalla inicial de proyectos
            transicionar(event);
        }else{
            //repetir login
            errLbl.setVisible(true);
        }

    }

    public void newBtnButtonClick(ActionEvent event) throws IOException {
        //switch screen
        escenaRegistroUsuario(event);
    }

    //Metodo general de cambio de escenas
    public void escenaRegistroUsuario(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("registrarUsuario.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void transicionar(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("proyectos.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}