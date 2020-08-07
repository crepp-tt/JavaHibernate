package controllers;

import Util.UserSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 *
 * @author Crepp
 */
public class MainController  {
    
    static StackPane stackPaneStatic;
    
    @FXML
    private StackPane stack_pane;
    
    
    @FXML
    private Button btnSignIn;
    static Button btnSignInStatic;
    
    @FXML
    private Button btnProfile;
    static Button btnProfileStatic;
    
    @FXML
    private Button btnLogout;
    static Button btnLogoutStatic;
    
    @FXML
    private Button btnManageConfUser;
    static Button btnManageConfUserStatic;
    
    @FXML
    private Button btnManageConfAdmin;
    static Button btnManageConfAdminStatic;
    
    @FXML
    private Button btnManageUser;
    static Button btnManageUserStatic;


    
    
    //load conf list in stackpane
    @FXML
    public void initialize() {
        stackPaneStatic = stack_pane;
        btnLogoutStatic = btnLogout;
        btnManageConfAdminStatic = btnManageConfAdmin;
        btnManageConfUserStatic = btnManageConfUser;
        btnProfileStatic = btnProfile;
        btnSignInStatic = btnSignIn;
        btnManageUserStatic = btnManageUser;
        
        btnProfile.setVisible(false);
        btnLogout.setVisible(false);
        btnManageConfUser.setVisible(false);
        btnManageConfAdmin.setVisible(false);
        btnSignIn.setVisible(true);
        btnManageUser.setVisible(false);
        
        
        AnchorPane hoi_nghi_scene = null;
        try {
            hoi_nghi_scene = FXMLLoader.load(getClass().getResource("/fxml/ConferenceList.fxml"));
            stack_pane.getChildren().add(hoi_nghi_scene);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //reload main scene
    public static void reload(){
        if(UserSession.getInstace() == null){
            btnProfileStatic.setVisible(false);
            btnSignInStatic.setVisible(true);
            btnLogoutStatic.setVisible(false);
            btnManageConfUserStatic.setVisible(false);
            btnManageConfAdminStatic.setVisible(false);
        }
        else{
            btnProfileStatic.setVisible(true);
            btnSignInStatic.setVisible(false);
            btnLogoutStatic.setVisible(true);
            if(UserSession.getInstace().getUser().getIsAdmin()== 1){
                btnManageConfAdminStatic.setVisible(true);
                btnManageUserStatic.setVisible(true);
            }
            else if(UserSession.getInstace().getUser().getIsAdmin() == 0){
                btnManageConfUserStatic.setVisible(true);
            }
        }
        

    }
    
    //add child to stackpane
    public static void addChildrenNode(Parent Node){
        try {
            stackPaneStatic.getChildren().add(Node);

        } catch (Exception e) {
            System.out.println(e);
        }
        
    }
    
    //delete last child in stackpane
    public static void deleteChildrenNode(){
        int size = stackPaneStatic.getChildren().size();
        try {
            stackPaneStatic.getChildren().remove(size - 1);

        } catch (Exception e) {
            System.out.println(e);

        }
        
    }
    

    
    //add action on btnListConf
    public void listConf(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ConferenceList.fxml"));
        Parent listConfParent = loader.load();
        MainController.deleteChildrenNode();
        MainController.addChildrenNode(listConfParent);
    }
    
    //add action on btnLogin
    public void login(ActionEvent event) throws IOException{
        Dialog<?> loginDialog = new Dialog<>();
        loginDialog.initOwner(stack_pane.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/login.fxml"));

        try {
            loginDialog.getDialogPane().setContent(loader.load());
            loginDialog.initStyle(StageStyle.DECORATED);
            loginDialog.setResizable(false);
            loginDialog.getDialogPane().setPrefSize(600, 400);
        } catch (Exception e) {
            System.out.println(e);
        }

        Window window = loginDialog.getDialogPane().getScene().getWindow();

        
        window.setOnCloseRequest(event1->{
            System.out.println("close");
        });
        loginDialog.showAndWait();
        reload();
    }
    
    //add action on btnLogout
    public void logout(ActionEvent event){
        MainController.deleteChildrenNode();
        UserSession.getInstace().cleanUserSession();
        initialize();
    }
    
    public void manageConfUser(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ManageConfUser.fxml"));
        Parent listConfParent = loader.load();
        MainController.deleteChildrenNode();
        MainController.addChildrenNode(listConfParent);
    }
    
    public void getProfile(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/Profile.fxml"));
        Parent listConfParent = loader.load();
        MainController.deleteChildrenNode();
        MainController.addChildrenNode(listConfParent);
    }
    
    public void manageConfAdmin(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ManageConfAdmin.fxml"));
        Parent listConfParent = loader.load();
        MainController.deleteChildrenNode();
        MainController.addChildrenNode(listConfParent);
    }
    
    public void manageUser(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ManageUser.fxml"));
        Parent listConfParent = loader.load();
        MainController.deleteChildrenNode();
        MainController.addChildrenNode(listConfParent);
    }
    
    public void end(ActionEvent event){
        Stage stage = (Stage)btnLogout.getScene().getWindow();
        stage.close();
    }
}
