/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entity.DsHoiNghi;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ManageConfAdminController {
    
    @FXML
    private TableView<DsHoiNghi> tableView;
    static TableView<DsHoiNghi> tableViewStatic;
    @FXML
    private TableColumn<DsHoiNghi, String> name;

    @FXML
    private TableColumn<DsHoiNghi, String> address;

    @FXML
    private TableColumn<DsHoiNghi, Date> startTime;

    @FXML
    private TableColumn<DsHoiNghi, Date> endTime;


    

    
    @FXML
    public void initialize(){
        
        tableViewStatic = tableView;
        //create context menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("Chi tiết");
        item1.setOnAction((event) -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ConferenceDetail.fxml"));
            
            try {
                Parent detailParent;
                detailParent = loader.load();
                ConfDetailController controller = loader.getController();
                controller.initConf(tableView.getSelectionModel().getSelectedItem());
                MainController.addChildrenNode(detailParent);
            } catch (IOException ex) {
                Logger.getLogger(ConfListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        MenuItem item2 = new MenuItem("Sửa");
        item2.setOnAction((event) -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ChangeConf.fxml"));
            
            try {
                Parent detailParent;
                detailParent = loader.load();
                ChangeConfController controller = loader.getController();
                controller.initConf(tableView.getSelectionModel().getSelectedItem());
                MainController.addChildrenNode(detailParent);
            } catch (IOException ex) {
                Logger.getLogger(ConfListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        MenuItem item3 = new MenuItem("Xoá");
        item3.setOnAction((event) -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Xác nhận");
            alert.setHeaderText(null);
            alert.setContentText("Bạn muốn xoá hội nghị này?");
            
            Optional<ButtonType> action = alert.showAndWait();
            
            if(action.get() == ButtonType.OK){
                Configuration cfg = new Configuration();
                cfg.configure("hibernate.cfg.xml");
                SessionFactory factory = cfg.buildSessionFactory();
                Session session = factory.openSession();
                session.beginTransaction();
                Query query = session.createQuery("delete from DsHoiNghi where id_hoi_nghi = " + tableView.getSelectionModel().getSelectedItem().getIdHoiNghi());
                query.executeUpdate();
                session.getTransaction().commit();
                session.clear();
                session.close();
                initialize();
            }
        });
        
        MenuItem item4 = new MenuItem("Yêu cầu tham dự");
        item4.setOnAction((event) -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/AttendListAdmin.fxml"));
            
            try {
                Parent detailParent;
                detailParent = loader.load();
                AttendListAdminController controller = loader.getController();
                controller.initConf(tableView.getSelectionModel().getSelectedItem());
                MainController.addChildrenNode(detailParent);
            } catch (IOException ex) {
                Logger.getLogger(ConfListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        contextMenu.getItems().addAll(item1, item2, item3, item4);
        tableView.setContextMenu(contextMenu);
        
        
        

                
        //set up columns
        name.setCellValueFactory(new PropertyValueFactory<DsHoiNghi, String>("tenHoiNghi"));
        address.setCellValueFactory(new PropertyValueFactory<DsHoiNghi, String>("tenDiaDiem"));
        startTime.setCellValueFactory(new PropertyValueFactory<DsHoiNghi, Date>("thoiGian"));
        endTime.setCellValueFactory(new PropertyValueFactory<DsHoiNghi, Date>("thoiGianKt"));
        
        //load data
        tableView.setItems(getConf());
    }
    
    public ObservableList<DsHoiNghi> getConf(){
        ObservableList<DsHoiNghi> confListObservableList = FXCollections.observableArrayList();
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        session.beginTransaction();
        String hql = "from DsHoiNghi";
        List<Object> result = session.createQuery(hql).list();
        for(Object a:result){
            DsHoiNghi conf = (DsHoiNghi)a;
            conf.setTenDiaDiem(conf.getDiaDiem().getTen());
            confListObservableList.add(conf);
        }
        return confListObservableList;
    }
    
    public static ObservableList<DsHoiNghi> getConfStatic(){
        ObservableList<DsHoiNghi> confListObservableList = FXCollections.observableArrayList();
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        session.beginTransaction();
        String hql = "from DsHoiNghi";
        List<Object> result = session.createQuery(hql).list();
        for(Object a:result){
            DsHoiNghi conf = (DsHoiNghi)a;
            conf.setTenDiaDiem(conf.getDiaDiem().getTen());
            confListObservableList.add(conf);
        }
        return confListObservableList;
    }
    
    //add action on add conf button
    public void addConf(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/AddConference.fxml"));
        Parent addConfParent = loader.load();
        MainController.addChildrenNode(addConfParent);
    }
    
    public static void reload(){
        tableViewStatic.setItems(getConfStatic());
    }

}
