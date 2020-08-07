/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Util.HibernateUtil;
import entity.User;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ManageUserController {
    
    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> name;
    
    @FXML
    private TableColumn<User, String> username;

    @FXML
    private TableColumn<User, String> email;

    @FXML
    private TableColumn<User, String> status;
    
    @FXML
    private TableColumn<User, Button> action;
    
    @FXML
    private ComboBox<String> choose;
    

    @FXML
    private TextField searchText;
    
    List<User> userList = new ArrayList<User>();
    



    

    
    @FXML
    public void initialize(){
        //set up search
        choose.getItems().add("Tên");
        choose.getItems().add("Email");
        choose.getSelectionModel().select("Tên");
        
        
        
        //set up columns
        name.setCellValueFactory(new PropertyValueFactory<User, String>("ten"));
        username.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        email.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        status.setCellValueFactory(new PropertyValueFactory<User, String>("trangThai"));
        action.setCellValueFactory(new PropertyValueFactory<User, Button>("button"));
        
        //load data
        tableView.setItems(getUser());
        




        

    }
    
    public ObservableList<User> getUser(){
        ObservableList<User> userListObservableList = FXCollections.observableArrayList();
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        session.beginTransaction();
        String hql = "from User where isAdmin=0";
        List<Object> result = session.createQuery(hql).list();
        for(Object a:result){
            User user = (User)a;
            user.setTrangThai();
            if(user.getIsBan() == 1){
                user.setButton(new Button("Bỏ chặn"));
            }
            else
                user.setButton(new Button("Chặn"));
            user.getButton().setOnAction((event) -> {
                user.setIsBan(Math.abs(user.getIsBan() - 1));
                User user1 = new User(user.getIdUser(), user.getTen(),user.getUsername(),user.getPassword(),user.getEmail(), 0, user.getIsBan());
                
                session.update(user);
                session.getTransaction().commit();
                session.close();
                initialize();
            });
            userListObservableList.add(user);
            userList.add(user);
        }
        return userListObservableList;
    }
    
    public void search(ActionEvent event){
        ObservableList<User> userListObservableList = FXCollections.observableArrayList();
        if(choose.getSelectionModel().getSelectedItem() == "Tên"){
            for(User a: userList){
                if(a.getTen().contains(searchText.getText().toString())){
                    userListObservableList.add(a);
                }
            }
        }
        else if(choose.getSelectionModel().getSelectedItem() == "Email"){
            for(User a: userList){
                if(a.getEmail().contains(searchText.getText().toString())){
                    userListObservableList.add(a);
                }
            }
        }
        tableView.setItems(userListObservableList);
        
    }
    
    

}
