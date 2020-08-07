/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Util.RSAEncryption;
import Util.UserSession;
import entity.User;
import java.net.URL;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;



/**
 * FXML Controller class
 *
 * @author Crepp
 */
public class LoginController implements Initializable {

    @FXML
    private Pane signupScene;
    @FXML
    private TextField usernameSignUp;
    @FXML
    private TextField email;
    @FXML
    private Button btnSignUp;
    @FXML 
    private PasswordField password1;
    @FXML 
    private PasswordField password2;
    @FXML
    private TextField name;
    
    
    @FXML
    private Pane signinScene;
    @FXML
    private TextField usernameSignIn;
    @FXML
    private PasswordField passwordSignIn;
    @FXML
    private Button btnSignIn;
    
    private RSAEncryption rsa;
    
    private static final String regex = "^(.+)@(.+)$";
    
    PrivateKey privateKey;
    
    PublicKey publicKey;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            rsa = new RSAEncryption();
            privateKey = rsa.getPrivate("Key/privateKey");
            publicKey = rsa.getPublic("Key/publicKey");
        } catch (Exception e) {
            System.out.println(e);
        }
    }    
    
    //change to sign up scene
    public void getSignUp(ActionEvent event) {
        usernameSignIn.setText("");
        passwordSignIn.setText("");
        signupScene.toFront();
    }
    
    //change to sign in scene
    public void getSignIn(ActionEvent event){
        usernameSignUp.setText("");
        email.setText("");
        password1.setText("");
        password2.setText("");
        signupScene.toBack();
    }
    
    
    
    public void signIn(ActionEvent event) throws Exception{
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Lỗi đăng nhập");
        a.setHeaderText("Đăng nhập thất bại");
        if(usernameSignIn.getText().trim().isEmpty() || passwordSignIn.getText().trim().isEmpty()){
            a.setContentText("Nhập đầy đủ thông tin yêu cầu!");
            a.showAndWait();
            return;
        }
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        session.beginTransaction();
        List result = session.createQuery("FROM User  WHERE username = '" + usernameSignIn.getText()+"'").list();
        Iterator it = result.iterator();
        
        if(it.hasNext()){
                Object o = (Object)it.next();
                User user = (User)o;

                if(passwordSignIn.getText().equals(rsa.decrypt(user.getPassword(), publicKey)) ){
                    if(user.getIsBan() == 1){
                        a.setContentText("Tài khoản đã bị chặn!");
                        a.showAndWait();
                    }
                    else{
                        UserSession.setInstace(user);
                        Stage stage = (Stage)btnSignIn.getScene().getWindow();
                        stage.close();
                    }
                    
                }
                else{
                    a.setContentText("Mật khẩu hoặc tên đăng nhập không đúng!");
                    a.showAndWait();
                }
            }
        else{
            a.setContentText("Mật khẩu hoặc tên đăng nhập không đúng!");
            a.showAndWait();
        }
    }
    
    public void signUp(ActionEvent event) throws Exception{
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Lỗi đăng ký");
        a.setHeaderText("Đăng ký thất bại");
        if(usernameSignUp.getText().trim().isEmpty() || password1.getText().trim().isEmpty() || password2.getText().trim().isEmpty() || email.getText().trim().isEmpty()){
            a.setContentText("Nhập đầy đủ thông tin yêu cầu!");
            a.showAndWait();
            return;
        }
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        session.beginTransaction();
        List result = session.createQuery("FROM User  WHERE username = '" + usernameSignUp.getText()+"'").list();
        Iterator it = result.iterator();
        
        if(it.hasNext()){
            a.setContentText("Tên đăng nhập đã tồn tại!");
            a.showAndWait();
            return;
        }
        else{
            result = session.createQuery("FROM User  WHERE email = '" + email.getText()+"'").list();
            it = result.iterator();
            if(it.hasNext()){
                a.setContentText("Email đã tồn tại!");
                a.showAndWait();
                return;
            }
            else{
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(email.getText());
                if(matcher.matches() == false){
                    a.setContentText("Email không hợp lệ!");
                    a.showAndWait();
                    return;
                }
                if(!password1.getText().equals(password2.getText())){
                    a.setContentText("Mật khẩu nhập lại không đúng!");
                    a.showAndWait();
                    return;
                }
                else{

                    String password = rsa.encrypt(password1.getText(), privateKey);
                    User nUser = new User(name.getText(), usernameSignUp.getText(), password, email.getText(), 0, 0);
                    try {
                        session.save(nUser);
                        session.getTransaction().commit();
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Đăng ký thành công");
                        alert.setHeaderText(null);
                        alert.setContentText("Đăng ký thành công tài khoản " + usernameSignUp.getText() + "!");
                        alert.showAndWait();
                    } catch (Exception e) {
                        System.out.println(e);
                    }finally{
                        session.close();
                    }
                    signupScene.toBack();
                }
            }
        }
    }
}
