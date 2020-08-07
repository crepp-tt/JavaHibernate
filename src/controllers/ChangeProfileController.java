/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Util.HibernateUtil;
import Util.RSAEncryption;
import Util.UserSession;
import entity.User;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.Session;

/**
 * FXML Controller class
 *
 * @author Crepp
 */
public class ChangeProfileController {
    
    @FXML
    private TextField name;

    @FXML
    private TextField email;
    
    @FXML
    private Button btnChange;
    
    @FXML
    private PasswordField newPass1;

    @FXML
    private PasswordField newPass2;

    private static final String regex = "^(.+)@(.+)$";
    private RSAEncryption rsa;
    
    PrivateKey privateKey;
    
    PublicKey publicKey;

    @FXML
    public void initialize() {
        name.setText(UserSession.getInstace().getUser().getTen());
        email.setText(UserSession.getInstace().getUser().getEmail());
        try {
            rsa = new RSAEncryption();
            privateKey = rsa.getPrivate("Key/privateKey");
            publicKey = rsa.getPublic("Key/publicKey");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void change(ActionEvent event) throws Exception{
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email.getText());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thay đổi thông tin thất bại");
        alert.setHeaderText(null);
        if(matcher.matches() == false){
            alert.setContentText("Email không hợp lệ!");
            alert.showAndWait();
        }
        else{
            if(!newPass1.getText().equals(newPass2.getText())){
                alert.setContentText("Mật khẩu nhập lại không đúng!");
                alert.showAndWait();
            }
            else{
                String password = rsa.encrypt(newPass1.getText(), privateKey);
                Session session = HibernateUtil.getSessionFactory().openSession();
                User user = new User(UserSession.getInstace().getUser().getIdUser(), name.getText(),UserSession.getInstace().getUser().getUsername(), password, email.getText(), UserSession.getInstace().getUser().getIsAdmin(), 0);
                session.beginTransaction();
                session.update(user);
                session.getTransaction().commit();
                session.close();
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Thay đổi thông tin thành công");
                a.setHeaderText(null);
                a.setContentText("Đã thay đổi thông tin!");
                a.showAndWait();
                UserSession.getInstace().cleanUserSession();
                UserSession.setInstace(user);
                Stage stage = (Stage)btnChange.getScene().getWindow();
                stage.close();
            }
            
        }
    }
    
}
