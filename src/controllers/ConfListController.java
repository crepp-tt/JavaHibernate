/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Util.HNCellFactory;
import entity.DsHoiNghi;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * FXML Controller class
 *
 * @author Crepp
 */
public class ConfListController implements Initializable {

    @FXML
    private ListView<DsHoiNghi> confListView; 
    
    private ObservableList<DsHoiNghi> confObservableList;
    
    //get conf list form database
    public ConfListController(){
        confObservableList = FXCollections.observableArrayList();
        try {
            Configuration cfg = new Configuration();
            cfg.configure("hibernate.cfg.xml");
            SessionFactory factory = cfg.buildSessionFactory();
            Session session = factory.openSession();
            session.beginTransaction();
            List result = session.createQuery("from DsHoiNghi").list();
            Iterator it = result.iterator();
            while(it.hasNext()){
                Object o = (Object)it.next();
                DsHoiNghi conf = (DsHoiNghi)o;
                confObservableList.add(conf);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    //load conf list-set detail
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        confListView.setItems(confObservableList);
        confListView.setCellFactory(new HNCellFactory());
        confListView.setOnMouseClicked((event) -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ConferenceDetail.fxml"));
            
            try {
                Parent detailParent;
                detailParent = loader.load();
                ConfDetailController controller = loader.getController();
                controller.initConf(confListView.getSelectionModel().getSelectedItem());
                MainController.addChildrenNode(detailParent);
            } catch (IOException ex) {
                Logger.getLogger(ConfListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    

    
    
}
