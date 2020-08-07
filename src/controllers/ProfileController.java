/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Util.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * FXML Controller class
 *
 * @author Crepp
 */
public class ProfileController  {
    
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label username;

    @FXML
    private Label name;

    @FXML
    private Label email;
    
    
    
    @FXML
    public void initialize() {
        username.setText(UserSession.getInstace().getUser().getUsername());
        name.setText(UserSession.getInstace().getUser().getTen());
        email.setText(UserSession.getInstace().getUser().getEmail());
    }  
    
    public void changeProfile(ActionEvent event){
        Dialog<?> changeProfileDialog = new Dialog<>();
        changeProfileDialog.initOwner(anchorPane.getScene().getWindow());
        FXMLLoader fXMLLoader = new FXMLLoader();
        fXMLLoader.setLocation(getClass().getResource("/fxml/ChangeProfile.fxml"));

        try {
            changeProfileDialog.getDialogPane().setContent(fXMLLoader.load());
            changeProfileDialog.initStyle(StageStyle.DECORATED);
            changeProfileDialog.setResizable(false);
            changeProfileDialog.getDialogPane().setPrefSize(600, 400);
        } catch (Exception e) {
            System.out.println(e);
        }

        Window window = changeProfileDialog.getDialogPane().getScene().getWindow();
        

        changeProfileDialog.showAndWait();
        window.setOnCloseRequest(event1->{
            System.out.println("close");
        });
        MainController.reload();
        initialize();

    }
    

    
}
