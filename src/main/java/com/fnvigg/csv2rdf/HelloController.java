package com.fnvigg.csv2rdf;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController {
    //Controller para la pantalla de bienvenida
    private Stage stage;
    private Scene scene;
    private Parent root;

    public Button loginBtn;
    public TextField userFld;

    public Button newUserBtn;
    public Label prueba;
    public PasswordField passFld;
    public Label errLbl;

    @FXML
    protected void loginBtnButtonClick(ActionEvent event) throws IOException {
        String user = userFld.getText();
        String pass = passFld.getText();

        boolean validado = DatabaseH2.getUser(user, pass);

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
        //new FXMLLoader(HelloApplication.class.getResource("/views/hello-view.fxml"));
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("/views/registrarUsuario.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void transicionar(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("/views/proyectos.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}