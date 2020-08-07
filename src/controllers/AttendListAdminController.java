/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entity.DsHoiNghi;
import entity.ThamGiaHn;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author Crepp
 */
public class AttendListAdminController {
    @FXML
    private TableView<ThamGiaHn> tableView;
    
    @FXML
    private TableColumn<ThamGiaHn, String> confName;

    @FXML
    private TableColumn<ThamGiaHn, String> username;

    @FXML
    private TableColumn<ThamGiaHn, Button> action;
    
    private DsHoiNghi conf;
    
    @FXML
    public void initialize(){

        confName.setCellValueFactory(new PropertyValueFactory<ThamGiaHn, String>("tenHoiNghi"));
        username.setCellValueFactory(new PropertyValueFactory<ThamGiaHn, String>("username"));
        action.setCellValueFactory(new PropertyValueFactory<ThamGiaHn, Button>("button"));
        
    }
    
    public ObservableList<ThamGiaHn> getAttend(){
        ObservableList<ThamGiaHn> attempListObservableList = FXCollections.observableArrayList();
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        session.beginTransaction();
        String hql = "from ThamGiaHn where id_hoi_nghi = "+ conf.getIdHoiNghi()+" and trang_thai = 'pending'";
        List<Object> result = session.createQuery(hql).list();
        session.clear();
        session.close();
        
        Session session1 = factory.openSession();
        session1.beginTransaction();
        
        
        for(Object a:result){
            ThamGiaHn join = (ThamGiaHn)a;
            join.setTenHoiNghi(join.getDsHoiNghi().getTenHoiNghi());
            join.setUsername(join.getUser().getUsername());
            join.setButton();
            join.getButton().setOnMouseClicked((event) -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Xác nhận");
                alert.setHeaderText(null);
                alert.setContentText("Chấp nhận cho người dùng tham gia hội nghị?");
                Optional<ButtonType> action = alert.showAndWait();
            
            if(action.get() == ButtonType.OK){
                
                

                ThamGiaHn nAttend = new ThamGiaHn(join.getId(),join.getDsHoiNghi(),join.getUser(),"accepted");
                session1.update(nAttend);
                session1.getTransaction().commit();
                session1.close();     
            }
            else if(action.get() == ButtonType.CANCEL){
                ThamGiaHn nAttend = new ThamGiaHn(join.getId(), join.getDsHoiNghi(),join.getUser(),"declined");
                session1.update(nAttend);
                session1.getTransaction().commit();
                session1.close();
            }
                try {
                    initConf(this.conf);
                } catch (IOException ex) {
                    Logger.getLogger(AttendListAdminController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            attempListObservableList.add(join);
        }
        return attempListObservableList;
    }
    
    public void initConf(DsHoiNghi conf) throws IOException{
        System.out.println(conf.getTenHoiNghi());
        this.conf = new DsHoiNghi(conf.getIdHoiNghi(), conf.getDiaDiem(), conf.getTenHoiNghi(), conf.getMoTa(),conf.getMoTaChiTiet(),conf.getThoiGian(), conf.getThoiGianKt(),conf.getHinhAnh());

        //load data
        tableView.setItems(getAttend());
    }
    
    public void back(ActionEvent event){
        MainController.deleteChildrenNode();
    }
}
