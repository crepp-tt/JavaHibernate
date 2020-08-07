/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Util.UserSession;
import entity.DsHoiNghi;
import entity.ThamGiaHn;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ManageConfUserController {
    
    @FXML
    private TableView<DsHoiNghi> tableView;

    @FXML
    private TableColumn<DsHoiNghi, String> name;

    @FXML
    private TableColumn<DsHoiNghi, String> address;

    @FXML
    private TableColumn<DsHoiNghi, Date> startTime;

    @FXML
    private TableColumn<DsHoiNghi, Date> endTime;

    @FXML
    private TableColumn<DsHoiNghi, String> status;
    
    @FXML
    private TableColumn<DsHoiNghi, Button> action;
    

    
    @FXML
    private TextField searchText;
    
    @FXML
    private ComboBox<String> choose;
    
    List<DsHoiNghi> confList = new ArrayList<DsHoiNghi>();
    
    @FXML
    public void initialize(){
               
        
        choose.getItems().add("Tên hội nghị");
        choose.getItems().add("Trạng thái");
        choose.getSelectionModel().select("Tên hội nghị");
                
        //set up columns
        name.setCellValueFactory(new PropertyValueFactory<DsHoiNghi, String>("tenHoiNghi"));
        address.setCellValueFactory(new PropertyValueFactory<DsHoiNghi, String>("tenDiaDiem"));
        startTime.setCellValueFactory(new PropertyValueFactory<DsHoiNghi, Date>("thoiGian"));
        endTime.setCellValueFactory(new PropertyValueFactory<DsHoiNghi, Date>("thoiGianKt"));
        status.setCellValueFactory(new PropertyValueFactory<DsHoiNghi, String>("trangThai"));
        action.setCellValueFactory(new PropertyValueFactory<DsHoiNghi, Button>("button"));
        
        //load data
        tableView.setItems(getConf());
        
        tableView.setOnMouseClicked((event) -> {
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
    }
    
    public ObservableList<DsHoiNghi> getConf(){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        ObservableList<DsHoiNghi> confListObservableList = FXCollections.observableArrayList();
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        session.beginTransaction();
        String hql = "from ThamGiaHn where id_user = "+ UserSession.getInstace().getUser().getIdUser();
        List<Object> result = session.createQuery(hql).list();
        for(Object a:result){
            ThamGiaHn join = (ThamGiaHn)a;
            DsHoiNghi conf = join.getDsHoiNghi();
            conf.setTrangThai(join.getTrangThai());
            conf.setTenDiaDiem(conf.getDiaDiem().getTen());
            conf.setButton();
            if(conf.getThoiGian().after(now)){
                conf.getButton().setDisable(false);
                conf.getButton().setOnAction((event) -> {
                    Query query = session.createQuery("delete from ThamGiaHn where id_hoi_nghi = " + conf.getIdHoiNghi() + " and id_user="+UserSession.getInstace().getUser().getIdUser());
                    query.executeUpdate();
                    session.getTransaction().commit();
                    session.clear();
                    session.close();
                    initialize();
                });
            }
            confListObservableList.add(conf);
            confList.add(conf);
        }
        return confListObservableList;
    }
    
    public void search(ActionEvent event){
        ObservableList<DsHoiNghi> confListObservableList = FXCollections.observableArrayList();
        if(choose.getSelectionModel().getSelectedItem() == "Tên hội nghị"){
            for(DsHoiNghi a: confList){
                if(a.getTenHoiNghi().contains(searchText.getText().toString())){
                    confListObservableList.add(a);
                }
            }
        }
        else if(choose.getSelectionModel().getSelectedItem() == "Trạng thái"){
            for(DsHoiNghi a: confList){
                if(a.getTrangThai().contains(searchText.getText().toString())){
                    confListObservableList.add(a);
                }
            }
        }
        tableView.setItems(confListObservableList);
        
    }

}
