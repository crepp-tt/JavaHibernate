/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import entity.DsHoiNghi;
import entity.ThamGiaHn;
import entity.User;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 *
 * @author Crepp
 */
public class AttendListController {
    @FXML
    private TableView<User> tableView;
    
    @FXML
    private TableColumn<User, Integer> id;

    @FXML
    private TableColumn<User, String> name;

    @FXML
    private TableColumn<User, String> email;
    
    private DsHoiNghi conf;
    
    @FXML
    public void initialize(){

        id.setCellValueFactory(new PropertyValueFactory<User, Integer>("idUser"));
        name.setCellValueFactory(new PropertyValueFactory<User, String>("ten"));
        email.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
        
    }
    
    public ObservableList<User> getUser(){
        ObservableList<User> userListObservableList = FXCollections.observableArrayList();
        Configuration cfg = new Configuration();
        cfg.configure("hibernate.cfg.xml");
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        session.beginTransaction();
        String hql = "from ThamGiaHn where id_hoi_nghi = "+ conf.getIdHoiNghi()+" and trang_thai = 'accepted'";
        List<Object> result = session.createQuery(hql).list();
        for(Object a:result){
            ThamGiaHn join = (ThamGiaHn)a;
            User u = join.getUser();
            userListObservableList.add(u);
        }
        return userListObservableList;
    }
    
    public void initConf(DsHoiNghi conf) throws IOException{
        System.out.println(conf.getTenHoiNghi());
        this.conf = new DsHoiNghi(conf.getIdHoiNghi(), conf.getDiaDiem(), conf.getTenHoiNghi(), conf.getMoTa(),conf.getMoTaChiTiet(),conf.getThoiGian(), conf.getThoiGianKt(),conf.getHinhAnh());

        //load data
        tableView.setItems(getUser());
    }
    
    public void back(ActionEvent event){
        MainController.deleteChildrenNode();
    }
}
